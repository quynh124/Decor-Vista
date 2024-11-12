
package com.sessionbeans;

import com.entitybeans.Consultations;
import com.entitybeans.Designer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class ConsultationsFacade extends AbstractFacade<Consultations> implements ConsultationsFacadeLocal {

    @PersistenceContext(unitName = "FunitureWebsite-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConsultationsFacade() {
        super(Consultations.class);
    }

    @Override
    public List<Consultations> findByUser(Integer userId) {
        String jpql = "SELECT c FROM Consultations c WHERE c.userID.userID = :userID";
        return em.createQuery(jpql, Consultations.class)
                .setParameter("userID", userId)
                .getResultList();
    }

    @Override
    public void create(Consultations consultations) {
        em.persist(consultations);
    }

    @Override
    public void edit(Consultations consultations) {
        em.merge(consultations);
    }

    @Override
    public void remove(Consultations consultations) {
        em.remove(em.merge(consultations));
    }

    @Override
    public Consultations find(Object id) {
        return em.find(Consultations.class, id);
    }

    @Override
    public List<Consultations> findAll() {
        return em.createNamedQuery("Consultations.findAll", Consultations.class).getResultList();
    }

    @Override
    public List<Consultations> findByDay(Date day) {
        TypedQuery<Consultations> query = em.createNamedQuery("Consultations.findByDay", Consultations.class);
        query.setParameter("day", day);
        return query.getResultList();
    }

    @Override
    public List<Consultations> findByMonthAndYear(int month, int year) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, year);
        start.set(Calendar.MONTH, month);
        start.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, year);
        end.set(Calendar.MONTH, month);
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        return em.createQuery("SELECT c FROM Consultations c WHERE c.day BETWEEN :start AND :end", Consultations.class)
                .setParameter("start", start.getTime())
                .setParameter("end", end.getTime())
                .getResultList();
    }

    @Override
    public List<Consultations> findByDesignerID(Integer designerID) {
        try {
            return em.createQuery("SELECT c FROM Consultations c WHERE c.designerID = :designerID", Consultations.class)
                    .setParameter("designerID", designerID)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return empty if query fails
        }
    }

    @Override
    public List<Consultations> findConsultationsByDesignerID(int designerID) {
        return em.createQuery("SELECT c FROM Consultations c WHERE c.designerID.designerID = :designerID", Consultations.class)
                .setParameter("designerID", designerID)
                .getResultList();
    }

    @Override
    public List<Consultations> findByDesignerID(int designerID) {
        return em.createQuery("SELECT c FROM Consultations c WHERE c.designerID.designerID = :designerID", Consultations.class)
                .setParameter("designerID", designerID)
                .getResultList();
    }

    @Override
    public List<Consultations> findByDesignerID(Designer loggedInDesigner) {
        return em.createQuery("SELECT c FROM Consultations c WHERE c.designerID = :designer", Consultations.class)
                .setParameter("designer", loggedInDesigner)
                .getResultList();
    }

    @Override
    public List<Object[]> getConsultationStats() {
        return em.createNativeQuery(
                "SELECT d.DesignerID, d.FirstName, d.LastName, COUNT(c.ConsultationID) AS consultationCount "
                + "FROM Consultations c "
                + "JOIN Designer d ON c.DesignerID = d.DesignerID "
                + // Directly using SQL
                "GROUP BY d.DesignerID, d.FirstName, d.LastName "
                + "ORDER BY consultationCount DESC")
                .getResultList();
    }

}
