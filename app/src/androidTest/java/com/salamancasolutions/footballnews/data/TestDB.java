package com.salamancasolutions.footballnews.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by diego.olguin on 18/08/2015.
 */
public class TestDB extends AndroidTestCase {


    public static final String LOG_TAG = TestDB.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(FootballNewsDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }


    public void testCreateDb() throws Throwable {

        Log.d(LOG_TAG, "Start testCreateDb");

        final HashSet<String> tableNameHashSet = new HashSet<>();

        tableNameHashSet.add(MatchColumns.TABLE_NAME);

        mContext.deleteDatabase(FootballNewsDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FootballNewsDbHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without the results entry", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MatchColumns.TABLE_NAME + ")", null);

        assertTrue("Error: This means that we were unable to querty the dataase for table information.", c.moveToFirst());

        final HashSet<String> resultColumnHashSet = new HashSet<>();

        resultColumnHashSet.add(MatchColumns._ID);
        resultColumnHashSet.add(MatchColumns.COLUMN_HOME_TEAM);
        resultColumnHashSet.add(MatchColumns.COLUMN_AWAY_TEAM);
        resultColumnHashSet.add(MatchColumns.COLUMN_HOME_SCORE);
        resultColumnHashSet.add(MatchColumns.COLUMN_AWAY_SCORE);
        resultColumnHashSet.add(MatchColumns.COLUMN_MATCH_DATE);
        resultColumnHashSet.add(MatchColumns.COLUMN_MATCH_STATUS);
        resultColumnHashSet.add(MatchColumns.COLUMN_TEAM_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);

            resultColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required columns", resultColumnHashSet.isEmpty());
        db.close();
    }

    public void testResultTable() {

        Log.d(LOG_TAG, "Start testResultTable");

        FootballNewsDbHelper dbHelper = new FootballNewsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues matchValues = new ContentValues();

        matchValues.put(MatchColumns.COLUMN_IDENTIFIER, "13434");
        matchValues.put(MatchColumns.COLUMN_HOME_TEAM, "Team1");
        matchValues.put(MatchColumns.COLUMN_HOME_SCORE, 1);
        matchValues.put(MatchColumns.COLUMN_AWAY_TEAM, "Team2");
        matchValues.put(MatchColumns.COLUMN_AWAY_SCORE, 2);
        matchValues.put(MatchColumns.COLUMN_MATCH_STATUS, "FINISHED");
        matchValues.put(MatchColumns.COLUMN_MATCH_DATE, "10/09/2015 15:00:00");
        matchValues.put(MatchColumns.COLUMN_TEAM_ID, 81);

        long resultRowId = db.insert(MatchColumns.TABLE_NAME, null, matchValues);

        Log.d("Insert id:", String.valueOf(resultRowId));

        assertTrue("Error: Insert record failed", resultRowId != -1);

        Cursor resultCursor = db.query(
                MatchColumns.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error: No records found", resultCursor.moveToFirst());

        assertFalse("Error: More than one record returned from the results query", resultCursor.moveToNext());


        String[] projection = {MatchColumns._ID, MatchColumns.COLUMN_MATCH_DATE, MatchColumns.COLUMN_MATCH_STATUS, MatchColumns.COLUMN_TEAM_ID};
        String selection = MatchColumns.COLUMN_TEAM_ID + "= ? ";
        String[] selectionArgs = new String[]{"81"};

        resultCursor = db.query(
                MatchColumns.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        Log.d("Count query by team_id:", String.valueOf(resultCursor.getCount()));


        assertTrue("Error: No records found query by team_id", resultCursor.moveToFirst());
        assertFalse("Error: More than one record returned from the results query", resultCursor.moveToNext());


        resultCursor.close();
        dbHelper.close();
    }

    public void testDeleteTable() {
        FootballNewsDbHelper dbHelper = new FootballNewsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues matchValues = new ContentValues();

        matchValues.put(MatchColumns.COLUMN_IDENTIFIER, "13434");
        matchValues.put(MatchColumns.COLUMN_HOME_TEAM, "Team1");
        matchValues.put(MatchColumns.COLUMN_HOME_SCORE, 1);
        matchValues.put(MatchColumns.COLUMN_AWAY_TEAM, "Team2");
        matchValues.put(MatchColumns.COLUMN_AWAY_SCORE, 2);
        matchValues.put(MatchColumns.COLUMN_MATCH_STATUS, "FINISHED");
        matchValues.put(MatchColumns.COLUMN_MATCH_DATE, "10/09/2015 15:00:00");
        matchValues.put(MatchColumns.COLUMN_TEAM_ID, 81);

        long resultRowId = db.insert(MatchColumns.TABLE_NAME, null, matchValues);


        String[] selectionArgs = new String[]{"81"};

        int res = db.delete(MatchColumns.TABLE_NAME, MatchColumns.COLUMN_TEAM_ID + "= ?", selectionArgs);

        Log.d("Count delete by team_id", String.valueOf(res));
        assertTrue("Error: Deleting by team_id", res > 0);

    }

    public void testUpdateTable() {
        FootballNewsDbHelper dbHelper = new FootballNewsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues matchValues = new ContentValues();

        matchValues.put(MatchColumns.COLUMN_IDENTIFIER, "13434");
        matchValues.put(MatchColumns.COLUMN_HOME_TEAM, "Team1");
        matchValues.put(MatchColumns.COLUMN_HOME_SCORE, 1);
        matchValues.put(MatchColumns.COLUMN_AWAY_TEAM, "Team2");
        matchValues.put(MatchColumns.COLUMN_AWAY_SCORE, 2);
        matchValues.put(MatchColumns.COLUMN_MATCH_STATUS, "FINISHED");
        matchValues.put(MatchColumns.COLUMN_MATCH_DATE, "10/09/2015 15:00:00");
        matchValues.put(MatchColumns.COLUMN_TEAM_ID, 81);

        long resultRowId = db.insert(MatchColumns.TABLE_NAME, null, matchValues);

        ContentValues newValues = new ContentValues(matchValues);
        newValues.put(MatchColumns.COLUMN_MATCH_STATUS, "PLAYING");

        String[] selectionArgs = new String[]{String.valueOf(resultRowId)};

        int res = db.update(MatchColumns.TABLE_NAME, newValues, MatchColumns._ID + "= ?", selectionArgs);

        Log.d("Count update by _id", String.valueOf(res));

        assertTrue("Error: update by _id", res > 0);
    }


}
