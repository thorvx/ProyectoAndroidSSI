package com.salamancasolutions.footballnews.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.salamancasolutions.footballnews.MainActivity;
import com.salamancasolutions.footballnews.Match;
import com.salamancasolutions.footballnews.R;
import com.salamancasolutions.footballnews.Utility;
import com.salamancasolutions.footballnews.data.MatchColumns;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class MatchsResultsService extends IntentService {

    private static final String LOG_TAG = MatchsResultsService.class.getSimpleName();

    public static final String TEAM_ID = "com.salamancasolutions.footballnews.Service.extra.TEAM_ID";
    public static final String START_DATE = "com.salamancasolutions.footballnews.Service.extra.START_DATE";
    public static final String END_DATE = "com.salamancasolutions.footballnews.Service.extra.END_DATE";

    public static final String RESPONSE_STATUS = "RESPONSE_STATUS";
    public static final String RESPONSE_STATUS_OK = "RESPONSE_STATUS_OK";
    public static final String RESPONSE_STATUS_NOK = "RESPONSE_STATUS_NOK";

    private static final int RESULT_NOTIFICATION_ID = 3004;

    public MatchsResultsService() {
        super("MatchsResultsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(LOG_TAG, "Service started");
        if (intent != null) {
            final String param1 = intent.getStringExtra(TEAM_ID);
            final String param2 = intent.getStringExtra(START_DATE);
            final String param3 = intent.getStringExtra(END_DATE);
            handleActionFetch(param1, param2, param3);
        }
        Log.v(LOG_TAG, "Service finished");
    }

    /**
     * Handle action in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetch(String param1, String param2, String param3) {
        String statusResponse=null;
        try {
            String resultString = Utility.getJsonStringFromNetwork(param1, param2, param3);
            ArrayList<Match> results = Utility.parseFixtureJson(resultString);

            if (!existsLocalData(param1)) {
                saveData(results, param1);
            } else {
                deleteData(param1);
                saveData(results, param1);
            }

            //Show notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText(getString(R.string.msg_notif));
            builder.setSmallIcon(R.drawable.ic_notification);

            Uri notifSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(notifSound);

            Intent resultIntent = new Intent(this, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(RESULT_NOTIFICATION_ID, builder.build());
            Log.d(LOG_TAG, "Notification sent");

            statusResponse = RESPONSE_STATUS_OK;

        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to get results");
            statusResponse = RESPONSE_STATUS_NOK;
        } finally {
            //Send status to main activity
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.ServiceReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(RESPONSE_STATUS, statusResponse);
            sendBroadcast(broadcastIntent);
        }

    }

    private boolean existsLocalData(String team) {

        Cursor cursor = this.getContentResolver().query(
                MatchColumns.CONTENT_URI,
                null,
                MatchColumns.COLUMN_TEAM_ID + " = " + team,
                null,
                null);

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }

    }

    private void saveData(ArrayList<Match> results, String teamId) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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

            this.getContentResolver().insert(MatchColumns.CONTENT_URI, matchValues);

        }

    }

    private void deleteData(String teamId) {

        int res = this.getContentResolver().delete(MatchColumns.CONTENT_URI, MatchColumns.COLUMN_TEAM_ID + "=" + teamId, null);

        if (res > 0) Log.d("Matchs deleted", String.valueOf(res));
        else Log.d("Matchs not deleted", String.valueOf(res));

    }

}
