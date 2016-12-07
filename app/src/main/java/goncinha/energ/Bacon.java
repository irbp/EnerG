package goncinha.energ;

/**
 * Created by italo on 04/12/16.
 */

public class Bacon {
    private String online;
    private Float corrente;
    private String nome;

    public Bacon() {

    }

    public Bacon(String online, String nome) {
        this.online = online;
        this.nome = nome;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}