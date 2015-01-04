/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Team;
import model.User;
import model.UserRole;

/**
 *
 * @author Toppe
 */
public class UserDAO implements Serializable {

    private Connection con;
    private static UserDAO instance;

    private UserDAO() throws SQLException {
    }

    public static synchronized UserDAO getInstance() throws SQLException {

        if (instance == null) {
            instance = new UserDAO();
        }

        return instance;
    }

    public boolean login(User user) throws SQLException {
        con = DAOUtil.connect();
        CallableStatement stmt = con.prepareCall("{ call p_login(?, ?) }");
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            user.setId(rs.getInt("user_id"));
            user.setRole(getRole(rs.getInt("role_id")));
            user.setAssociatedTeam(getAssociatedTeam(rs.getInt("team_id")));
            user.setPassword(null); // Vill inte lagra lösenord i sessioner.
            return true;
        }
        con.close();
        return false;
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) throws SQLException {
        con = DAOUtil.connect();
        PreparedStatement s = con.prepareStatement("UPDATE user SET password = ? WHERE user_id = ? AND password = ?");
        s.setString(1, newPassword);
        s.setInt(2, user.getId());
        s.setString(3, oldPassword);
        con.close();
        return s.executeUpdate() != 0;
    }

    private Team getAssociatedTeam(int teamId) throws SQLException {
        con = DAOUtil.connect();
        Team team = null;
        PreparedStatement stmt = con.prepareStatement(" SELECT * FROM team WHERE team_id = ?; ");
        stmt.setInt(1, teamId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            team = new Team();
            team.setId(rs.getInt("team_id"));
            team.setName(rs.getString("name"));
        }
        con.close();
        return team;
    }

    private UserRole getRole(int roleId) throws SQLException {
        con = DAOUtil.connect();
        UserRole role = null;
        PreparedStatement stmt = con.prepareStatement(" SELECT * FROM user_role WHERE role_id = ?");
        stmt.setInt(1, roleId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            role = new UserRole();
            role.setId(rs.getInt("role_id"));
            role.setName(rs.getString("name"));
        }
        con.close();
        return role;
    }
    /*
     public void createUser(String username, String password) throws SQLException {

     password = Hasher.createHash(password);

     CallableStatement stmt = con.prepareCall("{ call p_create_user('" + username + ", " + password + "') }");
     stmt.executeQuery();
     ResultSet rs = stmt.getResultSet();
     }*/
}
