package org.opencommunity.util;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

public class Client<A> {
	private String proxy;
	private int port;
	private String protocol;
	
	
	public Client(String proxy)
		{
		if(proxy==null || proxy.isEmpty()) return;
		int sepProt=proxy.indexOf("://");
		int sepPort=proxy.indexOf(":",sepProt+3);
		
		protocol = proxy.substring(0,sepProt);				
		port     = new Integer(proxy.substring(sepPort+1));
		this.proxy    = proxy.substring(sepProt+3,sepPort);		
		}
	public A getForObject(String url, Class<A> cls, Object params)
		{				
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		
		if(proxy!=null)
			{
			InetSocketAddress address = new InetSocketAddress(proxy,port);
			Proxy proxy = new Proxy(protocol.equals("http")?Proxy.Type.HTTP:null,address);	
		        factory.setProxy(proxy);
			}
		
	    RestTemplate template = new RestTemplate();
	    template.setRequestFactory(factory);
	    
		return template.getForObject(url,cls,params);
		}
	
}
