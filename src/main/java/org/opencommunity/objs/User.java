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
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.opencommunity.exception.InvalidPassword;
import org.opencommunity.persistence.Repositories;
import org.opencommunity.util.AutomaticPassword;
import org.opencommunity.util.Util;

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
	private Date lastaccesstime;
	private Date sendRegisterMail;
	private String background;
	private String avatar;
	private String locale;
	
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
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = {CascadeType.ALL})
	private Set<Device> devices = new HashSet<Device>();
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = {CascadeType.ALL})
	private Set<Address> addresses = new HashSet<Address>();
	
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
	public void setAddresses(Set<Address> addresses) {this.addresses = addresses;}
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
		this.lastaccesstime=new Date();
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
	public Set<Address> getAddresses() {return addresses;}
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
	
	
	public void resetPassword() throws Exception
		{
		this.psw= new AutomaticPassword().newPassword();
		Community.getInstance().resetPasswordMail(this);
		}
	
	
	public void sendPassword() throws Exception
		{
		Community.getInstance().sendPasswordMail(this);
		}

	public boolean hasRole(String role)
		{
		for(Role r : roles)
			if(r.getId().equals(role)) return true;
		return false;
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
	
	public void removeRole(String role)
		{
		for(Role r : roles)
			{			
			if(r.getId().equals(role)) 
				{
				this.roles.remove(r);
				return;
				}
			}
		}
	public void addRole(Role role)
		{
		if(role==null) return;
		this.roles.add(role);
		}
	
	
	public void register() throws Exception
		{		
		save();
		this.registerId = new Pending(this,Community.getInstance().getUserRole()).save();
		this.sendWelcome();
		}
	public void register(Role role) throws Exception
		{		
		save();
		this.registerId = new Pending(this,Community.getInstance().getUserRole(),role).save();
		this.sendWelcome();
		}
	
	public void save()
		{				
		Repositories.user.save(this);		
		}
	public void sendOTP() throws Exception
		{
		Community.getInstance().sendOTP(this);				
		}
	
	public void sendWelcome() throws Exception
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
	public Date getLastaccesstime() {
		return lastaccesstime;
	}
	public void setLastaccesstime(Date lastaccesstime) {
		this.lastaccesstime = lastaccesstime;
	}
	public Date getSendRegisterMail() {		
		return sendRegisterMail;
	}
	public void setSendRegisterMail(Date sendRegisterMail) {
		this.sendRegisterMail = sendRegisterMail;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) throws Exception{
	this.avatar=avatar;
	try{this.avatar = Util.getInstance().saveImage(avatar);}catch(Exception e){}
	}
	public String getBackground() {
		return background;
	}
	public void setBackground(String background) throws Exception{
		this.background = background;
		try{this.background = Util.getInstance().saveImage(background);}catch(Exception e){}
		
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
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
	
	
	/*DISPOSITIVI*/
	public Set<Device> getDevices() {
		return devices;
	}
	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}
	public void send(String title, String msg, Map from)	throws Exception{
		List<String> list = new Vector<String>();
		for(Device dev:devices)
			list.add(dev.getId());
		
		Community.getInstance().send(title, msg,from,list.toArray(new String[0]));
		}
	/*/DISPOSITIVI*/
	
	
	static private 	ObjectMapper mapper = new ObjectMapper();	

	public void extend(User user)
		{
		this.firstName	=	user.firstName;
		this.lastName	=	user.lastName;
		try{this.setBackground(user.background);}catch(Exception e){}
		try{this.setAvatar(user.avatar);}catch(Exception e){}
		this.setAddresses(user.getAddresses());
		
		if(user.jsondata!=null)	this.jsondata=user.jsondata;
		if(user.psw!=null)		this.psw=user.psw;		
		}
}
