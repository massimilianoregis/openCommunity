package org.opencommunity.objs;

import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


@Entity
public class Envelope
	{	
	@Id	
	private String id;
	@Column(name="efrom")
	private String from;
	private String subject;
	private String url;
	@Transient
	static public JavaMailSender postman;	
	public Envelope()
		{
		}
	public Envelope(String from, String subject, String url)
		{		
		this.from=from;
		this.subject=subject;
		this.url=url;
		this.id=subject.hashCode()+"";
		}
	public Envelope(String from, String subject, String url, JavaMailSender postman)
		{		
		this.from=from;
		this.subject=subject;
		this.url=url;
		
		this.postman=postman;
		this.id=subject.hashCode()+"";
		System.out.println("---------------------------");
		System.out.println(this.url);
		System.out.println(this.postman);
		}
	public Envelope setPostman(JavaMailSender postman) {
		Envelope.postman = postman;
		return this;
	}
	public String getId() {
		return id;
	}
	
	public String getSubject() {
		return subject;
	}
	public String getUrl() {
		return url;
	}
	public String getFrom()
		{	
		return from;
		}

	public void setFrom(String from) {
		this.from = from;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void send(String to, Object obj) 
		{
		try
			{
			Map<String,Object> model = null;
			if(obj instanceof Map)
				model=(Map)obj;
			else
				{
				ObjectMapper m = new ObjectMapper();
				model = m.convertValue(obj, Map.class);
				}
				
			URL url = new URL(this.url);						
			VelocityEngineFactoryBean vf =  new VelocityEngineFactoryBean();
				vf.setResourceLoaderPath(url.getProtocol()+"://"+url.getHost()+":"+url.getPort());
				vf.setPreferFileSystemAccess(false);			
			
			String body = VelocityEngineUtils.mergeTemplateIntoString(vf.createVelocityEngine(), url.getPath(), model);
			
			VelocityContext context = new VelocityContext(model);				
			StringWriter writer = new StringWriter();
			Velocity.evaluate(context, writer, "VELOCITY", this.subject);			
			
			MimeMessage mimeMessage = this.postman.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setFrom(this.from);
				message.setTo(to);
				message.setSubject(this.subject);
			System.out.println("send:"+body);
				message.setText(body,true);
				
			
			this.postman.send(mimeMessage);
			}
		catch(Exception e)
			{
			e.printStackTrace();
			}
		}

	
}
