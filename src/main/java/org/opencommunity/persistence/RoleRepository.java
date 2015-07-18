package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.Role;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

public interface RoleRepository extends Repository<Role, String> 
{
	public List<Role> findAll();
	public Role save(Role wsdl);
	public Role findOne(String mail);
	public void delete(String entity);
	public boolean exists(String entity);	
}
