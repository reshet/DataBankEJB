/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatforma.amr.service;

import com.mplatrforma.amr.entity.Account;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author reshet
 */
@Stateless
public class AmrEAO implements AmrEAOLocal {
    @PersistenceContext
    EntityManager em;
    
    public AmrEAO()
    {
    }

    @Override
    public long getBooks() {
        long res = 223;
        Query q = em.createQuery("Select count(a) from Account a");
        res = (Long)q.getSingleResult();
        return res;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createAccount() {
        //em.getTransaction().begin();
        Account acc = new Account();
        acc.setPassword("karasik009");
        acc.setUsername("karasik");
        em.persist(acc);
        //em.getTransaction().commit();
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void main(String[] args)
    {
//        EntityManagerFactory factory = Persistence.createEntityManagerFactory("DatabankEnterprise-ejbPU");
//	EntityManager em = factory.createEntityManager();
//         em.getTransaction().begin();
//        Account acc = new Account();
//        acc.setPassword("karasik009");
//        acc.setUsername("karasik");
//        em.persist(acc);
//        em.getTransaction().commit();
//        
//        Query q = em.createQuery("select t from UserAccount t");
//		List<Account> todoList = q.getResultList();
//		for (Account tod : todoList) {
//			System.out.println(tod);
//		}
//		System.out.println("Size: " + todoList.size());
//		
//		em.close();
    }
}
