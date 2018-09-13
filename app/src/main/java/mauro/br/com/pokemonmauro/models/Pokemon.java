package mauro.br.com.pokemonmauro.models;


public class Pokemon {
    private int number;
    private String name;
    private String url;

    public Pokemon() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumber() {
        String[] urlPartes = url.split("/");
        return Integer.parseInt(urlPartes[urlPartes.length - 1]);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
