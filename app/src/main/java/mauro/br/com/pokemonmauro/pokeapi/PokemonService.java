package mauro.br.com.pokemonmauro.pokeapi;

import mauro.br.com.pokemonmauro.models.PokemonDetails;
import mauro.br.com.pokemonmauro.models.PokemonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface PokemonService {
    @GET("pokemon")

    Call<PokemonResponse> getPokemonList(@Query("limit") int limit, @Query("offset") int offset);

    @GET("pokemon/{id}")
    Call<PokemonDetails> getPokemonDetails(@Path("id") int id);
}
