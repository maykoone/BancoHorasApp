package br.com.maykoone.app.bancohoras;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.maykoone.app.bancohoras.db.RegistroPontoEntity;

/**
 * Created by maykoone on 03/09/15.
 */
public class Util {

    public static long calculeTime(List<RegistroPontoEntity> registros) {
        long totalTimeMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Iterator<RegistroPontoEntity> it = registros.iterator();
        while (it.hasNext()) {
            try {
                Date dataRegistroInicio = sdf.parse(it.next().getDataEvento());
                Date dataRegistroFinal = it.hasNext() ? sdf.parse(it.next().getDataEvento()) : new Date();
                totalTimeMillis += dataRegistroFinal.getTime() - dataRegistroInicio.getTime();
            } catch (ParseException e) {
                Log.e("calculeTime", e.getMessage());
            }
        }

        return totalTimeMillis;
    }

    public static String formatTime(long time) {
        long diffMinutes = Math.abs(time) / (60 * 1000) % 60;
        long diffHours = Math.abs(time) / (60 * 60 * 1000) % 24;
        String timeFormatted = String.format("%02d:%02d", diffHours, diffMinutes);
        return time < 0 ? "-" + timeFormatted : timeFormatted;
    }
}
