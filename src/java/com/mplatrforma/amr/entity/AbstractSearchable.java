package com.mplatrforma.amr.entity;

import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractSearchable {
	//@PersistenceContext
       // private EntityManager em;
        
        public EntitySearchRepresenter getSearchRepresenter(long id_search_repres,EntityManager em)
	{
		EntitySearchRepresenter repres;
		try{
			repres = em.find(EntitySearchRepresenter.class,id_search_repres);
		}
		
		finally
		{
			//em.close();
		}

			
		return repres;
	}
	
	
	public abstract long createSearchRepresenter();
	
	public  void updateTagCloudBridge(String identity,ArrayList<String> tag_cloud, long repres_id,EntityManager em)
	{
		EntitySearchRepresenter repres;
		try{
			repres = em.find(EntitySearchRepresenter.class,repres_id);
			repres.updateTagCloud(identity, tag_cloud);
		}finally
		{
			//em.close();
		}
	}
	public  void updateEntityID(long entity_id,long repres_id,EntityManager em)
	{
		EntitySearchRepresenter repres;
		try{
			repres = em.find(EntitySearchRepresenter.class,repres_id);
			repres.setEntity_id(entity_id);
			
		}finally
		{
			//em.close();
		}
	}
	public  void updateEntityRepresent(long repres_id,String text,EntityManager em)
	{
		EntitySearchRepresenter repres;
		try{
			repres = em.find(EntitySearchRepresenter.class,repres_id);
			repres.setText_represent(text);
			
		}finally
		{
			//em.close();
		}
	}
	
	public ArrayList<String> getPermutations(String str)
	{
		ArrayList<String> pairs = new ArrayList<String>();
		if (str == null) return pairs;
		str = str.toLowerCase();
		pairs.add(str);
		String[] arr = str.split(" ");
		for (int i = 0; i < arr.length -1;i++)
		{
			String st = arr[i]+" "+arr[i+1];
			pairs.add(st);
		}
		for (int i = 0; i < arr.length;i++)
		{
			pairs.add(arr[i]);
		}
		return pairs;
	
	}
}
