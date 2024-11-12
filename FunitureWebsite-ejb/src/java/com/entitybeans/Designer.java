
package com.entitybeans;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "Designer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Designer.findAll", query = "SELECT d FROM Designer d"),
    @NamedQuery(name = "Designer.findByDesignerID", query = "SELECT d FROM Designer d WHERE d.designerID = :designerID"),
    @NamedQuery(name = "Designer.findByFirstName", query = "SELECT d FROM Designer d WHERE d.firstName = :firstName"),
    @NamedQuery(name = "Designer.findByLastName", query = "SELECT d FROM Designer d WHERE d.lastName = :lastName"),
    @NamedQuery(name = "Designer.findByPhone", query = "SELECT d FROM Designer d WHERE d.phone = :phone"),
    @NamedQuery(name = "Designer.findByExperience", query = "SELECT d FROM Designer d WHERE d.experience = :experience"),
    @NamedQuery(name = "Designer.findByUsername", query = "SELECT d FROM Designer d WHERE d.username = :username"),
    @NamedQuery(name = "Designer.findByPassword", query = "SELECT d FROM Designer d WHERE d.password = :password")})
public class Designer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DesignerID")
    private Integer designerID;
    @Size(max = 255)
    @Column(name = "FirstName")
    private String firstName;
    @Size(max = 255)
    @Column(name = "LastName")
    private String lastName;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "phone")
    private String phone;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "address")
    private String address;
    @Column(name = "experience")
    private Integer experience;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "image")
    private String image;
    @Size(max = 20)
    @Column(name = "Username")
    private String username;
    @Size(max = 20)
    @Column(name = "Password")
    private String password;

    public Designer() {
    }

    public Designer(Integer designerID) {
        this.designerID = designerID;
    }

    public Integer getDesignerID() {
        return designerID;
    }

    public void setDesignerID(Integer designerID) {
        this.designerID = designerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (designerID != null ? designerID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Designer)) {
            return false;
        }
        Designer other = (Designer) object;
        if ((this.designerID == null && other.designerID != null) || (this.designerID != null && !this.designerID.equals(other.designerID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.entitybeans.Designer[ designerID=" + designerID + " ]";
    }
    
}
