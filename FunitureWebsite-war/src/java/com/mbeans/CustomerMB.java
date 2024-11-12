/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mbeans;

import com.entitybeans.Categories;
import com.entitybeans.Users;
import com.sessionbeans.UsersFacadeLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import org.primefaces.PrimeFaces;

/**
 *
 * @author BAOTHI
 */
@Named(value = "customerMB")
@RequestScoped
public class CustomerMB {

    @EJB
    private UsersFacadeLocal usersFacade;
    private Users users;

    public CustomerMB() {
        users = new Users();
    }

    public String addUser() {
        usersFacade.create(users);
        return "./login.xhtml";
    }

    public String resetForm() {
        users = null;
        return "register";
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

}
