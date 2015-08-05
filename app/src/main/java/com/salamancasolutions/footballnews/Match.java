package com.salamancasolutions.footballnews;

import java.util.Date;

/**
 * Created by diego.olguin on 31/07/2015.
 */
public class Match {
    private String identifier;
    private int homeLogo;
    private String homeTeam;
    private String homeScore;
    private String awayTeam;
    private String awayScore;
    private int awayLogo;
    private Date matchDate;
    private String matchStatus;

    public Match() {
    }

    public Match(String identifier, int homeLogo, String homeTeam, String homeScore, String awayTeam, String awayScore, int awayLogo, Date matchDate, String matchStatus) {
        this.identifier = identifier;
        this.homeLogo = homeLogo;
        this.homeTeam = homeTeam;
        this.homeScore = homeScore;
        this.awayTeam = awayTeam;
        this.awayScore = awayScore;
        this.awayLogo = awayLogo;
        this.matchDate = matchDate;
        this.matchStatus = matchStatus;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getHomeLogo() {
        return homeLogo;
    }

    public void setHomeLogo(int homeLogo) {
        this.homeLogo = homeLogo;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }

    public int getAwayLogo() {
        return awayLogo;
    }

    public void setAwayLogo(int awayLogo) {
        this.awayLogo = awayLogo;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }
}