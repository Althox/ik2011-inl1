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
        this.con = DAOUtil.connect();
    }

    public static synchronized UserDAO getInstance() throws SQLException {

        if (instance == null) {
            instance = new UserDAO();
        }

        return instance;
    }

    public boolean login(User user) throws SQLException {
        CallableStatement stmt = con.prepareCall("{ call p_login(?,?) }");
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
        
        return false;
    }

    private Team getAssociatedTeam(int teamId) {
        Team team = new Team();
        try {
            PreparedStatement stmt = con.prepareStatement(" SELECT * FROM team WHERE team_id = ?; ");
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                team.setId(rs.getInt("team_id"));
                team.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return team;
    }

    private UserRole getRole(int roleId) {
        UserRole role = new UserRole();

        try {
            PreparedStatement stmt = con.prepareStatement(" SELECT * FROM user_role WHERE role_id = ?");
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                role.setId(rs.getInt("role_id"));
                role.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return role;
    }

    public void createUser(String username, String password) throws SQLException {

        password = Hasher.createHash(password);

        CallableStatement stmt = con.prepareCall("{ call p_create_user('" + username + ", " + password + "') }");
        stmt.executeQuery();
        ResultSet rs = stmt.getResultSet();
    }
}
