
package com.sessionbeans;

import com.entitybeans.Feedback;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class FeedbackFacade extends AbstractFacade<Feedback> implements FeedbackFacadeLocal {

    @PersistenceContext(unitName = "FunitureWebsite-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FeedbackFacade() {
        super(Feedback.class);
    }

    @Override
    public List<Feedback> findFeedbackByProductId(String productId) {
        try {
            return em.createQuery("SELECT f FROM Feedback f WHERE f.targetId.productID = :productId", Feedback.class)
                    .setParameter("productId", productId)
                    .getResultList();
        } catch (Exception e) {
            // Handle exceptions (log, rethrow, etc.)
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object[]> getTopRatedProducts() {
        return em.createQuery(
                "SELECT p.productID, p.productName, AVG(f.rating) AS averageRating, "
                + "SUM(CASE WHEN f.rating = 5 THEN 1 ELSE 0 END) AS fiveStarCount "
                + // Đếm số đánh giá 5 sao
                "FROM Feedback f JOIN f.targetId p "
                + "WHERE f.targetType = 'product' "
                + "GROUP BY p.productID, p.productName "
                + "HAVING AVG(f.rating) > 4", Object[].class) // Chỉ lấy sản phẩm có rating trung bình > 4
                .getResultList(); // Trả về kết quả
    }

    @Override
    public List<Object[]> getLowRatedProducts() {
        return em.createQuery(
                "SELECT p.productID, p.productName, COUNT(f.rating) AS lowStarCount, "
                + "SUM(CASE WHEN f.rating < 3 THEN 1 ELSE 0 END) AS lowStarReviews " // Đếm số đánh giá dưới 3 sao
                + "FROM Feedback f JOIN f.targetId p "
                + "WHERE f.targetType = 'product' "
                + "GROUP BY p.productID, p.productName "
                + "HAVING COUNT(f.rating) > 0 " // Đảm bảo có ít nhất một đánh giá
                + "ORDER BY lowStarCount DESC", Object[].class)
                .setMaxResults(10)
                .getResultList();
    }
 

  public List<Object[]> getAverageRatingsForProducts() {
    String jpql = "SELECT f.targetId.productID, f.targetId.productName, COALESCE(AVG(f.rating), 0) " +
                  "FROM Feedback f " +
                  "WHERE f.targetType = 'product' " +
                  "GROUP BY f.targetId.productID, f.targetId.productName";
    
    try {
        return em.createQuery(jpql, Object[].class).getResultList();
    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<>(); // Return an empty list if there's an error
    }
}

   

}
