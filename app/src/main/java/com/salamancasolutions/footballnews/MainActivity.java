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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private ListView mainList;
    private MainListAdapter mainListAdapter;
    private static final String LOG_TAG = Utility.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<Match> matches = new ArrayList<Match>();

      matches.add(new Match("1", 0, "FC Barcelona", "0", "Athletic Club", "4", 0, new Date(), "FINISHED"));
        matches.add(new Match("2", 0, "Málaga CF", "1", "FC Barcelona", "2", 0, new Date(), "FINISHED"));
        matches.add(new Match("3", 0, "Club Atlético de Madrid", "1", "FC Barcelona", "0", 0, new Date(), "FINISHED"));
        matches.add(new Match("4", 0, "FC Barcelona", "0", "Málaga CF", "0", 0, new Date(), "FINISHED"));
        matches.add(new Match("5", 0, "Athletic Club", "2", "FC Barcelona", "1", 0, new Date(), "FINISHED"));
        matches.add(new Match("6", 0, "FC Barcelona", "0", "Club Atlético de Madrid", "4", 0, new Date(), "FINISHED"));

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String teamId = prefs.getString("favorite_team", "81");

        GetResultTask task = new GetResultTask();
        task.execute(teamId, "60");
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
            if (params.length != 2)
                return null;

            String resultString = Utility.getJsonStringFromNetwork(params[0], params[1]);

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
