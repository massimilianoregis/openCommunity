package org.opencommunity.balance;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Movement {
	@Id
	private String id;
	private int value;
	private String why;
	private Date date;
	
	public Movement(){
		this.id			=	UUID.randomUUID().toString();
		this.date		= 	new Date();
	}
	public Movement(int value, String why){
		this.id			=	UUID.randomUUID().toString();
		this.value		=	value;
		this.why		=	why;
		this.date		= 	new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getWhy() {
		return why;
	}

	public void setWhy(String why) {
		this.why = why;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
