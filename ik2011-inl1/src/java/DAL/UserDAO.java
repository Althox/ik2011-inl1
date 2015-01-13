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
            rs.close();
            stmt.close();
            con.close();
            return true;
        }
        rs.close();
        stmt.close();
        con.close();
        return false;
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) throws SQLException {
        con = DAOUtil.connect();
        PreparedStatement s = con.prepareStatement("UPDATE user SET password = ? WHERE user_id = ? AND password = ?");
        s.setString(1, newPassword);
        s.setInt(2, user.getId());
        s.setString(3, oldPassword);
        boolean result = s.executeUpdate() != 0;
        s.close();
        con.close();
        return result;
    }

    private Team getAssociatedTeam(int teamId) throws SQLException {
        Team team = null;
        PreparedStatement stmt = con.prepareStatement(" SELECT * FROM team WHERE team_id = ?; ");
        stmt.setInt(1, teamId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            team = new Team();
            team.setId(rs.getInt("team_id"));
            team.setName(rs.getString("name"));
        }
        rs.close();
        stmt.close();
        return team;
    }

    private UserRole getRole(int roleId) throws SQLException {
        UserRole role = null;
        PreparedStatement stmt = con.prepareStatement(" SELECT * FROM user_role WHERE role_id = ?");
        stmt.setInt(1, roleId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            role = new UserRole();
            role.setId(rs.getInt("role_id"));
            role.setName(rs.getString("name"));
        }
        rs.close();
        stmt.close();
        return role;
    }

    public void giveTeamInSeveralLeagues(User user) throws SQLException {
        con = DAOUtil.connect();
        CallableStatement queryStatement = con.prepareCall("{ call p_get_team_in_several_leagues() }");
        queryStatement.execute();
        ResultSet rs = queryStatement.getResultSet();
        if (rs.next()) {
            int teamId = rs.getInt("team_id");
            PreparedStatement updateStatement = con.prepareStatement("UPDATE user SET team_id = ? WHERE user_id = ? ");
            updateStatement.setInt(1, teamId);
            updateStatement.setInt(2, user.getId());
            if (updateStatement.executeUpdate() > 0)
                user.setAssociatedTeam(getAssociatedTeam(teamId));
            rs.close();
            queryStatement.close();
            updateStatement.close();
            
            con.close();
        } else {
            con.close();
            throw new SQLException("Inga lag som deltar i flera ligor funna, tyvärr. :(");
        }
    }
}
