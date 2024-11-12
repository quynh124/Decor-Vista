/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mbeans;

import com.entitybeans.Consultations;
import com.entitybeans.Designer;
import com.entitybeans.Users;

import com.sessionbeans.ConsultationsFacadeLocal;
import com.sessionbeans.DesignerFacadeLocal;
import com.sessionbeans.UsersFacadeLocal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.primefaces.PrimeFaces;

/**
 *
 * @author BAOTHI
 */
@Named(value = "consultationsMB")
@RequestScoped
public class ConsultationsMB {

    @EJB
    private UsersFacadeLocal usersFacade;

    private List<Consultations> userConsultations;

    @EJB
    private DesignerFacadeLocal designerFacade;

    @EJB
    private ConsultationsFacadeLocal consultationsFacade;

    private List<Consultations> consultationsForDate;
    private int selectedDesignerID;
    private Consultations consultation = new Consultations();
    private Consultations selectedConsultation = new Consultations();
// Getter và Setter
    private Consultations consultations;

    private List<Consultations> consultationsList = new ArrayList<>();

    public int getSelectedDesignerID() {
        return selectedDesignerID;
    }

    public void setSelectedDesignerID(int selectedDesignerID) {
        this.selectedDesignerID = selectedDesignerID;
    }

    private Date selectedDate;
    private String fullName;
    private Integer userID;

    private Date selectedTime;
    Designer designer;

    private Date scheduleddatetime;
    private Date startTime; // Thời gian bắt đầu
    private Date endTime; // Thời gian kết thúc
    private Long designerId;

    private Consultations selectedConsulation;
    private int consultationsID;
    private int designerID;
    private Designer loggedInDesigner;
    private Date day;
    private String notes;
    private String status;

    public ConsultationsMB() {
        consultations = new Consultations();
    }

    public List<Consultations> getConsultationsList() {
        return consultationsList;
    }

    public void setConsultationsList(List<Consultations> consultationsList) {
        this.consultationsList = consultationsList;
    }

    public List<Consultations> showAllConsultation1() {
        return consultationsFacade.findAll();
    }

   public List<Consultations> showAllConsultationss() {
    FacesContext context = FacesContext.getCurrentInstance();
    Designer loggedInDesigner = (Designer) context.getExternalContext().getSessionMap().get("loggedInDesigner");

    if (loggedInDesigner == null) {
        return new ArrayList<>(); // Return an empty list if no designer is logged in
    }

    try {
        return consultationsFacade.findConsultationsByDesignerID(loggedInDesigner.getDesignerID());
    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<>(); // Return an empty list if there’s an error
    }
}

