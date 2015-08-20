package com.salamancasolutions.footballnews;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.salamancasolutions.footballnews.data.FootballNewsDbHelper;
import com.salamancasolutions.footballnews.data.MatchColumns;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class MainActivity extends ActionBarActivity {

    private ListView mainList;
    private MainListAdapter mainListAdapter;
    private static final String LOG_TAG = Utility.class.getSimpleName();
    private String teamId;
    private DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Match> matches = new ArrayList<Match>();

        mainListAdapter = new MainListAdapter(this, matches);

        mainList = (ListView)findViewById(R.id.main_list);
        mainList.setAdapter(mainListAdapter);


        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), MatchDetailsActivity.class);
                Match item = (Match) mainListAdapter.getItem(i);
                intent.putExtra("IDMATCH", item.getIdentifier());
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshResults();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void refreshResults(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        teamId = prefs.getString("favorite_team", "81");
        int futureDays = Integer.parseInt(prefs.getString("next_days", "10"));
        int pastDays = Integer.parseInt(prefs.getString("past_days", "10"));

        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, futureDays);
        Date futureDay = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, futureDays * -1);
        cal.add(Calendar.DAY_OF_MONTH, pastDays * -1);
        Date pastDay = cal.getTime();

        loadResultsList(teamId);
        DateFormat dfapi = new SimpleDateFormat("yyyy-MM-dd");
        GetResultTask task = new GetResultTask();
        task.execute(teamId, dfapi.format(pastDay), dfapi.format(futureDay));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        if (id == R.id.action_refresh) {
            refreshResults();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class GetResultTask extends AsyncTask<String, Void, ArrayList<Match>> {

        @Override
        protected ArrayList<Match> doInBackground(String... params) {
            if (params.length != 3)
                return null;

            String resultString = Utility.getJsonStringFromNetwork(params[0], params[1], params[2]);

            try {

                return Utility.parseFixtureJson(resultString);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing" + e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Match> results) {

            if(results!=null) {

                if(!existsLocalData(teamId)){
                    saveData(results, teamId);
                } else {
                    deleteData(teamId);
                    saveData(results, teamId);
                    //updateData(results, teamId);
                }

                mainListAdapter.clear();
                mainList.invalidate();
                for (Match result : results) {
                    mainListAdapter.add(result);
                }
            } else {
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.error_get_results), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void updateData(ArrayList<Match> results, String teamId) {

        for (Match result : results) {

            if(existsMatch(result.getIdentifier())){
                updateMatch(result);
            } else {
                ContentValues matchValues = new ContentValues();
                matchValues.put(MatchColumns.COLUMN_IDENTIFIER, result.getIdentifier());
                matchValues.put(MatchColumns.COLUMN_HOME_TEAM, result.getHomeTeam());
                matchValues.put(MatchColumns.COLUMN_HOME_SCORE, result.getHomeScore());
                matchValues.put(MatchColumns.COLUMN_AWAY_TEAM, result.getAwayTeam());
                matchValues.put(MatchColumns.COLUMN_AWAY_SCORE, result.getAwayScore());
                matchValues.put(MatchColumns.COLUMN_MATCH_STATUS, result.getMatchStatus());
                matchValues.put(MatchColumns.COLUMN_MATCH_DATE, df.format(result.getMatchDate()));
                matchValues.put(MatchColumns.COLUMN_TEAM_ID, teamId);

                Uri res = this.getContentResolver().insert(MatchColumns.CONTENT_URI,matchValues);
                long resultRowId = ContentUris.parseId(res);

                if(resultRowId>0) Log.d("Match insert", result.getIdentifier());
                else Log.d("Match not insert", result.getIdentifier());
            }
        }

    }

    private void deleteData(String teamId) {

        int res = this.getContentResolver().delete(MatchColumns.CONTENT_URI, MatchColumns.COLUMN_TEAM_ID + "=" + teamId, null);

        if(res>0) Log.d("Matchs deleted", String.valueOf(res));
        else Log.d("Matchs not deleted",  String.valueOf(res));

    }

    private void updateMatch(Match result) {

        ContentValues matchValues = new ContentValues();
        matchValues.put(MatchColumns.COLUMN_HOME_TEAM, result.getHomeTeam());
        matchValues.put(MatchColumns.COLUMN_HOME_SCORE, result.getHomeScore());
        matchValues.put(MatchColumns.COLUMN_AWAY_TEAM, result.getAwayTeam());
        matchValues.put(MatchColumns.COLUMN_AWAY_SCORE, result.getAwayScore());
        matchValues.put(MatchColumns.COLUMN_MATCH_STATUS, result.getMatchStatus());
        matchValues.put(MatchColumns.COLUMN_MATCH_DATE, df.format(result.getMatchDate()));
        matchValues.put(MatchColumns.COLUMN_TEAM_ID, teamId);

        int res = this.getContentResolver().update(MatchColumns.CONTENT_URI, matchValues, MatchColumns.COLUMN_IDENTIFIER + "=" + result.getIdentifier(), null );
        if(res>0) Log.d("Match updated", result.getIdentifier());
        else Log.d("Match not updated", result.getIdentifier());

    }

    private boolean existsMatch(String identifier) {

        Cursor cursor = this.getContentResolver().query(
                MatchColumns.CONTENT_URI,
                null,
                MatchColumns.COLUMN_IDENTIFIER + "=" + identifier,
                null,
                null);

        if(cursor.moveToFirst()){
            return true;
        } else {
            return false;
        }

    }


    private void saveData(ArrayList<Match> results, String teamId) {

        for (Match result : results) {
            ContentValues matchValues = new ContentValues();
            matchValues.put(MatchColumns.COLUMN_IDENTIFIER, result.getIdentifier());
            matchValues.put(MatchColumns.COLUMN_HOME_TEAM, result.getHomeTeam());
            matchValues.put(MatchColumns.COLUMN_HOME_SCORE, result.getHomeScore());
            matchValues.put(MatchColumns.COLUMN_AWAY_TEAM, result.getAwayTeam());
            matchValues.put(MatchColumns.COLUMN_AWAY_SCORE, result.getAwayScore());
            matchValues.put(MatchColumns.COLUMN_MATCH_STATUS, result.getMatchStatus());
            matchValues.put(MatchColumns.COLUMN_MATCH_DATE, df.format(result.getMatchDate()));
            matchValues.put(MatchColumns.COLUMN_TEAM_ID, teamId);

            this.getContentResolver().insert(MatchColumns.CONTENT_URI,matchValues);

        }

    }

    private boolean existsLocalData(String team) {

        Cursor cursor = this.getContentResolver().query(
                MatchColumns.CONTENT_URI,
                null,
                MatchColumns.COLUMN_TEAM_ID + " = " + team,
                null,
                null);

        if(cursor.moveToFirst()){
            return true;
        } else {
            return false;
        }

    }

    public void loadResultsList(String teamId){

        mainListAdapter.clear();
        mainList.invalidate();

        Cursor cursor = this.getContentResolver().query(
                MatchColumns.CONTENT_URI,
                null,
                MatchColumns.COLUMN_TEAM_ID + " = " + teamId,
                null,
                null);

        while (cursor.moveToNext()) {

            mainListAdapter.add(new Match(cursor.getString(1), 0, cursor.getString(2), cursor.getString(4), cursor.getString(3), cursor.getString(5), 0, toFormatedDate(cursor.getString(6)), cursor.getString(7)));

        }

    }

    public Date toFormatedDate(String s){
        Date res = null;
        try {

            res = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

}
