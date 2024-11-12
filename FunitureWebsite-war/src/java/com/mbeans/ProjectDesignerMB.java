/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mbeans;

import java.io.Serializable;
import com.entitybeans.Designer;
import com.entitybeans.ProjectDesigner;
import com.sessionbeans.DesignerFacadeLocal;
import com.sessionbeans.ProjectDesignerFacadeLocal;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

/**
 *
 * @author BAOTHI
 */
@Named(value = "projectDesignerMB")
@RequestScoped
public class ProjectDesignerMB implements Serializable {

    @EJB
    private DesignerFacadeLocal designerFacade;

    @EJB
    private ProjectDesignerFacadeLocal projectDesignerFacade;

    private ProjectDesigner projectDesigner;

    private Designer loggedInDesigner;
    private List<ProjectDesigner> projectList = new ArrayList<>();
    private int designerID;
    private boolean showProjectIDColumn; // Thuộc tính để điều khiển hiển thị cột

    private ProjectDesigner selectedProject; // Dự án được chọn để chỉnh sửa
    private Part file;
    private String projectID;
    private String projectName;
    private String description;
    private String image;
    private boolean isEditMode = false;
    private String projectname;

    public ProjectDesigner getProjectDesigner() {
        return projectDesigner;
    }

    public void setProjectDesigner(ProjectDesigner projectDesigner) {
        this.projectDesigner = projectDesigner;
    }

    public ProjectDesignerMB() {
        projectDesigner = new ProjectDesigner();
    }

    public List<ProjectDesigner> showAllProjectDesigner() {
        return projectDesignerFacade.findAll();
    }
// public List<ProjectDesigner> getProjectList() {
//        return projectList;
//    }

    @PostConstruct
    public void init() {
        loadDesignerAndProjects();
    }

    public String addProject() {
        try {
            if (file != null) {
                String uploadDirectory = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/designer/project");
                String fileName = getFileName(file);
                String filePath = uploadDirectory + "/" + fileName;
                try (InputStream input = file.getInputStream()) {
                    Path destination = Paths.get(uploadDirectory, fileName);
                    Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
                }
                projectDesigner.setImage(fileName);
            } else {
                ProjectDesigner existingProject = projectDesignerFacade.find(projectDesigner.getProjectID());
                if (existingProject != null) {
                    projectDesigner.setImage(existingProject.getImage());
                }
            }
            FacesContext context = FacesContext.getCurrentInstance();
            Object designerObj = context.getExternalContext().getSessionMap().get("loggedInDesigner");
            if (designerObj instanceof Designer) {
                Designer loggedInDesigner = (Designer) designerObj;
                projectDesigner.setDesignerID(loggedInDesigner); // Set designer ID in the project
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No designer logged in."));
                return null;
            }
            if (projectDesigner.getProjectID() == null) {
                projectDesignerFacade.create(projectDesigner);
            } else {
                projectDesignerFacade.edit(projectDesigner);
            }
            projectDesigner = new ProjectDesigner();
            loadDesignerAndProjects();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Project saved successfully."));

            return "projectdesigner?faces-redirect=true";
        } catch (IOException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to upload image."));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while saving the project."));
            return null;
        }
    }

    public void loadDesignerAndProjects() {
        FacesContext context = FacesContext.getCurrentInstance();
        Object designerObj = context.getExternalContext().getSessionMap().get("loggedInDesigner");
        if (designerObj != null && designerObj instanceof Designer) {
            loggedInDesigner = (Designer) designerObj;
            designerID = loggedInDesigner.getDesignerID();
            projectList = projectDesignerFacade.findProjectsByDesignerID(designerID);
            if (projectList == null) {
                projectList = new ArrayList<>();
            }
            System.out.println("Loaded designerID from session: " + designerID);
            System.out.println("Number of projects loaded: " + projectList.size());
        } else {
            projectList = new ArrayList<>();
            System.out.println("No designer found in session.");
        }
    }

