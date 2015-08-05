package com.deeplogics.mobilecloud.app.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import com.deeplogics.mobilecloud.app.json.DateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;

//u.setProfilePic("data:image/png;base64," 
		//+ DatatypeConverter.printBase64Binary(output.toByteArray()));
@Document(collection = "users")
public class Users {
	
	/** Auto-Generated ID **/
	@Id
	private String id;
	
	@Indexed(unique=true)
	private String email;
	
	@JsonIgnore
	private String password;
	
	private String name;
	
	private String phone;
	
	private String aboutMe;
	
	private List<String> skills;
	
	@JsonIgnore
	private boolean enabled;
	
	@JsonIgnore
	private boolean accountNonLocked;
	
	@JsonIgnore
	private Collection<String> role;
	
	@DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
	private Date joinedDate;
	
	@Field(value = "profile_pic")
	private String photoID;
	
	@JsonIgnore
	private String token;
	
	public Users() {}

	public Users(String email, String name, String phone, String password) {
		super();
		setEmail(email);
		setName(name);
		setPhone(phone);
		setPassword(password);
		skills = new ArrayList<>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String username) {
		this.id = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@JsonIgnore
	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}
	
	@JsonSerialize(using = DateSerializer.class)
	public Date getJoinedDate() {
		return joinedDate;
	}

	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}

	@JsonIgnore
	@JsonProperty("enabled")
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@JsonIgnore
	@JsonProperty("accountNonLocked")
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@JsonIgnore
	@JsonProperty("role")
	public Collection<String> getRole(){
		return role;
	}
	
	public void setRole(Collection<String> role){
		this.role = role;
	}

	public String getPhotoID() {
		return photoID;
	}

	public void setPhotoID(String photoID) {
		this.photoID = photoID;
	}
	
	@JsonIgnore
	@JsonProperty(value="token")
	public String getToken() {
		return token;
	}
	
	@JsonIgnore
	@JsonProperty(value="token")
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Two Users will generate the same hashcode if they have exactly the same
	 * values for their email, name, phone and password.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(email, name, phone);
	}
	
	/**
	 * If both Users have the email, name and phone
	 * then they are the same user.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Users) {
			Users other = (Users) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(email, other.email)
					&& Objects.equal(name, other.name)
					&& Objects.equal(phone, other.phone);
		} else {
			return false;
		}
	}
}
