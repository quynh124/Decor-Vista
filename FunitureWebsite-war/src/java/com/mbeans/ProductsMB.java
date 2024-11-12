/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mbeans;

import com.entitybeans.Favorites;
import com.entitybeans.Products;
import com.entitybeans.Suppliers;
import com.entitybeans.Users;
import com.sessionbeans.CategoriesFacadeLocal;
import com.sessionbeans.FavoritesFacadeLocal;
import com.sessionbeans.ProductsFacadeLocal;
import com.sessionbeans.SubcategoriesFacadeLocal;
import com.sessionbeans.SuppliersFacadeLocal;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import javax.servlet.http.Part;
import org.primefaces.PrimeFaces;

/**
 *
 * @author BAOTHI
 */
@Named(value = "productsMB")
@RequestScoped
public class ProductsMB {

    @EJB
    private FavoritesFacadeLocal favoritesFacade;

    @EJB
    private ProductsFacadeLocal productsFacade;

    @EJB
    private SubcategoriesFacadeLocal subcategoriesFacade;

    @EJB
    private SuppliersFacadeLocal suppliersFacade;

    private List<Products> filteredProducts;
    private String searchKeyword;
    private List<String> selectedSubcategoryId = new ArrayList<>();
    private String sortBy;
    private String sortByName; // Thêm thuộc tính này

    private Products products;
    private String subcategoryID;
    private String supplierID;
    private int quantity;
    private Products selectedProduct;
    private List<String> selectSupplierId = new ArrayList<>();
    private List<Products> allProducts; // Tất cả sản phẩm
    private int pageSize = 8; // Số lượng sản phẩm hiển thị mỗi trang
    private int currentPage = 1; // Trang hiện tại
    private List<Products> paginatedProducts; // Danh sách sản phẩm sau khi phân trang
    private Part image;

    public ProductsMB() {
        products = new Products();
        searchKeyword = "";
        sortBy = "asc";

    }

    public List<Products> showAllProducts() {
        return productsFacade.findAll();
    }

    public void addToFavoritesFromShop(String productID) {
        FacesContext context = FacesContext.getCurrentInstance();
        Users loggedInUser = (Users) context.getExternalContext().getSessionMap().get("loggedInUser");

        if (loggedInUser != null) {
            Products product = productsFacade.findProductById(productID); // Tìm sản phẩm dựa trên ID

            if (product != null) {
                // Khởi tạo đối tượng Favorites
                Favorites favorite = new Favorites();
                favorite.setUserID(loggedInUser); // Đặt người dùng
                favorite.setProductID(product); // Đặt sản phẩm

                // Thêm sản phẩm vào danh sách yêu thích
                favoritesFacade.create(favorite);

                // Thông báo cho người dùng rằng sản phẩm đã được thêm vào yêu thích
                context.addMessage(null, new FacesMessage("Product added to favorites!"));
            } else {
                // Thông báo lỗi nếu không tìm thấy sản phẩm
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Product not found.", null));
            }
        } else {
            // Thông báo lỗi nếu người dùng chưa đăng nhập
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You must be logged in to add favorites.", null));
        }
    }

    public List<Products> paginate(List<Products> products) {
        if (products == null || products.isEmpty()) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu không có sản phẩm
        }

        int totalProducts = products.size();
        int fromIndex = (currentPage - 1) * pageSize;

        // Kiểm tra chỉ số để tránh IndexOutOfBoundsException
        if (fromIndex >= totalProducts) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu trang hiện tại vượt quá tổng số sản phẩm
        }

        int toIndex = Math.min(fromIndex + pageSize, totalProducts); // Chỉ số cuối
        paginatedProducts = products.subList(fromIndex, toIndex); // Tạo danh sách con cho trang hiện tại

        return paginatedProducts; // Trả về danh sách đã phân trang
    }

    public List<String> getSelectedSubcategoryId() {
        return selectedSubcategoryId;
    }

    public void setSelectedSubcategoryId(List<String> selectedSubcategoryId) {
        this.selectedSubcategoryId = selectedSubcategoryId;
    }

    public String addProducts() {
        try {
            products.setProductID(generateRandomProductID());
            // Kiểm tra xem có hình ảnh được tải lên không
            if (image != null) {
                String uploadDirectory = ""; // Đường dẫn lưu trữ file
                String fileName = getFileName(image);
                String filePath = uploadDirectory + "/" + fileName;

                // Lưu file vào thư mục uploads
                try (InputStream input = image.getInputStream()) {
                    Path destination = Paths.get(uploadDirectory, fileName);
                    Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
                }

                products.setImage(filePath);
            } else {
                // Nếu không có hình ảnh mới, giữ lại hình ảnh cũ
                Products existingProduct = productsFacade.find(products.getProductID());
                if (existingProduct != null) {
                    products.setImage(existingProduct.getImage());
                }
            }

            // Thiết lập ID cho subcategory và supplier
            products.setSubcategoryID(subcategoriesFacade.find(subcategoryID));
            products.setSupplierID(suppliersFacade.find(supplierID));

            // Nếu ID sản phẩm chưa được thiết lập, tức là đang thêm mới
            if (products.getProductID() == null) {
                // Sử dụng getRandom() để tạo ID ngẫu nhiên cho sản phẩm mới
                products.setProductID(generateRandomProductID()); // Gọi phương thức getRandom() ở đây
                productsFacade.create(products);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Product added successfully!"));
            } else {
                // Nếu đã có ID, có nghĩa là đang cập nhật
                productsFacade.edit(products);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Product updated successfully!"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving product: " + e.getMessage(), null));
        }

        return "products"; // Điều hướng đến trang products
    }

