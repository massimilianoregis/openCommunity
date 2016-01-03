package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.Device;
import org.springframework.data.repository.Repository;

public interface DeviceRepository extends Repository<Device, String> 
{
	public List<Device> findAll();
	public Device save(Device id);
	public Device findOne(String mail);
	public void delete(String entity);
	public boolean exists(String entity);	
}
