package com.salamancasolutions.footballnews.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FootballNewsContentProvider extends ContentProvider {


    private static final UriMatcher uriMatcher = buildUriMatcher();

    static final int RESULT = 100;
    static final int RESULT_WITH_TEAM = 101;

    private FootballNewsDbHelper dbHelper;


    /**
     * Construcción del UriMatcher, Este UriMatcher hara posible
     * que por cada URI que se pase, se devuelva un valor constante
     * que nos permita identificar luego en el sistema
     * */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FootballNewsDbContract.CONTENT_AUTHORITY;

        // “content://com.salamancasolutions.footballnews/matchs” –> Acceso genérico a tabla de partidos
        // “content://com.salamancasolutions.footballnews/matchs/81″ –> Acceso directo a partidos por id equipo
        matcher.addURI(authority, FootballNewsDbContract.PATH_RESULT, RESULT);
        matcher.addURI(authority, FootballNewsDbContract.PATH_RESULT + "/#", RESULT_WITH_TEAM);

        return matcher;
    }

    public FootballNewsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int deleted;

        if (selection == null)
            selection = "1";

        if (match == RESULT) {
            deleted = db.delete(MatchColumns.TABLE_NAME, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return deleted;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case RESULT:
                return MatchColumns.CONTENT_TYPE;
            case RESULT_WITH_TEAM:
                return MatchColumns.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri returnUri;

        if (match == RESULT) {
            long id = db.insert(MatchColumns.TABLE_NAME, null, values);

            if (id > 0)
                returnUri = MatchColumns.buildMatchUri(id);
            else
                throw new SQLException("Failed to insert row into " + uri);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public boolean onCreate() {

        dbHelper = new FootballNewsDbHelper(getContext());
        return false;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        //Si es una consulta a un team_id concreto construimos el WHERE
        String where = selection;

        if(uriMatcher.match(uri) == RESULT_WITH_TEAM){
            where = MatchColumns.COLUMN_TEAM_ID + "=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(MatchColumns.TABLE_NAME, projection, where,
                selectionArgs, null, null, sortOrder);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int updated;

        if (selection == null)
            selection = "1";

        if (match == RESULT) {
            updated = db.update(MatchColumns.TABLE_NAME, values, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return updated;
    }
}
