/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mbeans;

import com.entitybeans.Users;
import com.sessionbeans.UsersFacadeLocal;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import javax.ejb.EJB;

@Named
@ViewScoped
public class UserEditBean implements Serializable {
    private Users user;
@EJB
    private UsersFacadeLocal usersFacade;
    @PostConstruct
    public void init() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        user = (Users) session.getAttribute("loggedInUser");
    }

    public String updateUser() {
         FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().invalidateSession();
       usersFacade.edit(user);
        try {
            context.getExternalContext().redirect("/FunitureWebsite-war/faces/home.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Getters and Setters for `user`
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
