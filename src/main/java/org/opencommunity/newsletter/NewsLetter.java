package org.opencommunity.newsletter;

import java.util.List;
import java.util.Map;

import org.opencommunity.newsletter.repository.MailRepository;
import org.opencommunity.objs.Envelope;
import org.opencommunity.persistence.EnvelopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class NewsLetter 
	{

	private JavaMailSender sender;	
	
	MailRepository mailRepository;
	
	EnvelopeRepository envelopeRepository;
	
	@Autowired
	public NewsLetter(MailRepository mail, EnvelopeRepository template, JavaMailSender sender)
		{
		this.mailRepository=mail;
		this.envelopeRepository=template;
		this.sender=sender;
		}
		
	public void send(String from, String to,String subject, String url, Map map)
		{
		new Envelope(from, subject, url, sender).send(to, map);
		}
	
	public Envelope getTemplate(String name)
		{
		return envelopeRepository.findOne(name).setPostman(sender);
		}
	public List<Envelope> getTemplates()
		{
		return envelopeRepository.findAll();
		}
	
	public List<Mail> getAllMail()
		{
		return mailRepository.findAll();
		}
	public Mail getMail(String mail)
		{
		return mailRepository.findOne(mail);
		}
	public List<Mail> getMailByTemplate(String template)
		{
		return mailRepository.findByGroupsName(template);
		}
	
	public void save(Envelope template)
		{
		envelopeRepository.save(template);
		}
	public void save(Mail mail)
		{
		mailRepository.save(mail);
		}
	}
