/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import DAL.UserDAO;
import model.User;
import java.io.Serializable;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Toppe
 */
@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable{
    
    private User user;
    private boolean isLoggedIn = false;
    
    public UserBean(){
        user = new User();
    }
    
    public void login(String username, String password){
        try{
            UserDAO dao = new UserDAO();
            this.setUser(dao.login(username, password));
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
    }
    
    public void setUser(User user){
        this.user = user;
    }
    
    public User getUser(){
        return this.user;
    }
    
        /**
     * render if logged in
     * @return 
     */
    public boolean isLoggedIn(){
        isLoggedIn = user != null;
        return isLoggedIn;
    }
    
}
