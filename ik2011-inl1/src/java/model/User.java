/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Jeff
 */
@Named("user")
@SessionScoped
public class User implements Serializable{
    private int id;
    private String username;
    private String password;
    private UserRole role;
    private Team associatedTeam;

    public User() {
    }
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    } 
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Team getAssociatedTeam() {
        return associatedTeam;
    }

    public void setAssociatedTeam(Team associatedTeam) {
        this.associatedTeam = associatedTeam;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
