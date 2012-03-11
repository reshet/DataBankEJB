/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatforma.amr.service;

import com.mplatforma.amr.service.remote.RxStorageBeanRemote;
import com.mplatforma.amr.service.remote.UserAccountBeanRemote;
import com.mplatrforma.amr.entity.RxBlobStored;
import com.mplatrforma.amr.entity.UserAccount;
import com.mplatrforma.amr.entity.UserAccount2;
import com.mresearch.databank.shared.RxStoredDTO;
import com.mresearch.databank.shared.UserAccountDTO;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author reshet
 */
@WebService
@Stateless(mappedName="RxStorageRemoteBean",name="RxStorageRemoteBean")
public class RxStorageSessionBean implements RxStorageBeanRemote{

    @PersistenceContext
    private EntityManager em;
    
    public EntityManager getEM()
    {
        return em;
    }
    @Override
    public long storeFile(byte[] cont, String name, String desc) {
        RxBlobStored blob = new RxBlobStored(cont, name, desc);
        em.persist(blob);
        
        return blob.getId();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] getFileContents(long id) {
        RxBlobStored stored = em.find(RxBlobStored.class, id);
        return stored.getContents();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RxStoredDTO getFileInfo(long id) {
        RxBlobStored stored = em.find(RxBlobStored.class, id);
        return stored.toDTO();
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean deleteFile(long file_id) {
         try
         {
            RxBlobStored stored = em.find(RxBlobStored.class, file_id);
            em.remove(stored);
            return true;
         }
         finally
         {
             return false;
         }
    }
    
   
    
}
