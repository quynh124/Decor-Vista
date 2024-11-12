
package com.sessionbeans;

import com.entitybeans.Suppliers;
import java.util.List;
import javax.ejb.Local;


@Local
public interface SuppliersFacadeLocal {

    void create(Suppliers suppliers);

    void edit(Suppliers suppliers);

    void remove(Suppliers suppliers);

    Suppliers find(Object id);

    List<Suppliers> findAll();

    List<Suppliers> findRange(int[] range);

    int count();

    public Long countTotalSuppliers();
    
}
