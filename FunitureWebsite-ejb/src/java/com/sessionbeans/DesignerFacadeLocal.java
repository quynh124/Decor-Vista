
package com.sessionbeans;

import com.entitybeans.Designer;
import java.util.List;
import javax.ejb.Local;


@Local
public interface DesignerFacadeLocal {

    void create(Designer designer);

    void edit(Designer designer);

    void remove(Designer designer);

    Designer find(Object id);

    List<Designer> findAll();

    List<Designer> findRange(int[] range);

    int count();
     public Designer checkDesigner(String uname, String pword);

    public Designer findDesignerById(Integer designerID);

    public Long countTotalDesigners();
}
