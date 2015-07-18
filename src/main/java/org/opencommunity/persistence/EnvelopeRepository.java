package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.Envelope;
import org.opencommunity.objs.User;
import org.springframework.data.repository.Repository;


public interface EnvelopeRepository extends Repository<Envelope, String> 
{
	public List<Envelope> findAll();
	public Envelope save(Envelope user);
	public Envelope findOne(String name);
	public void delete(String entity);
	public boolean exists(String entity);	
}
