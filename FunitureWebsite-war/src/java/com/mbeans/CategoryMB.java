
package com.mbeans;

import com.entitybeans.Categories;
import com.entitybeans.Subcategories;
import com.sessionbeans.CategoriesFacadeLocal;
import com.sessionbeans.SubcategoriesFacadeLocal;
import java.util.List;
import java.util.Random;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.primefaces.PrimeFaces;


@Named(value = "categoryMB")
@RequestScoped
public class CategoryMB {

    @EJB
    private SubcategoriesFacadeLocal subcategoriesFacade;

    @EJB
    private CategoriesFacadeLocal categoriesFacade;
    private Categories selectedCategory;

    Categories category;

    private boolean isUpdating; // Flag to indicate if we're updating an existing category

    public CategoryMB() {
        category = new Categories(); // Initialize with a new instance
        isUpdating = false; // Default to not updating
    }

    public String addCategory() {
        try {
            // Tạo ID ngẫu nhiên cho danh mục mới
            category.setCategoryID(generateRandomCategoryID());
            categoriesFacade.create(category);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Category added successfully!"));
        } catch (ConstraintViolationException e) {
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Constraint violation: " + violation.getMessage(), null));
            }
            return null; // Giữ lại trang để sửa lỗi
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "An error occurred: " + e.getMessage(), null));
            return null; // Giữ lại trang để sửa lỗi
        }

        resetForm(); // Reset form sau khi thêm
        return "category"; // Chuyển hướng hoặc điều hướng đến trang mong muốn
    }

    public String updateCategory() {
        try {
            // Nạp đối tượng Categories hiện tại từ cơ sở dữ liệu
            Categories existingCategory = categoriesFacade.find(category.getCategoryID());

            if (existingCategory != null) {
                // Kiểm tra từng trường để chỉ cập nhật nếu có thay đổi
                if (!existingCategory.getCategoryID().equals(category.getCategoryID())) {
                    existingCategory.setCategoryID(category.getCategoryID());
                }
                if (!existingCategory.getCategoryName().equals(category.getCategoryName())) {
                    existingCategory.setCategoryName(category.getCategoryName());
                }
                if (!existingCategory.getDescription().equals(category.getDescription())) {
                    existingCategory.setDescription(category.getDescription());
                }

                // Cập nhật đối tượng với những thay đổi
                categoriesFacade.edit(existingCategory);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Category updated successfully!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category not found.", null));
            }
        } catch (ConstraintViolationException e) {
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Constraint violation: " + violation.getMessage(), null));
            }
            return null; // Ở lại trang hiện tại để sửa lỗi
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "An error occurred: " + e.getMessage(), null));
            return null; // Ở lại trang hiện tại để sửa lỗi
        }

        // Reset form sau khi cập nhật
        resetForm();
        return "category"; // Điều hướng lại trang
    }

    public String findCategoryByID(String id) {
        if (id == null || id.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category ID is null or empty.", null));
            return null;
        }
        category = categoriesFacade.find(id);
        if (category == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category not found.", null));
        }
        return "category"; 
    }

    public String resetForm() {
        category = new Categories(); // Khởi tạo lại thể loại mới
        return "category"; // Điều hướng đến trang mong muốn
    }

   public String deleteCategories(String id) {
    try {
        Categories categoryToDelete = categoriesFacade.find(id);
        if (categoryToDelete != null) {
            // Tìm tất cả các Subcategory liên quan đến Category này
            List<Subcategories> subcategoriesList = subcategoriesFacade.findByCategory(id); // Tìm các subcategories bằng category

            // Cập nhật Category của các Subcategory đó thành null
            for (Subcategories subcategory : subcategoriesList) {
                subcategory.setCategoryID(null); // Đặt CategoryID của Subcategory thành null
                subcategoriesFacade.edit(subcategory); // Cập nhật Subcategory
            }

            // Xóa Category từ cơ sở dữ liệu
            categoriesFacade.remove(categoryToDelete);

            // Thông báo thành công
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category deleted successfully."));
        } else {
            // Thông báo nếu không tìm thấy Category
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Category not found.", null));
        }
    } catch (PersistenceException e) {
        // Thông báo lỗi khóa ngoại bằng alert JavaScript qua PrimeFaces
        PrimeFaces.current().executeScript("alert('Cannot delete category due to foreign key constraint.');");
    } catch (Exception e) {
        // Thông báo lỗi khác bằng alert JavaScript qua PrimeFaces
        PrimeFaces.current().executeScript("alert('An error occurred while deleting the category.');");
    }
    return "category"; // Điều hướng đến trang category
}

    // Helper method to generate random alphanumeric CategoryID
    private String generateRandomCategoryID() {
        String randomID;
        do {
            randomID = generateRandomString(5); // Generate a random string
        } while (categoriesFacade.find(randomID) != null); // Ensure it's unique
        return randomID;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder randomID = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            randomID.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomID.toString();
    }

    public List<Categories> showAllCategories() {
        return categoriesFacade.findAll();
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public Categories getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Categories selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public boolean isIsUpdating() {
        return isUpdating;
    }

    public void setIsUpdating(boolean isUpdating) {
        this.isUpdating = isUpdating;
    }

    public String saveCategory() {
        try {
            if (category.getCategoryID() != null && !category.getCategoryID().isEmpty()) {
                // Nếu categoryID đã có, gọi phương thức cập nhật
                categoriesFacade.edit(category);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Category updated successfully!"));
            } else {
                // Nếu không có categoryID, tạo mới danh mục
                category.setCategoryID(generateRandomCategoryID());
                categoriesFacade.create(category);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Category added successfully!"));
            }
        } catch (ConstraintViolationException e) {
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Constraint violation: " + violation.getMessage(), null));
            }
            return null; // Ở lại trang hiện tại để sửa lỗi
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "An error occurred: " + e.getMessage(), null));
            return null; // Ở lại trang hiện tại để sửa lỗi
        }

        // Đặt lại form sau khi thêm hoặc cập nhật
        resetForm();
        return "category"; // Chuyển hướng hoặc điều hướng đến trang mong muốn
    }

}
