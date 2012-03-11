/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatforma.amr.service;

import com.mplatforma.amr.service.remote.UserAccountBeanRemote;
import com.mplatrforma.amr.entity.UserAccount;
import com.mplatrforma.amr.entity.UserAccount2;
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
@Stateless(mappedName="UserAccountRemoteBean",name="UserAccountRemoteBean")
public class UserAccountSessionBean implements UserAccountBeanRemote{

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public UserAccountDTO getUserAccount(String email, String password) {
        return UserAccount.toDTO(new UserAccount(em).getUserAccount(email, password));
    }
    

    @Override
    public UserAccountDTO updateAccountResearchState(UserAccountDTO dto) {
        UserAccount account;
        UserAccountDTO returnBack = dto;
        account = em.find(UserAccount.class,dto.getId());
        if (account != null)
        {
                account.updateAccountResearchState(dto);
                returnBack = UserAccount.toDTO(account);
        }
        return returnBack;
    }

    @Override
    public void initDefaults() {
        new UserAccount(em).createDefaults();
    }

    @Override
    public UserAccountDTO getDefaultUser() {
        return UserAccount.toDTO(new UserAccount(em).getDefaultUser());
    }
    
}
