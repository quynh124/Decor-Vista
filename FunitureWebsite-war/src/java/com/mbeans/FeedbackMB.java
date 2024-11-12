package com.mbeans;

import com.entitybeans.Designer;
import com.entitybeans.Feedback;
import com.entitybeans.FeedbackDes;
import com.entitybeans.Products;
import com.entitybeans.Users;
import com.sessionbeans.FeedbackDesFacadeLocal;
import com.sessionbeans.FeedbackFacadeLocal;
import com.sessionbeans.FeedbackFacadeLocal;
import com.sessionbeans.ProductsFacadeLocal;
import com.sessionbeans.UsersFacadeLocal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import org.primefaces.PrimeFaces;

@Named(value = "feedbackMB")
@RequestScoped
public class FeedbackMB {

    @EJB
    private FeedbackDesFacadeLocal feedbackDesFacade;

    @EJB
    private ProductsFacadeLocal productsFacade;

    @EJB
    private UsersFacadeLocal usersFacade;

    @EJB
    private FeedbackFacadeLocal feedbackFacade;
    private List<Feedback> feedbackList;
    private Feedback feedbacks;
    private String selectedProductID;
    private String targetType;  // For select dropdown value
    private int rating;
    private String targetId;
    private String feedbackType;
    private String comment;
    private FeedbackDes feedbackDes;
    private Map<String, Double> productRatings = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            loadAverageRatings();
        } catch (Exception e) {
            System.err.println("Error initializing FeedbackMB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public FeedbackMB() {
        feedbacks = new Feedback();
        feedbackDes = new FeedbackDes();

    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public void loadFeedbackForProduct() {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedProductID = (String) context.getExternalContext().getSessionMap().get("selectedProductID");

        if (selectedProductID != null) {
            feedbackList = feedbackFacade.findFeedbackByProductId(selectedProductID);
            System.out.println("Loaded feedback for product ID: " + selectedProductID);
        } else {
            System.out.println("selectedProductID is null");
        }
    }

    public void submitFeedbackForService() {
        FacesContext context = FacesContext.getCurrentInstance();
        Users loggedInUser = (Users) context.getExternalContext().getSessionMap().get("loggedInUser");

        if (loggedInUser != null) {
            Feedback feedback = new Feedback();
            feedback.setUserID(loggedInUser);
            feedback.setRating(rating);
            feedback.setComment(comment);
            feedback.setTargetType("service");

            try {
                feedbackFacade.create(feedback);
                context.addMessage(null, new FacesMessage("Feedback submitted successfully!")); // Thông báo thành công
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error submitting feedback", null)); // Thông báo lỗi
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You must be logged in to submit feedback.", null)); // Thông báo người dùng chưa đăng nhập
        }
    }

    public void submitFeedbackForProduct() {
        FacesContext context = FacesContext.getCurrentInstance();
        Users loggedInUser = (Users) context.getExternalContext().getSessionMap().get("loggedInUser");
        if (loggedInUser != null) {
            Feedback feedback = new Feedback();
            feedback.setUserID(loggedInUser);
            feedback.setRating(rating);
            feedback.setComment(comment);
            feedback.setTargetType("product");
            String productID = (String) context.getExternalContext().getSessionMap().get("selectedProductID");

            if (productID != null) {

                Products targetProduct = productsFacade.find(productID);
                if (targetProduct != null) {
                    feedback.setTargetId(targetProduct);
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Product not found.", null));
                    return;
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Product ID is null.", null));
                return;
            }
            try {
                feedbackFacade.create(feedback);
                context.addMessage(null, new FacesMessage("Feedback submitted successfully!"));
            } catch (NumberFormatException e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Product ID.", null));
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error submitting feedback", null));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You must be logged in to submit feedback.", null));
        }
    }

    public Products findProductById(String productId) {
        return productsFacade.findProductById(productId); // Gọi phương thức từ facade
    }

    public String deleteFeedbacks(int id) {
        feedbackFacade.remove(feedbackFacade.find(id));
        return "feedback";
    }

    public String deleteFeedback(int feedbackId) {
        try {
            Feedback feedbackToDelete = feedbackFacade.find(feedbackId);
            if (feedbackToDelete != null) {
                // Xóa Feedback từ cơ sở dữ liệu
                feedbackFacade.remove(feedbackToDelete);

                // Thông báo thành công
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Feedback deleted successfully."));
                return "feedback"; // Điều hướng trang thành công
            } else {
                // Nếu không tìm thấy Feedback
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Feedback not found.", null));
                return null;
            }
        } catch (PersistenceException e) {
            // Thông báo lỗi khóa ngoại bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('Cannot delete feedback due to foreign key constraint.');");
            return null;
        } catch (Exception e) {
            // Thông báo lỗi khác bằng alert JavaScript qua PrimeFaces
            PrimeFaces.current().executeScript("alert('An error occurred while deleting the feedback.');");
            return null;
        }
    }

    public List<Feedback> showAllFeedbacks() {
        return feedbackFacade.findAll();
    }

    public String resetForm() {
        feedbacks = null;
        return "feedback";
    }

    public Feedback getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Feedback feedbacks) {
        this.feedbacks = feedbacks;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public FeedbackFacadeLocal getFeedbackFacade() {
        return feedbackFacade;
    }

    public void setFeedbackFacade(FeedbackFacadeLocal feedbackFacade) {
        this.feedbackFacade = feedbackFacade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public FeedbackDes getFeedbackDes() {
        return feedbackDes;
    }

    public void setFeedbackDes(FeedbackDes feedbackDes) {
        this.feedbackDes = feedbackDes;
    }

    public List<FeedbackDes> loadFeedbackForDesigner() {
        FacesContext context = FacesContext.getCurrentInstance();
        Designer TempDesigner = (Designer) context.getExternalContext().getSessionMap().get("selectedDesigner");
        return feedbackDesFacade.getFeedbackByDesigner(TempDesigner.getDesignerID());
    }

    public FeedbackFacadeLocal getFeedbacksFacade() {
        return feedbackFacade;
    }

    public void setFeedbacksFacade(FeedbackFacadeLocal feedbacksFacade) {
        this.feedbackFacade = feedbacksFacade;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }
    private List<FeedbackDes> feedbackDesList;

    public List<FeedbackDes> getFeedbackDesList() {
        return feedbackDesList;
    }

    public void setFeedbackDesList(List<FeedbackDes> feedbackDesList) {
        this.feedbackDesList = feedbackDesList;
    }

    public String submitFeedback() {
        try {
            Users user = usersFacade.find(1); // Retrieve the currently logged-in user
            FacesContext context = FacesContext.getCurrentInstance();
            Designer selectedDesigner = (Designer) context.getExternalContext().getSessionMap().get("selectedDesigner");

            feedbackDes.setUserID(user);
            feedbackDes.setDesignerID(selectedDesigner);

            feedbackDesFacade.create(feedbackDes); // Save feedback
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Feedback submitted successfully."));
            feedbackDes = new FeedbackDes();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error submitting feedback", null));
        }
        return null;
    }

    public void loadAverageRatings() {
        try {
            List<Object[]> ratings = feedbackFacade.getAverageRatingsForProducts();
            for (Object[] rating : ratings) {
                String productId = (String) rating[0];
                Object avgRating = rating[2];  // Might be Integer or Double

                Double averageRating = (avgRating instanceof Integer)
                        ? ((Integer) avgRating).doubleValue()
                        : (Double) avgRating;

                productRatings.put(productId, averageRating != null ? averageRating : 0.0);
            }
        } catch (Exception e) {
            System.err.println("Error loading average ratings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public double getAverageRatingForProduct(String productId) {
        return productRatings.getOrDefault(productId, 0.0);
    }

}
