package org.opencommunity.objs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.opencommunity.exception.InvalidJWT;
import org.opencommunity.exception.UserJustPresent;
import org.opencommunity.exception.UserNotFound;
import org.opencommunity.notification.Notify;
import org.opencommunity.objs.user.UnknowUser;
import org.opencommunity.persistence.Repositories;
import org.opencommunity.services.CommunityService.JWTResponse;
import org.opencommunity.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name="Communities")
public class Community 
	{	
	@Id			
	private String name;
	private String root;	
	@OneToOne(cascade=CascadeType.ALL) private Envelope welcome;
	@OneToOne(cascade=CascadeType.ALL) private Envelope otp;
	private String secretKey;
	private String dataRegister;
	private String dataUnknow;
	@OneToOne(cascade=CascadeType.ALL)
	private Notify notify;
	
	
	@Autowired
	@Transient
	@JsonIgnore
	private SessionData session;
	
	
	
	static private Community instance;
	static public Community getInstance()
		{
		return instance;
		}
	public Community()
		{		
		Community.instance=this;
		}
	
	public Community(String name,String root, Envelope welcome,Envelope otp,String secretKey, Notify notify)
		{
		Community.instance=this;
		this.root=root;
		this.welcome=welcome;
		this.otp=otp;
		this.name=name;
		this.secretKey=secretKey;		
		this.notify=notify;
		}
	public void setDataRegister(String data)		{this.dataRegister=data;}
	public void setDataUnknow(String data)			{this.dataUnknow=data;}
	
	
	public String getName() 		{return name;}
	public String getRoot() 		{return root;}
	public Envelope getWelcome() 	{return welcome;}
	
	
	public User me()				{return session.getUser();}
	public void logout()			{session.setUser(null);}
	
	/**USER**/
	public User addUser(String mail) throws UserJustPresent
		{
		if(Repositories.user.exists(mail)) throw new UserJustPresent();
		User user = new User(mail,this, new File(this.root,mail));			
			 user.setData(dataRegister);
		return user;
		}			
	public User login(String mail, String psw) throws UserNotFound
		{				
		User user = Repositories.user.findOne(mail);	
		
		if(user!=null && user.canAccess(psw,this))
			{
			try	{Repositories.log.save(new Log(mail,psw,true));}catch (Exception e) {}
			setActualUser(user);
			return user;
			}
		try	{Repositories.log.save(new Log(mail,psw,false));}catch (Exception e) {}	
		throw new UserNotFound();
		}
	public User getUser(String mail)
		{
		return Repositories.user.findOne(mail);
		}
	//@Transactional
	public void save(User user)
		{		
		try{user.getRoot().mkdirs();}catch(Exception e){}
		Repositories.user.save(user);
		}
	@JsonIgnore
	public List<User> getUsers()
		{		
		return Repositories.user.findDistinctUserByRolesCompany(name);
		//return Repositories.user.findAll();
		}
	public void resetPasswordMail(User user) throws Exception
		{
		System.out.println("send to:"+user.getMail()+" "+welcome);
		welcome.send(user.getMail(),user);		
		}
	
	
	public void sendPasswordMail(User user) throws Exception
		{
		System.out.println("send to:"+user.getMail()+" "+welcome);
		welcome.send(user.getMail(),user);		
		}
	
	public void sendOTP(User user) throws Exception
		{
		System.out.println("send to:"+user.getMail()+" "+welcome);
		if(otp!=null)	otp.send(user.getMail(),user);
		if(otp==null)	welcome.send(user.getMail(),user);
		}
	
	public void sendWelcomeMail(User user) throws Exception
		{
		System.out.println("send to:"+user.getMail()+" "+welcome);
		welcome.send(user.getMail(),user);		
		}
	
	
	/**ROLES**/
	public Role getUserRole()
		{
		return Repositories.role.findOne(new Role(Role.USER,this.name).getId());
		}
	public Role getAdminRole()
		{
		return Repositories.role.findOne(new Role(Role.ADMIN,this.name).getId());
		}
	public Role getRole(String name)
		{
		return Repositories.role.findOne(new Role(name,this.name).getId());
		}
	public void addRole(String name){
		addRole(null,name);
	}
	public void addRole(String company,String name)
		{
		Repositories.role.save(new Role(company==null?name:company,this.name));
		}
	public List<User> getUserFromRole(String role)
		{
		return Repositories.user.findUserByRolesId(role);
		}
	public List<Role> getRoles()
		{		
		return Repositories.role.findAll();
		}
	
	
	
	public String getSecretKey() 
		{
		return secretKey;
		}
	public int maxLoginTry()
		{
		return 3;
		}
	public void setActualUser(User user)
		{
		this.session.setUser(user);
		}
	public String confirmRegistration(String id) 
		{
		Pending pending=Repositories.pending.findOne(id);		
		pending.execute();
		return pending.getUser();
		}
	public String getDataRegister() {
		return dataRegister;
	}
	public String getDataUnknow() {
		return dataUnknow;
	}
	
	public User getUserFromJWT(String jwt) throws InvalidJWT
		{
		if(jwt==null)
			{
			User user = new UnknowUser(this);
			user.setData(this.dataUnknow);
			return user;
			}
		try {						
			return new JWT(jwt,this).getUser();
			/*
			String mail = (String)new JWT(jwt,this).getObject().get("mail");
			if(mail==null)
				{
				User user = new UnknowUser(this);
				user.setData(this.dataUnknow);
				return user;
				}
			return  getUser(mail);*/		
			}		
		catch (InvalidJWT e) {
//			User user = new UnknowUser(this);
//				user.setData(this.dataUnknow);
//			return user;
			throw e;
			}
		catch (Exception e) {
//			User user = new UnknowUser(this);
//				user.setData(this.dataUnknow);
//			return user;
			throw new InvalidJWT();
			}
		}
	
	@JsonIgnore
	public List<Log> getLogs()
		{
		return Repositories.log.findAll();
		}
	@JsonIgnore
	public List<Pending> getPengings()
		{
		return Repositories.pending.findAll();
		}
	
	/*NOTIFY*/
	public Notify getNotify() {
		return notify;
	}
	public void setNotify(Notify notify) {
		this.notify = notify;
	}
	public void sendToMail(String title, String msg, Map payload,String ... mail) throws Exception{
		List devices= new ArrayList();
		for(String item:mail)
			try{
			Set<Device> dvs = this.getUser(item).getDevices();
			for(Device d:dvs)
				devices.add(d.getId());
			}catch(Exception e){}
			
		if(devices.size()>0)
		this.notify.send(title, msg,payload, (String[])devices.toArray(new String[0]));
	}
	public Object send(String title, String msg, Map payload,String ... devices) throws Exception{		
		return this.notify.send(title, msg,payload, devices);
	}
	/*/NOTIFY*/
	
	public static void main(String[] args)throws Exception {
		String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJtYWlsIjoibWFzc2ltaWxpYW5vLnJlZ2lzQGdtYWlsLmNvbSIsInBzdyI6bnVsbCwiZmlyc3ROYW1lIjoiTWFzc2ltaWxpYW5vIiwibGFzdE5hbWUiOiJSZWdpcyIsImxhc3RhY2Nlc3N0aW1lIjpudWxsLCJzZW5kUmVnaXN0ZXJNYWlsIjpudWxsLCJiYWNrZ3JvdW5kIjpudWxsLCJhdmF0YXIiOiJodHRwOi8vOTUuMTEwLjIyOC4xNDA6ODA4MC9vcGVuQ29tbXVuaXR5L2NvbW11bml0eS9pbWFnZS9waWN0dXJlP3R5cGU9bGFyZ2UiLCJsb2d0cnkiOjAsInJlZ2lzdGVySWQiOm51bGwsInJvbGVzIjpbeyJpZCI6ImJhc2UudXNlciIsIm5hbWUiOiJ1c2VyIiwiY29tcGFueSI6ImJhc2UifV0sImp3dCI6bnVsbCwiY29tbXVuaXR5IjoiYmFzZSIsImRhdGEiOnt9fQ.0Mjb6G-nRlj4ylwBDWPTUnQSPprFd8e1HXQp8y6dG3k";
		User user = new JWT(jwt, "37ea72d1-5c9e-4635-92c6-e732406aac21").getUser();
		JWTResponse response = new JWTResponse(user,"base","37ea72d1-5c9e-4635-92c6-e732406aac21");
		System.out.println(user.getFirstName());
	}
	}


