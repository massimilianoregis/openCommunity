package org.opencommunity.security;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.opencommunity.exception.InvalidJWT;
import org.opencommunity.exception.PermissionDenied;
import org.opencommunity.objs.Community;
import org.opencommunity.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecuredAspect {
 	

	
	@Before(value="@annotation(org.opencommunity.security.Secured)&& @annotation(sc)", argNames = "sc")
	public void secure(JoinPoint joinPoint,Secured sc) {	
		String company = sc.company().isEmpty()?null:sc.company();
		if(!AccessControl.canAccess(sc.value(), company))throw new PermissionDenied();
		
	}

	
	@Around(value="@annotation(org.opencommunity.security.SilentSecured) && @annotation(sc)", argNames = "sc")
	public Object aroundsecure(final ProceedingJoinPoint joinPoint,SilentSecured sc) throws Throwable{
		String company = sc.company().isEmpty()?null:sc.company();
		if(!AccessControl.canAccess(sc.value(), company)) return null;
							
		return joinPoint.proceed();		
	}
}
