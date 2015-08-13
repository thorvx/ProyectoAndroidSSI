package com.salamancasolutions.footballnews;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;

import java.text.DateFormat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<Match> matches = new ArrayList<Match>();

        matches.add(new Match("1", 0, "Team 1", "0", "Team 2", "0", 0, new Date(), "TIMED"));


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
        String teamId = prefs.getString("favorite_team", "81");
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

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        GetResultTask task = new GetResultTask();
        task.execute(teamId, df.format(pastDay), df.format(futureDay));

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

}
