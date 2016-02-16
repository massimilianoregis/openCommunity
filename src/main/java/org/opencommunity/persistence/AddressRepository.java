package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.Address;
import org.springframework.data.repository.Repository;

public interface AddressRepository extends Repository<Address, String> 
{
	public List<Address> findAll();
	public Address save(Address item);
	public Address findOne(String id);
	public void delete(String entity);
	public boolean exists(String entity);	
}
