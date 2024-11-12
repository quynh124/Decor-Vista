package com.mbeans;

import com.entitybeans.Favorites;
import com.entitybeans.Products;
import com.entitybeans.Users;
import com.sessionbeans.FavoritesFacadeLocal;
import com.sessionbeans.ProductsFacadeLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Named(value = "favoritesMB")
@RequestScoped
public class FavoritesMB {

    @EJB
    private FavoritesFacadeLocal favoritesFacade;

    @EJB
    private ProductsFacadeLocal productsFacade;

    private List<Products> favoriteProducts;
    private Favorites favorites;

    public FavoritesMB() {
        favorites = new Favorites();
    }

    public List<Favorites> getFavoriteProducts() {
        FacesContext context = FacesContext.getCurrentInstance();
        Users loggedInUser = (Users) context.getExternalContext().getSessionMap().get("loggedInUser");

        if (loggedInUser != null) {
            // Lấy danh sách yêu thích từ FavoritesFacade
            List<Favorites> favorites = favoritesFacade.findFavoritesByUserId(loggedInUser.getUserID());
            return favorites;
        }
        return new ArrayList<>(); // Trả về danh sách rỗng nếu không đăng nhập
    }

    public String deleteDeliveries(int id) {
        favoritesFacade.remove(favoritesFacade.find(id));
        return "favories";
    }

    public void removeFavorites(int id) {
        FacesContext context = FacesContext.getCurrentInstance();
        Users loggedInUser = (Users) context.getExternalContext().getSessionMap().get("loggedInUser");
        System.out.println("Attempting to remove favorite with ID: " + id);

        if (loggedInUser != null) {
            // Tìm và xóa yêu thích dựa trên FavoriteID
            Favorites favorite = favoritesFacade.find(id);
            if (favorite != null && favorite.getUserID().equals(loggedInUser)) {
                favoritesFacade.remove(favorite);
                context.addMessage(null, new FacesMessage("Product removed from favorites!"));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not remove favorite item.", null));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You must be logged in to remove favorites.", null));
        }
    }

    public Favorites getFavorites() {
        return favorites;
    }

    public void setFavorites(Favorites favorites) {
        this.favorites = favorites;
    }

    public List<Favorites> showAllFavorites() {
        return favoritesFacade.findAll();
    }
}
