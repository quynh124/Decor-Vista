
package com.entitybeans;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "ProjectDesigner")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProjectDesigner.findAll", query = "SELECT p FROM ProjectDesigner p"),
    @NamedQuery(name = "ProjectDesigner.findByProjectID", query = "SELECT p FROM ProjectDesigner p WHERE p.projectID = :projectID"),
    @NamedQuery(name = "ProjectDesigner.findByProjectName", query = "SELECT p FROM ProjectDesigner p WHERE p.projectName = :projectName")})
public class ProjectDesigner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ProjectID")
    private Integer projectID;
    @Size(max = 255)
    @Column(name = "ProjectName")
    private String projectName;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "Description")
    private String description;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "Image")
    private String image;
    @JoinColumn(name = "DesignerID", referencedColumnName = "DesignerID")
    @ManyToOne
    private Designer designerID;

    public ProjectDesigner() {
    }

    public ProjectDesigner(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public void setImage(String image) {
        this.image = image;
    }

    public Designer getDesignerID() {
        return designerID;
    }

    public void setDesignerID(Designer designerID) {
        this.designerID = designerID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectID != null ? projectID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectDesigner)) {
            return false;
        }
        ProjectDesigner other = (ProjectDesigner) object;
        if ((this.projectID == null && other.projectID != null) || (this.projectID != null && !this.projectID.equals(other.projectID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.entitybeans.ProjectDesigner[ projectID=" + projectID + " ]";
    }
    
}
