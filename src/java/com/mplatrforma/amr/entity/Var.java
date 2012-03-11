package com.mplatrforma.amr.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




import com.mresearch.databank.shared.RealVarDTO_Detailed;
import com.mresearch.databank.shared.SocioResearchDTO;
import com.mresearch.databank.shared.TextVarDTO_Detailed;
import com.mresearch.databank.shared.UserAccountDTO;
import com.mresearch.databank.shared.VarDTO;
import com.mresearch.databank.shared.VarDTO_Detailed;
import com.mresearch.databank.shared.VarDTO_Light;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name = "Var.getResearchVarsLight", query = "SELECT NEW com.mresearch.databank.shared.VarDTO_Light(x.id, x.code, x.label) FROM Var x WHERE x.research_id = :id ORDER BY x.id")
})
public class Var {
        @Transient
        private EntityManager em;
        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
	private long id;
	private long research_id;
	private String code;
	private String label;
	private String code_schema_id;
	private String var_type;
	private ArrayList<Double> v_label_codes;
	private ArrayList<String> v_label_values;
	private ArrayList<Long> generalized_var_ids;
	private ArrayList<Double> cortage;	
	private ArrayList<String> cortage_string;
        
        public Var()
        {}
        public void setEM(EntityManager em)
        {
            this.em = em;
        }
        public static ArrayList<VarDTO_Light> getResearchVarsLight(EntityManager em, long research_id)
        {
            ArrayList<VarDTO_Light> list = new ArrayList<VarDTO_Light>();
        
            TypedQuery<VarDTO_Light> q = em.createNamedQuery("Var.getResearchVarsLight", VarDTO_Light.class );
            q.setParameter("id", research_id);
            List<VarDTO_Light> l = q.getResultList();
            for(VarDTO_Light v:l)
            {
                list.add(v);
            }

            return list;
        }
	public ArrayList<String> getCortage_string() {
		return cortage_string;
	}

	public void setCortage_string(ArrayList<String> cortage_string) {
		this.cortage_string = cortage_string;
	}

	public long getID(){return id;}
	
	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setCode_schema_id(String code_schema_id) {
		this.code_schema_id = code_schema_id;
	}

	public String getCode_schema_id() {
		return code_schema_id;
	}

	public void setVar_type(String var_type) {
		this.var_type = var_type;
	}

	public String getVar_type() {
		return var_type;
	}
	
