package com.mbeans;

import com.entitybeans.Products;
import com.entitybeans.Subcategories;
import com.sessionbeans.CategoriesFacadeLocal;
import com.sessionbeans.ProductsFacadeLocal;
import com.sessionbeans.SubcategoriesFacadeLocal;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.primefaces.PrimeFaces;

@Named(value = "subcategoriesMB")
@RequestScoped
public class SubcategoriesMB {

    @EJB
    private ProductsFacadeLocal productsFacade;

    @EJB
    private CategoriesFacadeLocal categoriesFacade;

    @EJB
    private SubcategoriesFacadeLocal subcategoriesFacade;
    private List<Subcategories> filteredSubcategories;
    private Products product;
    private Subcategories subcategory;
    private String categoryID;
    private Part image;
    private String imageName;

    public SubcategoriesMB() {
        subcategory = new Subcategories();
    }

    public String addSubcategory() {
        try {
            // Tạo ID ngẫu nhiên cho subcategory mới
            subcategory.setSubcategoryID(generateRandomSubcategoryID());

            // Xử lý file hình ảnh nếu có tải lên
            if (image != null) {
                String uploadDirectory = "";
                String fileName = getFileName(image);
                String filePath = uploadDirectory + "/" + fileName;

                // Lưu trữ file vào thư mục chỉ định
                try (InputStream input = image.getInputStream()) {
                    Path destination = Paths.get(uploadDirectory, fileName);
                    Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
                }

                // Thiết lập đường dẫn ảnh cho subcategory
                subcategory.setImage(filePath);
            }

            // Gán Category cho Subcategory
            subcategory.setCategoryID(categoriesFacade.find(categoryID));

            // Thực hiện thêm mới
            subcategoriesFacade.create(subcategory);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Subcategory added successfully!"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "subcategory"; // Điều hướng đến trang subcategory
    }

    public String updateSubcategory() {
        try {
            // Xử lý file hình ảnh nếu có tải lên mới
            if (image != null) {
                String uploadDirectory = ""; // Đường dẫn lưu trữ ảnh
                String fileName = getFileName(image);
                String filePath = uploadDirectory + "/" + fileName;

                // Lưu trữ file vào thư mục chỉ định
                try (InputStream input = image.getInputStream()) {
                    Path destination = Paths.get(uploadDirectory, fileName);
                    Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
                }

                // Cập nhật đường dẫn ảnh cho subcategory nếu có hình ảnh mới
                subcategory.setImage(filePath);
            }

            // Cập nhật thông tin subcategory
            subcategory.setCategoryID(categoriesFacade.find(categoryID));
            subcategoriesFacade.edit(subcategory);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Subcategory updated successfully!"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "subcategory"; // Điều hướng đến trang subcategory
    }

    public List<Subcategories> showAllSubcategories() {
        return subcategoriesFacade.findAll();
    }

    private String generateRandomSubcategoryID() {
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

    public String resetForm() {
        subcategory = new Subcategories();
        image = null;
        categoryID = null;
        return "subcategory";
    }

   public String deleteSubcategories(String id) {
    try {
        Subcategories subcategoryToDelete = subcategoriesFacade.find(id);
        if (subcategoryToDelete != null) {
            // Lấy danh sách các sản phẩm liên quan đến subcategory
            List<Products> productsList = productsFacade.findBySubcategoryId(subcategoryToDelete);

            // Cập nhật SubcategoryID của các sản phẩm liên quan về null hoặc một giá trị khác
            for (Products product : productsList) {
                product.setSubcategoryID(null); // hoặc một giá trị khác
                productsFacade.edit(product);
            }

            // Xóa hình ảnh từ thư mục
            deleteImage(subcategoryToDelete.getImage());

            // Xóa subcategory từ cơ sở dữ liệu
            subcategoriesFacade.remove(subcategoryToDelete);

            // Hiển thị thông báo thành công
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subcategory deleted successfully."));
        } else {
            // Hiển thị thông báo lỗi nếu không tìm thấy subcategory
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete subcategory: not found.", null));
        }
    } catch (PersistenceException e) {
        // Thông báo lỗi khóa ngoại bằng alert JavaScript qua PrimeFaces
        PrimeFaces.current().executeScript("alert('Cannot delete subcategory due to foreign key constraint.');");
    } catch (Exception e) {
        // Thông báo lỗi khác bằng alert JavaScript qua PrimeFaces
        PrimeFaces.current().executeScript("alert('An error occurred while deleting the subcategory.');");
    }
    
    return "subcategory"; // Điều hướng đến trang danh sách subcategory
}


    private void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
    }

    public String findSubcategoryByID(String id) {
        subcategory = subcategoriesFacade.find(id);
      
        return "subcategory";
    }

    public Subcategories getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategories subcategory) {
        this.subcategory = subcategory;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }

    // Utility method to extract file name from Part
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String cd : contentDisposition.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public void findCategory(String categoryID) {
        System.out.println("Finding category: " + categoryID); // Log để kiểm tra
        this.categoryID = categoryID; // Giả sử bạn đang lưu categoryID vào biến này
        filteredSubcategories = getFilteredSubcategories(); // Gọi phương thức lọc
    }

    public List<Subcategories> getFilteredSubcategories() {
        List<Subcategories> subcategories = null;
        if (categoryID != null && !categoryID.isEmpty()) {
            subcategories = subcategoriesFacade.findByCategory(categoryID);
        } else {
            subcategories = subcategoriesFacade.findAll(); // Nếu không có categoryID, trả về tất cả subcategories
        }

        // Log kích thước danh sách
        System.out.println("Filtered Subcategories Size: " + (subcategories != null ? subcategories.size() : 0));
        return subcategories;
    }

}
