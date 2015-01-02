/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

/**
 *
 * @author Toppe
 */
public class DAO implements Serializable{
    
    private Connection con = null;
    //init
    private static DAO instance;
    public static synchronized DAO getInstance() throws SQLException{
        if(instance == null){
            instance = new DAO();
        }
        return instance;
    }
    
    public DAO(){
    }
    
    private void connect() throws SQLException{
        String url = "jdbc:mysql://jval.synology.me/ik2011_labb5";
        String username = "??????"; //TODO: inte klar
        String password = "TobJaf";
        con = DriverManager.getConnection(url,username,password);
        con.setAutoCommit(false);
    }
    
    public void disconnect(){
        try{
            this.con.rollback();
            this.con.close();
        }catch(SQLException e){
            System.out.println("COULD NOT DISCONNECT!!!");
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
    
    
}