	public VarDTO toDTO()
	{
		VarDTO  dto = new VarDTO();
		dto.setCode(code);
		dto.setLabel(label);
		dto.setId(id);
		dto.setV_label_codes(v_label_codes);
		dto.setV_label_values(v_label_values);
		//dto.setDistribution(calcDistributionSimple());
		return dto;
	}
	public VarDTO_Light toDTO_Light()
	{
		VarDTO_Light  dto = new VarDTO_Light();
		dto.setCode(code);
		dto.setLabel(label);
		dto.setId(id);
		return dto;
	}
	public VarDTO toDTO(UserAccountDTO watching_user,EntityManager em)
	{
		VarDTO  dto = new VarDTO();
		dto.setCode(code);
		dto.setLabel(label);
		dto.setId(id);
		dto.setV_label_codes(v_label_codes);
		dto.setV_label_values(v_label_values);
		if (watching_user != null)calcDistribution(watching_user,dto,em);
			else dto.setDistribution(calcDistributionSimple());
		return dto;
	}
	public VarDTO_Detailed toDTO_Detailed(UserAccountDTO watching_user,EntityManager em)
	{
		VarDTO_Detailed  dto;
		if(var_type.equals(VarDTO_Detailed.alt_var_type))dto = new VarDTO_Detailed();
			else
			if(var_type.equals(VarDTO_Detailed.real_var_type))dto = new RealVarDTO_Detailed();
				else dto = new TextVarDTO_Detailed();
		
		dto.setCode(code);
		dto.setLabel(label);
		dto.setId(id);
		dto.setV_label_codes(v_label_codes);
		dto.setV_label_values(v_label_values);
		dto.setGen_vars_ids(generalized_var_ids);
		dto.setGen_var_names(getGenVarsNames(generalized_var_ids,em));
		dto.setGen_research_names(getGenResearchesNames(generalized_var_ids,em));
		dto.setGen_research_ids(getGenResearchesIds(generalized_var_ids,em));
		dto.setVar_type(var_type);
                if (dto instanceof RealVarDTO_Detailed) 
                {
                    ((RealVarDTO_Detailed)dto).setFiltered_cortage(this.cortage);
                    ((RealVarDTO_Detailed)dto).calc_statstics();
                }
                else if(dto instanceof TextVarDTO_Detailed)
                {
                    ((TextVarDTO_Detailed)dto).setFiltered_cortage(this.cortage_string);
                }
                else if (dto instanceof VarDTO_Detailed)
                    if (watching_user != null)calcDistribution(watching_user,dto,em);
			else dto.setDistribution(calcDistributionSimple());
		
		return dto;
	}
	private ArrayList<String> getGenVarsNames(ArrayList<Long> gen_var_ids,EntityManager em)
	{
		ArrayList<String> names = new ArrayList<String>();
                if(gen_var_ids != null)
		try
		{
			for(Long id:gen_var_ids)
			{
				Var var,detached_var;
				var = em.find(Var.class,id);
				if (var != null)
				{
					//detached_var = pm.detachCopy(var);
					names.add(var.getLabel());
				//	return detached_var;
				}
			}
		}
		finally
		{
			////em.close();
		}
		
		return names;
	}

	private ArrayList<String> getGenResearchesNames(ArrayList<Long> gen_var_ids,EntityManager em)
	{
		ArrayList<String> names = new ArrayList<String>();
                if(gen_var_ids != null)
		try
		{
			for(Long id:gen_var_ids)
			{
				Var var;
				var = em.find(Var.class,id);
				if (var != null)
				{
					SocioResearch research,res_detached;
					research = em.find(SocioResearch.class,var.getResearch_id());
					//res_detached = em.detachCopy(research);
					names.add(research.getName());
				//	return detached_var;
				}
			}
		}
		finally
		{
			////em.close();
		}
		
		return names;
	}
	private ArrayList<Long> getGenResearchesIds(ArrayList<Long> gen_var_ids,EntityManager em)
	{
		ArrayList<Long> ids = new ArrayList<Long>();
                if(gen_var_ids != null)
		try
		{
			for(Long id:gen_var_ids)
			{
				Var var;
				var = em.find(Var.class,id);
				if (var != null)
				{
					SocioResearch research,res_detached;
					research = em.find(SocioResearch.class,var.getResearch_id());
					ids.add(research.getID());
				//	return detached_var;
				}
			}
		}
		finally
		{
			////em.close();
		}
		
		return ids;
	}
	private ArrayList<Double> calcDistributionSimple()
	{
		//PersistenceManager pm = PMF.get().getPersistenceManager();
		//UserAccount account = 
		
		
		ArrayList<Double> distr = new ArrayList<Double>(v_label_codes.size());
		for(int i = 0;i < v_label_codes.size();i++)
		{
			distr.add(new Double(0));
		}
		for(Double value:cortage)
		{
			if (!value.equals(Double.NaN))
			{
				int setIndex = v_label_codes.indexOf(value);
				if (setIndex >=0)
				{
					Double val = distr.get(setIndex);
					distr.set(setIndex, val+1);
				}
			}
		}
		return distr;
	}
	private Var findVar(String code)
	{
		//suppose codes unique
		Var var,detached_var;
		try
		{
			Query q = em.createQuery("select from Var x where code = :code");
                        q.setParameter("code", code);
			var = (Var) q.getSingleResult();
			if (var != null)
			{
				//detached_var = pm.detachCopy(var);
				return var;
			}
		}
		finally
		{
			////em.close();
		}
		return null;
	}
	private Var getVar(long id,EntityManager em)
	{
		//suppose codes unique
		Var var,detached_var;
		try
		{
			var = em.find(Var.class,id);
			if (var != null)
			{
				//detached_var = pm.detachCopy(var);
                                em.detach(var);
				return var;
			}
		}
		finally
		{
			////em.close();
		}
		return null;
	}
	public ArrayList<VarDTO_Light> getResearchVarsSummaries(Long research_id,EntityManager em) {
		ArrayList<VarDTO_Light> list = new ArrayList<VarDTO_Light>();
	    SocioResearch dsResearch;
		try {
			 dsResearch = em.find(SocioResearch.class, research_id);
		     //detached = pm.detachCopy(dsResearch);
		     ArrayList<Long> var_ids = dsResearch.getVar_ids();
		     for(Long var_id:var_ids)
		     {
		    	 Var var = em.find(Var.class,var_id);
		    	 //Var detached_var = em.detachCopy(var);
		    	 list.add(var.toDTO_Light());
		     }
		} finally {
	      ////em.close();
	    }
		return list;
	}
	
