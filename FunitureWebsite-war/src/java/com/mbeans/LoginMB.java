package com.mbeans;

import com.entitybeans.Designer;
import com.entitybeans.Users;
import com.sessionbeans.DesignerFacadeLocal;
import com.sessionbeans.UsersFacadeLocal;

import java.io.IOException;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Named(value = "loginMB")
@RequestScoped
public class LoginMB {

    @EJB
    private DesignerFacadeLocal designerFacade;

    @EJB
    private UsersFacadeLocal usersFacade;

    private String userName;
    private String password;
    private String captcha;

    public LoginMB() {
    }

    private Users loggedInUser;

    public Users getLoggedInUser() {
        if (loggedInUser == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            loggedInUser = (Users) context.getExternalContext().getSessionMap().get("loggedInUser");
        }
        return loggedInUser;
    }

    public String getLoggedInFullName() {
        return getLoggedInUser().getFullName();
    }

    public String getLoggedInEmail() {
        return getLoggedInUser().getEmail();
    }

    public String getLoggedInPhone() {
        return getLoggedInUser().getPhone();
    }

    public String getLoggedInAddress() {
        return getLoggedInUser().getAddress();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoggedInUsername() {
        Users loggedInUser = (Users) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInUser");
        return (loggedInUser != null) ? loggedInUser.getUserName() : null; // Safely return the username if the user is not null
    }

    public boolean isLoggedIn() {
        return getLoggedInUsername() != null;
    }

    public UsersFacadeLocal getUsersFacade() {
        return usersFacade;
    }

    public void setUsersFacade(UsersFacadeLocal usersFacade) {
        this.usersFacade = usersFacade;
    }

    private Designer loggedInDesigner;

    public String checkLogin() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Admin login check
        if ("decorvista".equals(userName) && "admin".equals(password)) {
            try {
                context.getExternalContext().getSessionMap().put("loggedInUsername", userName);
                context.getExternalContext().redirect("/FunitureWebsite-war/faces/admin/dashboard.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            // User login check
            Users user = usersFacade.checkUsers(userName, password);
            if (user != null) {
                // Store user information in session
                context.getExternalContext().getSessionMap().put("loggedInUser", user);
                try {
                    context.getExternalContext().redirect("/FunitureWebsite-war/faces/home.xhtml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            } else {
                // Designer login check
                Designer designer = designerFacade.checkDesigner(userName, password);
                if (designer != null) {
                    // Store designer information in session
                    context.getExternalContext().getSessionMap().put("loggedInDesigner", designer);
                    try {
                        context.getExternalContext().redirect("/FunitureWebsite-war/faces/design/index.xhtml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                } else {
                    // Invalid credentials
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid credentials", "Invalid username or password"));
                    return "login";
                }
            }
        }
    }

    public String getLoggedInDesigner() {
        // Retrieve the logged-in designer from the session
        Designer loggedInDesigner = (Designer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInDesigner");

        // Return the designer's username if logged in, otherwise return null
        return (loggedInDesigner != null) ? loggedInDesigner.getUsername() : null;
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().invalidateSession();
        try {
            context.getExternalContext().redirect("/FunitureWebsite-war/faces/home.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isLoggedInDesign() {
        return getLoggedInDesigner() != null;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
