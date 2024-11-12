
package com.sessionbeans;

import com.entitybeans.Users;
import java.util.List;
import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Local
public interface UsersFacadeLocal {

    void create(Users users);

    void edit(Users users);

    void remove(Users users);

    Users find(Object id);

    List<Users> findAll();

    List<Users> findRange(int[] range);

    int count();
    public Users checkUsers(String uname, String pword);

    public boolean validateCaptcha(HttpServletRequest request, String userCaptcha);

    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response);

    public Long countTotalCustomers();
}
