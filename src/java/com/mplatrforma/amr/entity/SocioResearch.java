package com.mplatrforma.amr.entity;

import com.mplatforma.amr.service.RxStorageSessionBean;
import java.util.ArrayList;
import java.util.Date;

import com.mresearch.databank.shared.ResearchFilesDTO;
import com.mresearch.databank.shared.SearchTaskResearchDTO;
import com.mresearch.databank.shared.SocioResearchDTO;
import com.mresearch.databank.shared.SocioResearchFilesDTO;
import com.mresearch.databank.shared.VarDTO;
import com.mresearch.databank.shared.VarDTO_Light;
import java.sql.Blob;
import javax.ejb.EJB;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.Transient;

@Entity
public class SocioResearch extends AbstractSearchable{

    
        @Transient
        private EntityManager em;
        
        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
	private long id;
	private long id_search_repres;
	private short vars_tagcloud_created;
	private String name;
	private String org_prompter;

    public SocioResearch() {
    }
	public Long getFile_accessor_id() {
		return file_accessor_id;
	}
	private Long spssFile_blobkey;
	private Long file_accessor_id;
	private byte[] ddi3_physical;
	private byte[] ddi3_logical;
	private ArrayList<Long> var_ids;
	private ArrayList<String> case_ids;
	private Long org_order_id;
	private Long org_impl_id;
	private String org_order_name;
	private String org_impl_name;
	private long var_weight_id;
	private String var_weight_name;
	private int selection_size;
	private String gen_geathering;
	private String method;
	private String selection_appr_rand;
	private String selection_appr_complexity;
	private ArrayList<String> publications;
	private ArrayList<String> publications_dois;
	private ArrayList<String> publications_urls;
	private ArrayList<String> researchers;
	private ArrayList<String> concepts;
        
        @Temporal(javax.persistence.TemporalType.DATE)
	private Date start_date;
        
        @Temporal(javax.persistence.TemporalType.DATE)
	private Date end_date;
	
        private ArrayList<String> files_ids;
	
        public ArrayList<String> getFiles_ids() {
		return files_ids;
	}
	public ArrayList<String> getFiles_descs() {
		return files_descs;
	}

	private ArrayList<String> files_descs;
	
	public long getID(){return id;}
	public void setSpssFile(long spssFile) {
		this.spssFile_blobkey = spssFile;
	}
	public long getSpssFile() {
		return spssFile_blobkey;
	}
	public void generateDDI3()
	{
		//TODO 
	}
	

