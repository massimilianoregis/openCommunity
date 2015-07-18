package org.opencommunity.security;

import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.servlet.http.HttpServletRequest;

import org.opencommunity.util.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@PropertySource({"application.properties"})
public class AccessControl {
static public AccessControl instance;

private HttpServletRequest request;
private Client client;

@Value("${communityJwt}") 		private String communityJwt;

@Autowired
public AccessControl(HttpServletRequest request, Client client)
	{	
	this.request=request;
	AccessControl.instance=this;
	this.client=client;
	}


static public org.opencommunity.objs.User getUser()
	{
	Object usr = instance.request.getAttribute("user");
	if(usr!=null)
		{
		if(usr instanceof org.opencommunity.objs.User) return (org.opencommunity.objs.User)usr;
		if(usr instanceof String) return null;
		}
	String jwt = instance.request.getHeader("X-Requested-With");
		
	//if(jwt==null) return null;
	try {		
		org.opencommunity.objs.User user = (org.opencommunity.objs.User)instance.request.getAttribute("user");
		if(user==null) user = (org.opencommunity.objs.User)instance.client.getForObject(instance.communityJwt,org.opencommunity.objs.User.class,jwt);
		instance.request.setAttribute("user",user);
		return user;
		}
	catch (Exception e) {
		instance.request.setAttribute("user","");
		return null;
		}
	}
static public boolean canAccess(String role,String company)
	{
	try{
		return getUser().canAccess(role,company);
		}
	catch (Exception e) {
		return false;
		}
	}
/*
static public boolean canAccess(String role,String company)
	{
	System.out.println("access Control?");
	try{
	return instance.getUser().canAccess(role, company);
	}catch (Exception e) {
	e.printStackTrace();
	return false;
	}
	}*/

public static void main(String[] args) {
	String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJtYWlsIjoibWFzc2ltaWxpYW5vLnJlZ2lzQGVrYXJvcy5pdCIsInBzdyI6bnVsbCwiZmlyc3ROYW1lIjoiTWFzc2ltaWxpYW5vIiwibGFzdE5hbWUiOiJSZWdpcyIsImxhc3RhY2Nlc3N0aW1lIjpudWxsLCJzZW5kUmVnaXN0ZXJNYWlsIjpudWxsLCJsb2d0cnkiOjAsInJlZ2lzdGVySWQiOm51bGwsInJvbGVzIjpbeyJpZCI6ImJhc2UudXNlciIsIm5hbWUiOiJ1c2VyIiwiY29tcGFueSI6ImJhc2UifSx7ImlkIjoiYmFzZS5hZG1pbiIsIm5hbWUiOiJhZG1pbiIsImNvbXBhbnkiOiJiYXNlIn1dLCJqd3QiOm51bGwsImNvbW11bml0eSI6ImJhc2UiLCJkYXRhIjp7fX0";
		
	SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
	InetSocketAddress address = new InetSocketAddress("bcprx.gbm.lan",8080);
	Proxy proxy = new Proxy(Proxy.Type.HTTP,address);	
        factory.setProxy(proxy);
        
    RestTemplate template = new RestTemplate();
    template.setRequestFactory(factory);
	System.out.println(template.getForObject("http://95.110.224.34:8080/community-0.0.2-SNAPSHOT/community/jwt?jwt={jwt}",Object.class,jwt));
}
}
