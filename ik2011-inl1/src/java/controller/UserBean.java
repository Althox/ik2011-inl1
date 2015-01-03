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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.enterprise.context.SessionScoped;

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
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Invalid Login!",
                                "Please Try Again!"));
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
        return "";
    }
    
    public boolean isLoggedIn() {
        return this.loggedIn;
    }
}
