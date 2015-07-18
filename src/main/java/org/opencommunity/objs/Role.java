package org.opencommunity.objs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Role implements Serializable{
	static public String USER = "user";
	static public String ADMIN = "admin";

	@Id
	private String id;
	private String name;
	private String company;
	public Role()
		{
		
		}
	public Role(String role)
		{
		this.id=role;
		}
	public Role(String role, String company)
		{
		this.company=company;		
		this.name=role;
		if(company!=null && company.isEmpty()) company=null;
		if(company==null)	this.id=role;
		else				this.id=company+"."+role;
		}
	
	/* (non-Javadoc)
	 * @see org.community.RoleInterface#getName()
	 */
	
	public String getName() {
		return name;
	}
	
	
	public String getCompany() {
		return company;
		}
//	@JsonIgnore
//	public Community getCommunity()
//		{
//		return Repositories.community.findOne(company);
//		}
	
	
	public String getId() {
		return id;
	}
	
	
	public int hashCode() {
		
		return id.hashCode();
		}
	
	public boolean equals(Object obj) {
		
		if(obj instanceof String)
			if(company!=null)
				return obj.toString().equalsIgnoreCase(company+"."+id);
			else 
				return obj.toString().equalsIgnoreCase(id);
		return super.equals(obj);
	}
	@Override
	public String toString() {
		return this.name;
	}
}
