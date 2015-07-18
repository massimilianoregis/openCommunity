package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.Log;
import org.springframework.data.repository.Repository;


public interface LogRepository extends Repository<Log, String> 
{
	public List<Log> findAll();
	public Log save(Log user);
	public Log findOne(String uuis);
	public void delete(String uuis);
	public boolean exists(String uuis);
	public List<Log> findLogByUser(String uid);
}
