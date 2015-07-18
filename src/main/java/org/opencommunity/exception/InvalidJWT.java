package org.opencommunity.exception;

public class InvalidJWT extends Exception {
	public InvalidJWT()
		{
		super("invalid JWT");
		}
}