	private ArrayList<Double> processFilters(ArrayList<String> filters,ArrayList<Integer> filtered_indecies,EntityManager em)
	{
		ArrayList<Double> filtered_cortage = new ArrayList<Double>();
		ArrayList<VarDTO_Light> vars = getResearchVarsSummaries(research_id,em);
		for(Double val:cortage)filtered_cortage.add(val);
		ArrayList<Integer> init_filtered_indecies = new ArrayList<Integer>();
		int k = 0;
		for(Double val:filtered_cortage)init_filtered_indecies.add(k++);
										
		for(String filter:filters)
		{
			ArrayList<String> var_codes = new ArrayList<String>();
			ArrayList<VarDTO_Light> vars_to_use = new ArrayList<VarDTO_Light>();
			ArrayList<Double> step_filtered_cortage = new ArrayList<Double>();
			ArrayList<Integer> step_filtered_indecies = new ArrayList<Integer>();
			
			for(Double elem:filtered_cortage) step_filtered_cortage.add(elem);
			for(VarDTO_Light vardto:vars)
			{
				//in formula varcode is presented in [] - by protocol
				if (filter.contains(vardto.getCode()))
				{
					var_codes.add(vardto.getCode());
					if (!vardto.getCode().equals(this.code))vars_to_use.add(vardto);
				}
			}
			//use indecies???
			int i = 0;
			int ik = step_filtered_cortage.size();
			Map<String, Object> map = new HashMap<String, Object>();
			Expression e = ExpressionBuilder.build(filter);
			ArrayList<Var> varsss = new ArrayList<Var>();
			for(VarDTO_Light vardto:vars_to_use)
			{
				Var v = getVar(vardto.getId(),em);
				varsss.add(v);
			}
			for(Double val:step_filtered_cortage)
			{
				try {
					map.clear();
					//new ExpressionBuilder().testBuilder();
					if (var_codes.contains(this.code))map.put(this.code, val);
					for(Var va:varsss)
					{
						map.put(va.getCode(), va.getCortage().get(init_filtered_indecies.get(i)));
					}
					   // Expression e = ExpressionBuilder.build("str != 'qwerty' && n1 / n2 >= 3 * (n2 + 10 / n1 * (2+3))");
					Boolean a = (Boolean) e.execute(map);
					if(a)
					{
						step_filtered_indecies.add(i);
					}else
					{
						filtered_cortage.remove(i - (step_filtered_cortage.size() - ik));ik--;
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				i++;
			}
			init_filtered_indecies = step_filtered_indecies;
		}
		for(Integer in:init_filtered_indecies)
		{
			filtered_indecies.add(in);
		}
		//filtered_indecies = init_filtered_indecies;
		return filtered_cortage;
	}
	
	
	public ArrayList<Double> calcDistribution(UserAccountDTO watching_user,VarDTO dto_calcing_for,EntityManager em)
	{
		SocioResearch research;
		ArrayList<Long> var_ids = new ArrayList<Long>();
		long weight_var_id;
		try
		{
			research = em.find(SocioResearch.class,this.research_id);
			//detached_res = pm.detachCopy(research);
			var_ids = research.getVar_ids();
			weight_var_id = research.getVar_weight_id();
		}
		finally
		{
			////em.close();
		}
		//UserAccount account = 
		
		
		ArrayList<Double> distr = new ArrayList<Double>(v_label_codes.size());
		for(int i = 0;i < v_label_codes.size();i++)
		{
			distr.add(new Double(0));
		}
		//prepare filters
		ArrayList<Double> filtered = new ArrayList<Double>();
		ArrayList<Integer> filtered_indecies = new ArrayList<Integer>();
		ArrayList<String> filters = watching_user.getFiltersToProcess(research_id);
		if (filters != null && filters.size() > 0 && var_ids != null)
		{
			filtered = processFilters(filters, filtered_indecies,em);
		}
		else
		{
			//plumb
			int i = 0; 
			for(Double d:cortage)filtered_indecies.add(i++);
			filtered = cortage;
		}
	
		
		//with weights
		if(weight_var_id != 0 && watching_user.getWeights_use()==1)
		{
			Var weight_var;
			ArrayList<Double> weights;
			try
			{
				weight_var = em.find(Var.class,weight_var_id);
				//detached_w_var = pmm.detachCopy(weight_var);
				weights = weight_var.getCortage();
			}
			finally
			{
				////em.close();
			}
			if (weights != null && weights.size() >= filtered.size())
			{
				int j = 0;
				for(Double value:filtered)
				{
					if (!value.equals(Double.NaN))
					{
						int setIndex = v_label_codes.indexOf(value);
						if (setIndex >=0)
						{
							Double val = distr.get(setIndex);
							Double increment = weights.get(filtered_indecies.get(j));
							distr.set(setIndex, val+increment);
						}
					}
					j++;
				}
			}
		}else
		{
			for(Double value:filtered)
			{
				if (!value.equals(Double.NaN))
				{
					int setIndex = v_label_codes.indexOf(value);
					if (setIndex >=0)
					{
						Double val = distr.get(setIndex);
						distr.set(setIndex, val+1);
					}
				}
			}
		}	
		
		if(dto_calcing_for != null)
		{
			dto_calcing_for.setDistribution(distr);
			if(dto_calcing_for instanceof RealVarDTO_Detailed)
			{
				((RealVarDTO_Detailed)dto_calcing_for).setFiltered_cortage(filtered);
			}
			if(dto_calcing_for instanceof TextVarDTO_Detailed)
			{
				ArrayList<String> cortage = new ArrayList<String>();
				for(Integer index:filtered_indecies)
				{
					cortage.add(this.cortage_string.get(index));
				}
				((TextVarDTO_Detailed)dto_calcing_for).setFiltered_cortage(cortage);
			}
			if(dto_calcing_for instanceof VarDTO_Detailed)
			{
				((VarDTO_Detailed)dto_calcing_for).setNumber_of_records(filtered.size());
			}
		}
		return distr;
	}
	
	
	public static ArrayList<Double> calc2DDistribution(long var1_id, long var2_id, UserAccountDTO watching_user,EntityManager em)
	{
		ArrayList<Double> distr = new ArrayList<Double>();
		
	    Var dsVar1,dsVar2;

           // @PersistenceContext EntityManager pm;
	    try {
	      dsVar1 = em.find(Var.class, var1_id);
	     // dsVar1 = pm.detachCopy(dsVar1);
	      dsVar2 = em.find(Var.class, var2_id);
	      //dsVar2 = pm.detachCopy(dsVar2);
	    } finally {
	      ////em.close();
	    }
	    
	    ArrayList<Integer> initial_filters_usage = new ArrayList<Integer>();
	    if(watching_user.getFilters_usage(dsVar1.getResearch_id()).size()>0)
	    for(Integer use:watching_user.getFilters_usage(dsVar1.getResearch_id()))
	    {
	    	initial_filters_usage.add(use);
	    }
	    
	    if(watching_user.getFilters_use() == null || !(watching_user.getFilters_use()==1))
	    {
	    	//if filters not to be used we disable all possible filters in account to calculate 2DD with its own filters
	    	//otherwise filters in 2DD used both with user-defined filters
	    	ArrayList<Integer> new_usage = watching_user.getFilters_usage(dsVar1.getResearch_id());
	    	for(int i = 0; i <new_usage.size();i++)
	    	{
	    		new_usage.set(i, (Integer)0);
	    	}
	    	watching_user.setFilters_usage(new_usage, dsVar1.getResearch_id());
	    }
	    Integer filters_use_initial = watching_user.getFilters_use();
	    watching_user.setFilters_use((Integer)1);
            
            //HERE REAL BUGG!!! INFLUENCE 2dd distribd with user self-defined filters!
            
            
            watching_user.setFilters(new ArrayList<String>());
	    
            //REMOVE THIS UPPER STRING LATER!!!
            ArrayList<String> filters = watching_user.getFilters();
	    ArrayList<Long> filter_categs = watching_user.getFilters_categories();
	    
	    ArrayList<Integer> usage = watching_user.getFilters_usage(dsVar1.getResearch_id());
	    
	    for(int i = 0; i < dsVar2.getV_label_codes().size();i++)
	    {
	    	ArrayList<Double> distrib_per_alternative = new ArrayList<Double>();
	    	String filter = dsVar2.getCode()+" == "+dsVar2.getV_label_codes().get(i);
	    	filters.add(filter);
	    	filter_categs.add(dsVar1.getResearch_id());
	    	usage.add((Integer)1);
	    	watching_user.setFilters_usage(usage, dsVar1.getResearch_id());
	    	
	    	distrib_per_alternative = dsVar1.calcDistribution(watching_user,null,em);
	    	for(Double d:distrib_per_alternative)distr.add(d);
	        
	    	filters.remove(filters.size()-1);
	    	filter_categs.remove(filter_categs.size()-1);
	    	usage.remove(usage.size()-1);
	    	watching_user.setFilters_usage(usage, dsVar1.getResearch_id());
	    //	watching_user.getF
	    	//TODO
	    	//here adding filters, calculating distrib and then restoring filters and weights in original state. 
	    }
     	watching_user.setFilters_use(filters_use_initial);
     	watching_user.setFilters_usage(initial_filters_usage, dsVar1.getResearch_id());
 	   //watching_user.getF
		//here make real 2DD
		return distr;
	}
	
	public ArrayList<Double> getV_label_codes() {
		return v_label_codes;
	}

	public void setV_label_codes(ArrayList<Double> v_label_codes) {
		this.v_label_codes = v_label_codes;
	}

	public ArrayList<String> getV_label_values() {
		return v_label_values;
	}

	public void setV_label_values(ArrayList<String> v_label_values) {
		this.v_label_values = v_label_values;
	}

	public ArrayList<Double> getCortage() {
		return cortage;
	}

	public void setCortage(ArrayList<Double> cortage) {
		this.cortage = cortage;
	}

	public Long getResearch_id() {
		return research_id;
	}

	public void setResearch_id(Long research_id) {
		this.research_id = research_id;
	}

	public ArrayList<Long> getGeneralized_var_ids() {
		return generalized_var_ids;
	}

	public void setGeneralized_var_ids(ArrayList<Long> generalized_var_ids) {
		this.generalized_var_ids = generalized_var_ids;
	}

}
