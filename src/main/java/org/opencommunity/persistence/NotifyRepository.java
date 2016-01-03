package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.notification.Notify;
import org.springframework.data.repository.Repository;


public interface NotifyRepository extends Repository<Notify, String> 
{
	public List<Notify> findAll();
	public Notify save(Notify user);
	public Notify findOne(String name);
	public void delete(String entity);
	public boolean exists(String entity);	
}
