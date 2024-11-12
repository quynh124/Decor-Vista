
package com.sessionbeans;

import com.entitybeans.ProjectDesigner;
import java.util.List;
import javax.ejb.Local;


@Local
public interface ProjectDesignerFacadeLocal {

    void create(ProjectDesigner projectDesigner);

    void edit(ProjectDesigner projectDesigner);

    void remove(ProjectDesigner projectDesigner);

    ProjectDesigner find(Object id);

    List<ProjectDesigner> findAll();

    List<ProjectDesigner> findRange(int[] range);

    int count();
     public List<ProjectDesigner> findProjectsByDesignerID(int designerID);

    public ProjectDesigner findProjectById(Long projectID);

    public ProjectDesigner findProjectById(String projectID);
}
