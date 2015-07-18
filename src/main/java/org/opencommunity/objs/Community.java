package org.opencommunity.objs;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.opencommunity.exception.InvalidJWT;
import org.opencommunity.exception.UserJustPresent;
import org.opencommunity.exception.UserNotFound;
import org.opencommunity.objs.user.UnknowUser;
import org.opencommunity.persistence.Repositories;
import org.opencommunity.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name="Communities")
public class Community 
	{	
	@Id			
	private String name;
	private String root;	
	@OneToOne(cascade=CascadeType.ALL) 
	private Envelope welcome;
	private String secretKey;
	private String dataRegister;
	private String dataUnknow;
	
	
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
	public Community(String name,String root, Envelope welcome,String admin)
		{
		this(name,root,welcome,admin,null);
		}
	public Community(String name,String root, Envelope welcome,String admin,String psw)
		{
		Community.instance=this;
		this.root=root;
		this.welcome=welcome;
		this.name=name;
		this.secretKey=UUID.randomUUID().toString();
			addRole(Role.ADMIN);
			addRole(Role.USER);
		try{
			User user = addUser(admin);						 
			 	 user.addRole(getAdminRole());
			 	 user.addRole(getUserRole());
			 	 user.setPassword(psw);
			 	 user.save();
			 	 user.sendPassword();
			}
		catch(UserJustPresent e){}	
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
		}
	public void resetPasswordMail(User user)
		{
		System.out.println("send to:"+user.getMail()+" "+welcome);
		welcome.send(user.getMail(),user);		
		}
	
	
	public void sendPasswordMail(User user)
		{
		System.out.println("send to:"+user.getMail()+" "+welcome);
		welcome.send(user.getMail(),user);		
		}
	
	
	public void sendWelcomeMail(User user)
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
	public void addRole(String name)
		{
		Repositories.role.save(new Role(name,this.name));
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
	public void confirmRegistration(String id) 
		{
		Repositories.pending.findOne(id).execute();
		}
	public String getDataRegister() {
		return dataRegister;
	}
	public String getDataUnknow() {
		return dataUnknow;
	}
	
	public User getUserFromJWT(String jwt)
		{
		if(jwt==null)
			{
			User user = new UnknowUser(this);
			user.setData(this.dataUnknow);
			return user;
			}
		try {			
			String mail = (String)new JWT(jwt,this).getObject().get("mail");
			if(mail==null)
				{
				User user = new UnknowUser(this);
				user.setData(this.dataUnknow);
				return user;
				}
			return  getUser(mail);		
			}
		catch (InvalidJWT e) {
			User user = new UnknowUser(this);
				user.setData(this.dataUnknow);
			return user;
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
	}
