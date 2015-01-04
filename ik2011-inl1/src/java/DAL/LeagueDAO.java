/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import model.League;
import model.Match;
import model.Team;

/**
 *
 * @author Jeff
 */
public class LeagueDAO {

    private Connection con;
    private static LeagueDAO instance;

    private final String TABLE_NAME = "league";

    private final String ID_COLUMN = "league_id";
    private final String NAME_COLUMN = "name";

    private LeagueDAO() throws SQLException {
        this.con = DAOUtil.connect();
    }

    public static synchronized LeagueDAO getInstance() throws SQLException {

        if (instance == null) {
            instance = new LeagueDAO();
        }

        return instance;
    }
    
    public League getLeagueBasicInfoById(int leagueId ) throws SQLException {
        PreparedStatement stmnt = con.prepareStatement("SELECT * FROM league WHERE league_id = ?");
        stmnt.setInt(1, leagueId);
        ResultSet rs = stmnt.getResultSet();
        League league;
        if (rs.first())
            league = DAOUtil.parseBasicLeagueInformation(rs);
        else
            throw new SQLException("Empty result for league_id: "+leagueId);
        
        return league;
    }
    
    public ArrayList<League> getAllLeagues() throws SQLException {
        CallableStatement smnt = con.prepareCall("{ call p_get_all_leagues() }");
        smnt.executeQuery();
        ResultSet results = smnt.getResultSet();

        ArrayList<League> leagues = new ArrayList();

        while (results.next()) {
            leagues.add(DAOUtil.parseBasicLeagueInformation(results));
        }

        return leagues;
    }
    
    /**
     * Hämtar all data som finns om en liga
     * 
     * @param leagueId
     * @return
     * @throws SQLException 
     */
    public League getCompleteLeagueData(int leagueId) throws SQLException {
        League league = getLeagueBasicInfoById(leagueId);
        league.setTeams(getTeamsForLeague(league));
        league.setMatches(getMatchesForLeague(league));
        
        return league;
    }
    
    public League getCompleteLeagueData(League league) throws SQLException {
        league.setTeams(getTeamsForLeague(league));
        league.setMatches(getMatchesForLeague(league));
        
        return league;
    }
    
    public ArrayList<Team> getTeamsForLeague(League league) throws SQLException {
        CallableStatement smnt = con.prepareCall("{ call p_get_teams_for_league(?, ?) }");
        smnt.setInt(1, league.getId());
        smnt.setString(2, league.getSeason());
        smnt.executeQuery();
        ResultSet results = smnt.getResultSet();

        ArrayList<Team> teams = new ArrayList();

        while (results.next()) {
            teams.add(DAOUtil.parseTeamInformation(results));
        }
        
        return teams;
    }
    
    public ArrayList<Match> getMatchesForLeague(League league) throws SQLException {
        CallableStatement smnt = con.prepareCall("{ call p_get_matches_for_league(?, ?) }");
        smnt.setInt(1, league.getId());
        smnt.setString(2, league.getSeason());
        smnt.executeQuery();
        ResultSet results = smnt.getResultSet();

        ArrayList<Match> matches = new ArrayList();
        
        while (results.next()) {
            matches.add(DAOUtil.parseMatchInformation(results));
        }
        
        return matches;
    }

    public ArrayList<League> uploadLeagueData(ArrayList<League> leagues) throws SQLException, LeagueExistsException {
        
        for (League league : leagues) {
            uploadLeague(league);
            uploadTeams(league.getTeams());
            uploadTeamLeagueRelationships(league);
            uploadMatches(league);
        }
        
        return leagues;
    }
    
    private void uploadLeague(League league) throws SQLException {
        try {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO " + TABLE_NAME + "(" + NAME_COLUMN + ") VALUES(?)");
            prepareLeagueInsert(stmt, league);
            stmt.execute();
            league.setId(getReturnedId(stmt.getGeneratedKeys()));
            stmt.close();
        
        } catch (SQLException sqle) {
            PreparedStatement stmnt = con.prepareStatement("TRUNCATE TABLE league");
            stmnt.executeUpdate();
            stmnt.close();
            
            stmnt = con.prepareStatement("TRUNCATE TABLE team");
            stmnt.executeUpdate();
            stmnt.close();
            
            stmnt = con.prepareStatement("TRUNCATE TABLE league_teams");
            stmnt.executeUpdate();
            stmnt.close();
            
            stmnt = con.prepareStatement("TRUNCATE TABLE ik2011_bandy.match");
            stmnt.executeUpdate();
            stmnt.close();
            
            stmnt = con.prepareStatement("TRUNCATE TABLE match_result");
            stmnt.executeUpdate();
            stmnt.close();
            
            uploadLeague(league);
        }
    }
    
