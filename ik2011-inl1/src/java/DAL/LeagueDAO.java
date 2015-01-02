/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import model.League;
import model.Match;

/**
 *
 * @author Jeff
 */
public class LeagueDAO extends DAO {
    private final String TABLE_NAME = "league";
    
    private final String ID_COLUMN = "league_id";
    private final String NAME_COLUMN = "name";
    
    private LeagueDAO() throws SQLException {
        super();
    }
    
    private void prepareLeagueInsert(PreparedStatement stmt, League l) throws SQLException {
        stmt.setString(1, l.getName());
    }
    
    private void prepareMatchInsert(PreparedStatement stmt, Match m) throws SQLException {
        
    }
    
    private League parseBasicInformation(ResultSet set) throws SQLException {
        League l = new League();
        
        l.setId(set.getInt(ID_COLUMN));
        l.setName(set.getString(NAME_COLUMN));
        
        return l;
    }
    
    private League parseMatchInformation(League l, ResultSet set) {
        return l;
    }
    
    public ArrayList<League> getLeagues() throws SQLException {
        CallableStatement smnt = con.prepareCall("{ call p_get_all_leagues() }");
        smnt.executeQuery();
        ResultSet results = smnt.getResultSet();
        
        ArrayList<League> leagues = new ArrayList();
        
        while (results.next()) {
            
            leagues.add(parseBasicInformation(results));
        }
        
        return leagues;
    }
    
    public void uploadLeagueData(ArrayList<League> leagues, boolean updateExisting) throws SQLException, LeagueExistsException  {
        
        
        String errorMessage = "";
        ArrayList<League> prevLeagues = getLeagues();
        ArrayList<League> toUpdate = new ArrayList();
        
        Iterator<League> it = leagues.iterator();
        
        while (it.hasNext()) {
            League league = it.next();
            
            int index = -1;
            if ((index = getIndex(prevLeagues, league)) != -1) 
                if (updateExisting) {
                    toUpdate.add(prevLeagues.get(index));
                    it.remove();
                } else {
                    errorMessage += "["+league.getName()+"] ";
                }
        }
        
        if (!errorMessage.isEmpty())
            throw new LeagueExistsException("The league(s): "+errorMessage.trim()+" already exists.");
        
        
        PreparedStatement stmt = con.prepareStatement("INSERT INTO "+TABLE_NAME+"("+NAME_COLUMN+") VALUES(?)");
        
        for (League l : leagues) {
            prepareLeagueInsert(stmt, l);
            stmt.addBatch();
        }
        
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private int getIndex(ArrayList<League> prevLeagues, League newLeague) {
        int foundMatch = -1;
        for (League prevLeague : prevLeagues) {
            foundMatch = (prevLeague.getName().equalsIgnoreCase(newLeague.getName())) ? prevLeagues.indexOf(prevLeague) : -1;
        }
        
        return foundMatch;
    }
    
    public class LeagueExistsException extends Exception {

        public LeagueExistsException(String message) {
            super(message);
        }
    }
}
