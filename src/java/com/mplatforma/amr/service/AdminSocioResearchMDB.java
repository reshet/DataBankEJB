/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatforma.amr.service;

import com.mresearch.databank.jobs.IndexResearchJob;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.client.Client;
import com.mplatforma.amr.service.remote.RxStorageBeanRemote;
import com.mplatforma.amr.service.remote.UserSocioResearchBeanRemote;
import com.mplatrforma.amr.entity.SocioResearch;
import com.mplatrforma.amr.entity.Var;
import com.mresearch.databank.jobs.ParseSpssJob;
import com.mresearch.databank.shared.ResearchFilesDTO;
import com.mresearch.databank.shared.RxStoredDTO;
import com.mresearch.databank.shared.SocioResearchDTO;
import com.mresearch.databank.shared.VarDTO_Detailed;
import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.opendatafoundation.data.FileFormatInfo;
import org.opendatafoundation.data.FileFormatInfo.Format;
import org.opendatafoundation.data.mod.SPSSFile;
import org.opendatafoundation.data.mod.SPSSFileException;
import org.opendatafoundation.data.mod.SPSSNumericVariable;
import org.opendatafoundation.data.mod.SPSSStringVariable;
import org.opendatafoundation.data.mod.SPSSVariable;
import org.w3c.dom.Document;
import org.elasticsearch.action.index.IndexResponse;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.*;

/**
 *
 * @author reshet
 */
@MessageDriven(mappedName = "jms/spss_parse", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
     @ActivationConfigProperty(propertyName = "destinationName", propertyValue = "jms/spss_parse")
   
  //  @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/myQCF")
    
})
public class AdminSocioResearchMDB implements MessageListener {
    
    @PersistenceContext
    private EntityManager em;
    @Resource
    private MessageDrivenContext mdc;
    @EJB
    private RxStorageBeanRemote store;
    public AdminSocioResearchMDB() {
    }
    
    @Override
    public void onMessage(Message message) {
        //message.
         ObjectMessage msg = null;
        try {
            if (message instanceof ObjectMessage) {
                msg = (ObjectMessage) message;
                System.out.println(msg.getStringProperty("title"));
                Object obj = msg.getObject();
                if(obj instanceof ParseSpssJob)
                {
                     ParseSpssJob job = (ParseSpssJob)obj;
                     parseSPSS(job.getId_file(),job.getLength());
               }else if (obj instanceof IndexResearchJob)
                {
                    IndexResearchJob job = (IndexResearchJob)obj;
                    perform_indexing(job.getId_Research());         
                }
            }
        } catch (Throwable te) {
            te.printStackTrace();
        }
    }
    
