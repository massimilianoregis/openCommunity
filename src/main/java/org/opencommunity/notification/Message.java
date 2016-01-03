package org.opencommunity.notification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {
	private List<String> tokens;
	private boolean production;
	private Notification notification;
	
	public static class Notification{
		private String alert;
		private String title;
		private Android android = new Android();
		private Ios ios = new Ios();
		public String getAlert() {
			return alert;
		}
		public void setAlert(String alert) {
			this.alert = alert;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public Android getAndroid() {
			return android;
		}
		public void setAndroid(Android android) {
			this.android = android;
		}
		public Ios getIos() {
			return ios;
		}
		public void setIos(Ios ios) {
			this.ios = ios;
		}
		
		
	}
	
	public static class OS{
		private Map payload = new HashMap();
		private String sound ="ping.aiff";
		
		public Map getPayload() {
			return payload;
		}
		public void setPayload(Map payload) {
			this.payload = payload;
		}
		public String getSound() {
			return sound;
		}
		public void setSound(String sound) {
			this.sound = sound;
		}
	}
	
	public static class Android extends OS{}
	public static class Ios extends OS{}
	
	public Message() {
		// TODO Auto-generated constructor stub
	}
	
	public Message(String title, String message, String ... token){
		Notification notification = new Notification();
			notification.setAlert(message);
			notification.setTitle(title);
		
		this.setNotification(notification);
		this.setProduction(false);
		this.setTokens(Arrays.asList(token));
	}
	public List<String> getTokens() {
		return tokens;
	}
	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
	public boolean isProduction() {
		return production;
	}
	public void setProduction(boolean production) {
		this.production = production;
	}
	public Notification getNotification() {
		return notification;
	}
	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
	
	
	
}
