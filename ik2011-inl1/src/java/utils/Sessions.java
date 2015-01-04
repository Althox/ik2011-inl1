/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Jeff
 */
public abstract class Sessions {
    public static HttpSession getSession() {
        return (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    }
}
