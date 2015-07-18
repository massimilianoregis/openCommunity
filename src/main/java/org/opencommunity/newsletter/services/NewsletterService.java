package org.opencommunity.newsletter.services;

import java.util.List;

import org.opencommunity.newsletter.Mail;
import org.opencommunity.newsletter.Mail.Group;
import org.opencommunity.newsletter.NewsLetter;
import org.opencommunity.objs.Envelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Transactional("communityTransactionManager")
@Controller
@RequestMapping("/newsletter")
public class NewsletterService 
	{
	@Autowired
	NewsLetter newsletter;
	
	@RequestMapping(value="/mail", method = RequestMethod.GET)
	public @ResponseBody List<Mail> getList()
		{
		return newsletter.getAllMail();
		}
	
	@RequestMapping(value="/mail", method = RequestMethod.POST)
	public @ResponseBody String setMail(@RequestBody Mail mail)
		{
		newsletter.save(mail);
		return "OK";
		}
	
	@RequestMapping(value="/template", method = RequestMethod.GET)
	public @ResponseBody List<Envelope> getTemplate()
		{		
		return newsletter.getTemplates();
		}
	
	@RequestMapping(value="/template", method = RequestMethod.POST)
	public @ResponseBody void saveTemplate(@RequestBody Envelope template)
		{		
		newsletter.save(template);
		}
	
	@RequestMapping(value="/send", method = RequestMethod.GET)
	public @ResponseBody boolean sendTemplate(String mail,String template)
		{
		try
			{
			Mail to = newsletter.getMail(mail);
			newsletter.getTemplate(template).send(to.getMail(), to);
			for(Group gr :to.getGroups())
				if(gr.getName().equals(template))
					gr.setSent(true);
			this.newsletter.save(to);
			return true;
			}
		catch(Exception e)
			{
			return false;
			}
		}
	@RequestMapping(value="/sendAll", method = RequestMethod.GET)
	public @ResponseBody void sendTemplate(String template)
		{
		List<Mail> tos = newsletter.getMailByTemplate(template);
		System.out.println(tos.size());
		for(Mail to:tos)
			newsletter.getTemplate(template).send(to.getMail(), to);
		}
	
	@RequestMapping(value="/sendOne", method = RequestMethod.GET)
	public void send(@RequestBody SendRequest request) throws Exception
		{
		newsletter.send(
				request.getFrom(), 
				request.getTo(), 
				request.getSubject(), 
				request.getUrl(), 
				request.getData());
		}
	}

