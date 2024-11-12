
package com.sessionbeans;

import com.entitybeans.Feedback;
import java.util.List;
import javax.ejb.Local;

@Local
public interface FeedbackFacadeLocal {

    void create(Feedback feedback);

    void edit(Feedback feedback);

    void remove(Feedback feedback);

    Feedback find(Object id);

    List<Feedback> findAll();

    List<Feedback> findRange(int[] range);

    int count();
     public List<Feedback> findFeedbackByProductId(String productId);

    public List<Object[]> getTopRatedProducts();

    public List<Object[]> getLowRatedProducts();

   

    public List<Object[]> getAverageRatingsForProducts();
}
