package org.opencommunity.exception;

public class UserNotFound extends Exception 
{
@Override
public String getMessage() 
	{	
	return "Utente non trovato";
	}
}
