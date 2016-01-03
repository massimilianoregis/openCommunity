package org.opencommunity.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Transactional("communityTransactionManager")
@RequestMapping("/message")
public class MessageController {
	@Autowired
	private MessageRepository msg;

	@RequestMapping("/test")
	public @ResponseBody void test()
		{
			{
			Message msg = new Message();
				msg.setTo("massimiliano.regis@gmail.com");
				msg.setFrom("michipaperino@gmail.com");
				msg.setMessage("ciao!");
			
			this.msg.save(msg);
			}
			{
			Message msg = new Message();
				msg.setFrom("massimiliano.regis@gmail.com");
				msg.setTo("michipaperino@gmail.com");
				msg.setMessage("ciao!!");
			
			this.msg.save(msg);
			}
		}
	@RequestMapping("/add")
	public @ResponseBody Message add(String from, String to, String message)
		{
		Message msg = new Message();
			msg.setFrom(from);
			msg.setTo(to);
			msg.setMessage(message);
		
		return this.msg.save(msg);
		}
	@RequestMapping("/conversation")
	public @ResponseBody Collection<String> getList(String owner)
		{
		Set<String> result = new HashSet<String>();
		List<Message> msgs = this.msg.findByFromOrToOrderByTimeAsc(owner,owner);
		for(Message msg:msgs)
			if(msg.getTo().equals(owner))	result.add(msg.getFrom());
			else							result.add(msg.getTo());
		return result;
		}
	@RequestMapping("/list")
	public @ResponseBody List<Message> getList(String from, String to)
		{
		if(to==null)	return msg.findByFromOrToOrderByTimeAsc(from,from);
		if(from==null)	return msg.findByFromOrToOrderByTimeAsc(to,to);
		if(from==null&&to==null) return msg.findAll();
		List<String> users = new ArrayList<String>();
			users.add(from);
			users.add(to);
		return msg.findByFromInAndToInOrderByTimeAsc(users, users);
		}
}
