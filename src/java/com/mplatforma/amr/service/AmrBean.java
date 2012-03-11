/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatforma.amr.service;

import com.mplatforma.amr.service.remote.AmrBeanRemote;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.faces.bean.ManagedBean;
import javax.jws.WebService;


/**
 *
 * @author reshet
 */
//@Remote
@WebService
@Stateless(mappedName="myRemoteBean",name="myRemoteBean")
@ManagedBean(name="myRemoteBean")
public class AmrBean implements AmrBeanRemote {
    @EJB AmrEAOLocal eao;
    public AmrBean()
    {
        //eao.createAccount();
    }
    
    
    
    @Override
    public long getBookCount()
    {
        eao.createAccount();
        return eao.getBooks();
        //return 22;
    }
    
}
