package br.com.maykoone.app.bancohoras.db;

import java.io.Serializable;

/**
 * Created by maykoone on 04/07/15.
 */
public class RegistroPontoEntity implements Serializable {
    private int id;
    private String dataEvento;

    public RegistroPontoEntity() {
    }

    public RegistroPontoEntity(int id, String dataEvento) {
        this.id = id;
        this.dataEvento = dataEvento;
    }

    public RegistroPontoEntity(String dataEvento) {
        this.dataEvento = dataEvento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(String dataEvento) {
        this.dataEvento = dataEvento;
    }

    @Override
    public String toString() {
        return dataEvento;
    }
}
