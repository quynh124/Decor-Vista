/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mbeans;

import com.entitybeans.Suppliers;
import com.sessionbeans.SuppliersFacadeLocal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import org.primefaces.PrimeFaces;

/**
 *
 * @author BAOTHI
 */
@Named(value = "supplierMB")
@RequestScoped
public class SupplierMB {

    @EJB
    private SuppliersFacadeLocal suppliersFacade;

    private Suppliers suppliers;

    public SupplierMB() {
        suppliers = new Suppliers();
    }

    // Thêm nhà cung cấp mới
    public String addSuppliers() {
        try {
            suppliers.setSupplierID(generateRandomSupplierID());
            // Nếu ID nhà cung cấp chưa được thiết lập, tức là đang thêm mới
            if (suppliers.getSupplierID() == null) {
                suppliers.setSupplierID(generateRandomSupplierID()); // Tạo ID ngẫu nhiên
                suppliersFacade.create(suppliers);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Supplier added successfully!"));
            } else {
                // Nếu đã có ID, có nghĩa là đang cập nhật
                updateSuppliers();
            }
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi nếu có
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving supplier: " + e.getMessage(), null));
        }

        return "suppliers"; // Điều hướng đến trang suppliers
    }

// Cập nhật thông tin nhà cung cấp
    public String updateSuppliers() {
        try {
            // Cập nhật thông tin nhà cung cấp đã tồn tại
            suppliersFacade.edit(suppliers);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Supplier updated successfully!"));
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi nếu có
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating supplier: " + e.getMessage(), null));
        }

        return "suppliers"; // Điều hướng đến trang suppliers
    }

    private String generateRandomSupplierID() {
        String randomID;
        do {
            randomID = generateRandomString(5); // Tạo một chuỗi ngẫu nhiên có độ dài 5 ký tự
        } while (suppliersFacade.find(randomID) != null); // Đảm bảo ID là duy nhất
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

    public List<Suppliers> getLimitedSuppliers() {
        List<Suppliers> allSuppliers = showAllSuppliers(); // Lấy danh sách tất cả nhà cung cấp
        return allSuppliers.stream().limit(6).collect(Collectors.toList()); // Giới hạn 6 nhà cung cấp
    }

    public String deleteSuppliers(String id) {
        try {
            Suppliers supplierToDelete = suppliersFacade.find(id); // Tìm nhà cung cấp theo ID
            if (supplierToDelete != null) {
                suppliersFacade.remove(supplierToDelete); // Xóa nhà cung cấp khỏi cơ sở dữ liệu
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Supplier deleted successfully.")); // Thông báo thành công
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete supplier: not found.", null)); // Thông báo lỗi
            }
        } catch (PersistenceException e) {
            // Thông báo lỗi khóa ngoại bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('Cannot delete supplier due to foreign key constraint.');");
        } catch (Exception e) {
            // Thông báo lỗi khác bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('An error occurred while deleting the supplier.');");
        }

        return "suppliers"; // Điều hướng đến trang danh sách nhà cung cấp
    }

    public String findSupplierbyID(String id) {
        suppliers = suppliersFacade.find(id);
        return "suppliers";
    }

    public List<Suppliers> showAllSuppliers() {
        return suppliersFacade.findAll();
    }

    public String resetForm() {
        suppliers = null;
        return "suppliers";
    }

    public Suppliers getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Suppliers suppliers) {
        this.suppliers = suppliers;
    }

    public List<List<Suppliers>> getGroupedSuppliers() {
        List<Suppliers> allSuppliers = showAllSuppliers();
        List<List<Suppliers>> groupedSuppliers = new ArrayList<>();

        for (int i = 0; i < allSuppliers.size(); i += 6) {
            int end = Math.min(i + 6, allSuppliers.size());
            groupedSuppliers.add(allSuppliers.subList(i, end));
        }
        return groupedSuppliers;
    }

}
