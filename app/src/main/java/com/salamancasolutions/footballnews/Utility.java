package com.salamancasolutions.footballnews;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getJsonStringFromNetwork(String team, String days) {
        Log.d(LOG_TAG, "Starting network connection");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String timeFrame = "n" + days; // n30 next 30 days, p30 previous 30 days

        try {
            final String FIXTURE_BASE_URL = "http://api.football-data.org/alpha/teams/";
            final String FIXTURE_PATH = "fixtures";
            final String TIME_FRAME_PARAMETER = "timeFrame";

            Uri builtUri = Uri.parse(FIXTURE_BASE_URL).buildUpon()
                    .appendPath(team)
                    .appendPath(FIXTURE_PATH)
                    .appendQueryParameter(TIME_FRAME_PARAMETER, timeFrame)
                    .build();
            URL url = new URL(builtUri.toString());
            //http://api.football-data.org/alpha/teams/81/fixtures?timeFrame=n30

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null)
                return "";
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0)
                return "";

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                    e.printStackTrace();
                }
            }
        }

        return "";
    }


    //Parse string JSON to List
    public static ArrayList<Match> parseFixtureJson(String fixtureJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(fixtureJson);
        ArrayList<Match> result = new ArrayList<Match>();

        JSONArray fixturesArray = jsonObject.getJSONArray("fixtures");

        for (int i = 0; i < fixturesArray.length(); i++) {
            String selfLink;
            String matchDate;
            String status;
            String homeTeam;
            String awayTeam;
            String homeScore;
            String awayScore;
            String fixtureId;
            Date date = new Date();
            JSONObject matchObject = fixturesArray.getJSONObject(i);
            JSONObject resultObject = matchObject.getJSONObject("result");

            //http://api.football-data.org/alpha/fixtures/147488
            selfLink = matchObject.getJSONObject("_links").getJSONObject("self").getString("href");
            fixtureId = selfLink.substring(selfLink.lastIndexOf("/") + 1, selfLink.length());
            matchDate = matchObject.getString("date");
            status = matchObject.getString("status");

            homeTeam = matchObject.getString("homeTeamName");
            awayTeam = matchObject.getString("awayTeamName");
            homeScore = resultObject.getString("goalsHomeTeam");
            awayScore = resultObject.getString("goalsAwayTeam");

            Log.d("Id", fixtureId);
            Log.d("matchDate", matchDate);
            Log.d("status", status);

            try{
                //2015-08-30T15:00:00Z
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                date = df1.parse(matchDate);
            } catch (Exception e){
                Log.e(LOG_TAG, "Error parsing date", e);
            }

            Match match = new Match(fixtureId,
                    0,
                    homeTeam,
                    homeScore.equalsIgnoreCase("-1")?"0":homeScore,
                    awayTeam,
                    awayScore.equalsIgnoreCase("-1")?"0":awayScore,
                    0,
                    date,
                    status);
            result.add(match);

        }
        return result;
    }

}
