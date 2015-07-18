package org.opencommunity.objs;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Log {
	@Id
	private String uid;
	private Date date;
	private String user;
	private String psw;
	private boolean success;
	
	public Log() 
		{
		this.uid= UUID.randomUUID().toString();
		}
	public Log(String user, String psw,boolean success) 
		{
		this.uid= UUID.randomUUID().toString();
		this.date=new Date();
		this.user=user;
		this.psw=psw;
		this.success=success;
		}
	public Date getDate() {
		return date;
		}
	public String getPsw() {
		return psw;
	}
	public String getUser() {
		return user;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setPsw(String psw) {
		this.psw = psw;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
