
package com.entitybeans;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "FeedbackDes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FeedbackDes.findAll", query = "SELECT f FROM FeedbackDes f"),
    @NamedQuery(name = "FeedbackDes.findByFeedbackDesID", query = "SELECT f FROM FeedbackDes f WHERE f.feedbackDesID = :feedbackDesID"),
    @NamedQuery(name = "FeedbackDes.findByContent", query = "SELECT f FROM FeedbackDes f WHERE f.content = :content"),
    @NamedQuery(name = "FeedbackDes.findByRating", query = "SELECT f FROM FeedbackDes f WHERE f.rating = :rating")})
public class FeedbackDes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "feedbackDesID")
    private Integer feedbackDesID;
    @Size(max = 255)
    @Column(name = "content")
    private String content;
    @Column(name = "rating")
    private Integer rating;
    @JoinColumn(name = "DesignerID", referencedColumnName = "DesignerID")
    @ManyToOne
    private Designer designerID;
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @ManyToOne
    private Users userID;

    public FeedbackDes() {
    }

    public FeedbackDes(Integer feedbackDesID) {
        this.feedbackDesID = feedbackDesID;
    }

    public Integer getFeedbackDesID() {
        return feedbackDesID;
    }

    public void setFeedbackDesID(Integer feedbackDesID) {
        this.feedbackDesID = feedbackDesID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Designer getDesignerID() {
        return designerID;
    }

    public void setDesignerID(Designer designerID) {
        this.designerID = designerID;
    }

    public Users getUserID() {
        return userID;
    }

    public void setUserID(Users userID) {
        this.userID = userID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (feedbackDesID != null ? feedbackDesID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FeedbackDes)) {
            return false;
        }
        FeedbackDes other = (FeedbackDes) object;
        if ((this.feedbackDesID == null && other.feedbackDesID != null) || (this.feedbackDesID != null && !this.feedbackDesID.equals(other.feedbackDesID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.entitybeans.FeedbackDes[ feedbackDesID=" + feedbackDesID + " ]";
    }
    
}
