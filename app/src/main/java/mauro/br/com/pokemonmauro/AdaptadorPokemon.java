package mauro.br.com.pokemonmauro;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import mauro.br.com.pokemonmauro.models.Pokemon;

import java.util.ArrayList;


public class AdaptadorPokemon extends RecyclerView.Adapter<AdaptadorPokemon.ViewHolder> implements View.OnClickListener {

    private ArrayList<Pokemon> podedex;
    private Context contexto;
    private View.OnClickListener listener;

    public AdaptadorPokemon(Context contexto) {
        this.contexto = contexto;
        podedex = new ArrayList<>();
    }

    @Override
    public AdaptadorPokemon.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdaptadorPokemon.ViewHolder holder, int position) {
        Pokemon pokemon = podedex.get(position);
        int pokeNumber = position + 1;
        holder.mTextView.setText("#" + pokeNumber + " " + pokemon.getName());
        Glide.with(contexto)
                .load("http://pokeapi.co/media/sprites/pokemon/" + pokemon.getNumber() +".png")
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return podedex.size();
    }

    public void addListPokemon(ArrayList<Pokemon> listPokemon) {
        podedex.addAll(listPokemon);
        notifyDataSetChanged();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null){
            listener.onClick(view);
            view.setSelected(true);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.imagePokemon);
            mTextView = (TextView) itemView.findViewById(R.id.nombrePokemon);
        }
    }
}
