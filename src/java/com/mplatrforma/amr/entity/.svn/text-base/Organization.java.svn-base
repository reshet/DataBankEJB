/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatrforma.amr.entity;

import com.mresearch.databank.shared.OrgDTO;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author reshet
 */
@Entity
public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String address;	
    private String telephone;
    public String getName() {
            return name;
    }
    public void setName(String name) {
            this.name = name;
    }
    public String getAddress() {
            return address;
    }


    public void setAddress(String address) {
            this.address = address;
    }
    public String getTelephone() {
            return telephone;
    }
    public void setTelephone(String telephone) {
            this.telephone = telephone;
    }
    public Organization()
    {}
    public Organization(OrgDTO dto)
    {
            this.name = dto.getName();
            this.address = dto.getAdress();
            this.telephone = dto.getTel();
    }	
    public OrgDTO toDTO()
    {
            OrgDTO dto = new OrgDTO(this.name,this.address,this.telephone);
            dto.setId(this.getId());
            return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Organization)) {
            return false;
        }
        Organization other = (Organization) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mplatrforma.amr.entity.Organization[ id=" + id + " ]";
    }
    
}
