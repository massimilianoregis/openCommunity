package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.Application;
import org.opencommunity.objs.User;
import org.springframework.data.repository.Repository;


public interface ApplicationRepository extends Repository<Application, String> 
{
	public List<Application> findAll();
	public Application save(Application user);
	public Application findOne(String id);
	public Application findByName(String name);
	public void delete(String entity);
	public boolean exists(String entity);	
}
