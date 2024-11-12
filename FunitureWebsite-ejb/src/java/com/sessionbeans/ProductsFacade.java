
package com.sessionbeans;

import com.entitybeans.Products;
import com.entitybeans.Subcategories;
import com.entitybeans.Suppliers;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


@Stateless
public class ProductsFacade extends AbstractFacade<Products> implements ProductsFacadeLocal {

    @PersistenceContext(unitName = "FunitureWebsite-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductsFacade() {
        super(Products.class);
    }

    @Override
    public Products find(Object id) {
        return em.find(Products.class, id);
    }

    public Products find(String productID) {
        System.out.println("Searching for product with ID: " + productID);
        // Giả sử bạn đang sử dụng EntityManager để truy vấn sản phẩm từ cơ sở dữ liệu
        return em.find(Products.class, productID);
    }


    @Override
    public Products findProductById(String productID) {
    try {
        return em.createQuery("SELECT p FROM Products p WHERE p.productID = :productID", Products.class)
                 .setParameter("productID", productID)
                 .getSingleResult();
    } catch (NoResultException e) {
        System.out.println("No product found with ID: " + productID);
        return null; // Return null if no result found
    } catch (Exception e) {
        e.printStackTrace(); // Log any other exceptions
        return null; // Return null for any other exceptions
    }
}

    @Override
    public List<Products> findBySubcategoryId(Subcategories subcategory) {
        return em.createQuery("SELECT p FROM Products p WHERE p.subcategoryID = :subcategory", Products.class)
                .setParameter("subcategory", subcategory)
                .getResultList();
    }

    @Override
    public List<Products> findSupplierId(Suppliers suppliers) {
        return em.createQuery("SELECT p FROM Products p WHERE p.supplierID = :suppliers", Products.class)
                .setParameter("suppliers", suppliers)
                .getResultList();
    }

    @Override
    public List<Products> findProducts(String searchKeyword, List<String> selectedSubcategoryIds, List<String> selectSupplierId, String sortBy, String sortByName) {
        StringBuilder queryBuilder = new StringBuilder("SELECT p FROM Products p WHERE 1=1");

        // Thêm điều kiện tìm kiếm theo từ khóa
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            queryBuilder.append(" AND p.productName LIKE :keyword");
        }

        if (selectedSubcategoryIds != null && !selectedSubcategoryIds.isEmpty()) {
            queryBuilder.append(" AND p.subcategoryID.subcategoryID IN :subcategoryIds");
        }

        if (selectSupplierId != null && !selectSupplierId.isEmpty()) {
            queryBuilder.append(" AND p.supplierID.supplierID IN :supplierId");
        }

        String sortField = null;
        String sortOrder = null;

        // Kiểm tra sortBy để phân tách giá trị
        if (sortBy != null) {
            String[] sortOptions = sortBy.split("-"); // Tách chuỗi "price-asc" hoặc "name-desc"
            if (sortOptions.length == 2) {
                sortField = sortOptions[0];  // "price" hoặc "name"
                sortOrder = sortOptions[1];  // "asc" hoặc "desc"
            }
        }

        if (sortField != null && sortOrder != null) {
            queryBuilder.append(" ORDER BY ");

            // Kiểm tra xem sắp xếp theo giá hay theo tên
            if (sortField.equals("price")) {
                queryBuilder.append("p.unitPrice ").append(sortOrder);  // Sắp xếp theo giá
            } else if (sortField.equals("name")) {
                queryBuilder.append("p.productName ").append(sortOrder);  // Sắp xếp theo tên
            }
        }

        TypedQuery<Products> query = em.createQuery(queryBuilder.toString(), Products.class);

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            query.setParameter("keyword", "%" + searchKeyword + "%");
        }

        // Thiết lập tham số cho danh sách subcategory
        if (selectedSubcategoryIds != null && !selectedSubcategoryIds.isEmpty()) {
            query.setParameter("subcategoryIds", selectedSubcategoryIds);
        }

        // Thiết lập tham số cho supplierID
        if (selectSupplierId != null && !selectSupplierId.isEmpty()) {
            query.setParameter("supplierId", selectSupplierId);
        }

        return query.getResultList();
    }
    @Override
    public List<Products> findProductsByKeyword(String keyword) {
    if (keyword == null || keyword.isEmpty()) {
        // Nếu từ khóa trống, trả về danh sách tất cả sản phẩm
        return em.createQuery("SELECT p FROM Products p", Products.class).getResultList();
    }
    
    // Truy vấn cơ sở dữ liệu để tìm kiếm sản phẩm theo từ khóa
    TypedQuery<Products> query = em.createQuery("SELECT p FROM Products p WHERE p.productName LIKE :keyword", Products.class);
    query.setParameter("keyword", "%" + keyword + "%");
    return query.getResultList();
}


    @Override
    public Long countTotalProducts() {
        return em.createQuery("SELECT COUNT(p) FROM Products p", Long.class).getSingleResult();
    }

 
}
