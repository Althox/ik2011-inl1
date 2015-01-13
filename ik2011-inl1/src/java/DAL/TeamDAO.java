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
import model.League;

/**
 *
 * @author Jeff
 */
public class TeamDAO {

    private Connection con;
    private static TeamDAO instance;

    public TeamDAO() {
    }

    /*public static synchronized TeamDAO getInstance() throws SQLException {
        if (instance == null) {
            instance = new TeamDAO();
        }

        return instance;
    }*/

    public void connect() throws SQLException {
        con = DAOUtil.connect();
    }

    public void disconnect() throws SQLException {
        con.close();
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

    public ArrayList<League> getLeaguesForTeam(Team team) throws SQLException {
        CallableStatement stmt = con.prepareCall("{ call p_get_leagues_for_team(?) }");
        stmt.setInt(1, team.getId());
        ResultSet rs = stmt.executeQuery();

        ArrayList<League> list = new ArrayList<>();
        while (rs.next()) {
            League l = new League();
            l.setId(rs.getInt("league_id"));
            l.setName(rs.getString("name"));
            l.setSeason(rs.getString("year"));
            list.add(l);
        }
        return list;
    }
}
