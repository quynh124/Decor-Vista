
package com.sessionbeans;

import com.entitybeans.Users;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Stateless
public class UsersFacade extends AbstractFacade<Users> implements UsersFacadeLocal {

    @PersistenceContext(unitName = "FunitureWebsite-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsersFacade() {
        super(Users.class);
    }
     @Override
     public Users checkUsers(String uname, String pword) {

        String sql = "SELECT u FROM Users u WHERE u.userName = :userName and u.password = :password";
        Query query = em.createQuery(sql);
        query.setParameter("userName", uname);
        query.setParameter("password", pword);
        try {
            return (Users) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
       @Override
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("image/jpg");

            int iTotalChars = 6;
            int iHeight = 40;
            int iWidth = 150;
            Font font = new Font("Arial", Font.BOLD, 30);
            Random randChars = new Random();

            // Tạo mã CAPTCHA ngẫu nhiên
            String captchaCode = (Long.toString(Math.abs(randChars.nextLong()), 36)).substring(0, iTotalChars);
            request.getSession().setAttribute("captcha", captchaCode);

            // Tạo hình ảnh CAPTCHA
            BufferedImage bufferedImage = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferedImage.getGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, iWidth, iHeight);
            g.setFont(font);
            g.setColor(Color.BLACK);
            g.drawString(captchaCode, 20, 30);

            // Thêm nhiễu
            for (int i = 0; i < 15; i++) {
                g.setColor(new Color(randChars.nextInt(255), randChars.nextInt(255), randChars.nextInt(255)));
                g.drawLine(randChars.nextInt(iWidth), randChars.nextInt(iHeight), randChars.nextInt(iWidth), randChars.nextInt(iHeight));
            }
            g.dispose();
            ImageIO.write(bufferedImage, "jpg", response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public boolean validateCaptcha(HttpServletRequest request, String userCaptcha) {
        String sessionCaptcha = (String) request.getSession().getAttribute("captcha");
        return sessionCaptcha != null && sessionCaptcha.equals(userCaptcha);
    }
    @Override
     public Long countTotalCustomers() {
        return em.createQuery("SELECT COUNT(u) FROM Users u", Long.class).getSingleResult();
    }
}
