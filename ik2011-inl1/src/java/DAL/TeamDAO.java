/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Match;
import model.Team;

/**
 *
 * @author Jeff
 */
public class TeamDAO {
    private Connection con;
    private static TeamDAO instance;

    private TeamDAO() throws SQLException {
        this.con = DAOUtil.connect();
    }
    
    public static synchronized TeamDAO getInstance() throws SQLException {
        if (instance == null) {
            instance = new TeamDAO();
        }

        return instance;
    }
    
    public ArrayList<Match> getMatchesForTeam(Team team, int leagueId) throws SQLException {
        CallableStatement stmnt = con.prepareCall("{ call p_get_matches_for_team( ?, ? ) }");
        stmnt.setInt(1, leagueId);
        stmnt.setInt(2, team.getId());
        
        ResultSet rs = stmnt.executeQuery();
        ArrayList<Match> matches = new ArrayList();
        while (rs.next()) {
            matches.add(DAOUtil.parseMatchInformation(rs));
        }
        
        return matches;
    }
}
