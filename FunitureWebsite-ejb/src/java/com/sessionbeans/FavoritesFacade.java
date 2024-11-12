
package com.sessionbeans;

import com.entitybeans.Favorites;
import com.entitybeans.Products;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


@Stateless
public class FavoritesFacade extends AbstractFacade<Favorites> implements FavoritesFacadeLocal {

    @PersistenceContext(unitName = "FunitureWebsite-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FavoritesFacade() {
        super(Favorites.class);
    }

    @Override
    public void create(Favorites favorites) {
        em.persist(favorites);
    }

    @Override
    public List<Favorites> findFavoritesByUserId(int userId) {
        TypedQuery<Favorites> query = em.createQuery(
                "SELECT f FROM Favorites f WHERE f.userID.userID = :userId",
                Favorites.class);
        query.setParameter("userId", userId);

        // Kiểm tra nếu có kết quả
        List<Favorites> result = query.getResultList();

        return result != null ? result : new ArrayList<>(); // Trả về danh sách rỗng nếu không có kết quả
    }

    @Override
    public void removeFavorites(int userId, String productId) {
        // Tìm sản phẩm yêu thích dựa trên userId và productId
        try {
            Favorites favorite = em.createQuery("SELECT f FROM Favorites f WHERE f.userID.userID = :userId AND f.productID.productID = :productId", Favorites.class)
                    .setParameter("userId", userId)
                    .setParameter("productId", productId)
                    .getSingleResult();

            // Nếu tìm thấy, xóa khỏi cơ sở dữ liệu
            if (favorite != null) {
                em.remove(favorite);
            }
        } catch (NoResultException e) {
            // Không tìm thấy sản phẩm yêu thích
        }
    }

    public boolean isFavorite(Long userId, Long productId) {

        TypedQuery<Favorites> query = em.createQuery(
                "SELECT f FROM Favorites f WHERE f.userID.id = :userId AND f.productID.id = :productId", Favorites.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean isFavorite(Integer userId, String productId) {

        TypedQuery<Favorites> query = em.createQuery(
                "SELECT f FROM Favorites f WHERE f.userID.id = :userId AND f.productID.id = :productId", Favorites.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return !query.getResultList().isEmpty();
    }

}
