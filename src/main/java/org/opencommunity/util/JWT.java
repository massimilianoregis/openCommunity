package org.opencommunity.util;

import net.minidev.json.JSONObject;

import org.opencommunity.exception.InvalidJWT;
import org.opencommunity.objs.Community;
import org.opencommunity.objs.User;
import org.opencommunity.services.CommunityService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACVerifier;

public class JWT 
	{
	static private ObjectMapper json = new ObjectMapper();
	private String jwt;
	private Payload payload;
	
	
	public JWT(String jwt,Community community) throws InvalidJWT
		{
		try
			{
			this.jwt=jwt;
			JWSObject object =JWSObject.parse(jwt);		
			
			CommunityService.JWTResponse jwtObject = json.readValue(object.getPayload().toString(), CommunityService.JWTResponse.class);
			String secret = community.getSecretKey();
			
			JWSVerifier verifier = new MACVerifier(secret.getBytes());
			if(!object.verify(verifier)) throw new InvalidJWT();
			
			this.payload= object.getPayload();
			}
		catch (Exception e) {
			throw new InvalidJWT();
			}
		}
	public User getUser() throws Exception
		{		
		System.out.println("payload "+getPayload());
		
		return (User)this.json.readValue(getPayload(),User.class);		
		}
	public JSONObject getObject()
		{
		return payload.toJSONObject();
		}
	public String getPayload() 
		{
		return payload.toString();
		}
	}
