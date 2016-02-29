package org.opencommunity;

import java.io.IOException;
import java.net.InetAddress;

import org.opencommunity.notification.Notify;
import org.opencommunity.objs.Community;
import org.opencommunity.objs.Envelope;
import org.opencommunity.objs.Role;
import org.opencommunity.objs.User;
import org.opencommunity.persistence.CommunityRepository;
import org.opencommunity.persistence.Repositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mail.javamail.JavaMailSender;


//@PropertySource({"community.properties"})
@Configuration
public class CommunityConfiguration {
	
	 
	@Value("${welcome.url}")	private String wUrl;	
	@Value("${welcome.from}")	private String from;
	@Value("${welcome.subject}")private String subject;
	@Value("${otp.url}")		private String otpUrl;	
	@Value("${otp.from}")		private String otpFrom;
	@Value("${otp.subject}")	private String otpSubject;
	@Value("${community.root}") private String basePath;
	@Value("${smtp.url}") 		private String smtpUrl;
	@Value("${smtp.port}") 		private int smtpPort;
	@Value("${smtp.user}") 		private String smtpUser;
	@Value("${smtp.password}") 	private String smtpPassword;
	@Value("${smtp.auth}") 		private boolean smtpAuth;
	@Value("${community.secret}") 	private String secretKey;
	@Autowired
	CommunityRepository communityRepository;
	@Autowired
	Repositories repository;
	@Autowired
	Notify notify;
	
	@Bean
	public Community community()
		{
		Community community = communityRepository.findOne("base");
		Envelope.postman=postman();
		if(community!=null) return community;
		
		community =  new Community(
				"base",
				basePath,
				new Envelope(from,subject,wUrl,postman()),
				new Envelope(otpFrom,otpSubject,otpUrl,postman()),
				secretKey,
				notify
				);		
		community.addRole(Role.ADMIN);
		community.addRole(Role.USER);
		
		try{
			User user = community.addUser("massimiliano.regis@ekaros.it");						 
			 	 user.addRole(community.getAdminRole());
			 	 user.addRole(community.getUserRole());
			 	 user.setPassword("pippo");
			 	 user.save();
			 	 user.sendPassword();
			}
		catch(Exception e){}	
				
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
			if(!smtpUser.isEmpty())		result.setUsername(smtpUser);
			if(!smtpPassword.isEmpty())	result.setPassword(smtpPassword);			
			if(smtpAuth)
				{
				result.getJavaMailProperties().setProperty("mail.smtp.auth", "true");
				result.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "true");
				result.getJavaMailProperties().setProperty("mail.smtp.ssl.trust", smtpUrl);
				}
		
		return result;
		}
	 @Bean
     public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
         PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
         propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(Boolean.TRUE);
         try{
         if(InetAddress.getLocalHost().getHostAddress().equals("95.110.228.140"))
        	 propertySourcesPlaceholderConfigurer.setLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:online/*.properties"));
         else
        	 propertySourcesPlaceholderConfigurer.setLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:local/*.properties"));
         }catch(Exception e)
         {
        	 propertySourcesPlaceholderConfigurer.setLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:online/*.properties"));
         }
         
         return propertySourcesPlaceholderConfigurer;
     }
	}
