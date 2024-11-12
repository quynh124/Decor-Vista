package com.mbeans;

import com.entitybeans.Products;
import com.sessionbeans.ProductsFacadeLocal;
import com.sessionbeansprocess.CartSBLocal;
import java.io.IOException;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@Named(value = "cartProcessMB")
@SessionScoped
public class CartProcessMB implements Serializable {

    private double totalCart;
    private double sumCart1;
    @EJB
    private ProductsFacadeLocal productsFacade;
    private Products selectedProduct;
    private int quantity = 1;
    private Map<Products, Integer> cartMap = new HashMap<>();

    public CartProcessMB() {
        sumCart1 = calculateItemsTotal();
    }

    @PostConstruct
    public void init() {
        cartMap = new HashMap<>();
    }

   public String addCart1() {
    FacesContext context = FacesContext.getCurrentInstance();
    String productID = (String) context.getExternalContext().getSessionMap().get("selectedProductID");
    System.out.println("Adding product from shop-detail with ID: " + productID);

    Products pro = productsFacade.find(productID);
    if (pro == null) {
        System.out.println("Product not found for ID: " + productID);
        return "shop-detail"; // Product not found
    }

    // Use the quantity directly from the bean's property
    int inputQuantity = this.quantity;

    // Add to cart
    if (cartMap.containsKey(pro)) {
        cartMap.put(pro, cartMap.get(pro) + inputQuantity); // Increase quantity
    } else {
        cartMap.put(pro, inputQuantity); // Add new product with quantity
    }

    // Update cart total
    sumCart1 = calculateItemsTotal();

    // Optionally clear quantity if you want it to reset after adding
     this.quantity = 1; // Uncomment this if you want to reset to default after adding

    // Return to shop detail page
    return "shop-detail"; // Ensure your navigation rule returns to the correct page
}


    public void addCartFromShop(String productID) {
        System.out.println("Received product ID from shop: " + productID);

        Products product = productsFacade.findProductById(productID);

        if (product == null) {
            System.out.println("Product not found for ID: " + productID);
            return; // Early exit if product is not found
        }

        // Thêm sản phẩm vào giỏ hàng với quantity cố định là 1
        int quantityToAdd = 1;
        if (cartMap.containsKey(product)) {
            cartMap.put(product, cartMap.get(product) + quantityToAdd); // Tăng số lượng
        } else {
            cartMap.put(product, quantityToAdd); // Thêm sản phẩm mới
        }
    }

    public String increaseCart(String idPro, int newquantity) {
        Products pro = productsFacade.find(idPro);
        cartMap.replace(pro, newquantity);
        return "cart";
    }


    public void decreaseCart(String idPro, int quantity) {
        Products pro = productsFacade.find(idPro);
        if (quantity <= 0) {
            removeItemCart(idPro);
        } else {
            cartMap.put(pro, quantity);
        }
    }

    public double sumCart() {
        double sum = 0;
        if (cartMap == null) {
            cartMap = new HashMap<>();
        }
        for (Map.Entry<Products, Integer> e : cartMap.entrySet()) {
            Products product = e.getKey();
            if (product != null && product.getUnitPrice() != null) {
                sum += product.getUnitPrice().doubleValue() * e.getValue();
            }
        }
        return sum;
    }

    public int sumProductCart() {
        int sum = 0;
        for (Map.Entry<Products, Integer> e : cartMap.entrySet()) {
            sum += e.getValue();
        }
        return sum;
    }

    public String removeItemCart(String idPro) {
        Products pro = productsFacade.find(idPro);
        cartMap.remove(pro);
        return "cart";
    }

    public String clearnCart() {
        cartMap.clear();

        return "cart";
    }

    public int totalItemsInCart() {
        int totalItems = 0;
        if (cartMap != null) {
            for (Map.Entry<Products, Integer> entry : cartMap.entrySet()) {
                totalItems += entry.getValue(); // Sum the quantities of each product
            }
        }
        return totalItems;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        // Optional: Validate to ensure quantity is always positive
        if (quantity < 1) {
            this.quantity = 1; // Ensure a minimum quantity of 1
        } else {
            this.quantity = quantity;
        }
    }

    public Map<Products, Integer> getCartMap() {
        return cartMap;
    }

    public void setCartMap(Map<Products, Integer> cartMap) {
        this.cartMap = cartMap;
    }

    public double getTotalCart() {
        return totalCart;
    }

    public void setTotalCart(double totalCart) {
        this.totalCart = totalCart;
    }

    public void updateSumCart(double deliveryPrice) {
        sumCart1 = calculateItemsTotal() + deliveryPrice;
    }

    public double calculateItemsTotal() {
        // Calculate the total price of items in the cart
        return 100.0; // Example value
    }

    public double getSumCart1() {
        return sumCart1;
    }

    public void setSumCart1(double sumCart1) {
        this.sumCart1 = sumCart1;
    }

    public void updateTotalCart(double deliveryPrice) {
        totalCart = sumCart() + deliveryPrice;
    }

    public Products getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Products selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

}