	private void setCase_ids(ArrayList<String> case_ids) {
		this.case_ids = case_ids;
	}
	public ArrayList<String> getCase_ids() {
		return case_ids;
	}
	//public double getWeightedSize()
	public SocioResearch(SocioResearchDTO rDTO,EntityManager em)
	{
                this.em = em;
		//createFileAccessor();
		vars_tagcloud_created = 0;
		this.setId_search_repres(createSearchRepresenter());
		this.setName(rDTO.getName());
		this.setOrg_impl_id(rDTO.getOrg_impl_id());
		this.setOrg_order_id(rDTO.getOrg_order_id());
		this.setOrg_impl_name(rDTO.getOrg_impl_name());
		this.setOrg_order_name(rDTO.getOrg_order_name());
		this.setStart_date(rDTO.getStart_date());
		this.setEnd_date(rDTO.getEnd_date());
		this.setGen_geathering(rDTO.getGen_geathering());
		this.setSelection_size(rDTO.getSelection_size());
		this.setSelection_appr_rand(rDTO.getSel_randomity());
		this.setSelection_appr_complexity(rDTO.getSel_complexity());
		this.setVar_ids(rDTO.getVar_ids());
		this.setPublications(rDTO.getPublications());
		this.setPublications_dois(rDTO.getPublications_dois());
		this.setPublications_urls(rDTO.getPublications_urls());
		this.setResearchers(rDTO.getResearchers());
		this.setMethod(rDTO.getMethod());
		this.setVar_weight_id(rDTO.getVar_weight_id());
		this.setVar_weight_name(rDTO.getVar_weight_name());
		this.setConcepts(rDTO.getConcepts());
		updateEntityRepresent(id_search_repres, name,em);
		if (vars_tagcloud_created==0)generateVarsTagCloud();
		//this.org_prompter = rDTO.getOrg_prompter();
	}
	private void createFileAccessor()
	{
		ResearchFilesAccessor accessor;
		try{
			accessor = new ResearchFilesAccessor();
			em.persist(accessor);
			this.file_accessor_id = accessor.getId();
		}finally
		{
			//em.close();
		}
	}
	public void updateFileAccessor(ResearchFilesDTO dto)
	{
		ResearchFilesAccessor accessor;
		try{
			accessor = em.find(ResearchFilesAccessor.class,file_accessor_id);
			accessor.updateFromDTO(dto);
		}finally
		{
			//em.close();
		}
	}
	private void generateVarsTagCloud()
	{
	    try {
	    	for(Long var_id:var_ids)
			{
	    		Var dsVar, detached;
	    		dsVar = em.find(Var.class, var_id);
	   	       // detached = pm.detachCopy(dsVar);
	   	      //  VarDTO_Light dto = new VarDTO_Light();
	   	        
	   	        VarDTO dto = dsVar.toDTO();
	   	        updateTagCloudBridge("var-"+String.valueOf(var_id), getPermutations(dto.getLabel()), id_search_repres,em);
	   	        int i = 0;
	   	        
//	   	        for(String alt_name:dto.getV_label_values())
//	   	        {
//	   	        	updateTagCloudBridge(var_id+"-"+String.valueOf(i), getPermutations(alt_name), id_search_repres);
//	   	        	i++;
//		   	    }
			}  
	    	vars_tagcloud_created = 1;
	    } finally {
	     // em.close();
	    }
	}
	public SocioResearch(EntityManager em)
	{
               // em = new RxStorageSessionBean().getEM();
                this.em = em;
            vars_tagcloud_created = 0;
		createFileAccessor();
	//this.setId_search_repres(createSearchRepresenter());
	}

