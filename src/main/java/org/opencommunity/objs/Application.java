package org.opencommunity.objs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class Application {
	@Id
	private String id;
	private String name;
	private String img;
	@Column(columnDefinition="TEXT")
	
	public String data;
	
	public Application()
		{
		this.id=UUID.randomUUID().toString();
		}
	public String getId() {
		return id;
	}
	public String getImg() {
		return img;
	}
	public String getName() {
		return name;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public void setData(String data) {
//		this.data=data;
//	}
	public Map getData() {
		try{
			return new ObjectMapper().readValue(data, Map.class);			
			}
		catch(Exception e){return new HashMap();}		
	}
	
	public void setData(Map data) {
		try	{
			this.data =new ObjectMapper().writeValueAsString(data);
			}
		catch(Exception e){}	
	}
}
