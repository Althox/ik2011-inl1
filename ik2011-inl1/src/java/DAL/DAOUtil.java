/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.League;
import model.Match;
import model.Team;

/**
 *
 * @author Jeff
 */
public abstract class DAOUtil {
    
    public static Connection connect() throws SQLException {
        //Class.forName("org.mariadb.jdbc.Driver"); //driver - Vi använder en extern MariaDB server, därför behövs denna driver istället.
        String url = "jdbc:mysql://jval.synology.me/ik2011_bandy"; //server/db
        String username = "school"; //username
        String pass = "TobJaf"; //pass
        return DriverManager.getConnection(url, username, pass);
    }

    public static League parseBasicLeagueInformation(ResultSet set) throws SQLException {
        League l = new League();
        l.setId(set.getInt("league_id"));
        l.setName(set.getString("name"));
        return l;
    }

    public static Match parseMatchInformation(ResultSet set) throws SQLException {
        Match match = new Match();
        match.setId(set.getInt("match_id"));
        match.setHome(new Team(set.getInt("home_id"), set.getString("home_name")));
        match.setAway(new Team(set.getInt("away_id"), set.getString("away_name")));
        match.setHomeScore(set.getInt("home_score"));
        match.setAwayScore(set.getInt("away_score"));
        match.setDate(set.getTimestamp("date"));
        return match;
    }

    public static Team parseTeamInformation(ResultSet set) throws SQLException {
        Team team = new Team();
        team.setId(set.getInt("team_id"));
        team.setId(set.getInt("name"));
        return team;
    }
}
