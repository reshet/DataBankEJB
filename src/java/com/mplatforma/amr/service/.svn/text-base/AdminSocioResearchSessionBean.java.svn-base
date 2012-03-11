/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatforma.amr.service;

import com.mplatforma.amr.service.remote.AdminSocioResearchBeanRemote;
import com.mplatforma.amr.service.remote.RxStorageBeanRemote;
import com.mplatforma.amr.service.remote.UserAccountBeanRemote;
import com.mplatrforma.amr.entity.Organization;
import com.mplatrforma.amr.entity.ResearchFilesAccessor;
import com.mplatrforma.amr.entity.SingleStringEntity;
import com.mplatrforma.amr.entity.SocioResearch;
import com.mplatrforma.amr.entity.UserAccount;
import com.mplatrforma.amr.entity.UserAccount2;
import com.mplatrforma.amr.entity.Var;
import com.mresearch.databank.jobs.IndexResearchJob;
import com.mresearch.databank.jobs.ParseSpssJob;
import com.mresearch.databank.shared.OrgDTO;
import com.mresearch.databank.shared.ResearchFilesDTO;
import com.mresearch.databank.shared.RxStoredDTO;
import com.mresearch.databank.shared.SocioResearchDTO;
import com.mresearch.databank.shared.UserAccountDTO;
import com.mresearch.databank.shared.VarDTO_Detailed;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.opendatafoundation.data.FileFormatInfo;
import org.opendatafoundation.data.FileFormatInfo.Format;
import org.opendatafoundation.data.mod.SPSSFile;
import org.opendatafoundation.data.mod.SPSSFileException;
import org.opendatafoundation.data.mod.SPSSNumericVariable;
import org.opendatafoundation.data.mod.SPSSStringVariable;
import org.opendatafoundation.data.mod.SPSSVariable;
import org.w3c.dom.Document;

/**
 *
 * @author reshet
 */
@WebService
@Stateless(mappedName="AdminSocioResearchRemoteBean",name="AdminSocioResearchRemoteBean")
public class AdminSocioResearchSessionBean implements AdminSocioResearchBeanRemote{

