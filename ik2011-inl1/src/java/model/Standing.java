/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Jeff
 */
public class Standing implements Serializable {
    private static final int WIN_POINTS = 2;
    private static final int TIE_POINTS = 1;
    private static final int LOSE_POINTS = 0;
    
    private Team team;
    private int position;
    
    private int matchesPlayed;
    private int wins;
    private int tied;
    private int losses;
    
    private int totalPoints;
    
    private int goalsFor;
    private int goalsAgainst;
    
    
    
    public Standing() {
    }

    public Standing(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getTied() {
        return tied;
    }

    public void setTied(int tied) {
        this.tied = tied;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
    
    
    public void addWin() {
        this.wins++;
        this.matchesPlayed++;
        this.totalPoints += WIN_POINTS;
    }
    
    public void addTied() {
        this.tied++;
        this.matchesPlayed++;
        this.totalPoints += TIE_POINTS;
    }
    
    public void addLoss() {
        this.losses++;
        this.matchesPlayed++;
        this.totalPoints += LOSE_POINTS;
    }
    
    public void addGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst += goalsAgainst;
    }
    
    public void addGoalsFor(int goalsFor) {
        this.goalsFor += goalsFor;
    }
}
