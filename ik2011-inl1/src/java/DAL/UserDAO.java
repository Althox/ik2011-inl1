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
import model.User;

/**
 *
 * @author Toppe
 */
public class UserDAO extends DAO implements Serializable {

    public UserDAO() throws SQLException {
    }
    
    public User login(String username, String password) throws SQLException {
        PreparedStatement stmnt = con.prepareCall("SELECT * FROM user WHERE username = ?");
        
        return null;
    }
    
    public void createUser(String username, String password) throws SQLException {
        
        password = Hasher.createHash(password);
        
        CallableStatement stmt = con.prepareCall("{ call p_create_user('" +username+ ", "+password+"') }");
        stmt.executeQuery();
        ResultSet rs = stmt.getResultSet();
    }
}
