/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatrforma.amr.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author reshet
 */
@Entity
@Table(name = "SOMERESEARCH")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Someresearch.findAll", query = "SELECT s FROM Someresearch s"),
    @NamedQuery(name = "Someresearch.findById", query = "SELECT s FROM Someresearch s WHERE s.id = :id"),
    @NamedQuery(name = "Someresearch.findByName", query = "SELECT s FROM Someresearch s WHERE s.name = :name")})
public class Someresearch implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 200)
    @Column(name = "NAME")
    private String name;

    public Someresearch() {
    }

    public Someresearch(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof Someresearch)) {
            return false;
        }
        Someresearch other = (Someresearch) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mplatrforma.amr.entity.Someresearch[ id=" + id + " ]";
    }
    
}
