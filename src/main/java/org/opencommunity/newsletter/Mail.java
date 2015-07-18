package org.opencommunity.newsletter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Mail {
	@Id
	public String mail;
	private String name;
	
	@OneToMany(cascade = {CascadeType.ALL})
	public List<Group> groups=new ArrayList<Group>();
			
	public List<Group> getGroups() {
		return groups;
	}
	public String getMail() {
		return mail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	
	@Entity
	static public class Group
		{
		@Id		
		private String id;
		private String name;
		private Boolean sent;
		
		public Group() {
			this.id=UUID.randomUUID().toString();
		}
		public String getId() {
			return id;
		}
		public String getName() {
			return name;
		} 
		public Boolean getSent() {
			return sent;
		}
		
		public void setId(String id) {
			if(id==null) return;
			this.id = id;
		}		
		public void setName(String name) {
			this.name = name;
		}
		public void setSent(Boolean sent) {
			this.sent = sent;
		}
		}
}
