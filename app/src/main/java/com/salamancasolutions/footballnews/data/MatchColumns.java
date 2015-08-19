package com.salamancasolutions.footballnews.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by diego.olguin on 18/08/2015.
 */
public class MatchColumns implements BaseColumns {

    // Path base de results
    public static final Uri CONTENT_URI = FootballNewsDbContract.BASE_CONTENT_URI.buildUpon().appendPath(FootballNewsDbContract.PATH_RESULT).build();

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + FootballNewsDbContract.CONTENT_AUTHORITY + "/" + FootballNewsDbContract.PATH_RESULT;

    public static final String TABLE_NAME = "match";

    //Nombres de columnas

    public static final String COLUMN_IDENTIFIER = "match_identifier";
    public static final String COLUMN_HOME_TEAM = "home_team";
    public static final String COLUMN_AWAY_TEAM = "away_team";
    public static final String COLUMN_HOME_SCORE = "home_score";
    public static final String COLUMN_AWAY_SCORE = "away_score";
    public static final String COLUMN_MATCH_DATE = "match_date";
    public static final String COLUMN_MATCH_STATUS = "match_status";
    public static final String COLUMN_TEAM_ID = "team_id";


    public static int getTeamFromUri(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(1));
    }

    public static Uri buildMatchUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }


}
