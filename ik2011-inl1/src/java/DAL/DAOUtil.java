/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Jeff
 */
public class DAOUtil {
    
    public static Connection connect() throws SQLException {
        //Class.forName("org.mariadb.jdbc.Driver"); //driver - Vi använder en extern MariaDB server, därför behövs denna driver istället.
        String url = "jdbc:mysql://jval.synology.me/ik2011_bandy"; //server/db
        String username = "school"; //username
        String pass = "TobJaf"; //pass
        return DriverManager.getConnection(url, username, pass);
    }
}
