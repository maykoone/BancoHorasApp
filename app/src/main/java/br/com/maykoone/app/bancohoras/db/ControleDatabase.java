package br.com.maykoone.app.bancohoras.db;

import android.provider.BaseColumns;

/**
 * Created by maykoone on 04/07/15.
 */
public class ControleDatabase {
    private ControleDatabase() {
    }

    public static final class RegistroPontoType implements BaseColumns {
        public static final String REGISTRO_PONTO_TABLE = "registro_ponto";
        public static final String DATA_EVENTO = "data_evento";

        private RegistroPontoType() {
        }
    }
}