//    public List<ProjectDesigner> getProjectList() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        Designer selectedDesigner = (Designer) context.getExternalContext().getSessionMap().get("selectedDesigner");
//        return projectList = projectDesignerFacade.findProjectsByDesignerID(selectedDesigner.getDesignerID());
//    }
    public List<ProjectDesigner> getProjectList() {
    FacesContext context = FacesContext.getCurrentInstance();
    
    // Lấy designer đã chọn từ session
    Designer selectedDesigner = (Designer) context.getExternalContext().getSessionMap().get("selectedDesigner");

    // Nếu có designer đã đăng nhập, trả về danh sách dự án của designer đã đăng nhập
    if (loggedInDesigner != null) {
        return projectDesignerFacade.findProjectsByDesignerID(loggedInDesigner.getDesignerID());
    } else if (selectedDesigner != null) {
        // Nếu không có designer đã đăng nhập, nhưng có designer đã chọn từ session
        return projectDesignerFacade.findProjectsByDesignerID(selectedDesigner.getDesignerID());
    } else {
        // Nếu không có designer nào, trả về tất cả các dự án
        return projectDesignerFacade.findAll();
    }
}


    public List<ProjectDesigner> getAllProjectDesigners() {
        return projectDesignerFacade.findAll();
    }

    public List<ProjectDesigner> getProjectListByDesigner() {
        FacesContext context = FacesContext.getCurrentInstance();
        Designer selectedDesigner = (Designer) context.getExternalContext().getSessionMap().get("selectedDesigner");
        if (selectedDesigner != null) {
            return projectDesignerFacade.findProjectsByDesignerID(selectedDesigner.getDesignerID());
        }
        return new ArrayList<>();
    }

    public void setProjectList(List<ProjectDesigner> projectList) {
        this.projectList = projectList;
    }

    public String findProjectDesignerbyID(int id) {
        projectDesigner = projectDesignerFacade.find(id);
        return "projectdesigner";
    }

    public String resetForm() {
        projectDesigner = null;
        return "projectdesigner";
    }

    public DesignerFacadeLocal getDesignerFacade() {
        return designerFacade;
    }

    public void setDesignerFacade(DesignerFacadeLocal designerFacade) {
        this.designerFacade = designerFacade;
    }

    public ProjectDesignerFacadeLocal getProjectDesignerFacade() {
        return projectDesignerFacade;
    }

    public void setProjectDesignerFacade(ProjectDesignerFacadeLocal projectDesignerFacade) {
        this.projectDesignerFacade = projectDesignerFacade;
    }

    public String viewProjectDetails(ProjectDesigner project) {
        projectDesigner = project;
        return "projectDetails";
    }
     public String viewProjectDetails1(ProjectDesigner project) {
        projectDesigner = project;
        return "projectDetails";
    }

    public String updateProject() {
        try {
            if (file != null && file.getSize() > 0) {
                // Xử lý tải ảnh lên
                String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
                String filePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/designer/project/") + File.separator + fileName;
                InputStream input = file.getInputStream();
                Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                projectDesigner.setImage(fileName);
            }

            projectDesignerFacade.edit(projectDesigner);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Project updated successfully."));
            return "projectdesigner.xhtml?faces-redirect=true";
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving image file", null));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving project", null));
            return null;
        }
    }

    private String getFileName(Part file) {
        String contentDisposition = file.getHeader("content-disposition");
        for (String part : contentDisposition.split(";")) {
            if (part.trim().startsWith("filename")) {
                return part.substring(part.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public String updateProjectDes() {
        projectDesignerFacade.edit(projectDesigner);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project updated successfully!"));
        return "projectdesigner?faces-redirect=true";
    }

    public Designer getLoggedInDesigner() {
        return loggedInDesigner;
    }

    public void setLoggedInDesigner(Designer loggedInDesigner) {
        this.loggedInDesigner = loggedInDesigner;
    }

    public int getDesignerID() {
        return designerID;
    }

    public void setDesignerID(int designerID) {
        this.designerID = designerID;
    }

    public boolean isShowProjectIDColumn() {
        return showProjectIDColumn;
    }

    public void setShowProjectIDColumn(boolean showProjectIDColumn) {
        this.showProjectIDColumn = showProjectIDColumn;
    }

    public ProjectDesigner getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(ProjectDesigner selectedProject) {
        this.selectedProject = selectedProject;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectname) {
        if (projectDesigner != null) {
            projectDesigner.setProjectName(projectname);
        }
    }

    public void setDecription(String description) {
        if (projectDesigner != null) {
            projectDesigner.setDescription(description);
        }
    }

    public void setImage(String image) {
        if (projectDesigner != null) {
            projectDesigner.setImage(image);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public String deleteProject(int id) {
        try {
            ProjectDesigner project = projectDesignerFacade.find(id);
            if (project != null && project.getDesignerID().getDesignerID() == this.designerID) {
                projectDesignerFacade.remove(project);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project deleted successfully."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Project not found or you do not have permission to delete it.", null));
            }
        } catch (PersistenceException e) {
            // Thông báo lỗi khóa ngoại bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('Cannot delete project due to foreign key constraint.');");
        } catch (Exception e) {
            // Thông báo lỗi khác bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('An error occurred while deleting the project.');");
        }

        // Tải lại danh sách designer và project
        loadDesignerAndProjects();
        return "projectdesigner?faces-redirect=true"; // Điều hướng đến trang danh sách dự án
    }

    public String editproject() {
        projectDesignerFacade.edit(projectDesigner);
        return "projectdesigner";
    }

    public String editProject(ProjectDesigner project) {
        try {
            // Set the selected project to edit
            this.projectDesigner = project;

            // Update the project in the database
            projectDesignerFacade.edit(this.projectDesigner);

            // Display success message
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Project updated successfully."));

            // Reload the list of projects after editing
            loadDesignerAndProjects();

            // Redirect to the main project designer page
            return "projectdesigner?faces-redirect=true";
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();

            // Display error message
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update project: " + e.getMessage()));

            // Stay on the same page if an error occurs
            return null;
        }
    }

    public boolean isIsEditMode() {
        return isEditMode;
    }

    public void setIsEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

}
