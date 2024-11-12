/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mbeans;

import com.entitybeans.Consultations;
import com.entitybeans.Users;
import com.sessionbeans.ConsultationsFacadeLocal;
import com.sessionbeans.UsersFacadeLocal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;

/**
 *
 * @author BAOTHI
 */
@Named(value = "usersMB")
@RequestScoped
public class UsersMB {

    @EJB
    private ConsultationsFacadeLocal consultationsFacade;

    @EJB
    private UsersFacadeLocal usersFacade;
    private List<Consultations> userConsultations;

    private Users users;

    public UsersMB() {
    }

    public List<Users> showAllUsers() {
        return usersFacade.findAll();
    }

    public String deleteUsers(int id) {
        try {
            usersFacade.remove(usersFacade.find(id));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User deleted successfully."));
            return "customer"; // điều hướng trang thành công
        } catch (PersistenceException e) {
            // Thông báo lỗi khóa ngoại bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('Cannot delete user due to foreign key constraint.');");
            return null;
        } catch (Exception e) {
            // Thông báo lỗi khác bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('An error occurred while deleting the user.');");
            return null;
        }
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String loadUserProfile(String username) {
        users = usersFacade.checkUsers(username, username);
        return "profile";  // Redirect to profile page
    }

    @PostConstruct
    public void init() {
        // Load user details based on the userID stored in session
        FacesContext context = FacesContext.getCurrentInstance();
        Integer userID = (Integer) context.getExternalContext().getSessionMap().get("loggedInUserID");

        if (userID != null) {
            users = usersFacade.find(userID);  // Fetch user details by userID
        }
    }

    public List<Consultations> getUserConsultations() {
        return userConsultations;
    }

    public void setUserConsultations(List<Consultations> userConsultations) {
        this.userConsultations = userConsultations;
    }

}
