package com.mbeans;

import com.entitybeans.Consultations;
import com.entitybeans.Designer;
import com.entitybeans.ProjectDesigner;
import com.sessionbeans.ConsultationsFacadeLocal;
import com.sessionbeans.DesignerFacadeLocal;
import com.sessionbeans.ProjectDesignerFacadeLocal;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import javax.servlet.http.Part;
import org.primefaces.PrimeFaces;

@Named(value = "designerMB")
@RequestScoped
public class DesignerMB implements Serializable {

    @EJB
    private ProjectDesignerFacadeLocal projectdesignerFacade;

    @EJB
    private ConsultationsFacadeLocal consultationsFacade;

    @EJB
    private DesignerFacadeLocal designerFacade;

    public void setConsultations(Consultations consultations) {
        this.consultations = consultations;
    }
    private Consultations consultations;

    public Consultations getConsultations() {
        return this.consultations;
    }

    private List<ProjectDesigner> projectdesigner;
    private Designer designer;
    private int designerID;

    public int getDesignerID() {
        return designerID;
    }

    public void setDesignerID(int designerID) {
        this.designerID = designerID;
    }
    private Designer selectedDesigner;
    private boolean editMode = false;
    private Part image;         // Đối tượng lưu trữ file hình ảnh

    // Getter và setter cho image
    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }

    public DesignerMB() {
        designer = new Designer();
        selectedDesigner = new Designer();
        consultations = new Consultations();
    }

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        String currentPage = context.getViewRoot().getViewId();

        // Attempt to retrieve the designer from the session
        designer = (Designer) context.getExternalContext().getSessionMap().get("loggedInDesigner");

        if (designer != null) {
            // If designer exists in session, fetch the latest data from the database
            designer = designerFacade.find(designer.getDesignerID());

            // If on profile.xhtml, load associated projects
            if (currentPage.endsWith("profile.xhtml")) {
                projectdesigner = projectdesignerFacade.findProjectsByDesignerID(designer.getDesignerID());
            }
        } else {
            if (currentPage.endsWith("register.xhtml")) {
                designer = new Designer();
            }
            projectdesigner = null;

        }
    }

    public String edit(Designer designer) {
        this.designer = designer;
        try {
            designerFacade.edit(this.designer);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedInDesigner",
                    designerFacade.find(designer.getDesignerID()));
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Profile updated successfully."));
            return "profile.xhtml";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update profile."));
            return null; // Giữ nguyên trang để hiển thị thông báo lỗi
        }
    }

    public String edit() {
        designerFacade.edit(designer);
        return "profile";
    }

    public List<Designer> showAllDesigner() {
        return designerFacade.findAll();
    }

    public String addDesign() {
        designerFacade.create(designer);
        return "/client/login.xhtml?faces-redirect=true";
    }

//    public String addDesigner() {
//        try {
//            designerFacade.create(designer);
//            FacesContext.getCurrentInstance().addMessage(null, 
//                new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful", "Welcome " + designer.getFirstName()));
//            return "/FunitureWebsite-war/faces/client/login.xhtml";
//        } catch (Exception e) {
//            FacesContext.getCurrentInstance().addMessage(null, 
//                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration Failed", "An error occurred while registering."));
//            return null;
//        }
//    }
    public String addDesigner() {
        try {
            // Xử lý tệp hình ảnh nếu có tải lên
            if (image != null && image.getSize() > 0) {
                // Đường dẫn thư mục để lưu trữ ảnh
                String uploadDirectory = "";  // Đổi đường dẫn theo yêu cầu của bạn
                String fileName = getFileName(image);  // Lấy tên tệp
                String filePath = uploadDirectory + "" + fileName;

                // Lưu tệp vào thư mục
                try (InputStream input = image.getInputStream()) {
                    Path destination = Paths.get(uploadDirectory, fileName);
                    Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
                }

                // Cập nhật đường dẫn ảnh cho Designer
                designer.setImage(filePath);
            }

            // Thực hiện thêm mới Designer vào cơ sở dữ liệu
            designerFacade.create(designer);

            // Thông báo thành công
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Designer added successfully!"));

        } catch (IOException e) {
            e.printStackTrace();
            // Thông báo lỗi nếu có vấn đề khi tải lên ảnh
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to upload image"));
        }

        // Điều hướng đến trang login sau khi thêm designer thành công
        return "/client/login.xhtml?faces-redirect=true";
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
 
    public String resetForm() {
        designer = new Designer(); // Khởi tạo lại để reset form
        return "register.xhtml";
    }

    public String loadUserProfile(String username) {
        designer = designerFacade.checkDesigner(username, username);
        return "profile.xhtml";  // Redirect tới trang profile
    }

    public void toggleEditMode() {
        editMode = !editMode;
        if (!editMode) {
            // Nếu tắt chế độ chỉnh sửa, cập nhật lại thông tin từ cơ sở dữ liệu để hủy bỏ các thay đổi chưa lưu
            init();
        }
    }

    // Phương thức hủy chỉnh sửa
    public String cancelEdit() {
        editMode = false;
        init();
        return null;
    }

    // Phương thức lưu các thay đổi
    public String saveChanges() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            designerFacade.edit(designer);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile Updated",
                    "Your profile has been updated successfully."));
            editMode = false;
            return null; // Giữ nguyên trang hiện tại để hiển thị thông báo
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update Failed",
                    "An error occurred while updating your profile."));
            return null; // Giữ nguyên trang hiện tại để hiển thị thông báo lỗi
        }
    }

