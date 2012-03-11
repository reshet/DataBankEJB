/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatforma.amr.service.remote;

import javax.ejb.Remote;

/**
 *
 * @author reshet
 */
@Remote
public interface AmrBeanRemote {

    long getBookCount();
    
}
