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
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Toppe
 */
@ManagedBean(name = "userBean")
@RequestScoped
public class UserBean implements Serializable {

    public UserBean() {
    }

    public void login(User inUser) throws Exception {
        System.out.println(inUser.getUsername());
        System.out.println(inUser.getPassword());
        try {
            UserDAO dao = UserDAO.getInstance();
            int userId = dao.login(inUser.getUsername(), inUser.getPassword());
            System.out.println("Got userId: "+userId);
            if (userId != -1) {
                // get Http Session and store username
                HttpSession session = SessionUtil.getSession();
                session.setAttribute("user_id", userId);

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
            throw new Exception(e.getMessage());
        }
    }

    public String logout(User inUser) {
        HttpSession session = SessionUtil.getSession();
        session.invalidate();
        return "index";
    }
    
    public boolean isLoggedIn() {
        HttpSession session = SessionUtil.getSession();
        boolean b = session.getAttribute("user_id") != null;
        System.out.println(new Date()+" isLoggedIn result: "+b+" Session info: id="+session.getId()+" lastAccess="+session.getLastAccessedTime());
        return b;
    }
}
