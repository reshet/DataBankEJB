package com.mplatrforma.amr.entity;

import java.util.ArrayList;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
public class EntitySearchRepresenter {
        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        private long id;
	private long entity_id;	
	private String text_represent;
        private String entityType;
	private ArrayList<String> tags = new ArrayList<String>();	
	private ArrayList<String> tags_identities = new ArrayList<String>();
	public long getID(){return id;}
	public long getEntity_id() {
		return entity_id;
	}
	public void setEntity_id(long entity_id) {
		this.entity_id = entity_id;
	}
	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public String getText_represent() {
		return text_represent;
	}

	public void setText_represent(String text_represent) {
		this.text_represent = text_represent;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public ArrayList<String> getTags_identities() {
		return tags_identities;
	}

	public void setTags_identities(ArrayList<String> tags_identities) {
		this.tags_identities = tags_identities;
	}
	public void updateTagCloud(String identity,ArrayList<String> tag_cloud)
	{
		ArrayList<Integer> indecies = new ArrayList<Integer>();
		int i = 0;
                if (tags_identities!= null&&tags!=null)
		for (String tag_ident:tags_identities)
		{
			if (tag_ident.equals(identity)) {
				indecies.add(i);
				tags_identities.remove(i);
				tags.remove(i);
			}
		}
		if (tags_identities!= null&&tags!=null)
		for(String tag:tag_cloud)
		{
			tags_identities.add(identity);
			tags.add(tag);
		}
		
	}

	
}
