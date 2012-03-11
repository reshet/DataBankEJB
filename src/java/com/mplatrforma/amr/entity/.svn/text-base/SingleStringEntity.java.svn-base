/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatrforma.amr.entity;

import com.mresearch.databank.shared.SSE_DTO;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

/**
 *
 * @author reshet
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "SSE.getMatchingFull", query = "SELECT x FROM SingleStringEntity x WHERE x.clas = :cl AND x.kind = :kd AND x.contents = :vl"),
    @NamedQuery(name = "SSE.getMatching", query = "SELECT DISTINCT x FROM SingleStringEntity x WHERE x.clas = :cl AND x.kind = :kd")
})
public class SingleStringEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String clas;
    private String kind;
    private String contents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public static List<SingleStringEntity> getMatchingFull(EntityManager em, String clas, String kind, String value)
    {
        TypedQuery<SingleStringEntity> q = em.createNamedQuery("SSE.getMatchingFull", SingleStringEntity.class);
        q.setParameter("cl", clas);
        q.setParameter("kd", kind);
        q.setParameter("vl", value);
        
        List<SingleStringEntity> l = q.getResultList();
        return l;
    }
    public static List<SingleStringEntity> getMatching(EntityManager em, String clas, String kind)
    {
        TypedQuery<SingleStringEntity> q = em.createNamedQuery("SSE.getMatching", SingleStringEntity.class);
        q.setParameter("cl", clas);
        q.setParameter("kd", kind);
        List<SingleStringEntity> l = q.getResultList();
        return l;
    }
    public SingleStringEntity()
    {}
    public SingleStringEntity(SSE_DTO dto)
    {
            this.kind = dto.getKind();
            this.clas = dto.getClas();
            this.setContents(dto.getContents());
    }	
    public String getClas() {
            return clas;
    }
    public void setClas(String clas) {
            this.clas = clas;
    }
    public SSE_DTO toDTO()
    {
            SSE_DTO dto = new SSE_DTO(clas,kind,contents);
            dto.setId(this.getId());
            return dto;
    }
    public String getContents() {
            return contents;
    }
    public void setContents(String contents) {
            this.contents = contents;
    }

    public String getKind() {
            return kind;
    }
    public void setKind(String kind) {
            this.kind = kind;
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
        if (!(object instanceof SingleStringEntity)) {
            return false;
        }
        SingleStringEntity other = (SingleStringEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mplatrforma.amr.entity.SingleStringEntity[ id=" + id + " ]";
    }
    
}
