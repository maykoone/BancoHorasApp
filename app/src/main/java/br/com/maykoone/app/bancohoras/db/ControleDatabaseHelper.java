package br.com.maykoone.app.bancohoras.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.maykoone.app.bancohoras.db.ControleDatabase.RegistroPontoType;

/**
 * Created by maykoone on 04/07/15.
 */
public class ControleDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bancohoras_database";
    private static final String REGISTRO_PONTO_TABLE = "registro_ponto";

    public ControleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + RegistroPontoType.REGISTRO_PONTO_TABLE + " ( "
                + RegistroPontoType._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + RegistroPontoType.DATA_EVENTO + " TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    public void addRegistroPonto(RegistroPontoEntity e) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RegistroPontoType.DATA_EVENTO, e.getDataEvento());
        long generatedId = db.insert(RegistroPontoType.REGISTRO_PONTO_TABLE, null, values);
        db.close();
    }

    public void updateRegistroPonto(RegistroPontoEntity r) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RegistroPontoType.DATA_EVENTO, r.getDataEvento());
        long generatedId = db.update(RegistroPontoType.REGISTRO_PONTO_TABLE, values,
                RegistroPontoType._ID + "=?", new String[]{String.valueOf(r.getId())});
        db.close();
    }

    public List<RegistroPontoEntity> getAllRegistrosPontoForToday() {
        List<RegistroPontoEntity> result = new ArrayList<>();

        String query = "SELECT * FROM " + RegistroPontoType.REGISTRO_PONTO_TABLE + " WHERE DATE(" + RegistroPontoType.DATA_EVENTO + ")= " +
                "DATE('now','localtime') ORDER BY " + RegistroPontoType.DATA_EVENTO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String dataEvento = cursor.getString(1);
                int id = cursor.getInt(0);
                result.add(new RegistroPontoEntity(id, dataEvento));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return result;
    }

    public List<RegistroPontoEntity> getAllRegistrosPontoByMonthAndYear(int month, int year) {
        List<RegistroPontoEntity> result = new ArrayList<>();
        String monthStr = String.format("%02d", month);

        String query = "SELECT * FROM " + RegistroPontoType.REGISTRO_PONTO_TABLE
                + " WHERE strftime('%m', " + RegistroPontoType.DATA_EVENTO + ") = '" + monthStr + "'"
                + " AND strftime('%Y', " + RegistroPontoType.DATA_EVENTO + ") = '" + year + "'"
                + " ORDER BY " + RegistroPontoType.DATA_EVENTO;

        Log.i("QUERY", query);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String dataEvento = cursor.getString(1);
                int id = cursor.getInt(0);
                result.add(new RegistroPontoEntity(id, dataEvento));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return result;
    }

    public Set<String> getDistinctMonths() throws ParseException {
        Set<String> result = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        SimpleDateFormat sdfLong = new SimpleDateFormat("MMM yyyy");

        String query = "SELECT DISTINCT strftime('%m'," + RegistroPontoType.DATA_EVENTO + " ),"
                + "strftime('%Y'," + RegistroPontoType.DATA_EVENTO + " ) FROM "
                + RegistroPontoType.REGISTRO_PONTO_TABLE;

        Log.i("QUERY", query);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String month = cursor.getString(0);
                String year = cursor.getString(1);
                result.add(sdfLong.format(sdf.parse(month + "-" + year)));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return result;
    }

    public List<RegistroPontoEntity> getAllRegistrosPonto() {
        List<RegistroPontoEntity> result = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(RegistroPontoType.REGISTRO_PONTO_TABLE, null, null, null, null, null, RegistroPontoType.DATA_EVENTO);

        if (cursor.moveToFirst()) {
            do {
                String dataEvento = cursor.getString(1);
                int id = cursor.getInt(0);
                result.add(new RegistroPontoEntity(id, dataEvento));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return result;
    }

    public void deleteRegistroPonto(RegistroPontoEntity e) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(REGISTRO_PONTO_TABLE, RegistroPontoType._ID + "=?", new String[]{String.valueOf(e.getId())});

        db.close();
    }


}
