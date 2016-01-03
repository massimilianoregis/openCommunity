package org.opencommunity.notification;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component("notify")
@Entity
public class Notify  {
	@Id
	@Value("${notify.app}") private String app;
	@Value("${notify.secret}") private String privateKey;
	public Notify()	{}
	public Notify(String app,String privateKey)
		{
		this.app=app;
		this.privateKey=privateKey;
		}
	public void send(String title, String text,Map payload, String ... to) throws Exception{
		
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//		Proxy proxy= new Proxy(Type.HTTP, new InetSocketAddress("bcprx.gbm.lan", 8080));
//	    requestFactory.setProxy(proxy);
		 
         byte[] encodedAuth = Base64.encodeBase64(privateKey.getBytes(Charset.forName("US-ASCII")) );
         String authHeader = "Basic " + new String( encodedAuth );
		
		RestTemplate template = new RestTemplate(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("X-Ionic-Application-Id", app);
		headers.set("Authorization", authHeader);
		
	
		System.out.println(title+" "+text+" "+payload+" "+Arrays.asList(to));
		Message message = new Message(title, text,to);
		//message.getNotification().getIos().setSound("Cosmic");
				
		message.getNotification().getIos().setPayload(payload);
		message.getNotification().getAndroid().setPayload(payload);
		HttpEntity<String> requestEntity = new HttpEntity<String>(new ObjectMapper().writeValueAsString(message),headers);
		template.postForLocation("https://push.ionic.io/api/v1/push",requestEntity);
		
		}
	
	public static void main(String[] args) throws Exception{
//		new Notify("a7e2ff6d","624dd8c0ac6f796fda1000c56d7cb367d642bfbd777c77e9")
//			.send(	"prova", 
//					"invio notifica da spring", 
//					"8a2e838789d319dc3e3bf3e5e0af5a81c54f9d6890a02017e95fd8cf8d5ddf91"
//					);
		
	}
}
