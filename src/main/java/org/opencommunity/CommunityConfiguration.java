package org.opencommunity;

import org.opencommunity.objs.Community;
import org.opencommunity.objs.Envelope;
import org.opencommunity.persistence.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;


@PropertySource({"community.properties"})
@Configuration
public class CommunityConfiguration 
	{
	@Value("${welcome.url}")	private String wUrl;
	@Value("${welcome.from}")	private String from;
	@Value("${community.root}") private String basePath;
	@Value("${smtp.url}") 		private String smtpUrl;
	@Value("${smtp.port}") 		private int smtpPort;
	@Value("${smtp.user}") 		private String smtpUser;
	@Value("${smtp.password}") 	private String smtpPassword;
	@Value("${smtp.auth}") 		private boolean smtpAuth;
	@Autowired
	CommunityRepository communityRepository;
	
	@Bean
	public Community community()
		{
		Community community = communityRepository.findOne("base");
		if(community!=null) return community;
		
		community =  new Community(
				"base",
				basePath,
				new Envelope(from,"welcome",wUrl,postman()),
				"massimiliano.regis@ekaros.it",
				"pippo");		
		community.setDataRegister("{'catalogue':['24Art.General']}".replaceAll("'","\""));
		community.setDataUnknow("{'catalogue':['24Art.General']}".replaceAll("'","\""));
		communityRepository.save(community);
		return community;
		}
		
	@Bean
	public JavaMailSender postman()
		{
		org.springframework.mail.javamail.JavaMailSenderImpl result = new org.springframework.mail.javamail.JavaMailSenderImpl();
			result.setHost(smtpUrl);
			result.setPort(smtpPort);
			result.setUsername(smtpUser);
			result.setPassword(smtpPassword);
			System.out.println(wUrl);
			System.out.println(smtpUser+" "+smtpPassword);
			if(smtpAuth)
				{
				result.getJavaMailProperties().setProperty("mail.smtp.auth", "true");
				result.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "true");
				result.getJavaMailProperties().setProperty("mail.smtp.ssl.trust", smtpUrl);
				}
		return result;
		}

	}
