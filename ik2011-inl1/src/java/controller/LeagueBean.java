/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import DAL.LeagueDAO;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import messages.Message;
import model.League;
import model.Match;
import model.Standing;
import model.Team;

/**
 *
 * @author Jeff
 */
@Named("leagueController")
@SessionScoped
public class LeagueBean implements Serializable {

    private int selectedLeague = -1;
    private ArrayList<League> leagues;

    /**
     * Creates a new instance of LeagueBean
     */
    public LeagueBean() {
    }

    public void selectLeague(int leagueId) {
        this.selectedLeague = leagueId;
    }

    public League getSelectedLeague() {
        for (League l :leagues) {
            if (l.getId() == selectedLeague) {
                return l;
            }
        }
        return null;
    }

    public ArrayList<League> getAllLeagues() {

        if (leagues == null) {
            try {
                LeagueDAO dao = LeagueDAO.getInstance();
                dao.connect();
                ArrayList<League> lgs = dao.getAllLeagues();
                dao.disconnect();

                if (lgs.isEmpty()) {
                    leagues = null;
                    Message.addMessageToContext(Message.ERROR_NO_DATA_FOUND);
                } else {
                    selectedLeague = lgs.get(0).getId();
                }
                leagues = lgs;
                return leagues;
            } catch (SQLException sqle) {
                Message.addMessageToContext(Message.ERROR_UNKNOWN);
                return null;
            }
        } else {
            return leagues;
        }
    }
    
    private String currRowStyling = "";
    
    public String getRowStylings() {
        return currRowStyling;
    }
    
    /**
     * Genererar olika style-klasser för alla rader i datatabellen.
     * 
     * @param standings 
     */
    private void generateRowStyling(ArrayList<Standing> standings) {
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < standings.size(); i++) {
            if (i == 0 || i == 1)
                build.append("standings_advance_class");
            else if (i == standings.size()-1)
                build.append("standings_regress_class");
            else
                build.append("standings_unscathed_class");
            
            if (i != standings.size()-1)
                build.append(",");
        }
        
        currRowStyling = build.toString();
    }
    
    public ArrayList<Standing> getStandings() {

        if (selectedLeague == -1) {
            return new ArrayList();
        }

        try {
            LeagueDAO dao = LeagueDAO.getInstance();

            dao.connect();
            ArrayList<Match> matches = dao.getMatchesForLeague(selectedLeague);
            dao.disconnect();
            
            ArrayList<Standing> standings = generateStandings(matches);
            
            generateRowStyling(standings);
            return standings;
        } catch (Exception sqle) {
            /*Message.addMessageToContext("Feltyp " + sqle.getClass() + " - " + sqle.getMessage());
            for (StackTraceElement ste : sqle.getStackTrace()) {
                Message.addMessageToContext(ste.getLineNumber() + " -> " + ste.getFileName());
            }*/

        }
        Message.addMessageToContext(Message.ERROR_NO_DATA_FOUND);
        return null;
    }

    public ArrayList<Standing> generateStandings(ArrayList<Match> matches) {
        ArrayList<Standing> standings = new ArrayList();
        for (Match match : matches) {
            // We can get no information from matches that haven't been played yet.s
            if (match.getAwayScore() == -1 || match.getHomeScore() == -1) {
                continue;
            }
            
            prepareStanding(standings, match.getHome(), match.getHomeScore(), match.getAwayScore());
            prepareStanding(standings, match.getAway(), match.getAwayScore(), match.getHomeScore());

        }

        // Sorterar baserat på vem som vunnit mest.
        Collections.sort(standings, (Standing stOne, Standing stTwo) -> stTwo.getTotalPoints() - stOne.getTotalPoints());
        for (int i = 0; i < standings.size(); i++) {
            standings.get(i).setPosition(i + 1);
        }
        return standings;
    }

    /**
     * Skapar eller uppdaterar Standing-objekt,
     *
     * @param standings
     * @param team
     * @param goalsFor
     * @param goalsAgainst
     */
    private void prepareStanding(ArrayList<Standing> standings, Team team, int goalsFor, int goalsAgainst) {
        int teamIndex;
        Standing stand;
        if ((teamIndex = getIndexOfTeam(standings, team)) != -1) {
            //Message.addMessageToContext("Total indeces: "+(standings.size()-1)+" teamIndex. "+teamIndex);
            //stand = new Standing(team);
            stand = standings.get(teamIndex);
        } else {
            stand = new Standing(team);
            standings.add(stand);
        }
        prepareStandingValues(stand, goalsFor, goalsAgainst);
    }

    /**
     * Genererar värden till det medskickade Standing-objektet, baserat på
     * goalsFor och goalsAgainst,
     *
     * @param standing
     * @param goalsFor
     * @param goalsAgainst
     *
     */
    private void prepareStandingValues(Standing standing, int goalsFor, int goalsAgainst) {
        if (goalsFor > goalsAgainst) {
            standing.addWin();
        } else if (goalsFor == goalsAgainst) {
            standing.addTied();
        } else {
            standing.addLoss();
        }

        standing.addGoalsFor(goalsFor);
        standing.addGoalsAgainst(goalsAgainst);
    }

    /**
     *
     *
     * @param standings
     * @param team
     * @return indexet om funnen, -1 om laget ej påfanns
     */
    private int getIndexOfTeam(ArrayList<Standing> standings, Team team) {
        if (standings.isEmpty())
            return -1;
        
        for (int i = 0; i < standings.size(); i++) {
            if (team.getId() == standings.get(i).getTeam().getId()) {
                return i;
            }
        }
        return -1;
    }
}
