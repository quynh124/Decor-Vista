///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.mbeans;
//
//import com.entitybeans.Availability;
//import com.entitybeans.Consultations;
//import com.entitybeans.Designer;
//import com.entitybeans.Users;
//import com.sessionbeans.AvailabilityFacadeLocal;
//import com.sessionbeans.ConsultationsFacadeLocal;
//import com.sessionbeans.DesignerFacadeLocal;
//import java.io.Serializable;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import javax.annotation.PostConstruct;
//import javax.ejb.EJB;
//import javax.inject.Named;
//import javax.enterprise.context.SessionScoped;
//import javax.faces.context.FacesContext;
//import javax.xml.registry.infomodel.User;
//
//@Named(value = "calendarMB")
//@SessionScoped
//public class CalendarMB implements Serializable {
//
//    @EJB
//    private DesignerFacadeLocal designerFacade;
//
//    @EJB
//    private AvailabilityFacadeLocal availabilityFacade;
//
//    @EJB
//    private ConsultationsFacadeLocal consultationsFacade;
//
//    private Designer designer;
//    private List<Availability> availabilityList;
//    private List<Consultations> consultationsList;
//
//    // Variables for adding new events
//    private String eventTitle;
//    private Date startDate;
//    private Date endDate;
//    private String label;
//    private String description;
//
//    public CalendarMB() {
//    }
//
//    @PostConstruct
//    public void init() {
//        loadDesignerData();
//    }
//
//    private void loadDesignerData() {
//        // Retrieve designerID from session
//        Object designerIDObj = FacesContext.getCurrentInstance()
//                .getExternalContext()
//                .getSessionMap()
//                .get("designerID");
//
//        if (designerIDObj != null) {
//            Integer designerID = (Integer) designerIDObj;
//            designer = designerFacade.find(designerID);
//            if (designer != null) {
//                availabilityList = availabilityFacade.findByDesigner(designer);
//                consultationsList = consultationsFacade.findByDesigner(designer);
//            } else {
//                availabilityList = new ArrayList<>();
//                consultationsList = new ArrayList<>();
//            }
//        } else {
//            // Handle case where designerID is not found in session
//            availabilityList = new ArrayList<>();
//            consultationsList = new ArrayList<>();
//        }
//    }
//
//    // Method to add new availability or consultation
//    public String addEvent() {
//        // Implement logic to add event based on label or other criteria
//        // For example, if label is "Available", add to Availability
//        // If label is "Appointment", add to Consultations
//        // This is a simplified example:
//
//        if ("available".equalsIgnoreCase(label)) {
//            Availability availability = new Availability();
//            availability.setDesignerID(designer);
//            availability.setStartTime(startDate);
//            availability.setEndTime(endDate);
//            availabilityFacade.create(availability);
//            availabilityList.add(availability);
//        } else if ("appointment".equalsIgnoreCase(label)) {
//            Consultations consultation = new Consultations();
//            consultation.setDesignerID(designer);
//            consultation.setUserID(getCurrentUser()); // Implement method to get current user
//            consultation.setDay(startDate); // Assuming day represents the date of appointment
//            consultation.setScheduleddatetime(startDate);
//            consultation.setNotes(description);
//            consultationsFacade.create(consultation);
//            consultationsList.add(consultation);
//        }
//
//        // Clear form fields
//        eventTitle = "";
//        startDate = null;
//        endDate = null;
//        label = "";
//        description = "";
//
//        return "calendar?faces-redirect=true"; // Navigate to calendar page
//    }
//
//    // Implement a method to get the current user
//    private Users getCurrentUser() {
//        // Retrieve current user from session or security context
//        // Placeholder implementation:
//        return (Users) FacesContext.getCurrentInstance()
//                .getExternalContext()
//                .getSessionMap()
//                .get("currentUser");
//    }
//
//    // Getters and Setters for all fields
//
//    public Designer getDesigner() {
//        return designer;
//    }
//
//    public void setDesigner(Designer designer) {
//        this.designer = designer;
//    }
//
//    public List<Availability> getAvailabilityList() {
//        return availabilityList;
//    }
//
//    public void setAvailabilityList(List<Availability> availabilityList) {
//        this.availabilityList = availabilityList;
//    }
//
//    public List<Consultations> getConsultationsList() {
//        return consultationsList;
//    }
//
//    public void setConsultationsList(List<Consultations> consultationsList) {
//        this.consultationsList = consultationsList;
//    }
//
//    public String getEventTitle() {
//        return eventTitle;
//    }
//
//    public void setEventTitle(String eventTitle) {
//        this.eventTitle = eventTitle;
//    }
//
//    public Date getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(Date startDate) {
//        this.startDate = startDate;
//    }
//
//    public Date getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(Date endDate) {
//        this.endDate = endDate;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//
//    public void setLabel(String label) {
//        this.label = label;
//    }
//    
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//}