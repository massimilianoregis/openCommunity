package org.opencommunity.objs;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.opencommunity.persistence.Repositories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Pending 
{
	@Id
	private String id;
	@ManyToOne
	private Role role;
	@ManyToOne
	private Role role2;
	@ManyToOne	
	private User user;
		
	public Pending()
		{}
	public Pending(User user, Role role)
		{
		this.id= UUID.randomUUID().toString();
		this.user=user;
		this.role=role;				
		}
	public Pending(User user, Role role,Role role2)
		{
		this.id= UUID.randomUUID().toString();
		this.user=user;
		this.role=role;		
		this.role2=role2;
		}
	
	public String save()
		{
		Repositories.pending.save(this);
		return id;
		}
	
	public void execute()
		{		
		user.addRole(role);
		user.addRole(role2);
		
		user.save();
		Repositories.pending.delete(id);
		}
	
	public String getId() {
		return id;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRole() {
		return role.getId();
	}
	public String getRole2() {
		if(role2==null) return null;
		return role2.getId();
	}
	public void setRole2(Role role2) {
		this.role2 = role2;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	public String getUser() {
		return user.getMail();
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
