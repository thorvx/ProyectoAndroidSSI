package com.salamancasolutions.footballnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by diego.olguin on 04/08/2015.
 */
public class MainListAdapter extends ArrayAdapter {

    public MainListAdapter(Context context, List objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)parent.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_list_item, null);
        }


        TextView homeTeam = (TextView) convertView.findViewById(R.id.home_name);
        TextView awayTeam = (TextView) convertView.findViewById(R.id.away_name);
        TextView homeScore = (TextView) convertView.findViewById(R.id.home_score);
        TextView awayScore = (TextView) convertView.findViewById(R.id.away_score);
        TextView dateMatch = (TextView) convertView.findViewById(R.id.match_date);
        ImageView homeLogo = (ImageView) convertView.findViewById(R.id.home_logo);
        ImageView awayLogo = (ImageView) convertView.findViewById(R.id.away_logo);


        Match match = (Match) getItem(position);

        homeLogo.setImageResource(getLogoResource(match.getHomeTeam()));
        awayLogo.setImageResource(getLogoResource(match.getAwayTeam()));

        homeTeam.setText(match.getHomeTeam());
        awayTeam.setText(match.getAwayTeam());
        homeScore.setText(match.getHomeScore());
        awayScore.setText(match.getAwayScore());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateMatch.setText(df.format(match.getMatchDate()));

        return convertView;
    }

    public int getLogoResource(String team) {

        int imageResource = 0;

        switch (team) {
            case "FC Barcelona":
                imageResource = getContext().getResources().getIdentifier("ic_barcelona","mipmap", getContext().getPackageName());
                break;
            case "Athletic Club":
                imageResource = getContext().getResources().getIdentifier("ic_athletic","mipmap", getContext().getPackageName());
                break;
            case "Málaga CF":
                imageResource = getContext().getResources().getIdentifier("ic_malaga","mipmap", getContext().getPackageName());
                break;
            case "Club Atlético de Madrid":
                imageResource = getContext().getResources().getIdentifier("ic_atletico_m","mipmap", getContext().getPackageName());
                break;
            default:
                imageResource = getContext().getResources().getIdentifier("ic_emblem","mipmap", getContext().getPackageName());
                break;
        }
        return imageResource;
    }

}