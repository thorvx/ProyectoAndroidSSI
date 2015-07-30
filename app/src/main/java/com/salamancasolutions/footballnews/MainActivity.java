package com.salamancasolutions.footballnews;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] results = {"Wilsterman 1 - Aurora 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "Wilsterman 1 - Aurora 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1",
                "River Plate 1 - Boca Juniors 1"};

        ArrayList<String> resultList = new ArrayList<>(Arrays.asList(results));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.main_list_item,
                R.id.text_item_list,
                resultList);

        ListView mainList = (ListView)findViewById(R.id.main_list);
        mainList.setAdapter(arrayAdapter);
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
