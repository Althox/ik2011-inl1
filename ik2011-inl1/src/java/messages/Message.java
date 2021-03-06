/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

/**
 *
 * @author Jeff
 */
public enum Message {

    SUCCESS_PASSWORD_CHANGED("passwordChangeSuccessful", FacesMessage.SEVERITY_INFO),
    SUCCESS_LOGGED_OUT("loggedOut", FacesMessage.SEVERITY_INFO),
    SUCCESS_RESULTS_REPORTED("resultsReported", FacesMessage.SEVERITY_INFO),
    ERROR_LOGIN_FAILED("loginFailed", FacesMessage.SEVERITY_WARN),
    ERROR_REQUIRED("required", FacesMessage.SEVERITY_ERROR),
    ERROR_ALL_REQUIRED("allFieldsRequired", FacesMessage.SEVERITY_ERROR),
    ERROR_UNKNOWN("unknownError", FacesMessage.SEVERITY_FATAL),
    ERROR_PASSWORD_MISMATCH("passwordMismatch", FacesMessage.SEVERITY_ERROR),
    ERROR_PASSWORD_INCORRECT("passwordIncorrect", FacesMessage.SEVERITY_ERROR),
    ERROR_NO_DATA_FOUND("noDataFound", FacesMessage.SEVERITY_FATAL),
    ERROR_NOT_A_NUMBER("notANumber", FacesMessage.SEVERITY_ERROR),
    ERROR_UNAUTHORIZED("unauthorized", FacesMessage.SEVERITY_ERROR);

    private final String key;
    private final Severity severity;

    private Message(String key, Severity severity) {
        this.key = key;
        this.severity = severity;
    }

    public String getKey() {
        return this.key;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public String getMessage() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "messages");
        return bundle.getString(this.getKey());
    }

    public static void outputMessage(Message message) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "messages");
        String returnMessage = bundle.getString(message.getKey());
        Message.outputMessage(returnMessage);
    }

    public static void outputMessage(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("displayAtTop", new FacesMessage(message));
    }
}