	public SocioResearchDTO toDTO()
	{
		SocioResearchDTO  dto = new SocioResearchDTO();
		dto.setId(id);
		dto.setName(getName());
		dto.setOrg_impl_id(getOrg_impl_id());
		dto.setOrg_order_id(getOrg_order_id());
		dto.setStart_date(getStart_date());
		dto.setEnd_date(getEnd_date());
		dto.setGen_geathering(getGen_geathering());
		dto.setSelection_size(getSelection_size());
		//dto.setSelection_appr(getSelection_appr());
		dto.setSel_complexity(this.selection_appr_complexity);
		dto.setSel_randomity(this.selection_appr_rand);
		dto.setVar_ids(getVar_ids());
		dto.setPublications(getPublications());
		dto.setPublications_dois(getPublications_dois());
		dto.setPublications_urls(getPublications_urls());
		//this.setPublications_urls(rDTO.getPublications_urls());
		
		dto.setResearchers(getResearchers());
		dto.setMethod(getMethod());
		dto.setVar_weight_id(getVar_weight_id());
		dto.setConcepts(getConcepts());
		dto.setOrg_impl_name(getOrg_impl_name());
		dto.setOrg_order_name(getOrg_order_name());
		dto.setVar_weight_name(getVar_weight_name());
		dto.setFile_accessor_id(this.file_accessor_id);
		return dto;
	}
	public ResearchFilesDTO toFilesDTO(EntityManager em)
	{
	    try {
	    		ResearchFilesAccessor accessor,detached;
	    		accessor = em.find(ResearchFilesAccessor.class,file_accessor_id);
	    		//detached = em.detach(accessor);
	    		return accessor.toDTO();
	    } finally {
	     // em.close();
	    }
	}
	public void updateFromDTO(SocioResearchDTO rDTO,EntityManager em)
	{
                this.em = em;
		if (id_search_repres == 0) id_search_repres = createSearchRepresenter();
		if (getSearchRepresenter(id_search_repres,em).getEntity_id() == 0) updateEntityID(id, id_search_repres,em);
		this.setName(rDTO.getName());
		this.setOrg_impl_id(rDTO.getOrg_impl_id());
		this.setOrg_order_id(rDTO.getOrg_order_id());
		this.setOrg_impl_name(rDTO.getOrg_impl_name());
		this.setOrg_order_name(rDTO.getOrg_order_name());
		this.setStart_date(rDTO.getStart_date());
		this.setEnd_date(rDTO.getEnd_date());
		this.setGen_geathering(rDTO.getGen_geathering());
		this.setSelection_size(rDTO.getSelection_size());
		this.setSelection_appr_rand(rDTO.getSel_randomity());
		this.setSelection_appr_complexity(rDTO.getSel_complexity());
		//this.var_ids = rDTO.getVar_ids();
		this.setPublications(rDTO.getPublications());
		this.setPublications_dois(rDTO.getPublications_dois());
		this.setPublications_urls(rDTO.getPublications_urls());
		
		this.setResearchers(rDTO.getResearchers());
		this.setMethod(rDTO.getMethod());
		this.setVar_weight_id(rDTO.getVar_weight_id());
		this.setVar_weight_name(rDTO.getVar_weight_name());
		this.setConcepts(rDTO.getConcepts());
		updateEntityRepresent(id_search_repres, name,em);
		if (vars_tagcloud_created==0)generateVarsTagCloud();
		//this.org_prompter =rDTO.getOrg_prompter();
	}
	
	public void updateFromDTOGrouped(SocioResearchDTO rDTO,EntityManager em)
	{
                this.em= em;
		if (id_search_repres == 0) id_search_repres = createSearchRepresenter();
		if (getSearchRepresenter(id_search_repres,em).getEntity_id() == 0) updateEntityID(id, id_search_repres,em);
		//this.setName(rDTO.getName());
		this.setOrg_impl_id(rDTO.getOrg_impl_id());
		this.setOrg_order_id(rDTO.getOrg_order_id());
		this.setOrg_impl_name(rDTO.getOrg_impl_name());
		this.setOrg_order_name(rDTO.getOrg_order_name());
		this.setStart_date(rDTO.getStart_date());
		this.setEnd_date(rDTO.getEnd_date());
		this.setGen_geathering(rDTO.getGen_geathering());
		this.setSelection_size(rDTO.getSelection_size());
		this.setSelection_appr_rand(rDTO.getSel_randomity());
		this.setSelection_appr_complexity(rDTO.getSel_complexity());
		//this.var_ids = rDTO.getVar_ids();
		this.setPublications(rDTO.getPublications());
		this.setPublications_dois(rDTO.getPublications_dois());
		this.setPublications_urls(rDTO.getPublications_urls());
		
		this.setResearchers(rDTO.getResearchers());
		this.setMethod(rDTO.getMethod());
		
		//this.setVar_weight_id(rDTO.getVar_weight_id());
		//this.setVar_weight_name(rDTO.getVar_weight_name());
		this.setConcepts(rDTO.getConcepts());
		//updateEntityRepresent(id_search_repres, name);
		if (vars_tagcloud_created==0)generateVarsTagCloud();
		//this.org_prompter =rDTO.getOrg_prompter();
	}
	