    public void loadDesignerConsultations() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            loggedInDesigner = (Designer) context.getExternalContext().getSessionMap().get("loggedInDesigner");
            if (loggedInDesigner != null) {
                consultationsList = consultationsFacade.findByDesignerID(loggedInDesigner.getDesignerID());
            } else {
                // Log or handle missing designer
            }
        } catch (Exception e) {
            e.printStackTrace(); // Or log this to a logger
        }
    }

    public List<Consultations> showAllConsultations() {
        try {
            // Retrieve the logged-in designer's ID from the session
            Integer loggedInDesignerID = (Integer) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .get("designerID");

            // Check if designer ID is available
            if (loggedInDesignerID != null) {
                // Call method to retrieve consultations for the designer
                List<Consultations> consultationsList = consultationsFacade.findByDesignerID(loggedInDesignerID);

                // Check if the consultationsList is not empty
                if (consultationsList != null && !consultationsList.isEmpty()) {
                    return consultationsList;
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_WARN, "Warning", "No consultations found for this designer."));
                    return new ArrayList<>(); // Return empty list if no consultations found
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, "Error", "No designer ID found in session."));
                return new ArrayList<>(); // Return empty if no designerID found
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while fetching consultations."));
            return new ArrayList<>(); // Return empty list if an error occurs
        }
    }

    public Consultations getConsultations() {
        return consultations;
    }

    public void setConsultations(Consultations consultations) {
        this.consultations = consultations;
    }

    public void setDesigner(Designer designer) {
        this.designer = designer;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public DesignerFacadeLocal getDesignerFacade() {
        return designerFacade;
    }

    public void setDesignerFacade(DesignerFacadeLocal designerFacade) {
        this.designerFacade = designerFacade;
    }

    public ConsultationsFacadeLocal getConsultationsFacade() {
        return consultationsFacade;
    }

    public void setConsultationsFacade(ConsultationsFacadeLocal consultationsFacade) {
        this.consultationsFacade = consultationsFacade;
    }

//    public Integer getDesignerID() {
//        return designerID;
//    }
//
//    public void setDesignerID(Integer designerID) {
//        this.designerID = designerID;
//    }
    public Date getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(Date selectedTime) {
        this.selectedTime = selectedTime;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String deleteConsultations(int id) {
        try {

            consultationsFacade.remove(consultationsFacade.find(id));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("consultations deleted successfully."));
            return null; // điều hướng trang thành công
        } catch (PersistenceException e) {
            // Thông báo lỗi khóa ngoại bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('Cannot delete consultations due to foreign key constraint.');");
            return null;
        } catch (Exception e) {
            // Thông báo lỗi khác bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('An error occurred while deleting the consultations.');");
            return null;
        }
    }

    public Date getScheduleddatetime() {
        return scheduleddatetime;
    }

    public void setScheduleddatetime(Date scheduleddatetime) {
        this.scheduleddatetime = scheduleddatetime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<Consultations> getUserConsultations() {
        return userConsultations;
    }

    public void setUserConsultations(List<Consultations> userConsultations) {
        this.userConsultations = userConsultations;
    }

    public String createConsultation() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Users user = (Users) session.getAttribute("loggedInUser");
        FacesContext context = FacesContext.getCurrentInstance();
        Designer selectedDesigner = (Designer) context.getExternalContext().getSessionMap().get("selectedDesigner");
        try {
            if (selectedDesigner == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Designer not found.", null));
                return null;
            }
            if (consultations == null) {
                consultations = new Consultations();
            }
            consultations.setDesignerID(selectedDesigner);
            consultations.setDay(new Date());
            consultations.setUserID(user);
            consultations.setStatus("Booking");
            if (consultations.getConsultationID() == null) {
                consultationsFacade.create(consultations);
                return "booking";
            } else {
                Consultations existing = consultationsFacade.find(consultations.getConsultationID());
                if (existing != null) {
                    existing.setDay(new Date());
                    existing.setScheduleddatetime(consultations.getScheduleddatetime());
                    existing.setDesignerID(consultations.getDesignerID());
                    existing.setUserID(consultations.getUserID());
                    existing.setNotes(consultations.getNotes());

                    consultationsFacade.edit(existing);
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consultation updated successfully!", null));
                }
            }

            context.getExternalContext().getSessionMap().remove("tempconsultations");
            return null;

        } catch (ConstraintViolationException e) {
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        violation.getMessage(), null));
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "An unexpected error occurred. Please try again.", null));
            e.printStackTrace();
        }

        return null;
    }

    public String findAndLoadConsultation(Integer consultationID) {
        if (consultationID != null) {
            selectedConsultation = consultationsFacade.find(consultationID);
            if (selectedConsultation != null) {
                return "consultationschedule"; // Navigate to edit page
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR, "Error", "Consultation not found."));
        return null;
    }

    public String updateConsultation() {
        try {
            consultationsFacade.edit(selectedConsultation);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Success", "Consultation updated successfully."));
            return "profile"; // Navigate back to profile page
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while updating consultation."));
            return null;
        }
    }

    public String editCon() {
        consultationsFacade.edit(consultations);
        return "profile";
    }

    public String cancel() {
        // Reset or cancellation logic
        return "profile.xhtml?faces-redirect=true"; // Navigate to profile page
    }

    // Method to load consultations by selected date
    public void loadConsultationsByDate() {
        consultationsForDate = consultationsFacade.findByDay(selectedDate);
    }

    // Method to create a new consultation
    public void deleteConsultation(Consultations consultationToDelete) {
        consultationsFacade.remove(consultationToDelete);
        loadConsultationsByDate();
    }

    // Method to load consultations for the selected month
    public void loadConsultationsForMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedTime);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        consultationsForDate = consultationsFacade.findByMonthAndYear(month, year);
    }

    // Load consultations for the logged-in user
    public List<Consultations> loadUserConsultations() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Users user = (Users) session.getAttribute("loggedInUser");

        if (user != null) {
            userConsultations = consultationsFacade.findByUser(user.getUserID());
        }
        return userConsultations;
    }

    public void selectDate(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        selectedDate = calendar.getTime();
        loadConsultationsByDate(); // Load consultations for the selected date
    }

    public List<Consultations> getConsultationsForDate() {
        return consultationsForDate;
    }

    public void setConsultationsForDate(List<Consultations> consultationsForDate) {
        this.consultationsForDate = consultationsForDate;
    }

    public Consultations getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultations consultation) {
        this.consultation = consultation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getDesignerId() {
        return designerId;
    }

    public void setDesignerId(Long designerId) {
        this.designerId = designerId;
    }

    public int getConsultationsID() {
        return consultationsID;
    }

    public void setConsultationsID(int consultationsID) {
        this.consultationsID = consultationsID;
    }

    public int getDesignerID() {
        return designerID;
    }

    public void setDesignerID(int designerID) {
        this.designerID = designerID;
    }

    public Designer getLoggedInDesigner() {
        return loggedInDesigner;
    }

    public void setLoggedInDesigner(Designer loggedInDesigner) {
        this.loggedInDesigner = loggedInDesigner;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
