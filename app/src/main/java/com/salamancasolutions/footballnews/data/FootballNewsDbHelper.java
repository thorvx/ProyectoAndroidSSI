package com.salamancasolutions.footballnews.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by diego.olguin on 18/08/2015.
 */
public class FootballNewsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "matchs.db";

    //Sentencia SQL para crear la tabla de Match
    String sqlCreate = "CREATE TABLE " + MatchColumns.TABLE_NAME +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MatchColumns.COLUMN_IDENTIFIER + " TEXT, " +
            MatchColumns.COLUMN_HOME_TEAM + " TEXT, " +
            MatchColumns.COLUMN_AWAY_TEAM + " TEXT, " +
            MatchColumns.COLUMN_HOME_SCORE + " INTEGER, " +
            MatchColumns.COLUMN_AWAY_SCORE + " INTEGER, " +
            MatchColumns.COLUMN_MATCH_DATE + " TEXT, " +
            MatchColumns.COLUMN_MATCH_STATUS + " TEXT, " +
            MatchColumns.COLUMN_TEAM_ID + " INTEGER )";


    public FootballNewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public FootballNewsDbHelper(Context contexto, String nombre,
                                SQLiteDatabase.CursorFactory factory, int version) {

        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS match");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
    }


}