// Cập nhật thông tin sản phẩm
    public String updateProducts() {
        try {
            // Cập nhật thông tin sản phẩm đã tồn tại
            productsFacade.edit(products);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Product updated successfully!"));
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi nếu có
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating product: " + e.getMessage(), null));
        }

        return "products"; // Điều hướng đến trang products
    }

    private String generateRandomProductID() {
        String randomID;
        do {
            randomID = generateRandomString(5); // Tạo một chuỗi ngẫu nhiên có độ dài 5 ký tự
        } while (subcategoriesFacade.find(randomID) != null); // Đảm bảo ID là duy nhất
        return randomID;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // Ký tự sử dụng để tạo ID
        Random random = new Random();
        StringBuilder randomID = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            randomID.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomID.toString();
    }

    public String deleteProducts(String productID) {
        try {
            Products productsToDelete = productsFacade.find(productID);
            if (productsToDelete != null) {
                // Xóa hình ảnh từ thư mục
                deleteImage(productsToDelete.getImage());

                // Xóa sản phẩm từ cơ sở dữ liệu
                productsFacade.remove(productsToDelete);

                // Hiển thị thông báo thành công
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product deleted successfully."));
            } else {
                // Hiển thị thông báo lỗi nếu không tìm thấy sản phẩm
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Product not found.", null));
            }
        } catch (PersistenceException e) {
            // Thông báo lỗi khóa ngoại bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('Cannot delete product due to foreign key constraint.');");
        } catch (Exception e) {
            // Thông báo lỗi khác bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('An error occurred while deleting the product.');");
        }
        return "products"; // Điều hướng đến trang sản phẩm
    }

    private void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
    }

    public String resetForm() {
        products = null;
        image = null;
        subcategoryID = null;

        supplierID = null;
        return "products";
    }

    public String findProductforDetail(String productID) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("selectedProductID", productID); // Lưu ID vào session

        try {
            context.getExternalContext().redirect("shop-detail.xhtml"); // Điều hướng đến trang chi tiết sản phẩm
        } catch (IOException e) {
            e.printStackTrace(); // Ghi log lỗi nếu có
        }

        return null; // Trả về null để không thực hiện thêm điều hướng
    }

    public void loadSelectedProduct() {
        FacesContext context = FacesContext.getCurrentInstance();
        String productID = (String) context.getExternalContext().getSessionMap().get("selectedProductID");
        if (productID != null) {
            selectedProduct = productsFacade.find(productID); // Tìm sản phẩm theo ID

            // Log quá trình tải sản phẩm
            System.out.println("Loaded product: " + (selectedProduct != null ? selectedProduct.getProductName() : "Not found"));
        }
    }

    public String findProductforUpdate(String productID) {
        products = productsFacade.find(productID);
        if (products != null) {

            if (products.getSubcategoryID() != null) {
                subcategoryID = products.getSubcategoryID().getSubcategoryID();
            } else {
                subcategoryID = null;
            }

            if (products.getSupplierID() != null) {
                supplierID = products.getSupplierID().getSupplierID();
            } else {
                supplierID = null;
            }
        }
        return "products";
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public String getSubcategoryID() {
        return subcategoryID;
    }

    public void setSubcategoryID(String subcategoryID) {
        this.subcategoryID = subcategoryID;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public Products getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Products selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String cd : contentDisposition.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }

    public List<Products> getFilteredProducts() {
        if (filteredProducts == null) {
            updateProductList(); // Tải danh sách sản phẩm khi trang được load
        }
        return filteredProducts;
    }

    public void updateProductList() {
        // Lọc danh sách sản phẩm dựa trên từ khóa tìm kiếm, danh mục con đã chọn, nhà cung cấp đã chọn và các thuộc tính sắp xếp
        filteredProducts = productsFacade.findProducts(searchKeyword, selectedSubcategoryId, selectSupplierId, sortBy, sortByName);

        if (selectedProduct != null) {
            searchKeyword = selectedProduct.getProductName(); // Cập nhật từ khóa với tên sản phẩm đã chọn
        }

        // Nếu đã có từ khóa tìm kiếm, gọi hàm lọc theo từ khóa
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            filteredProducts = productsFacade.findProductsByKeyword(searchKeyword);
        }

        System.out.println("Filtered products count: " + (filteredProducts != null ? filteredProducts.size() : 0));

        // Phân trang các sản phẩm đã lọc
        filteredProducts = paginate(filteredProducts);
    }

    // Getters and setters cho searchKeyword, selectedSubcategoryId và sortBy
    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
// Getter và setter cho sortByName

    public String getSortByName() {
        return sortByName;
    }

    public void setSortByName(String sortByName) {
        this.sortByName = sortByName;
    }

    public List<String> getSelectSupplierId() {
        return selectSupplierId;
    }

    public void setSelectSupplierId(List<String> selectSupplierId) {
        this.selectSupplierId = selectSupplierId;
    }

    public void goToNextPage() {
        currentPage++; // Tăng trang hiện tại
        updateProductList(); // Cập nhật danh sách sản phẩm
    }

    public void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--; // Giảm trang hiện tại
            updateProductList(); // Cập nhật danh sách sản phẩm
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Products> getAllProducts() {
        return allProducts;
    }

    public void setAllProducts(List<Products> allProducts) {
        this.allProducts = allProducts;
    }

}
