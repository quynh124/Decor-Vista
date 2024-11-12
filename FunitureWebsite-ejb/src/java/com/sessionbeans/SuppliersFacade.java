
package com.sessionbeans;

import com.entitybeans.Suppliers;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class SuppliersFacade extends AbstractFacade<Suppliers> implements SuppliersFacadeLocal {

    @PersistenceContext(unitName = "FunitureWebsite-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SuppliersFacade() {
        super(Suppliers.class);
    }
    @Override
    public Long countTotalSuppliers() {
        return em.createQuery("SELECT COUNT(s) FROM Suppliers s", Long.class).getSingleResult();
    }
}
