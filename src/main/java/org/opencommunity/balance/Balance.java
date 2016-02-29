package org.opencommunity.balance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class Balance {
	@Id
	private String id;
	private String user;
	private int total;
	
	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(cascade = {CascadeType.ALL})
	private List<Movement> movements = new ArrayList<Movement>();
	
	public Balance(){
		this.id=UUID.randomUUID().toString();
	}
	public Balance(String user){
		this.id=UUID.randomUUID().toString();
		this.user=user;
		this.total=0;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<Movement> getMovements() {
		return movements;
	}
	public void setMovements(List<Movement> movements) {
		this.movements = movements;
	}
	public void add(int value,String why)	{		
		if(value<0) {
			sub(-value,why);
			return;
		}
		this.total+=value;
		movements.add(new Movement(value,why));
	}
	public void sub(int value,String why){
		this.total-=value;
		movements.add(new Movement(-value,why));
	}
}