    static
    {
         Locale locale = Locale.getDefault();
           System.out.println("Before setting, Locale is = " + locale);
         locale = new Locale("ru","RU");
        //  // Setting default locale  
        // // locale = Locale.ITALY;
         Locale.setDefault(locale);
          System.out.println("After setting, Locale is = " + locale);
    }
    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private RxStorageBeanRemote store;
   
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
    public Boolean deleteResearch(long id) {
        try
        {
            SocioResearch r = em.find(SocioResearch.class, id);
            em.remove(r);
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
       // throw new UnsupportedOperationException("Not supported yet.");
    }

     private SocioResearch addResearch(SocioResearchDTO researchDTO) {
        SocioResearch research = null;
        try {
          research = new SocioResearch(researchDTO,em);
          em.persist(research);
          research.updateEntityID(research.getID(),research.getId_search_repres(), em);
         // currentUser.getFriends().add(friend);
        } finally {
        }
        return research;
      }
    @Override
    public SocioResearchDTO updateResearch(SocioResearchDTO rDTO) {
       // throw new UnsupportedOperationException("Not supported yet.");
            if (rDTO.getId() == 0){ // create new
              SocioResearch newResearch = addResearch(rDTO);
              return newResearch.toDTO();
            }

            SocioResearch research = null;
            try {
              research = em.find(SocioResearch.class, rDTO.getId());
              research.updateFromDTO(rDTO,em);
              addSSE("SocioResearch","gengeath", rDTO.getGen_geathering());
              addSSE("SocioResearch","method", rDTO.getMethod());
              ArrayList<String> concepts = rDTO.getConcepts();
              if(concepts!=null && concepts.size()>0)
              {
                  for(String concept:concepts)
                  {
                          addSSE("SocioResearch","concept", concept);
                  }  
              }
              ArrayList<String> researchers = rDTO.getResearchers();
              if(researchers!=null && researchers.size()>0)
              {
                  for(String researcher:researchers)
                  {
                          addSSE("SocioResearch","researcher", researcher);
                  }
              }
              addSSE("SocioResearch","org_impl", rDTO.getOrg_impl_name());
              addSSE("SocioResearch","org_order", rDTO.getOrg_order_name());
              ArrayList<String> pubs = rDTO.getPublications();
              if(pubs!=null && pubs.size()>0)
              {
                  for(String pub:pubs)
                  {
                          addSSE("SocioResearch","publication", pub);
                  }
              }
              addSSE("SocioResearch","selection_complexity", rDTO.getSel_complexity());
              addSSE("SocioResearch","selection_randomity", rDTO.getSel_randomity());
              
              launchIndexing(rDTO);
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
            }
            
            return rDTO;

    }

    
    @Resource(mappedName="jms/myQCF")
    private  QueueConnectionFactory connectionFactory;

    @Resource(mappedName="jms/spss_parse")
    private  Queue queue;
    
//    @Resource(mappedName="jms/ES_index")
//    private  Queue index_queue;
    
    private void launchIndexing(SocioResearchDTO dto)
    {
         try {
            
            QueueConnection connection = connectionFactory.createQueueConnection();
            QueueSession session = connection.createQueueSession(false, 0);
            QueueSender q_sender = session.createSender(queue);

            ObjectMessage message = session.createObjectMessage();
            message.setStringProperty("title", "command to index SocioResearch");
            // here we create NewsEntity, that will be sent in JMS message
           // ParseSpssJob job = new ParseSpssJob(blobkey, length);
            IndexResearchJob job = new IndexResearchJob(dto.getId());
            message.setObject(job);    
            message.setJMSDestination(queue);
            q_sender.send(message);
            q_sender.close();
            connection.close();
            //response.sendRedirect("ListNews");

        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public long parseSPSS(long blobkey, long length) {
        
        try {
            
            QueueConnection connection = connectionFactory.createQueueConnection();
            QueueSession session = connection.createQueueSession(false, 0);
            QueueSender q_sender = session.createSender(queue);

            ObjectMessage message = session.createObjectMessage();
            message.setStringProperty("title", "command to parse SPSS file");
            // here we create NewsEntity, that will be sent in JMS message
            ParseSpssJob job = new ParseSpssJob(blobkey, length);
          
            message.setObject(job);   
            message.setJMSDestination(queue);
            q_sender.send(message);
            q_sender.close();
            connection.close();
            //response.sendRedirect("ListNews");

        } catch (JMSException ex) {
            ex.printStackTrace();
        }
            
	return 0;
    }

   
    @Override
    public SocioResearchDTO updateResearchGrouped(SocioResearchDTO rDTO) {
        if (rDTO.getId() == 0){ // create new
            return rDTO;
        }

        SocioResearch research = null;
        try {
          research = em.find(SocioResearch.class, rDTO.getId());
          research.updateFromDTOGrouped(rDTO,em);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
            
        }
        return rDTO;
    }

    @Override
    public VarDTO_Detailed generalizeVar(long var_id, ArrayList<Long> gen_var_ids,UserAccountDTO user) {
            VarDTO_Detailed detailed = null;
	    Var dsVar, detached;

	    try {
	      dsVar = em.find(Var.class, var_id);
	      dsVar.setGeneralized_var_ids(gen_var_ids);
	     // UserAccountDTO watching_user = (UserAccountDTO) this.getThreadLocalRequest().getSession().getAttribute("user");
	      detailed = dsVar.toDTO_Detailed(user,em);
	    } finally {
	    }
	    
	return detailed;
    }

    @Override
    public long addOrgImpl(OrgDTO dto) {
          Organization org = null;
	  long org_id = 0;
	    try {
	      org = new Organization(dto);
	      em.persist(org);
	      org_id = org.getId();
	    } finally {
	    }
	 return org_id;
    }

    @Override
    public Boolean addFileToAccessor(long id_research, long id_file, String desc, String category) {
	    SocioResearch dsResearch, detached;
            try {
	      dsResearch = em.find(SocioResearch.class, id_research);
	      ResearchFilesDTO dto = dsResearch.toFilesDTO(em);
	      dto.addFile(category, desc, id_file);
	      dsResearch.updateFileAccessor(dto);
	    } finally {
	    }
	 return true;
    }

    @Override
    public Boolean deleteFileFromAccessor(long id_research, long id_file) {
	    SocioResearch dsResearch, detached;
	    try {
	      dsResearch = em.find(SocioResearch.class, id_research);
	      ResearchFilesDTO dto = dsResearch.toFilesDTO(em);
	      dto.deleteFile(id_file);
	      dsResearch.updateFileAccessor(dto);
	      return store.deleteFile(id_file);
	    } finally {
                return false;
	    }
    }

    @Override
    public Boolean addSSE(String clas, String kind, String value) {
       SingleStringEntity entity = null;
        try {
          // for this version of the app, just get hardwired 'default' user
          //UserAccount currentUser = UserAccount.getDefaultUser(); // detached object
          //currentUser = pm.makePersistent(currentUser); // attach
            if(value!=null && !value.equals(""))
            {
                List<SingleStringEntity> res = SingleStringEntity.getMatchingFull(em, clas, kind, value);
                if(res.isEmpty())
                {
                   entity = new SingleStringEntity();
                   entity.setClas(clas);
                   entity.setKind(kind);
                   entity.setContents(value);
                   em.persist(entity);
                }
            }
            return true;
        } finally {
            return false;
        }
    }

    @Override
    public Boolean updateFileAccessor(long research_id, ResearchFilesDTO dto) {
        SocioResearch research = null;
        ResearchFilesAccessor accessor;
        try {
          research = em.find(SocioResearch.class, research_id);
          accessor = em.find(ResearchFilesAccessor.class,research.getFile_accessor_id());
          accessor.updateFromDTO(dto);
          return true;
        } catch (Exception e) {
              e.printStackTrace();
        } finally {
            return false;
        }
    }
 
}
