/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import DAL.LeagueDAO;
import DAL.TeamDAO;
import model.Match;
import model.Team;
import model.League;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import messages.Message;

/**
 *
 * @author Toppe
 */
@Named("adminBean")
@SessionScoped
public class AdminBean implements Serializable {

    private int selectedLeagueId = -1;
    private Match matchToUpdate = null;
    
    public AdminBean() {

    }
    
    public Match getMatchToUpdate() {
        return this.matchToUpdate;
    }
    
    public void setMatchToUpdate(Match match) {
        this.matchToUpdate = match;
    }
    
    public boolean isUpdating(){
        
        return matchToUpdate != null;
        
        /*Date today = new Date();
        return match.getDate().before(today) && match.getAwayScore() == -1 && match.getHomeScore() == -1 && match.getId() == matchId;*/
    }

    public ArrayList<Match> getMatchesForTeam(Team team) {
        ArrayList<Match> list = new ArrayList<>();

        if (selectedLeagueId == -1) {
            return list;
        }

        try {
            TeamDAO dao = new TeamDAO();
            dao.connect();
            list = dao.getMatchesForTeam(team, selectedLeagueId);
            dao.disconnect();
        } catch (SQLException e) {
            Message.outputMessage(e.getMessage());
        }
        return list;
    }

    public ArrayList<League> getLeaguesForTeam(Team team) {
        ArrayList<League> list = new ArrayList<>();
        try {
            TeamDAO dao = new TeamDAO();
            dao.connect();
            list = dao.getLeaguesForTeam(team);
            dao.disconnect();
        } catch (SQLException e) {
            Message.outputMessage(e.getMessage());
        }
        if (list.size() == 1) {
            selectedLeagueId = list.get(0).getId();
        }

        return list;
    }

    public boolean isSelectedLeague(int leagueId) {
        return selectedLeagueId != -1 && leagueId == selectedLeagueId;
    }

    public String selectLeague(int leagueId) {
        this.selectedLeagueId = leagueId;
        return "";
    }

    public int getSelectedLeague() {
        return selectedLeagueId;
    }

    public boolean isMatchReportable(Match match) {
        Date today = new Date();
        return match.getDate().before(today) && match.getAwayScore() == -1 && match.getHomeScore() == -1;
    }

    public boolean hasSelectedLeague() {
        return selectedLeagueId != -1;
    }

    public String reportScores(Match match, String homeScore, String awayScore) {
        
        if (homeScore.isEmpty() || awayScore.isEmpty()) {
            Message.outputMessage(Message.ERROR_ALL_REQUIRED);
            return "";
        }
            
        try {
            match.setHomeScore(Integer.parseInt(homeScore));
            match.setAwayScore(Integer.parseInt(awayScore));
        } catch (NumberFormatException nfe) {
            Message.outputMessage(Message.ERROR_NOT_A_NUMBER);
            return "";
        }

        try {
            LeagueDAO dao = new LeagueDAO();
            dao.connect();
            dao.uploadMatchResult(match);
            dao.disconnect();
            matchToUpdate = null;
            Message.outputMessage(Message.SUCCESS_RESULTS_REPORTED);
        } catch (SQLException sqle) {
            Message.outputMessage(sqle.getMessage());
        }
        
        return "";
    }
    
    public void removeAllResults(int teamId) {
        try {
            LeagueDAO dao = new LeagueDAO();
            dao.connect();
            dao.removeAllResults(teamId, selectedLeagueId);
            dao.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(AdminBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
