package com.mbeans;

import com.entitybeans.Products;
import com.sessionbeans.ConsultationsFacadeLocal;
import com.sessionbeans.DesignerFacadeLocal;
import com.sessionbeans.FeedbackFacadeLocal;
import com.sessionbeans.ProductsFacadeLocal;
import com.sessionbeans.SuppliersFacadeLocal;
import com.sessionbeans.UsersFacadeLocal;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.util.List;

@Named(value = "dashboardMB")
@RequestScoped
public class DashboardMB {
    
    @EJB
    private ConsultationsFacadeLocal consultationsFacade;
    
    @EJB
    private SuppliersFacadeLocal suppliersFacade;
    
    @EJB
    private UsersFacadeLocal usersFacade;
    
    @EJB
    private ProductsFacadeLocal productsFacade;
    
    @EJB
    private DesignerFacadeLocal designerFacade;
    
    @EJB
    private FeedbackFacadeLocal feedbackFacade; // Có thể cần nếu muốn lấy thêm thông tin

    public DashboardMB() {
    }
    
    private Long totalProducts;
    private Long totalSuppliers;
    private Long totalDesigners;
    private Long totalCustomers;
    
    private List<Object[]> topRatedProducts;
    private List<Object[]> lowRatedProducts;
    private List<Object[]> consultationStats;// Danh sách sản phẩm đánh giá thấp

    @PostConstruct
    public void init() {
        try {
            totalProducts = productsFacade.countTotalProducts();
            totalSuppliers = suppliersFacade.countTotalSuppliers();
            totalDesigners = designerFacade.countTotalDesigners();
            totalCustomers = usersFacade.countTotalCustomers();
            topRatedProducts = feedbackFacade.getTopRatedProducts();
            lowRatedProducts = feedbackFacade.getLowRatedProducts();
            consultationStats = consultationsFacade.getConsultationStats();
            System.out.println("Initialization completed successfully.");
        } catch (Exception e) {
            e.printStackTrace(); // Ghi lại lỗi
        }
    }
    
    public List<Object[]> getTopRatedProducts() {
        return topRatedProducts;
    }
    
    public List<Object[]> getLowRatedProducts() { // Getter cho sản phẩm đánh giá thấp
        return lowRatedProducts;
    }

    // Getter methods
    public Long getTotalProducts() {
        return totalProducts;
    }
    
    public Long getTotalSuppliers() {
        return totalSuppliers;
    }
    
    public Long getTotalDesigners() {
        return totalDesigners;
    }
    
    public Long getTotalCustomers() {
        return totalCustomers;
    }
    
    public List<Object[]> getConsultationStats() {
        return consultationStats;
    }
    
    public void setConsultationStats(List<Object[]> consultationStats) {
        this.consultationStats = consultationStats;
    }
    
}
