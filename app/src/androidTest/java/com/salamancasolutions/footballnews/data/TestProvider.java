package com.salamancasolutions.footballnews.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by diego.olguin on 18/08/2015.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(MatchColumns.CONTENT_URI, null, null);
        Cursor cursor = mContext.getContentResolver().query(MatchColumns.CONTENT_URI, null, null, null, null);

        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager packageManager = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), FootballNewsContentProvider.class.getName());

        try {
            ProviderInfo providerInfo = packageManager.getProviderInfo(componentName, 0);

            assertEquals(providerInfo.authority, FootballNewsDbContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(MatchColumns.CONTENT_URI);

        assertEquals(MatchColumns.CONTENT_TYPE, type);

        int testTeam = 81;

        type = mContext.getContentResolver().getType(MatchColumns.buildMatchUri(testTeam));
        assertEquals(MatchColumns.CONTENT_TYPE, type);

    }

    public void testBasicQuery() {
        // First insert
        FootballNewsDbHelper dbHelper = new FootballNewsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues matchValues = new ContentValues();

        matchValues.put(MatchColumns.COLUMN_IDENTIFIER,"13434");
        matchValues.put(MatchColumns.COLUMN_HOME_TEAM,"Team1");
        matchValues.put(MatchColumns.COLUMN_HOME_SCORE,1);
        matchValues.put(MatchColumns.COLUMN_AWAY_TEAM, "Team2");
        matchValues.put(MatchColumns.COLUMN_AWAY_SCORE, 2);
        matchValues.put(MatchColumns.COLUMN_MATCH_STATUS, "FINISHED");
        matchValues.put(MatchColumns.COLUMN_MATCH_DATE, "10/09/2015 15:00:00");
        matchValues.put(MatchColumns.COLUMN_TEAM_ID, 81);

        long rowId = db.insert(MatchColumns.TABLE_NAME, null, matchValues);

        assertTrue(rowId != -1);

        db.close();

        Cursor resultCursor = mContext.getContentResolver().query(MatchColumns.CONTENT_URI, null, null, null, null);

        assertTrue("Empty cursor returned. ", resultCursor.moveToFirst());

    }

    public void testUpdate() {
        ContentValues matchValues = new ContentValues();

        matchValues.put(MatchColumns.COLUMN_IDENTIFIER,"134345");
        matchValues.put(MatchColumns.COLUMN_HOME_TEAM,"Team2");
        matchValues.put(MatchColumns.COLUMN_HOME_SCORE,3);
        matchValues.put(MatchColumns.COLUMN_AWAY_TEAM, "Team3");
        matchValues.put(MatchColumns.COLUMN_AWAY_SCORE, 4);
        matchValues.put(MatchColumns.COLUMN_MATCH_STATUS, "FINISHED");
        matchValues.put(MatchColumns.COLUMN_MATCH_DATE, "10/09/2015 15:00:00");
        matchValues.put(MatchColumns.COLUMN_TEAM_ID, 81);

        Uri locationUri = mContext.getContentResolver().
                insert(MatchColumns.CONTENT_URI, matchValues);
        long resultRowId = ContentUris.parseId(locationUri);

        assertTrue(resultRowId != -1);
        Log.d(LOG_TAG, "New row id: " + resultRowId);

        ContentValues updatedValues = new ContentValues(matchValues);
        updatedValues.put(MatchColumns._ID, resultRowId);
        updatedValues.put(MatchColumns.COLUMN_HOME_TEAM, "Atletico Madrid");

        int count = mContext.getContentResolver().update(
                MatchColumns.CONTENT_URI, updatedValues, MatchColumns._ID + "= ?",
                new String[] { Long.toString(resultRowId)});
        assertEquals(count, 1);


        Cursor cursor = mContext.getContentResolver().query(
                MatchColumns.CONTENT_URI,
                null,
                MatchColumns._ID + " = " + resultRowId,
                null,
        null
        );

        assertTrue("Empty cursor returned value updated ", cursor.moveToFirst());

        cursor.close();
    }

    public void testInsert() {

    }

    public void testDelete() {

    }

}