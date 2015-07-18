package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.Pending;
import org.springframework.data.repository.Repository;


public interface PendingRepository extends Repository<Pending, String> 
{
	public List<Pending> findAll();
	public Pending save(Pending pending);
	public Pending findOne(String id);
	public void delete(String entity);
	public boolean exists(String entity);
	
}
