package projetoAPDC.util;

import java.util.UUID;

import com.google.cloud.datastore.Entity;

public class AuthToken {
	public static final long EXPIRATION_TIME = 1000*60*60*2; //2h
	
	public String name;
	public String tokenID;
	public long creationDate;
	public long expirationDate;
	
	public AuthToken() {}
	
	public AuthToken(String name) {
		this.name = name;
		this.tokenID = UUID.randomUUID().toString();
		this.creationDate = System.currentTimeMillis();
		this.expirationDate = this.creationDate + AuthToken.EXPIRATION_TIME;
	}
}
