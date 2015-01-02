/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import Util.Hasher;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Team;
import model.User;
import model.UserRole;

/**
 *
 * @author Toppe
 */
public class UserDAO extends DAO implements Serializable {

    public UserDAO() throws SQLException {
    }
    
    public User login(String username, String password) throws SQLException {
        CallableStatement stmt = con.prepareCall("{ call p_login('"+username+"', '"+password+"') }");
        ResultSet rs = stmt.executeQuery();
        
        User user = new User();    
        int team_id = 0;
        int role_id = 0;
        
        while(rs.next()){
            user.setId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            role_id = rs.getInt("role_id");
            team_id = rs.getInt("team_id");
        }
        
        UserRole role = new UserRole();
        Team team = new Team();
        role = this.getRole();
        team = this.getTeam();
        
        if(role_id == role.getId()){
            user.setRole(role);
        }
        if(team_id == team.getId()){
            user.setAssociatedTeam(team);
        }
        
        return user;
    }
    
    private Team getTeam(){
        Team team = new Team();
        try{
            PreparedStatement stmt = con.prepareCall(" SELECT * FROM team ");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                team.setId(rs.getInt("team_id"));
                team.setName(rs.getString("name"));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return team;
    }
    
    private UserRole getRole(){
        UserRole role = new UserRole();
        
        try{
            PreparedStatement stmt = con.prepareCall(" SELECT * FROM user_role ");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                role.setId(rs.getInt("role_id"));
                role.setName(rs.getString("name"));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        return role;
    }
    
    public void createUser(String username, String password) throws SQLException {
        
        password = Hasher.createHash(password);
        
        CallableStatement stmt = con.prepareCall("{ call p_create_user('" +username+ ", "+password+"') }");
        stmt.executeQuery();
        ResultSet rs = stmt.getResultSet();
    }
    

    
}
