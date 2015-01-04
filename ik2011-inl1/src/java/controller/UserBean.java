/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import DAL.UserDAO;
import Util.SessionUtil;
import model.User;
import java.io.Serializable;
import java.sql.SQLException;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.enterprise.context.SessionScoped;
import messages.Message;

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
                //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Test"));
                Message.addMessageToContext(Message.ERROR_LOGIN_FAILED);
            }
        } catch (SQLException e) {
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
        HttpSession sesson = SessionUtil.getSession();
        sesson.invalidate();
        inUser = null;
        loggedIn = false;
        
        Message.addMessageToContext(Message.SUCCESS_LOGGED_OUT);
        return "";
    }
    
    public boolean isLoggedIn() {
        return this.loggedIn;
    }
    
    public String changePassword(User user, String oldPass, String newPass, String newPassMatch) {
        
        if (oldPass.isEmpty() || newPass.isEmpty() || newPassMatch.isEmpty()) {
            Message.addMessageToContext(Message.ERROR_ALL_REQUIRED);
        }
        
        if (newPass.equals(newPassMatch)) {
            try {
                UserDAO dao = UserDAO.getInstance();
                if(dao.changePassword(user, oldPass, newPass)) {
                    Message.addMessageToContext(Message.SUCCESS_PASSWORD_CHANGED);
                    return "";
                }
                else
                    Message.addMessageToContext(Message.ERROR_PASSWORD_INCORRECT);
            } catch(SQLException sqle) {
                Message.addMessageToContext(Message.ERROR_UNKNOWN);
                return "";
            }
        } else {
            Message.addMessageToContext(Message.ERROR_PASSWORD_MISMATCH);
        }
        
        return "";
    }
}