//    public void loadSelectedDesigner() {
//        // Lấy designerID từ tham số URL
//        String designerIDStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("designerID");
//        if (designerIDStr != null) {
//            try {
//                int designerID = Integer.parseInt(designerIDStr);
//                // Truy vấn designer từ cơ sở dữ liệu
//                selectedDesigner = designerFacade.find(designerID);
//                if (selectedDesigner == null) {
//                    FacesContext.getCurrentInstance().addMessage(null,
//                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No designer found with ID: " + designerID));
//                }
//            } catch (NumberFormatException e) {
//                FacesContext.getCurrentInstance().addMessage(null,
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid Designer ID."));
//            }
//        } else {
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Designer ID is missing."));
//        }
//    }
    public String findAndLoadDesigner(Integer designerId) {
        if (designerId != null) {
            try {
                // Tìm designer từ cơ sở dữ liệu
                selectedDesigner = designerFacade.find(designerId);
                if (selectedDesigner == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Designer not found."));
                    return null;
                }

                // Lưu designer đã chọn vào session
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedDesigner", selectedDesigner);

                // Khởi tạo và lưu tempConsultations
//                Consultations tempConsultations = new Consultations(); // Tạo mới đối tượng Consultations
//                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tempconsultations", tempConsultations);
                designerID = selectedDesigner.getDesignerID();
                return "designerDetail"; // Chuyển đến trang chi tiết designer
            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while loading designer details."));
                return null;
            }
        }
        return null; // Trả về null nếu designerId là null
    }

     public String deleteDesigner(int designerID) {
        try {
            // Kiểm tra xem designer có tồn tại không trước khi xóa
            Designer designer = designerFacade.find(designerID);
            if (designer == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Designer not found."));
                return null; // Nếu không tìm thấy designer, trả về null
            }

            // Gọi dịch vụ để xóa designer
            designerFacade.remove(designer);
            
            // Thêm thông báo thành công
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Designer deleted successfully."));
            
            // Điều hướng về trang "designer" sau khi xóa thành công
            return "designer?faces-redirect=true"; // Dùng faces-redirect để thực hiện điều hướng

        } catch (PersistenceException e) {
            // Thông báo lỗi khóa ngoại qua PrimeFaces (dùng JavaScript)
            PrimeFaces.current().executeScript("alert('Cannot delete designer due to foreign key constraint.');");
            return null; // Không điều hướng nếu có lỗi
        } catch (Exception e) {
            // Thông báo lỗi chung qua PrimeFaces (dùng JavaScript)
            PrimeFaces.current().executeScript("alert('An error occurred while deleting the designer.');");
            return null; // Không điều hướng nếu có lỗi
        }
    }
    public void setFirstName(String firstName) {
        if (designer != null) {
            designer.setFirstName(firstName);
        }
    }

    // Thêm phương thức setter cho các thuộc tính khác
    public void setLastName(String lastName) {
        if (designer != null) {
            designer.setLastName(lastName);
        }
    }

    public void setPhone(String phone) {
        if (designer != null) {
            designer.setPhone(phone);
        }
    }

    public void setAddress(String address) {
        if (designer != null) {
            designer.setAddress(address);
        }
    }

    public void setImage(String image) {
        if (designer != null) {
            designer.setImage(image);
        }
    }

    public void setUsername(String username) {
        if (designer != null) {
            designer.setUsername(username);
        }
    }

    public void setPassword(String password) {
        if (designer != null) {
            designer.setPassword(password);
        }
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public Designer getDesigner() {
        return designer;
    }

    public void setDesigner(Designer designer) {
        this.designer = designer;
    }

    public Designer getSelectedDesigner() {
        return selectedDesigner;
    }

    public void setSelectedDesigner(Designer selectedDesigner) {
        this.selectedDesigner = selectedDesigner;
    }

    // Getters cho các trường profile
    public String getFirstName() {
        return designer != null ? designer.getFirstName() : "";
    }

    public String getLastName() {
        return designer != null ? designer.getLastName() : "";
    }

    public String getPhone() {
        return designer != null ? designer.getPhone() : "";
    }

    public String getAddress() {
        return designer != null ? designer.getAddress() : "";
    }

   

    public String getUsername() {
        return designer != null ? designer.getUsername() : "";
    }

    public String getPassword() {
        return designer != null ? designer.getPassword() : "";
    }

    public ConsultationsFacadeLocal getConsultationsFacade() {
        return consultationsFacade;
    }

    public void setConsultationsFacade(ConsultationsFacadeLocal consultationsFacade) {
        this.consultationsFacade = consultationsFacade;
    }

    public DesignerFacadeLocal getDesignerFacade() {
        return designerFacade;
    }

    public void setDesignerFacade(DesignerFacadeLocal designerFacade) {
        this.designerFacade = designerFacade;
    }

    public ProjectDesignerFacadeLocal getProjectdesignerFacade() {
        return projectdesignerFacade;
    }

    public void setProjectdesignerFacade(ProjectDesignerFacadeLocal projectdesignerFacade) {
        this.projectdesignerFacade = projectdesignerFacade;
    }

    public List<ProjectDesigner> getProjectdesigner() {
        return projectdesigner;
    }

    public void setProjectdesigner(List<ProjectDesigner> projectdesigner) {
        this.projectdesigner = projectdesigner;
    }

    private static class ProjectFacadeLocal {

        public ProjectFacadeLocal() {
        }
    }

    public String markDesigner() {
        FacesContext context = FacesContext.getCurrentInstance();
        Designer selectedDesigner = (Designer) context.getExternalContext().getSessionMap().get("selectedDesigner");
        if (selectedDesigner != null) {
            // Retrieve marked designers from session
            List<Designer> markedDesigners = (List<Designer>) context.getExternalContext().getSessionMap().get("markedDesigners");

            // Initialize if not present
            if (markedDesigners == null) {
                markedDesigners = new ArrayList<>();
                context.getExternalContext().getSessionMap().put("markedDesigners", markedDesigners);
            }

            // Check if the designer is already marked
            if (!markedDesigners.contains(selectedDesigner)) {
                markedDesigners.add(selectedDesigner);
                context.addMessage(null, new FacesMessage("Designer marked successfully!"));
            } else {
                context.addMessage(null, new FacesMessage("Designer is already marked!"));
            }
        } else {
            context.addMessage(null, new FacesMessage("No designer selected!"));
        }

        return null; // Stay on the same page
    }

    // Optionally, you can add a method to retrieve marked designers if needed
    public List<Designer> getMarkedDesigners() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (List<Designer>) context.getExternalContext().getSessionMap().get("markedDesigners");
    }

    public String removeDesigner(Designer designerToRemove) {
        FacesContext context = FacesContext.getCurrentInstance();

        // Retrieve marked designers from session
        List<Designer> markedDesigners = (List<Designer>) context.getExternalContext().getSessionMap().get("markedDesigners");

        if (markedDesigners != null) {
            markedDesigners.remove(designerToRemove);
            context.addMessage(null, new FacesMessage("Designer removed successfully!"));
        }

        return null; // Stay on the same page
    }

}
