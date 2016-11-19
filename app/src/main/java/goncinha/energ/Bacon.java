package goncinha.energ;

/**
 * Created by mateu on 14/11/2016.
 */

public class Bacon {
    private String name;
    private String disponibilidade;
    private int onoff;
    private int icon;
    private String io;

    public Bacon(String name, String disponibilidade,int onoff, int icon,String io) {
        this.name = name;
        this.disponibilidade = disponibilidade;
        this.icon = icon;
        this.onoff = onoff;
        this.io = io;
    }

    public String getIo() {
        return io;
    }

    public void setIo(String io) {
        this.io = io;
    }

    public int getOnoff() {
        return onoff;
    }

    public void setOnoff(int onoff) {
        this.onoff = onoff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(String disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
