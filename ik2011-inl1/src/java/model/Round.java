/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Date;
import java.util.ArrayList;

/**
 *
 * @author Jeff
 */
public class Round {
    private final int totalMatchups;
    private final ArrayList<Match> matches;

    public Round() {
        this.totalMatchups = -1; // Efectively ignores this parameter.
        this.matches = new ArrayList();
    }
    
    public Round(int totalMatchups) {
        this.totalMatchups = totalMatchups;
        matches = new ArrayList<>(totalMatchups);
    }
    
    public boolean addMatch(Match match){
        
        /*if (isFull())
            throw new RoundFullException("Round is full. Create a new one.");*/
        
        matches.add(match);
        return true;
    }
    
    public void setDate(Date date) {
        for (Match match : matches) {
            match.setDate(date);
        }
    }
    
    public boolean isFull() {
        return matches.size() == totalMatchups;
    }
    
    public boolean containsTeam(Team team) {
        boolean found = false;
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).getAway().getName().equals(team.getName()) ||
                matches.get(i).getHome().getName().equals(team.getName())) {
                found = true;
                break;
            }
        }
        
        return found;
    }
    
    public boolean containsAnyTeam(Round round) {
        boolean found = false;
        
        for (Match match : round.getMatches()) {
            found = containsTeam(match.getHome()) || containsTeam(match.getAway());
        }
        
        return found;
    }
    
    public ArrayList<Match> getMatches() {
        return new ArrayList(matches);
    }
    
    public class RoundFullException extends Exception {

        public RoundFullException(String message) {
            super(message);
        }
    }

    public int getTotalMatchups() {
        return totalMatchups;
    }
}
