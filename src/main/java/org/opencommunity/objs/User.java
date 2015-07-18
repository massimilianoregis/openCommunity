package org.opencommunity.objs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.opencommunity.exception.InvalidPassword;
import org.opencommunity.persistence.Repositories;
import org.opencommunity.util.AutomaticPassword;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class User implements Serializable 
{		
	@Id
	private String mail;
	@JsonIgnore
	@Column(unique=true)
	private String uid;
	
	//@JsonIgnore
	private String psw;	
	private String firstName;
	private String lastName;
	private String root;
	private Long lastaccesstime;
	private Date sendRegisterMail;
	
	@Transient
	private Integer logtry=0;
	
	//private List<String> tags;
	@Transient
	private String registerId;
	@Column(columnDefinition="TEXT")
	@JsonIgnore
	private String jsondata;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = {CascadeType.ALL})
	private Set<Role> roles = new HashSet<Role>();
	
	
	
	public User()
		{		
		}
	public User(String mail,Community cm, File root)
		{
		this.mail=mail;		
		root.mkdirs();
		this.root=root.getAbsolutePath();
		this.psw=AutomaticPassword.newPassword();
		
		this.uid=UUID.randomUUID().toString();		
		}
	
	public void setMail(String mail) 				{this.mail = mail;}
	public void setPsw(String psw) 					{this.psw = psw;}
	public void setFirstName(String firstName) 		{this.firstName = firstName;}
	public void setLastName(String lastName) 		{this.lastName = lastName;}
	public void setRoot(String root) 				{this.root = root;}
	public void setRegisterId(String registerId) 	{this.registerId = registerId;}
	public void setRoles(Set<Role> roles) 			{this.roles = roles;}
	public void setName(String fname, String sname)	{this.firstName=fname; this.lastName=sname;}
	public void setUid(String uid) 					{this.uid = uid;}
	public boolean canAccess(String psw,Community cm)
		{			
		System.out.println(logtry+"-->"+cm.maxLoginTry()+"--"+psw);
		if(logtry>=cm.maxLoginTry()) return false;
		this.logtry++;
		if(!this.psw.equals(psw) || !canAccess(Role.USER,cm.getName()))
			{						
				; //disable user psw
			return false;
			}		
		
		cm.setActualUser(this);
		this.lastaccesstime=System.currentTimeMillis();
		this.logtry=0;	
		return true;
		}
	
	
	
	public void setPassword(String psw)				
		{
		if(psw!=null && !psw.isEmpty())
			this.psw=psw;
//		byte[] bytesOfMessage = psw.getBytes("UTF-8");
//
//		MessageDigest md = MessageDigest.getInstance("MD5");
//		byte[] thedigest = md.digest(bytesOfMessage);
//		this.psw=new String(thedigest);
		}
	public String getUid() 			{return uid;}
	public String getMail() 		{return mail;}	
	public String getFirstName() 	{return firstName;}
	public String getLastName() 	{return lastName;}
	public String getPsw() 			{return psw;}		
	public Set<Role> getRoles() 	{return roles;}
	public String getRegisterId() 	{return registerId;}
	public Integer getLogtry() 		{return logtry;}
	@JsonIgnore
	public List<Log> getLogs()		
		{
		return Repositories.log.findLogByUser(this.mail);
		}

	
	@JsonIgnore
	public File getRoot() 			{return new File(root);}
	
	
	
	public void setPassword(String psw, String psw2) throws InvalidPassword
		{
		if(!psw.equals(psw2)) throw new InvalidPassword();
		setPassword(psw);
		this.save();
		}
	
	
	public void resetPassword()
		{
		this.psw= new AutomaticPassword().newPassword();
		Community.getInstance().resetPasswordMail(this);
		}
	
	
	public void sendPassword()
		{
		Community.getInstance().sendPasswordMail(this);
		}

	
	public boolean canAccess(String role, String company)
		{
		for(Role r : roles)
			{			
			if(company==null && r.getName().equals(role)) 		return true;
			if(r.getId().equals(company+"."+role))				return true;
			}
		return false;
		}
	
	
	public void addUserRole()
		{
		addRole(Community.getInstance().getUserRole());
		}
	
	
	public void addRole(Role role)
		{
		if(role==null) return;
		this.roles.add(role);
		}
	
	
	public void register()
		{		
		save();
		this.registerId = new Pending(this,Community.getInstance().getUserRole()).save();
		this.sendWelcome();
		}
	public void register(Role role)
		{		
		save();
		this.registerId = new Pending(this,Community.getInstance().getUserRole(),role).save();
		this.sendWelcome();
		}
	
	public void save()
		{				
		Repositories.user.save(this);		
		}
	
	
	public void sendWelcome()
		{
		Community.getInstance().sendWelcomeMail(this);
		sendRegisterMail=new Date();
		save();
		}
	
	
	@JsonGetter
	public Map getData()
		{		
		try{
			return mapper.readValue(jsondata, Map.class);			
			}
		catch(Exception e){return new HashMap();}		
		}
	public void setData(String json)
		{
		this.jsondata=json;
		}
	@JsonSetter
	public void setData(Map map)
		{	
		try	{
			this.jsondata =mapper.writeValueAsString(map);
			}
		catch(Exception e){}		
		}
	public InputStream getFile(String file) throws IOException
		{
		System.out.println(this.root);
		return new FileInputStream(new File(this.root,file));
		}
	public Long getLastaccesstime() {
		return lastaccesstime;
	}
	public void setLastaccesstime(Long lastaccesstime) {
		this.lastaccesstime = lastaccesstime;
	}
	public Date getSendRegisterMail() {		
		return sendRegisterMail;
	}
	public void setSendRegisterMail(Date sendRegisterMail) {
		this.sendRegisterMail = sendRegisterMail;
	}
	
//	public void setJsondata(String jsondata) throws Exception 
//		{
//		System.out.println("------SET-----");
//		System.out.println(jsondata);
//		jsondata = new String(Base64.decodeBase64(jsondata));		
//		System.out.println(jsondata);
//		System.out.println(((ObjectNode)mapper.readTree("{}")));
//		jsondata= extractJson().putAll((ObjectNode)mapper.readTree(jsondata)).asText();
//		System.out.println(jsondata);		
//		System.out.println("------SET-----");
//		}
	@Override
	public String toString() {
		return this.mail;
	}
	
	
	static private 	ObjectMapper mapper = new ObjectMapper();	

	public void extend(User user)
		{
		this.firstName=user.firstName;
		this.lastName=user.lastName;
		if(user.jsondata!=null)	this.jsondata=user.jsondata;
		if(user.psw!=null)		this.psw=user.psw;		
		}
}
