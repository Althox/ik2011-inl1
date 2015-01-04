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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Toppe
 */
@ManagedBean(name = "adminBean")
@SessionScoped
public class AdminBean implements Serializable{
    
    public AdminBean(){
        
    }
    
    public ArrayList<Match> getMatchesForTeam(Team team, League league){
        ArrayList<Match> list = new ArrayList<>();
        try{
            list = TeamDAO.getInstance().getMatchesForTeam(team, league.getId());
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return list;
    }
    
    public ArrayList<League> getTeamsForLeague(Team team){
        ArrayList<League> list = new ArrayList<>();
        try{
            list = TeamDAO.getInstance().getTeamsForLeague(team);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return list;
    }
    
}
