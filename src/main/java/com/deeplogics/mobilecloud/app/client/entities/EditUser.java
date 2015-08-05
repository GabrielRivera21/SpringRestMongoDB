package com.deeplogics.mobilecloud.app.client.entities;

import java.util.List;

/**
 * This class is instantiated with everything in null, and is only meant
 * to be used with the method EditUser from the UsersSvcApi.editUser() for
 * UsersController.editUser(), it will only update the fields that are not
 * in null.
 * 
 * Note: This is for the Retrofit Client Side Application.
 * 
 * @author Gabriel
 *
 */
public class EditUser {
	
	private String email = null;
	
	private String name = null;
	
	private String password = null;
	
	private String phone = null;
	
	private String aboutMe = null;
	
	private List<String> skills = null;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
	
}
