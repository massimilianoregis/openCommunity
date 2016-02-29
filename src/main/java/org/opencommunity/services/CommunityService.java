package org.opencommunity.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.opencommunity.exception.InvalidJWT;
import org.opencommunity.exception.UserJustPresent;
import org.opencommunity.exception.UserNotFound;
import org.opencommunity.objs.Community;
import org.opencommunity.objs.Device;
import org.opencommunity.objs.Envelope;
import org.opencommunity.objs.Log;
import org.opencommunity.objs.Pending;
import org.opencommunity.objs.Role;
import org.opencommunity.objs.User;
import org.opencommunity.persistence.CommunityRepository;
import org.opencommunity.persistence.Repositories;
import org.opencommunity.security.SilentSecured;
import org.opencommunity.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;


@Transactional("communityTransactionManager")
@Controller
@RequestMapping("/community")
public class CommunityService 
	{
	@Autowired
	private Community community;
	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private DataSource datasource;
	
	/**Immagini degli utenti**/
	@RequestMapping(value="/image/{name:.+}",method=RequestMethod.GET)
	public ResponseEntity<byte[]> getImage(@PathVariable String name,@RequestParam(required=false) Integer w,@RequestParam(required=false) Integer h) throws Exception
		{
		final HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_JPEG);

	    return new ResponseEntity<byte[]>(IOUtils.toByteArray(Util.getInstance().getImage(name,w,h)), headers, HttpStatus.CREATED);
		}
		
	static private ObjectMapper json = new ObjectMapper();
	/*Notification*/
	@RequestMapping(value="notify/{mail:.*}/send", method = RequestMethod.GET)
	public @ResponseBody Object sendUser(@PathVariable String mail,@RequestParam(required=false) String title,@RequestParam(required=false) String from, String msg,String type) throws Exception
		{						
		Map map = new HashMap();
			map.put("to",mail);
			map.put("from",from);
			map.put("type",type);
			map.put("target","user");
		return community.getUser(mail).send(title, msg,map);		
		}
	
	@RequestMapping(value="notify/send", method = RequestMethod.POST)
	public @ResponseBody void send(@RequestBody RequestSend send) throws Exception
		{
		if(send.getDevices()!=null)
			community.send(send.getTitle(), send.getText(),send.getPayload(),send.getDevices());
		if(send.getMail()!=null)
			community.sendToMail(send.getTitle(), send.getText(),send.getPayload(),send.getMail());
		}
	@RequestMapping("/notify/{mail:.*}/removeDevice")
	public @ResponseBody void removeDevice(@PathVariable String mail) throws Exception
		{		
		User user =community.getUser(mail); 
			user.getDevices().clear();
			user.save();
		}
	@RequestMapping("/notify/{mail:.*}/addDevice")
	public @ResponseBody void addDevice(@PathVariable String mail,String type, String id) throws Exception
		{		
		User user =community.getUser(mail); 
			user.getDevices().add(new Device(type,id));
			user.save();
		}
	/*/Notification*/
	
	@RequestMapping("/resetDB")
	public @ResponseBody void resetDB() throws Exception
		{						
		
		LocalSessionFactoryBuilder cnf =new LocalSessionFactoryBuilder(datasource);			
			cnf.scanPackages("org.opencommunity");
			cnf.getProperties().setProperty(org.hibernate.cfg.Environment.DIALECT, H2Dialect.class.getName());
			new SchemaExport(cnf).execute(false, true, true,true);
		
		//Persistence.generateSchema("community",null);		
		}
	
	@RequestMapping("/community")
	public @ResponseBody List<Community> communities() throws Exception
		{						
		return communityRepository.findAll();		
		}
	@RequestMapping("/logs")
	public @ResponseBody List<Log> logs(@RequestParam(required=false) String mail) throws Exception
		{						
		if(mail==null)
			return community.getLogs();
		else
			return community.getUser(mail).getLogs();
		}
	
	//@Secured("Admin")		
	@RequestMapping("/list")
	public @ResponseBody List<User> list() throws Exception
		{						
		return community.getUsers();		
		}
	
	@RequestMapping(value="/new", method = RequestMethod.GET)
	public @ResponseBody void save(String name, String surname, String mail, String psw) throws UserJustPresent
		{						
		User usr = community.addUser(mail);				
		usr.setName(name, surname);
		usr.setPassword(psw);
		usr.addRole(community.getUserRole());
		usr.setData(community.getDataRegister());
	    usr.save();
		}
	
	//salva utente
	@RequestMapping(value="/user", method = RequestMethod.POST)
	public @ResponseBody User me(@RequestBody User user) throws UserJustPresent
		{						
		User usr = community.getUser(user.getMail());		
		if(usr==null)	
			{
			usr = community.addUser(user.getMail());
			usr.addRole(community.getUserRole());
			}
		
		usr.extend(user);
			 
	    usr.save();
	    return new JWTResponse(usr,community);	    
		}
	//leggi lista utenti
	@RequestMapping(value= "/user",method=RequestMethod.GET)
	public @ResponseBody List<User> userList(String mail) throws Exception
		{						
		return community.getUsers();		
		}
	//leggi utente singolo
	@RequestMapping(value= "/user/{mail:.+}",method=RequestMethod.GET)
	public @ResponseBody User user(@PathVariable String mail) throws Exception
		{							
		try{
		return community.getUser(mail);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		}
	@RequestMapping(value= "/userget",method=RequestMethod.GET)
	public @ResponseBody User userget(String mail) throws Exception
		{									
		return community.getUser(mail);		
		}
	
	
	@RequestMapping("/pendings")
	public @ResponseBody List<Pending> pendings() throws Exception
		{						
		return Repositories.pending.findAll();		
		}
	@RequestMapping("/confirm/{id}")
	public @ResponseBody User confirm(@PathVariable String id) throws Exception
		{			
		String mail= community.confirmRegistration(id);	
		return new JWTResponse(community.getUser(mail),community);
		}

	/**GESTIONE RUOLI**/
	//@Secured("Admin")	
	@RequestMapping(value="/{mail}/role/{role:.+}",method=RequestMethod.GET)
	public @ResponseBody User list(@PathVariable String mail,@PathVariable String role) throws Exception
		{	
		Role r=Repositories.role.findOne(role);		
		if(r==null)
			{
			r = new Role(role);
			Repositories.role.save(r);
			}
			
		User user = community.getUser(mail);
		if(user.hasRole(role)) user.removeRole(role);
		else user.addRole(r);	
		user.save();
		return user;
		}	
	@RequestMapping(value="/{mail}/delrole/{role:.+}",method=RequestMethod.GET)
	public @ResponseBody User removeUserRole(@PathVariable String mail,@PathVariable String role) throws Exception
		{						
		User user = community.getUser(mail);
		user.removeRole(role);		
		user.save();
		return user;
		}
	@RequestMapping(value="/{mail}/addrole/{role:.+}",method=RequestMethod.GET)
	public @ResponseBody User addUserRole(@PathVariable String mail,@PathVariable String role) throws Exception
		{	
		Role r=Repositories.role.findOne(role);		
		if(r==null)
			{
			r = new Role(role);
			Repositories.role.save(r);
			}
			
		User user = community.getUser(mail);
		user.addRole(r);	
		user.save();
		return user;
		}	
	@RequestMapping(value="/role",method=RequestMethod.DELETE)
	public @ResponseBody void removeRole(String role) throws Exception
		{
		Repositories.role.delete(role);
		}	
	@RequestMapping(value="/role",method=RequestMethod.POST)
	public @ResponseBody void addRole(@RequestParam(required=false) String company,String role) throws Exception
		{
		community.addRole(company,role);		
		}
	@RequestMapping(value="/role",method=RequestMethod.GET)
	public @ResponseBody List<Role> getRoles() throws Exception
		{
		return community.getRoles();
		}	
	@RequestMapping(value="/role/{role:.+}",method=RequestMethod.GET)
	public @ResponseBody List<User> getUserRole(@PathVariable String role) throws Exception
		{
		System.out.println(role);
		return community.getUserFromRole(role);
		}



	
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public @ResponseBody User registerPost(@RequestBody RequestRegister request) throws Exception
		{
		User user = community.addUser(request.getMail()); 
			user.setName(request.getFirst_name(),request.getLast_name());
			user.setPassword(request.getPsw());
			user.setAvatar(request.avatar);
		if(request.getData()!=null)
			user.setData(request.getData());
		else
			user.setData(new HashMap());		
			
		user.register(community.getRole(request.getRole()));
		return user;
		}
	@RequestMapping(value="/register",method=RequestMethod.GET)
	public @ResponseBody User register(@RequestParam(defaultValue="") String mail, String psw, String first_name, String last_name,@RequestParam(required=false) String role) throws Exception
		{				
		User user = community.addUser(mail); 
			user.setName(first_name,last_name);
			user.setPassword(psw);
		if(role!=null)			
			user.addRole(community.getRole(role));
			user.setData(new HashMap());	
		user.register();
		
		return user;
		}
	
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public @ResponseBody Object loginPost(@RequestBody RequestLogin request) throws Exception,UserNotFound
		{
		return login(request.getMail(), request.getPsw());
		}
	
	@RequestMapping("/login")	
	public @ResponseBody Object login(String mail, String psw) throws Exception,UserNotFound
		{								
		if(psw==null || psw.isEmpty()) 
			{
			community.getUser(mail).sendOTP();
			return "{\"msg\":\"sent mail\"}";
			}
	
		User user=  community.login(mail, psw);
		System.out.println("logged:"+user);
		return new JWTResponse(user,community);
		}
	
	@RequestMapping("/login/uid")
	public @ResponseBody User loginByUID(String uid) throws Exception
		{
		try{community.confirmRegistration(uid);}catch(Exception e){}
		User user = Repositories.user.findUserByUid(uid);
		
		System.out.println("logged:"+user);
		return new JWTResponse(user,community);
		}
	
	@RequestMapping("/logout")
	public @ResponseBody void logout() throws Exception
		{				
		community.logout();		
		}	
	

	@RequestMapping("/me")
	public @ResponseBody User me()
		{						
		return community.me();		
		}
	@RequestMapping("/{mail}/sendPsw")
	public @ResponseBody void sendPassword(@PathVariable String mail) throws Exception
		{						
		community.getUser(mail).sendPassword();		
		}
	@RequestMapping("/{mail}/resetPsw")
	public @ResponseBody void resetPassword(@PathVariable String mail) throws Exception
		{						
		community.getUser(mail).resetPassword();		
		}
	@RequestMapping("/me/data/{name:.+}")
	public @ResponseBody byte[] data(@PathVariable String name) throws IOException
		{											
		return org.apache.commons.io.IOUtils.toByteArray(community.me().getFile(name));		
		}
	
	@RequestMapping(value="/user/{mail:.+}/extra", method = RequestMethod.POST)
	public @ResponseBody User saveData(@PathVariable String mail,@RequestBody Map map) 
		{	
		User user = this.community.getUser(mail);
		Map data = user.getData();
		if(user.getData()==null) 				
			data=new HashMap();							
			data.putAll(map);
			
		user.setData(data);
		user.save();
		return user;
		}
	
	@RequestMapping(value="/jwt")
	public @ResponseBody User check(String jwt) throws InvalidJWT
		{							
		User user = this.community.getUserFromJWT(jwt);
		return new JWTResponse(user, this.community,jwt);		
		}
	
	@RequestMapping(value="/sendMail")
	public @ResponseBody void send(@RequestBody RequestSendMail req ) throws Exception
		{			
		new Envelope(req.getFrom(),req.getSubject(),req.getTemplate()).send(req.getTo(), req.getData());
		}
	
	@SilentSecured("admin")		
	@RequestMapping(value="/accessTest")
	public @ResponseBody String test()
		{			
		return "CIAO";				
		}
	
	  @ExceptionHandler(Exception.class)
	  @ResponseBody
	  public Exception handleException(Exception  exception) {
	      return exception;
	  } 

	static public class RequestSendMail
		{
		String from;
		String to;
		String subject;
		String template;			
		Map data;
		public String getFrom() 					{return from;}
		public void setFrom(String from) 			{this.from = from;}
		public String getTo() 						{return to;}
		public void setTo(String to) 				{this.to = to;}
		public String getSubject() 					{return subject;}
		public void setSubject(String subject) 		{this.subject = subject;}
		public String getTemplate() 				{return template;}
		public void setTemplate(String template) 	{this.template = template;}
		public Map getData() 						{return data;}
		public void setData(Map data) 				{this.data = data;}		
		}
	static public class RequestLogin
		{
		private String mail;
		private String psw;		
		
		public String getMail() {return mail;}
		public void setMail(String mail) {this.mail = mail;}
		public String getPsw() {return psw;}
		public void setPsw(String psw) {this.psw = psw;}		
		}
	static public class RequestRegister
		{
		private String mail;
		private String psw;
		private String first_name;
		private String last_name;
		private String avatar;
		private Map data;
		private String role;
		
		public String getAvatar() {return avatar;}
		public void setAvatar(String avatar) {this.avatar = avatar;}
		public String getMail() {return mail;}
		public void setMail(String mail) {this.mail = mail;}
		public String getPsw() {return psw;}
		public void setPsw(String psw) {this.psw = psw;}
		public String getFirst_name() {return first_name;}
		public void setFirst_name(String first_name) {this.first_name = first_name;}
		public String getLast_name() {return last_name;}
		public void setLast_name(String last_name) {this.last_name = last_name;}
		public Map getData() 			{return data;}
		public void setData(Map data) 	{this.data = data;}
		public String getRole() {return role;}
		public void setRole(String role) {this.role = role;}
		}
	static public class JWTResponse extends User
		{
		private String jwt;
		private String community;
		public JWTResponse(){}
		public JWTResponse(User user,Community community,String jwt)
			{
			this.jwt=jwt;
			this.community=community.getName();
			setUser(user);			
				
			}
		public JWTResponse(User user,Community community)
			{
			this(user,community.getName(),community.getSecretKey());
			}
		public JWTResponse(User user,String name,String secret)
			{			
			setUser(user);			
			this.community=name;
			
			try{Payload payload = new Payload(json.writeValueAsString(this));
				JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
				JWSObject jwsObject = new JWSObject(header, payload);		
				JWSSigner signer = new MACSigner(secret.getBytes());
				jwsObject.sign(signer);		
				this.jwt=jwsObject.serialize();
				
			}catch(Exception e){e.printStackTrace();}
			}
		public void setUser(User user)
			{
			setFirstName(user.getFirstName());
			setLastName(user.getLastName());
			setMail(user.getMail());
			setRegisterId(user.getRegisterId());
			setRoles(user.getRoles());			
			setAddresses(user.getAddresses());
			setData(user.getData());
			try{setAvatar(user.getAvatar());}catch(Exception e){}
			try{setBackground(user.getBackground());}catch(Exception e){}	
			}
		public String getCommunity() 
			{
			return community;
			}
		public String getJwt() 
			{
			return jwt;
			}
		}
	
	public static class RequestSend
		{
		private String title;
		private String text;
		private String link;
		private Map payload;
		private String[] devices;
		private String[] mail;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		public String[] getDevices() {
			return devices;
		}
		public void setDevices(String[] devices) {
			this.devices = devices;
		}
		public String[] getMail() {
			return mail;
		}
		public void setMail(String[] mail) {
			this.mail = mail;
		}
		public Map getPayload() {
			return payload;
		}
		public void setPayload(Map payload) {
			this.payload = payload;
		}
		}
	
	
	}

