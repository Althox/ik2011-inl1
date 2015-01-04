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
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.enterprise.context.SessionScoped;
import messages.Message;
import utils.Sessions;

/**
 *
 * @author Toppe
 */
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable {
    private boolean loggedIn = false;
    public UserBean() {
    }

    public String login(User user) throws Exception {
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        try {
            UserDAO dao = UserDAO.getInstance();
            if (dao.login(user)) {
                loggedIn = true;
            } else {
                Message.outputMessage(Message.ERROR_LOGIN_FAILED);
            }
        } catch (SQLException e) {
            Message.outputMessage(Message.ERROR_UNKNOWN);
            System.out.println(e.getClass() + " " + e.getMessage());

            for (StackTraceElement el : e.getStackTrace()) {
                System.out.println(el.getLineNumber() + ": " + el.getFileName());
            }
        } catch (Exception e) {
            
            return "";
        }
        return "";
    }

    public String logout(User inUser) {
        HttpSession sesson = Sessions.getSession();
        sesson.invalidate();
        inUser = null;
        loggedIn = false;
        
        Message.outputMessage(Message.SUCCESS_LOGGED_OUT);
        return "";
    }
    
    public boolean isLoggedIn() {
        return this.loggedIn;
    }
    
    public String changePassword(User user, String oldPass, String newPass, String newPassMatch) {
        
        if (oldPass.isEmpty() || newPass.isEmpty() || newPassMatch.isEmpty()) {
            Message.outputMessage(Message.ERROR_ALL_REQUIRED);
        }
        
        if (newPass.equals(newPassMatch)) {
            try {
                UserDAO dao = UserDAO.getInstance();
                if(dao.changePassword(user, oldPass, newPass)) {
                    Message.outputMessage(Message.SUCCESS_PASSWORD_CHANGED);
                    return "";
                }
                else
                    Message.outputMessage(Message.ERROR_PASSWORD_INCORRECT);
            } catch(SQLException sqle) {
                Message.outputMessage(Message.ERROR_UNKNOWN);
                return "";
            }
        } else {
            Message.outputMessage(Message.ERROR_PASSWORD_MISMATCH);
        }
        
        return "";
    }
    
    public boolean hasAssociatedTeam(User user) {
        return user.getAssociatedTeam() != null;
    }
}
