package org.opencommunity.newsletter.repository;

import java.util.List;

import org.opencommunity.newsletter.Mail;
import org.opencommunity.objs.User;
import org.springframework.data.repository.Repository;


public interface MailRepository extends Repository<Mail, String> 
{
	public List<Mail> findAll();
	public Mail save(Mail user);
	public Mail findOne(String mail);
	public void delete(String entity);
	public boolean exists(String entity);
	public List<Mail> findByGroupsName(String template);
}
