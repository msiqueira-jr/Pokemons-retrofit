package mauro.br.com.pokemonmauro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.TextSliderView;


import mauro.br.com.pokemonmauro.models.Pokemon;
import mauro.br.com.pokemonmauro.models.PokemonResponse;
import mauro.br.com.pokemonmauro.pokeapi.PokemonService;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;

    private static final String TAG = "POKEDEX: ";

    private RecyclerView mRecyclerView;

    private AdaptadorPokemon adaptadorPokemon;

    private int offset;

    private boolean readyToRefresh;
    ArrayList<Pokemon> listPokemon;
    Map<Integer, String> mapPokemon = new TreeMap<Integer, String>();
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Slider
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);


        ArrayList<String> listUrl = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/0/0d/20091222155412%21025Pikachu.png");
        listName.add("(Pokémon) - Pikachu");

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/b/b8/20121117143907%21059Arcanine.png");
        listName.add("(Pokémon) - Arcanine");

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/1/13/20130824005218%21053Persian.png");
        listName.add("(Pokémon) - Persian");

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/9/9f/20110810055343%21033Nidorino.png");
        listName.add("(Pokémon) - Nidorino");

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/2/21/20101227163906%21001Bulbasaur.png");
        listName.add("(Pokémon) - Bulbasaur");

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/7/73/20130729080255%21002Ivysaur.png");
        listName.add("(Pokémon) - Ivysaur");

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/9/9b/20130523072701%21111Rhyhorn.png");
        listName.add("(Pokémon) - Rhyhorn");

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/a/ae/20130729080248%21003Venusaur.png");
        listName.add("(Pokémon) - Venusaur");

        listUrl.add("https://cdn.bulbagarden.net/upload/archive/b/b9/20130810070434%21172Pichu.png");
        listName.add("(Pokémon) - Pichu");

        //RequestOptions requestOptions = new RequestOptions();
        //requestOptions
        //        .centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(this);
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                    .description(listName.get(i))
          //          .setRequestOption(requestOptions)
            //        .setBackgroundColor(Color.WHITE)
              //      .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", listName.get(i));
            mDemoSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);



        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        adaptadorPokemon = new AdaptadorPokemon(this);

        mRecyclerView.setAdapter(adaptadorPokemon);
        mRecyclerView.setHasFixedSize(true);

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (readyToRefresh){
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount){
                            Log.i(TAG, "Final del Recycler...");

                            readyToRefresh = false;
                            offset += 20;
                            obtenerDatos(offset);
                        }
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        readyToRefresh = true;
        offset = 0;
        obtenerDatos(offset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(getText(R.string.buscar));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void obtenerDatos(int offset) {
        PokemonService service = retrofit.create(PokemonService.class);
        Call<PokemonResponse> pokemonResponseCall = service.getPokemonList(20, offset);
        pokemonResponseCall.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                Log.d(TAG, response.body().getResults().toString());

                readyToRefresh = true;
                if (response.isSuccessful()){
                    PokemonResponse pokemonResponse = response.body();
                    listPokemon = pokemonResponse.getResults();
                    for (Pokemon poke : listPokemon) {
                        poke.setNumber(poke.getNumber());
                        Log.d(TAG, "\n" + poke);
                        mapPokemon.put(poke.getNumber(), poke.getName());
                    }
                    Log.d(TAG,"Map Values " + "\n" + mapPokemon.values() + "\n" + mapPokemon.size());
                    adaptadorPokemon.addListPokemon(listPokemon);

                } else {
                    Log.e(TAG, "onResponse: " + response.errorBody());
                }

                adaptadorPokemon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Log.d(TAG, "Click: Posicion del layout " + mRecyclerView.getChildLayoutPosition(view));
                            Intent intent = new Intent(MainActivity.this, PokeInfo.class);
                            int pokedexNumber = mRecyclerView.getChildAdapterPosition(view) + 1;
                            if (pokedexNumber > 721){
                                pokedexNumber = pokedexNumber + 9279;
                                String pokedexName = mapPokemon.get(pokedexNumber);
                                intent.putExtra("pokeNum", pokedexNumber);
                                intent.putExtra("pokeName", pokedexName);
                            }else{
                                String pokedexName = mapPokemon.get(pokedexNumber);
                                intent.putExtra("pokeNum", pokedexNumber);
                                intent.putExtra("pokeName", pokedexName);
                            }
                            startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                readyToRefresh = true;
                Log.e(TAG, "onFailure: " +t.getMessage());
            }
        });
    }


    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
