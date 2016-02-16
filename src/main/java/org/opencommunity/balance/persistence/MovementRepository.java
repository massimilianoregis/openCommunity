package org.opencommunity.balance.persistence;

import java.util.List;

import org.opencommunity.balance.Movement;
import org.springframework.data.repository.Repository;


public interface MovementRepository extends Repository<Movement, String> 
{
	public List<Movement> findAll();
	public Movement save(Movement user);
	public Movement findOne(String id);	
	public void delete(String entity);
	public boolean exists(String entity);	
}
