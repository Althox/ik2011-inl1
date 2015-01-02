/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Toppe
 */
public class User implements Serializable{
    
    private int id;
    private String username;
    private String password;
    
    public User(int id, String username, String password){
        this.id = id; //kanske inte, men håller kvar tills vidare
        this.username = username;
        this.password = password;
    }
    
    public User(){} //tom konstruktor
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void setUsername(){
        this.username = username;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public String getPassword(){
        return this.password;
    }
    
}
