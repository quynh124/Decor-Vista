
package com.sessionbeans;

import com.entitybeans.Products;
import com.entitybeans.Subcategories;
import com.entitybeans.Suppliers;
import java.util.List;
import javax.ejb.Local;


@Local
public interface ProductsFacadeLocal {

    void create(Products products);

    void edit(Products products);

    void remove(Products products);

    Products find(Object id);

    List<Products> findAll();

    public Products findProductById(String productID);

    List<Products> findRange(int[] range);

    int count();

    public List<Products> findBySubcategoryId(Subcategories subcategory);

    public List<Products> findSupplierId(Suppliers suppliers);

    public List<Products> findProducts(String searchKeyword, List<String> selectedSubcategoryIds, List<String> selectSupplierId, String sortBy, String sortByName);

    public Long countTotalProducts();

    public List<Products> findProductsByKeyword(String keyword);

 
}
