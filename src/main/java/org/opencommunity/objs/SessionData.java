package org.opencommunity.objs;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;




@Component
@Scope(value="session",proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionData 
{
	private User user;
	public User getUser()
		{	
		return user;
		}
	public void setUser(User user)
		{
		this.user=user;
		}
}
