package org.opencommunity.messages;

import java.util.List;

import org.springframework.data.repository.Repository;


public interface MessageRepository extends Repository<Message, String> 
{
	public List<Message> findAll();
	public Message save(Message user);
	public Message findOne(String id);
	public List<Message> findByFromInAndToInOrderByTimeAsc(List<String> from,List<String> to);
	public List<Message> findByFromOrToOrderByTimeAsc(String from,String to);	
	public void delete(String entity);
	public boolean exists(String entity);	
}
