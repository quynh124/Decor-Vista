
package com.sessionbeans;

import com.entitybeans.Favorites;
import com.entitybeans.Products;
import java.util.List;
import javax.ejb.Local;


@Local
public interface FavoritesFacadeLocal {

    void create(Favorites favorites);

    void edit(Favorites favorites);

    void remove(Favorites favorites);

    Favorites find(Object id);

    List<Favorites> findAll();

    List<Favorites> findRange(int[] range);

    int count();

public List<Favorites> findFavoritesByUserId(int userId);

    public void removeFavorites(int userId, String productId);

      public boolean isFavorite(Integer userId, String productId);
    
}
