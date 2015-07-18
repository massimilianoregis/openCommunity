package org.community.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.junit.Test;
import org.opencommunity.objs.Pending;
import org.opencommunity.objs.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommunityTest 
	{
	private String id;
	private String mail = "massimiliano@ekaros.it";
	private String admin = "massimiliano.regis@ekaros.it";
	private String adminPsw = "pippo";
	private String psw = "pippero123";

	private ObjectMapper mapper = new ObjectMapper();
	public String toJson(Object obj)
		{
		try{
			return mapper.writeValueAsString(obj);
			}
		catch (Exception e) {
			e.printStackTrace();
			return "{}";
			}
		}
	
	@Test
	public void listaUtenti() throws Exception
		{
		User[] users = new RestTemplate().postForObject("http://localhost:8080/community/list",null,User[].class);		
		System.out.println(users[0].getMail());
		}
	
	@Test
	public void registrazioneUtente() throws Exception
		{
		JSONObject map = new JSONObject();
	  	   map.put("mail", mail);
	  	   map.put("psw", psw);
	  	   map.put("first_name", "Massimiliano");
	  	   map.put("last_name", "Regis");
	  	   
		User entity = new RestTemplate().postForObject("http://localhost:8080/community/register",map,User.class);	  
		this.id=entity.getRegisterId();
		System.out.println(id);
		}
	
	@Test
	public void confermaRegistrazione() throws Exception
		{
		Pending[] entity = new RestTemplate().postForObject("http://localhost:8080/community/pendings",null,Pending[].class);
		String id =entity[0].getId();
		
		System.out.println(id);
		new RestTemplate().postForLocation("http://localhost:8080/community/user/confirm?id="+id,null);
		}
	@Test
	public void login() throws Exception
		{
		Map<String, Object> map = new HashMap<String, Object>();
	  	   map.put("mail", admin);
	  	   map.put("psw", adminPsw);
	  	User entity =new RestTemplate().getForObject("http://localhost:8080/community/login?mail={mail}&psw={psw}",User.class,map);
	  	System.out.println(entity.getRoles());
		}

	
	@Test
	public void salvataggioJson() throws Exception
		{
		Map<String, Object> map = new HashMap<String, Object>();
	  	   map.put("mail", admin);
	  	   map.put("psw", adminPsw);
	  	   
	  	User entity =new RestTemplate().getForObject("http://localhost:8080/community/login?mail={mail}&psw={psw}",User.class,map);
	  		entity.setData("{\"price\":\"extra\",\"option\":[\"A\",\"B\",\"C\"]}");
	  		entity.setFirstName("nome");
	  	new RestTemplate().postForObject("http://localhost:8080/community/user",entity,User.class);
	  	
	  	System.out.println(entity.getRoles());
		}
	@Test
	public void sendPsw() throws Exception
		{
		Map<String, Object> map = new HashMap<String, Object>();
	  	   map.put("mail", admin);	  	   
	  	new RestTemplate().getForEntity("http://localhost:8080/community/{mail}/sendPsw",Object.class,map);	  	
		}
	//@Test
	public void resetPsw() throws Exception
		{
		Map<String, Object> map = new HashMap<String, Object>();
	  	   map.put("mail", admin);	  	   
	  	new RestTemplate().getForEntity("http://localhost:8080/community/{mail}/resetPsw",Object.class,map);	  	
		}
	//@Test
	public void newUser() throws Exception
		{
		if(true) return;
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

	    RestTemplate template = new RestTemplate();

	    HttpEntity<String> requestEntity = new HttpEntity<String>("",headers);
	    {
	    User[] entity = template.postForObject("http://localhost:8080/community/list",null,User[].class);	   	    
	    System.out.println(entity.length);
	    }
	    
	    {
	    User[] entity = template.postForObject("http://localhost:8080/community/list?realm=bunga",null,User[].class);	   	    
	    System.out.println(entity.length);
	    }
	    
	    {
	    Pending[] entity = template.postForObject(" http://localhost:8080/community/pendings",null,Pending[].class);
	    for(Pending p:entity)	    	
	    	System.out.println(p.getUser()+" "+p.getId());
	    	
		}
	    
	   try {
    	 MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
    	   map.add("mail", "xxxx@ekaros.it");
    	   map.add("psw", "xxxx");
    	   map.add("first_name", "test");
    	   map.add("second_name", "test");
		User entity = template.postForObject("http://localhost:8080/community/register",map,User.class);	   	    
		System.out.println(entity.getPsw());
		}catch (Exception e) {
			e.printStackTrace();
		}
			
	   
	  
		}
	
	public static void main(String[] args) throws Exception{
		new CommunityTest().salvataggioJson();
	}
}