    @EJB UserSocioResearchBeanRemote U_bean;
    private void perform_indexing(long id_research)
    {
        //SocioResearchDTO dto = U_bean.getResearch(id_research);
        SocioResearchDTO dto = new SocioResearchDTO();
        dto.setId((long)1);
        dto.setName("name");
        dto.setMethod("method");
        dto.setOrg_impl_name("org.impl.name");
        Node node = nodeBuilder().node();
        try {
            
            Client client = node.client();

    // on shutdown

           
//            IndexResponse response = client.prepareIndex("twitter", "tweet")
//            .setSource(jsonBuilder()
//                        .startObject()
//                            .field("user", "kimchy")
//                            .field("postDate", new Date())
//                            .field("message", "trying out Elastic Search")
//                        .endObject()
//                      )
//            .execute()
//            .actionGet();

            IndexResponse response = client.prepareIndex("databank", "research")
            .setSource(jsonBuilder()
                        .startObject()
                            .field("ID", dto.getID())
                            .field("name", dto.getName())
                            .field("method", dto.getMethod())
                            .field("org_impl_name", dto.getOrg_impl_name())
                            .field("org_order_name", dto.getOrg_order_name())
                            .field("gen_geathering", dto.getGen_geathering())
                            .field("sel_complexity", dto.getSel_complexity())
                            .field("sel_randomity", dto.getSel_randomity())
                            .field("start_date", dto.getStart_date())
                            .field("end_date", dto.getEnd_date())
                            .field("sel_size", dto.getSelection_size()) 
                            .array("publications",dto.getPublications().toArray())
                            .array("researchers", dto.getResearchers().toArray())
                            .array("concepts", dto.getConcepts().toArray())
                        .endObject()
                      )
            .execute()
            .actionGet();

//            GetResponse response2 = client.prepareGet("twitter", "tweet", "1")
//                 .execute()
//                 .actionGet();
            
            System.out.println(response.toString());
            
        } catch (IOException ex) {
            Logger.getLogger(ES_indexing_Bean.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
             node.close();
        }
    }
    
    private void parseSPSS(long blobkey,long length)
    {
        System.out.println("PARSE SPSS MDB, em = "+em);
        
        Long socioresearch_key = null;
	byte [] arr = new byte[(int)length];
        
        RxStoredDTO dto = store.getFileInfo(blobkey);
	arr = store.getFileContents(blobkey);
        SPSSFile s = new SPSSFile(arr);
        String ans = "";
        String answer = "";
        ans = "file_cr";
        ans+= "length = "+arr.length;
        //ans+="fetch_size = "+String.valueOf(b_serv.MAX_BLOB_FETCH_SIZE)+ " ";
        byte [] arr_first = new byte[100];
        for(int i = 0; i < 100;i++)
        {
                arr_first[i] = arr[i];
        }
        ans+=new String(arr_first);

        try {
                s.loadMetadata();
                ans+=" meta_loaded";
                s.loadData();
                ans+=" data_loaded";
                //org.w3c.dom.Document doc1 = s.getDDI3LogicalProduct();
                org.w3c.dom.Document doc2 = s.getDDI3PhysicalDataProduct(new FileFormatInfo(Format.SPSS));
                ans+=" doc created";

                socioresearch_key = createEmptyResearch(dto.getName(),blobkey);
                answer = addSPSStoSocioResearch(socioresearch_key, s, blobkey,doc2,ans);

        } catch (SPSSFileException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ans+=e.getMessage();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                ans+=e.getMessage();
                e.printStackTrace();
        }	
	//return ans+"  "+socioresearch_key + " : vars :  "+answer;
	//return socioresearch_key;
    }
     private long createEmptyResearch(String name,long file_id) {

        SocioResearch research = null;
        Long research_id = null;
        try {
          research = new SocioResearch(em);
          research.setName(name);
          ResearchFilesDTO dto = new ResearchFilesDTO();
          dto.addFile(ResearchFilesDTO.CG_arrays, name, file_id);
          research.updateFileAccessor(dto);
          em.persist(research);
          research_id = research.getID();
         // currentUser.getFriends().add(friend);
        } finally {
        }
        return research_id;
    }
    
    private String addSPSStoSocioResearch(long socioresearch_id,SPSSFile spss,long spss_blobkey,Document doc,String ans)
    {
            String ans1 = "";
            ArrayList<Long> var_ids = new ArrayList<Long>();
            for(int i = 0; i < spss.getVariableCount();i++)
            {
                    SPSSVariable s_var = spss.getVariable(i);
                    //ans1+=s_var.getLabel()+" ";
    //		try {
    //			Element el = s_var.getDDI3CodeScheme(doc);
    //			int a = 2;
    //			a+=a;
    //		} catch (DOMException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		} catch (SPSSFileException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		}
                    long s_key = createVar(s_var,socioresearch_id);
                    ans1+=s_key;
                    var_ids.add(s_key);
            }

        SocioResearch dsResearch;
        try {
          dsResearch = em.find(SocioResearch.class, socioresearch_id);
          dsResearch.setSpssFile(spss_blobkey);
          dsResearch.setVar_ids(var_ids);
          dsResearch.setSelection_size(spss.getRecordCount());
          em.persist(dsResearch);
        } finally {
        }
        return ans1;
    }
    
    
private long createVar(SPSSVariable s_var,long research_id)
{
    Var var = null;
    Long var_id = null;

    try 
    {
      var = new Var();

      var.setResearch_id(research_id);
     // Logger logger = Logger.getLogger("NameOfYourLogger");
         // logger.log(Level.SEVERE, "this message should get logged "+s_var.toString());//throw new IOException(doc2.getInputEncoding());

      //log();
      
     
  
      String multistring = "";
      multistring+=s_var.getLabel();
//      multistring+=" :: "+new String(s_var.getLabel().getBytes("Cp1251"),"Cp1251").substring(0,s_var.getLabel().length());
//      multistring+=" :: "+new String(s_var.getLabel().getBytes("UTF-8"),"UTF-8").substring(0,s_var.getLabel().length());
//      multistring+=" :: "+new String(s_var.getLabel().getBytes("Cp1251"),"UTF-8").substring(0,s_var.getLabel().length());
//      multistring+=" :: "+new String(s_var.getLabel().getBytes("UTF-8"),"Cp1251").substring(0,s_var.getLabel().length());
//      multistring+=" :: "+new String(s_var.getLabel().getBytes(),"UTF-8").substring(0,s_var.getLabel().length());
//      multistring+=" :: "+new String(s_var.getLabel().getBytes(),"Cp1251").substring(0,s_var.getLabel().length());
//	      multistring+=" :: "+new String(s_var.getLabel().getBytes("Cp866"),"UTF-8").substring(0,s_var.getLabel().length());
//	      multistring+=" :: "+new String(s_var.getLabel().getBytes(),"Cp866").substring(0,s_var.getLabel().length());
//	      multistring+=" :: "+new String(s_var.getLabel().getBytes("UTF-8"),"koi8-r").substring(0,s_var.getLabel().length());
//	      multistring+=" :: "+new String(s_var.getLabel().getBytes("ISO-8859-1"),"cp1251").substring(0,s_var.getLabel().length());

      //String step0= new String(s_var.getLabel().getBytes());
      //String step1 = new String(s_var.getLabel().getBytes("UTF8"));
      //String step2 = new String(step1.getBytes("CP1251"));
      //multistring+=" :: "+step1.substring(0,s_var.getLabel().length()/10);
          //multistring+=" :: "+step2.substring(0,s_var.getLabel().length()/10);
      //String step3 = new String(s_var.getLabel().getBytes("CP1251"));
      //String step4 = new String(step3.getBytes("UTF8"));
      //String step5 = new String(step0.getBytes("UTF8"));
      //String step6 = new String(step0.getBytes("CP1251"));

      //multistring+=" :: "+step3.substring(0,s_var.getLabel().length()/10);
      //multistring+=" :: "+step4.substring(0,s_var.getLabel().length()/10);
      //multistring+=" :: "+step5.substring(0,s_var.getLabel().length()/10);
      //multistring+=" :: "+step6.substring(0,s_var.getLabel().length()/10);

   //   logger.log(Level.SEVERE, "this message should get logged "+multistring);//throw new IOException(doc2.getInputEncoding());

//	      multistring+=" :: "+new String(s_var.getLabel().getBytes(),"koi8-r").substring(0,s_var.getLabel().length()/10);
//	      multistring+=" :: "+new String(s_var.getLabel().getBytes("koi8-r"),"UTF-8").substring(0,s_var.getLabel().length()/10);
//	      
      //var.setCode(new String(s_var.getName().getBytes("CP1251"),"UTF-8"));
      //var.setLabel(new String(s_var.getLabel().getBytes("CP1251"),"UTF-8"));
      var.setCode(new String(s_var.getName()));
      var.setLabel(multistring);
      if (s_var.valueLabelRecord != null)
      {
          var.setVar_type(VarDTO_Detailed.alt_var_type);
          ArrayList<String> labels_encoding = s_var.valueLabelRecord.getVLabelValues();
              for(int i = 0; i < labels_encoding.size();i++)
              {
                //  labels_encoding.set(i, new String(labels_encoding.get(i).getBytes("CP1251"),"UTF-8"));
                  labels_encoding.set(i, new String(labels_encoding.get(i)));
                  }
              var.setV_label_codes(s_var.valueLabelRecord.getVLabelCodes());
              var.setV_label_values(labels_encoding);  
      }
      else
      {
          if (s_var instanceof SPSSNumericVariable) var.setVar_type(VarDTO_Detailed.real_var_type);
          if (s_var instanceof SPSSStringVariable)
          {
                        var.setVar_type(VarDTO_Detailed.text_var_type);
                        SPSSStringVariable str_var = (SPSSStringVariable)s_var;
                        ArrayList<String> str_arr = new ArrayList<String>();
                        for(String str:str_var.data)
                        {
                                str_arr.add(str);
                        }
                        var.setCortage_string(str_arr);
          }
      }
      ArrayList<Double> values = new ArrayList<Double>();
      //SPSS
      if (s_var instanceof SPSSNumericVariable)
      {
          SPSSNumericVariable s_var_numeric = (SPSSNumericVariable)s_var;
          for(Double value:s_var_numeric.data)
          {
                  values.add(value);
          }
      }
      var.setCortage(values);
      //var.setV_label_map(map);
      em.persist(var);
      var_id = var.getID();
      
//     }  catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(AdminSocioResearchSessionBean.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
     // testDDI3work();
     }finally
    {
    }
     return var_id;
    }
}
