package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.Application;
import org.opencommunity.objs.Community;
import org.springframework.data.repository.Repository;


public interface CommunityRepository extends Repository<Community, String> 
{
	public List<Community> findAll();
	public Community save(Community user);
	public Community findOne(String id);	
	public void delete(String entity);
	public boolean exists(String entity);	
}
