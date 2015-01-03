/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Toppe
 */
public class Match implements Serializable{
    private int id;
    private Team home;
    private Team away;
    private int homeScore = -1;
    private int awayScore = -1;
    private Date date;

    public Match() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getHome() {
        return home;
    }

    public void setHome(Team home) {
        this.home = home;
    }

    public Team getAway() {
        return away;
    }

    public void setAway(Team away) {
        this.away = away;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    /**
     * Kollar upp om båda matcherna har samma lag som spelar mot varandra.
     * 
     * Använd främst för Round Robin struktur då denna kollar om matcherna är lika även med omvänd hemma/borta-tillhörighet.
     * För Double round robin, använd equalsIgnoreReversed().
     * 
     * @param match
     * @return 
     */
    public boolean equals(Match match) {
        return (this.getAway().getName().equals(match.getAway().getName()) &&
               this.getHome().getName().equals(match.getHome().getName())) ||
               (this.getAway().getName().equals(match.getHome().getName()) &&
               this.getHome().getName().equals(match.getAway().getName()));
    }
    
    public boolean equalsIgnoreReversed(Match match) {
        return this.getAway().getName().equals(match.getAway().getName()) &&
               this.getHome().getName().equals(match.getHome().getName());
    }
    
    public boolean containsTeam(Team team) {
        return team.getName().equals(this.getHome().getName()) || team.getName().equals(this.getAway().getName());
    }
}
