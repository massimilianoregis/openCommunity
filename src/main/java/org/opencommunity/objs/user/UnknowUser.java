package org.opencommunity.objs.user;

import java.io.Serializable;

import org.opencommunity.objs.Community;
import org.opencommunity.objs.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnknowUser extends User implements Serializable 
{		
	
	
	public UnknowUser(Community cm)
		{		
		}
	
}
