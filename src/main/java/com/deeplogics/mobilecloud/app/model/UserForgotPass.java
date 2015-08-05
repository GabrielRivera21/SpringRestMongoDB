package com.deeplogics.mobilecloud.app.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

@Document(collection = "userforgotpass")
public class UserForgotPass {
	
	@Id
	private String user;
	
	private String email;
	private String token;
	
	@DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
	private Date requestedDate;
	
	public UserForgotPass(String user, String email, String token, Date requestedDate) {
		this.user = user;
		this.email = email;
		this.token = token;
		this.requestedDate = requestedDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}
	
}
