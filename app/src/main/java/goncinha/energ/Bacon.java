package goncinha.energ;

/**
 * Created by italo on 04/12/16.
 */

public class Bacon {
    private String corrente, online;

    public Bacon() {

    }

    public Bacon(String corrente, String online) {
        this.corrente = corrente;
        this.online = online;
    }

    public String getCorrente() {
        return corrente;
    }

    public void setCorrente(String corrente) {
        this.corrente = corrente;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
