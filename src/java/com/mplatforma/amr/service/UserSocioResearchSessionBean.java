/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mplatforma.amr.service;

import com.mplatforma.amr.service.remote.UserSocioResearchBeanRemote;
import com.mplatrforma.amr.entity.Organization;
import com.mplatrforma.amr.entity.SingleStringEntity;
import com.mplatrforma.amr.entity.SocioResearch;
import com.mplatrforma.amr.entity.Var;
import com.mresearch.databank.shared.FilterBaseDTO;
import com.mresearch.databank.shared.FilterMultiDTO;
import com.mresearch.databank.shared.OrgDTO;
import com.mresearch.databank.shared.ResearchFilesDTO;
import com.mresearch.databank.shared.SSE_DTO;
import com.mresearch.databank.shared.SocioResearchDTO;
import com.mresearch.databank.shared.SocioResearchFilesDTO;
import com.mresearch.databank.shared.UserAccountDTO;
import com.mresearch.databank.shared.VarDTO;
import com.mresearch.databank.shared.VarDTO_Detailed;
import com.mresearch.databank.shared.VarDTO_Light;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author reshet
 */
@WebService
@Stateless(mappedName="UserSocioResearchRemoteBean",name="UserSocioResearchRemoteBean")
public class UserSocioResearchSessionBean implements UserSocioResearchBeanRemote{
    @PersistenceContext
    private EntityManager em;
    @Override
    public SocioResearchDTO getResearch(long id) {
        
        SocioResearch res = em.find(SocioResearch.class, id);
        return res.toDTO();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public VarDTO getVar(long id,UserAccountDTO dto) {
        Var v = em.find(Var.class, id);
        v.setEM(em);
        return v.toDTO(dto,em);
    }

    @Override
    public VarDTO_Detailed getVarDetailed(long id,UserAccountDTO dto) {
         Var v = em.find(Var.class, id);
         v.setEM(em);
        return v.toDTO_Detailed(dto,em);
    }

    
    @Override
    public ArrayList<SocioResearchDTO> getResearchSummaries() {
        ArrayList<SocioResearchDTO> list = new ArrayList<SocioResearchDTO>();
        
   // SocioResearch dsResearch, detached;
	try {
		TypedQuery<SocioResearch> q = em.createQuery("SELECT x FROM SocioResearch x", SocioResearch.class);
		List<SocioResearch> res = (List<SocioResearch>)q.getResultList();
		for(SocioResearch research:res)
		{
			list.add(research.toDTO());
		}
	} finally {
        }
        return list;
   //     throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static ArrayList<Long> intersection(ArrayList<Long> first,ArrayList<Long> second)
    {
            ArrayList<Long> first_cl = new ArrayList<Long>();
            for(Long id:second)
            {
                    for(Long id_f:first)
                    {
                            if (id_f.equals(id)) 
                            {
                                    first_cl.add(id_f);
                                    break;
                            }
                    }
            }
            return first_cl;
    }

    public static ArrayList<Long> union(ArrayList<Long> first,ArrayList<Long> second)
    {
            ArrayList<Long> first_cl = new ArrayList<Long>();
            for(Long id:first)
            {
                    first_cl.add(id);
            }
            for(Long id:second)
            {
                    if(!first_cl.contains(id))first_cl.add(id);
            }
            return first_cl;
    }
    public ArrayList<Long> getSubFiltered(ArrayList<Long> processed,FilterBaseDTO next_filter)
    {
        //TODO just rewritten from appengine variant. Here we can optimize on multiple filters in one sql statement!!!! 
	ArrayList<Long> ids = processed;
//	if(next_filter instanceof FilterDiapasonDTO)
//	{
//		for(FilterBaseDTO dto_sub:((FilterDiapasonDTO) next_filter).getMulti_dto_proxy().getFilters())
//		{
//			//here we suppose that multifilter is AND-filter (for diapa)
//			ids = getSubFiltered(processed, dto_sub);
//		}
//	}else
	if(next_filter instanceof FilterMultiDTO)
	{
		ArrayList<Long> or_ids = new ArrayList<Long>();
		for(FilterBaseDTO dto_sub:((FilterMultiDTO) next_filter).getFilters())
		{
			//here we suppose that multifilter is OR-filter
			ArrayList<Long > curr_ids = getSubFiltered(processed, dto_sub);
			or_ids = union(or_ids,curr_ids);
		}
		if (((FilterMultiDTO) next_filter).getFilters().size()>0)ids = or_ids;
	} else
	{
		String filt = next_filter.getFilter();
		if (filt!=null && !filt.equals(""))
		{
			//Query q = pm.newQuery(SocioResearch.class);
                        String q_str = "SELECT x FROM SocioResearch AS x WHERE ";
                        if(next_filter.getTarget_class_name().equals("SocioResearch"))
			{
                            q_str+=filt;
                            //q.setFilter(filt);
			}
                        TypedQuery<SocioResearch> tq = em.createQuery(q_str, SocioResearch.class);
                        ArrayList<Long> current_ids = new ArrayList<Long>();
			List<SocioResearch> res = tq.getResultList();
			for(SocioResearch research:res)
			{
				current_ids.add(research.getID());
			}
			ids = intersection(ids, current_ids);
		}
	}
	return ids;
    }    
    
    @Override
    public ArrayList<SocioResearchDTO> getResearchSummaries(List<FilterBaseDTO> filters) {
        ArrayList<SocioResearchDTO> list = new ArrayList<SocioResearchDTO>();
	ArrayList<Long> all_ids = new ArrayList<Long>();
	ArrayList<SocioResearchDTO> all_dtos = getResearchSummaries();
	for(SocioResearchDTO dto: all_dtos)
	{
		all_ids.add(dto.getId());
	}
	try {
            for(FilterBaseDTO dto:filters)
            {
                    all_ids = getSubFiltered(all_ids, dto);
            }
            list = getResearchDTOs(all_ids);
	} finally {
        }
	return list;        
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayList<VarDTO_Light> getResearchVarsSummaries(long research_id) {
// Variant 1
//        ArrayList<VarDTO_Light> list = new ArrayList<VarDTO_Light>();
//        SocioResearch dsResearch,detached;
//	try {
//		 dsResearch = em.find(SocioResearch.class, research_id);
//	     ArrayList<Long> var_ids = dsResearch.getVar_ids();
//	     for(Long var_id:var_ids)
//	     {
//	    	 Var var = em.find(Var.class,var_id);
//	    	 list.add(var.toDTO_Light());
//	     }
//	} finally 
//        {
//        }
        // Variant 2
//        TypedQuery<Var> q = em.createQuery("SELECT x FROM Var x WHERE x.research_id = :id ORDER BY x.id", Var.class);
//        q.setParameter("id", research_id);
//        List<Var> l = q.getResultList();
//        for(Var v:l)
//        {
//            list.add(v.toDTO_Light());
//        }
        //Variant 3
	return Var.getResearchVarsLight(em, research_id);
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayList<Double> get2DDistribution(long var_id1, long var_id2,UserAccountDTO dto) {
        return Var.calc2DDistribution(var_id1, var_id2, dto, em);
    }

    @Override
    public ResearchFilesDTO getResearchFiles(long research_id) {
	    SocioResearch dsResearch;
	    ResearchFilesDTO dto;
	    try {
	      dsResearch = em.find(SocioResearch.class, research_id);
	      dto = dsResearch.toFilesDTO(em);
	    } finally {
	    }
	 return dto;
    }

    @Override
    public SocioResearchFilesDTO getResearchFilesInCategory(long research_id, String category) {
        // WTF???????????//
        SocioResearch dsResearch, detached;
        ResearchFilesDTO dto;
        SocioResearchFilesDTO dto2;
        try {
          dsResearch = em.find(SocioResearch.class, research_id);
          //dsResearch.addFile(id_file, desc);
          dto = dsResearch.toFilesDTO(em);
          dto2 = new SocioResearchFilesDTO();
          dto2.setFiles_ids(dto.getFileIds(category));
          dto2.setFiles_descs(dto.getFileNames(category));
        } finally {
        }
        return dto2;
    }

    @Override
    public ArrayList<SSE_DTO> getSSEs(String clas, String kind) {
	ArrayList<SSE_DTO> list = new ArrayList<SSE_DTO>();
	try {
		List<SingleStringEntity> res = SingleStringEntity.getMatching(em, clas, kind);
		for(SingleStringEntity ent:res)
		{
			list.add(ent.toDTO());
		}

	} finally {
        }
	return list;
    }

    @Override
    public ArrayList<OrgDTO> getOrgList() {
        ArrayList<OrgDTO> list = new ArrayList<OrgDTO>();
  	try {
		TypedQuery<Organization> q = em.createQuery("SELECT x FROM Organization x", Organization.class);
		List<Organization> res = (List<Organization>)q.getResultList();
		for(Organization org:res)
		{
			list.add(org.toDTO());
		}
	} finally {
        }
        return list;
    }

    @Override
    public ArrayList<SocioResearchDTO> getResearchDTOs(ArrayList<Long> ids) {
       ArrayList<SocioResearchDTO> arr = new ArrayList<SocioResearchDTO>();
	if (ids != null)
		for(Long key:ids)
		{
			SocioResearchDTO dto = getResearch(key);
			arr.add(dto);
		}
	return arr;
    }
    
}
