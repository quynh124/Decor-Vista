
package com.entitybeans;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "Subcategories")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Subcategories.findAll", query = "SELECT s FROM Subcategories s"),
    @NamedQuery(name = "Subcategories.findBySubcategoryID", query = "SELECT s FROM Subcategories s WHERE s.subcategoryID = :subcategoryID"),
    @NamedQuery(name = "Subcategories.findBySubcategoryName", query = "SELECT s FROM Subcategories s WHERE s.subcategoryName = :subcategoryName")})
public class Subcategories implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "SubcategoryID")
    private String subcategoryID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "SubcategoryName")
    private String subcategoryName;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "Description")
    private String description;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "Image")
    private String image;
    @OneToMany(mappedBy = "subcategoryID")
    private Collection<Products> productsCollection;
    @JoinColumn(name = "CategoryID", referencedColumnName = "CategoryID")
    @ManyToOne
    private Categories categoryID;

    public Subcategories() {
    }

    public Subcategories(String subcategoryID) {
        this.subcategoryID = subcategoryID;
    }

    public Subcategories(String subcategoryID, String subcategoryName) {
        this.subcategoryID = subcategoryID;
        this.subcategoryName = subcategoryName;
    }

    public String getSubcategoryID() {
        return subcategoryID;
    }

    public void setSubcategoryID(String subcategoryID) {
        this.subcategoryID = subcategoryID;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
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

    @XmlTransient
    public Collection<Products> getProductsCollection() {
        return productsCollection;
    }

    public void setProductsCollection(Collection<Products> productsCollection) {
        this.productsCollection = productsCollection;
    }

    public Categories getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Categories categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (subcategoryID != null ? subcategoryID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Subcategories)) {
            return false;
        }
        Subcategories other = (Subcategories) object;
        if ((this.subcategoryID == null && other.subcategoryID != null) || (this.subcategoryID != null && !this.subcategoryID.equals(other.subcategoryID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.entitybeans.Subcategories[ subcategoryID=" + subcategoryID + " ]";
    }
    
}
