
package com.sessionbeans;

import com.entitybeans.Categories;
import com.entitybeans.Subcategories;
import java.util.List;
import javax.ejb.Local;


@Local
public interface CategoriesFacadeLocal {

    void create(Categories categories);

    void edit(Categories categories);

    void remove(Categories categories);

    Categories find(Object id);

    List<Categories> findAll();

    List<Categories> findRange(int[] range);

    int count();
    List<Subcategories> findSubcategoriesByCategoryId(String categoryId);
      public List<Subcategories> findByCategoryId(Categories category);

    public List<Subcategories> findByCategoryId(String categoryId);
}
