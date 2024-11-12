
package com.sessionbeansprocess;


import com.entitybeans.Products;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author BAOTHI
 */
@Local
public interface CartSBLocal {

    public void addCart(Products product, int quantity);

    public void increaseCart(Products product, int newquantity);

    public void decreaseCart(Products product, int newquantity);

    public double sumCart();

    public int sumProductCart();

    public String removeItemCart(Products product);

    public String clearnCart();

    public Map<Products, Integer> getCartMap();
    
}