	@Override
	public long createSearchRepresenter() {
		EntitySearchRepresenter repres;
		try{
			repres = new EntitySearchRepresenter();
			repres.setEntityType(SocioResearch.class.getName());
//			repres.setText_represent(getName());
			//repres.setEntity_id();
                        
			em.persist(repres);
		}finally
		{
			//em.close();
		}
		return repres.getID();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if (id_search_repres != 0){}
			//this.updateTagCloudBridge("name", getPermutations(this.name), getId_search_repres(),em);
	}
	public Long getOrg_impl_id() {
		return org_impl_id;
	}
	public void setOrg_impl_id(Long org_impl_id) {
		this.org_impl_id = org_impl_id;
	}
	public Long getOrg_order_id() {
		return org_order_id;
	}
	
	public void setOrg_order_id(Long org_order_id) {
		this.org_order_id = org_order_id;
	}
	public String getOrg_impl_name() {
		return org_impl_name;
	}
	
	public void setOrg_impl_name(String org_impl_name) {
		this.org_impl_name = org_impl_name;
		//this.updateTagCloudBridge("org_impl_name", getPermutations(this.org_impl_name), getId_search_repres(),em);
		//this.getSearchRepresenter(getId_search_repres()).updateTagCloud("org_impl_name", getPermutations(this.org_impl_name));
	}
	public String getOrg_order_name() {
		return org_order_name;
	}	
	public void setOrg_order_name(String org_order_name) {
		this.org_order_name = org_order_name;
	}
	public Date getStart_date() {
		return start_date;
	}
	
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Date getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	public String getGen_geathering() {
		return gen_geathering;
	}
	public void setGen_geathering(String gen_geathering) {
		this.gen_geathering = gen_geathering;
	}
	public int getSelection_size() {
		return selection_size;
	}
	
	public void setSelection_size(int selection_size) {
		this.selection_size = selection_size;
	}
	
	public ArrayList<Long> getVar_ids() {
		return var_ids;
	}
	public void setVar_ids(ArrayList<Long> var_ids) {
		this.var_ids = var_ids;
	}
	public ArrayList<String> getPublications() {
		return publications;
	}
	public void setPublications(ArrayList<String> publications) {
		this.publications = publications;
	}
	public ArrayList<String> getResearchers() {
		return researchers;
	}
	public void setResearchers(ArrayList<String> researchers) {
		this.researchers = researchers;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public long getVar_weight_id() {
		return var_weight_id;
	}
	public void setVar_weight_id(long var_weight_id) {
		this.var_weight_id = var_weight_id;
	}
	public String getVar_weight_name() {
		return var_weight_name;
	}
	public void setVar_weight_name(String var_weight_name) {
		this.var_weight_name = var_weight_name;
	}
	public ArrayList<String> getConcepts() {
		return concepts;
	}
	public void setConcepts(ArrayList<String> concepts) {
		this.concepts = concepts;
	}
	public long getId_search_repres() {
		return id_search_repres;
	}
	public void setId_search_repres(long id_search_repres) {
		this.id_search_repres = id_search_repres;
	}
	public ArrayList<String> getPublications_dois() {
		return publications_dois;
	}
	public void setPublications_dois(ArrayList<String> publications_dois) {
		this.publications_dois = publications_dois;
	}
	public String getSelection_appr_rand() {
		return selection_appr_rand;
	}
	public void setSelection_appr_rand(String selection_appr_rand) {
		this.selection_appr_rand = selection_appr_rand;
	}
	public String getSelection_appr_complexity() {
		return selection_appr_complexity;
	}
	public void setSelection_appr_complexity(String selection_appr_complexity) {
		this.selection_appr_complexity = selection_appr_complexity;
	}

	public void addFile(String id,String desc)
	{
		if(files_ids == null) files_ids = new ArrayList<String>();
		if(files_descs == null) files_descs = new ArrayList<String>();
		
		files_ids.add(id);
		files_descs.add(desc);
	}
	public ArrayList<String> getPublications_urls() {
		return publications_urls;
	}
	public void setPublications_urls(ArrayList<String> publications_urls) {
		this.publications_urls = publications_urls;
	}
}
