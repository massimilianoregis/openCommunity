package org.opencommunity.persistence;

import java.util.List;

import org.opencommunity.objs.User;
import org.springframework.data.repository.Repository;


public interface UserRepository extends Repository<User, String> 
{
	public List<User> findAll();
	public User save(User user);
	public User findOne(String mail);
	public void delete(String entity);
	public boolean exists(String entity);
	public List<User> findDistinctUserByRolesCompany(String name);	
	public User findUserByUid(String uid);
	public User findUserByMail(String uid);
}
