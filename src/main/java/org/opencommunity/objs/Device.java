package org.opencommunity.objs;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Device {
	private String os;
	@Id
	private String id;
	
	public Device() {
		// TODO Auto-generated constructor stub
	}
	public Device(String os, String id){
		this.os=os;
		this.id=id;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
