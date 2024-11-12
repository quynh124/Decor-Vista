
package com.mbeans;

import com.entitybeans.Designer;
import com.entitybeans.FeedbackDes;
import com.sessionbeans.DesignerFacadeLocal;
import com.sessionbeans.FeedbackDesFacadeLocal;
import com.sessionbeans.UsersFacadeLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named(value = "feedbackdesMB")
@RequestScoped
public class FeedbackDesMB {
    @EJB
    private FeedbackDesFacadeLocal feedbackDesFacade;
    
    @EJB
    private DesignerFacadeLocal designerFacade;
    
    @EJB
    private UsersFacadeLocal usersFacade;
    
    private FeedbackDes feedbackDes;
    private String content;
    private int feedbackDesID;
    private int rating;
    private int userID;
    private int designerID;
    private List<FeedbackDes> feedbackList;
     
     
   public List<FeedbackDes> showAllFeedDesigners() {
        return feedbackDesFacade.findAll();
    }
    public List<FeedbackDes> getFeedbackByLoggedInDesigner() {
        FacesContext context = FacesContext.getCurrentInstance();
        Designer loggedInDesigner = (Designer) context.getExternalContext().getSessionMap().get("loggedInDesigner");
        
        if (loggedInDesigner != null) {
            return feedbackDesFacade.findByDesignerID(loggedInDesigner.getDesignerID());
        }
        return new ArrayList<>();
    }
   
    public FeedbackDesFacadeLocal getFeedbackDesFacade() {
        return feedbackDesFacade;
    }

    public void setFeedbackDesFacade(FeedbackDesFacadeLocal feedbackDesFacade) {
        this.feedbackDesFacade = feedbackDesFacade;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFeedbackDesID() {
        return feedbackDesID;
    }

    public void setFeedbackDesID(int feedbackDesID) {
        this.feedbackDesID = feedbackDesID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDesignerID() {
        return designerID;
    }

    public void setDesignerID(int designerID) {
        this.designerID = designerID;
    }

    public DesignerFacadeLocal getDesignerFacade() {
        return designerFacade;
    }

    public void setDesignerFacade(DesignerFacadeLocal designerFacade) {
        this.designerFacade = designerFacade;
    }

    public FeedbackDes getFeedbackDes() {
        return feedbackDes;
    }

    public void setFeedbackDes(FeedbackDes feedbackDes) {
        this.feedbackDes = feedbackDes;
    }

    public UsersFacadeLocal getUsersFacade() {
        return usersFacade;
    }

    public void setUsersFacade(UsersFacadeLocal usersFacade) {
        this.usersFacade = usersFacade;
    }

    public List<FeedbackDes> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<FeedbackDes> feedbackList) {
        this.feedbackList = feedbackList;
    }
}
