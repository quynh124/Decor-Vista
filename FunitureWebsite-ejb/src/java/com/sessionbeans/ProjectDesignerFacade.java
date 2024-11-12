
package com.sessionbeans;

import com.entitybeans.ProjectDesigner;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


@Stateless
public class ProjectDesignerFacade extends AbstractFacade<ProjectDesigner> implements ProjectDesignerFacadeLocal {

    @PersistenceContext(unitName = "FunitureWebsite-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProjectDesignerFacade() {
        super(ProjectDesigner.class);
    }

 @Override
    public List<ProjectDesigner> findProjectsByDesignerID(int designerID) {
        TypedQuery<ProjectDesigner> query = em.createQuery(
            "SELECT pd FROM ProjectDesigner pd WHERE pd.designerID.designerID = :designerID", ProjectDesigner.class);
        query.setParameter("designerID", designerID);
        List<ProjectDesigner> results = query.getResultList();
        System.out.println("Designer ID: " + designerID + ", Number of projects found: " + results.size());
        return results;
    }

    
  
    @Override
    public ProjectDesigner findProjectById(Long projectID) {
        return em.find(ProjectDesigner.class, projectID);
    }

    @Override
    public void edit(ProjectDesigner projectDesigner) {
        em.merge(projectDesigner);
    }

    @Override
    public ProjectDesigner findProjectById(String projectID) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public ProjectDesigner findProjectByDesigner(Long designerID, Long projectID) {
    try {
        return em.createQuery("SELECT p FROM ProjectDesigner p WHERE p.designerID = :designerID AND p.projectID = :projectID", ProjectDesigner.class)
                            .setParameter("designerID", designerID)
                            .setParameter("projectID", projectID)
                            .getSingleResult();
    } catch (NoResultException e) {
        return null; // Handle case where project not found
    }
    }

}
