package com.salamancasolutions.footballnews;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private ListView mainList;
    private MainListAdapter mainListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<Match> matches = new ArrayList<Match>();

        matches.add(new Match("1", 0, "Barcelona", "0", "Valencia CF", "4", 0, new Date(), "FINISHED"));
        matches.add(new Match("2", 0, "Real Madrid", "1", "Atletico Madrid", "2", 0, new Date(), "FINISHED"));
        matches.add(new Match("3", 0, "Real Betis", "1", "Sporting Gijón", "0", 0, new Date(), "FINISHED"));
        matches.add(new Match("4", 0, "Rayo Vallecano", "0", "Granada CF", "0", 0, new Date(), "FINISHED"));
        matches.add(new Match("5", 0, "Athletic Bilbao", "2", "FC Málaga", "1", 0, new Date(), "FINISHED"));
        matches.add(new Match("6", 0, "Barcelona", "0", "Valencia CF", "4", 0, new Date(), "FINISHED"));
        matches.add(new Match("7", 0, "Real Madrid", "1", "Atletico Madrid", "2", 0, new Date(), "FINISHED"));
        matches.add(new Match("8", 0, "Real Betis", "1", "Sporting Gijón", "0", 0, new Date(), "FINISHED"));
        matches.add(new Match("9", 0, "Rayo Vallecano", "0", "Granada CF", "0", 0, new Date(), "FINISHED"));
        matches.add(new Match("10", 0, "Athletic Bilbao", "2", "FC Málaga", "1", 0, new Date(), "FINISHED"));

        mainListAdapter = new MainListAdapter(this, matches);

        mainList = (ListView)findViewById(R.id.main_list);
        mainList.setAdapter(mainListAdapter);


        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), MatchDetailsActivity.class);
                Match item = (Match)mainListAdapter.getItem(i);
                intent.putExtra("IDMATCH", item.getIdentifier());
                startActivity(intent);

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
