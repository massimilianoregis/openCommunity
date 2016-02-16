package org.opencommunity.balance.persistence;

import java.util.List;

import org.opencommunity.balance.Balance;
import org.springframework.data.repository.Repository;


public interface BalanceRepository extends Repository<Balance, String> 
{
	public List<Balance> findAll();
	public Balance save(Balance user);
	public Balance findOne(String id);
	public List<Balance> findBalanceByUser(String user);
	public void delete(String entity);
	public boolean exists(String entity);	
}
