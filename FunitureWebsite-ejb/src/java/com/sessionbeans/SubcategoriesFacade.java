/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sessionbeans;

import com.entitybeans.Subcategories;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author BAOTHI
 */
@Stateless
public class SubcategoriesFacade extends AbstractFacade<Subcategories> implements SubcategoriesFacadeLocal {

    @PersistenceContext(unitName = "FunitureWebsite-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubcategoriesFacade() {
        super(Subcategories.class);
    }

    @Override
    public List<Subcategories> findByCategory(String categoryID) {
        Query query = em.createQuery("SELECT s FROM Subcategories s WHERE s.categoryID.categoryID = :categoryID");
        query.setParameter("categoryID", categoryID); // Set tham số cho truy vấn
        return query.getResultList();
    }

}