    private void uploadTeams(ArrayList<Team> teams) throws SQLException {
        for (Team t : teams) {
            try {
                PreparedStatement teamStatement = con.prepareStatement("INSERT INTO team (name) VALUES (?)");
                prepareTeamInsert(teamStatement, t);
                teamStatement.execute();
                ResultSet trs = teamStatement.getGeneratedKeys();
                t.setId(getReturnedId(trs));
                teamStatement.close();
            } catch (SQLException sqle) {
                PreparedStatement teamStatement = con.prepareStatement("SELECT team_id FROM team WHERE name = ?");
                teamStatement.setString(1, t.getName());
                t.setId(getReturnedId(teamStatement.executeQuery()));
                teamStatement.close();
            }
        }
    }

    private void uploadTeamLeagueRelationships(League league) throws SQLException {
        PreparedStatement leagueTeamsStatement = con.prepareStatement("INSERT into league_teams (league_id, team_id, year) VALUES (?, ?, ?)");
        prepareLeagueTeamsInsert(leagueTeamsStatement, league);
        leagueTeamsStatement.executeBatch();
        leagueTeamsStatement.close();
    }

    private void uploadMatches(League league) throws SQLException {
        PreparedStatement matchesStatement = con.prepareStatement("INSERT INTO ik2011_bandy.match (league_id, team_home, team_away, date) VALUES (?, ?, ?, ?)");
        prepareLeagueMatchesInsert(matchesStatement, league);
        matchesStatement.executeBatch();
        matchesStatement.close();
    }
    
    public void uploadMatchResults(ArrayList<Match> matches) throws SQLException {
        PreparedStatement stmnt = con.prepareStatement("INSERT INTO match_result (match_id, score_home, score_away) VALUES (?, ?, ?)");
        prepareMatchResults(stmnt, matches);
        stmnt.executeBatch();
        stmnt.close();
    }

    private int getReturnedId(ResultSet rs) throws SQLException {
        int id = -1;

        rs.first();
        id = rs.getInt(1);

        if (id > 0) {
            return id;
        } else {
            throw new SQLException("Fick ej tillbaka en ordentlig nyckel! Mottagen: " + id);
        }
    }
    
    private void prepareLeagueInsert(PreparedStatement stmt, League l) throws SQLException {
        stmt.setString(1, l.getName());
    }

    private void prepareTeamInsert(PreparedStatement stmt, Team team) throws SQLException {
        stmt.setString(1, team.getName());
    }

    private void prepareLeagueTeamsInsert(PreparedStatement stmt, League l) throws SQLException {
        for (Team t : l.getTeams()) {
            stmt.setInt(1, l.getId());
            stmt.setInt(2, t.getId());
            stmt.setString(3, l.getSeason());
            stmt.addBatch();
        }
    }

    private void prepareLeagueMatchesInsert(PreparedStatement stmt, League l) throws SQLException {
        for (Match m : l.getMatches()) {
            stmt.setInt(1, l.getId());
            stmt.setInt(2, m.getHome().getId());
            stmt.setInt(3, m.getAway().getId());
            stmt.setTimestamp(4, new Timestamp(m.getDate().getTime()));
            stmt.addBatch();
        }
    }
    
    private void prepareMatchResults(PreparedStatement stmt, ArrayList<Match> matches) throws SQLException {
        for (Match match : matches) {
            stmt.setInt(1, match.getId());
            stmt.setInt(2, match.getHomeScore());
            stmt.setInt(3, match.getAwayScore());
            stmt.addBatch();
        }
    }    
    public class LeagueExistsException extends Exception {

        public LeagueExistsException(String message) {
            super(message);
        }
    }
}
