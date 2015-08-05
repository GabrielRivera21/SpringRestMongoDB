package com.deeplogics.mobilecloud.app.controller;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.deeplogics.mobilecloud.app.client.api.UsersSvcApi;
import com.deeplogics.mobilecloud.app.client.entities.EditUser;
import com.deeplogics.mobilecloud.app.model.UserForgotPass;
import com.deeplogics.mobilecloud.app.model.Users;
import com.deeplogics.mobilecloud.app.repository.UserForgotPassRepository;
import com.deeplogics.mobilecloud.app.repository.UsersRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGridException;
import com.wordnik.swagger.annotations.ApiOperation;


/**
 * Controller to manage the UsersSvc's API
 * @author Gabriel
 * 
 */
@Controller
public class UsersController {
	
	@Autowired
	private UsersRepository users;
	
	@Autowired
	private UserForgotPassRepository userforgotPass;
	
	@Autowired
	private GridFsTemplate gridOperations;
	
	/**
	 * Returns the User's information
	 * @param email
	 * @return User's information
	 */
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value="Retrieve the user by specifying his email via query parameter")
	@RequestMapping(value=UsersSvcApi.USERS_EMAIL_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Users getUserByEmail(@RequestParam(UsersSvcApi.EMAIL_PARAMETER) String email,
			Principal user){
		Users userDB = users.findByEmail(email);
		if(userDB != null && user.getName().equals(userDB.getId()))
			return userDB;
		
		return null;
	}
	
	/**
	 * Registers a new user into the database
	 * @param u
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Registers a new User into the Database.")
	@RequestMapping(value=UsersSvcApi.USERS_REGISTRATION_PATH, method=RequestMethod.POST)
	public @ResponseBody Users addUser(@RequestBody Users u, HttpServletResponse resp) throws IOException{
		Users uc = users.findByEmail(u.getEmail());
		
		if(uc == null){
			//Sets the role to the User
			Collection<String> role = new ArrayList<>();
			role.add("USER");
			List<String> skills = new ArrayList<>();
			
			//Sets the default values for the User's account
			u.setRole(role);
			u.setEnabled(false);
			u.setAccountNonLocked(true);
			u.setSkills(skills);
			u.setJoinedDate(new Date());
			
			//Generates a token to verify email address
			String token = generateToken();
			u.setToken(token);
			
			//Hashes the password for the user
			u.setPassword(hashPassword(u.getPassword()));
			
			//Save the User into the database.
			Users storedUser = users.save(u);
			
			//Sends an Email in order to verify email address and activate account.
			String content = htmlTextForAccountActivation(storedUser, token);
			sendEmail("Activate Account", storedUser.getEmail(), content);
			
			return storedUser;
		}else{
			//User already exists in the database
			resp.sendError(403, "User already exists.");
			return null;
		}
	}
	
	/**
	 * 
	 * @param email
	 * @param user_id
	 * @param token
	 * @param resp
	 * @throws IOException
	 */
	@ApiOperation(value="Verifies their email and activates their account.(Meant to be clicked in email.)")
	@RequestMapping(value=UsersSvcApi.USERS_ACTIVATEACC_PATH, method=RequestMethod.GET)
	public @ResponseBody void activateAccount(@RequestParam(UsersSvcApi.EMAIL_PARAMETER) String email,
			@RequestParam(UsersSvcApi.USERID_PARAMETER) String user_id,
			@RequestParam("token") String token, HttpServletResponse resp) throws IOException {
		Users user = users.findOne(user_id);
		
		if(user != null){
			if(user.getToken().equals(token) && user.getEmail().equals(email)){
				//Enables the account
				user.setToken(null);
				user.setEnabled(true);
				//Updates the User
				users.save(user);
				resp.sendRedirect("https://localhost:8443");
			}else {
				resp.sendError(403, "Forbidden");
			}
		}else {
			resp.sendError(404, "User does not exist");
		}
		
	}
	
	/**
	 * a method to edit the user's public field and/or password.
	 * @param edit
	 * @param user
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Edits the User's fields.")
	@RequestMapping(value=UsersSvcApi.USERS_EDIT_PATH, method=RequestMethod.PUT)
	public @ResponseBody Void editUser(@RequestBody EditUser edit, Principal user, 
			HttpServletResponse resp) throws IOException {
		
		//Retrieves the current user from the database
		Users userDB = users.findOne(user.getName());
		if(userDB == null)
			resp.sendError(404, "User does not exist.");
		else{
			if(edit.getEmail() != null) 
				userDB.setEmail(edit.getEmail());
			if(edit.getName() != null)
				userDB.setName(edit.getName());
			if(edit.getPhone() != null)
				userDB.setPhone(edit.getPhone());
			if(edit.getAboutMe() != null)
				userDB.setAboutMe(edit.getAboutMe());
			if(edit.getSkills() != null)
				userDB.setSkills(edit.getSkills());
			if(edit.getPassword() != null) 
				userDB.setPassword(hashPassword(edit.getPassword())); //Hashes the password
			
			//Updates the user
			users.save(userDB);
		}
		
		return null;
	}
	
	/**
	 * Uploads the Photo from the database
	 * @param file : photo to be uploaded
	 * @param user : Spring Security grabs the user from token
	 * @param resp : Server Response
	 * @return Void
	 * @throws IOException
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value="Uploads the User's Profile Pic into the Database.")
	@RequestMapping(value=UsersSvcApi.USERS_UPLOAD_PHOTO_PATH, method=RequestMethod.POST)
	public @ResponseBody Void uploadPhoto(@RequestParam(UsersSvcApi.FILE_PARAMETER) MultipartFile file,
				Principal user, HttpServletResponse resp) throws IOException {
		
		//Retrieves the User's info from the database
		Users userDB = users.findOne(user.getName());
		InputStream inputStream = null;
		
		try {
			if(userDB != null) {
				
				//Verifies that the content type is JPEG or PNG
				String contentType = file.getContentType();
				String extension = null;
				
				switch(contentType) {
					case "image/jpeg": 
						extension = ".jpg";
						break;
					case "image/png": 
						extension = ".png";
						break;
					default:
						resp.sendError(400);
						return null;
				}
				
				String filename = userDB.getEmail() + "_IMG" + extension;
				
				//Verify if user already has a photo stored in the DB, if it does
				//Delete the old photo.
				String fileID = userDB.getPhotoID();
				if(fileID != null)
					gridOperations.delete(new Query().addCriteria(Criteria.where("_id").is(fileID)));
				
				//Setting the metaData for the photo uploaded
				DBObject metaData = new BasicDBObject();
				metaData.put("desc", "profile_pic");
				metaData.put("user", userDB.getEmail());
				
				//Formats the Image for binary
				inputStream = new ByteArrayInputStream(file.getBytes());
				
				//Store the Image Binary Files and updates the User
				GridFSFile fsFileID = gridOperations.store(inputStream, filename, contentType, metaData);
				String objID = fsFileID.getId().toString();
				userDB.setPhotoID(objID);
				users.save(userDB);
				
				return null;
			}else {
				resp.sendError(404);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {}
			}
		}
		
		return null;
		
	}
	
	/**
	 * Retrieves the Photo from the database
	 * @param filename
	 * @return Photo File
	 */
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value="Retrieves the photo of the specified id.")
	@RequestMapping(value=UsersSvcApi.GET_PHOTO_PATH, method=RequestMethod.GET)
	public @ResponseBody void getPhotoFileFromDB(@PathVariable("id") String _id,
																HttpServletResponse response) {
		
		GridFSDBFile file = gridOperations.findOne(
	               new Query().addCriteria(Criteria.where("_id").is(_id)));
		
		response.setContentType("application/jpeg");
		
		try {
			file.writeTo(response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {}
		
	}
	
	/**
	 * Changes the User's password. must validate email, user_id and token from database
	 * @param email
	 * @param user_id
	 * @param token
	 * @throws IOException
	 */
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value="Changes the User's password and sends it via email. (Meant to be clicked in email)")
	@RequestMapping(value=UsersSvcApi.USERS_CHANGEPASS_PATH, method=RequestMethod.GET)
	public @ResponseBody void changePassword(@RequestParam("email") String email,
			@RequestParam("uid") String user_id, 
			@RequestParam("token") String token) throws IOException{
		//Checks to see if this user requested a change password
		UserForgotPass user = userforgotPass.findOne(user_id);
		
		if(user != null && user.getToken().equals(token)){
			Users userDB = users.findOne(user_id);
			if(userDB != null && userDB.getEmail().equals(email)){
				//TODO: Remove generatedPassword for Production Note: Temporary
				String password = generatePassword();
				String htmlText = htmlTextForGeneratedPass(password);
				
				//Sends the Email
				sendEmail("Password for SpringRestMongoDB", email, htmlText);
				
				//Hashes the password
				userDB.setPassword(hashPassword(password));
				
				users.save(userDB);
				userforgotPass.delete(user_id);
			}
		}
	}
	
	/**
	 * Requests a change password in the user's account and sends him an email
	 * @param email
	 * @param resp
	 * @return a server response and an email to the email provided
	 * @throws IOException
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value="Creates a reset password token and sends an email to the user to confirm.")
	@RequestMapping(value=UsersSvcApi.USERS_FORGOTPASS_PATH, method=RequestMethod.POST)
	public @ResponseBody HttpServletResponse forgotPassword(@RequestParam("email") String email,
			HttpServletResponse resp) throws IOException {
		//Retrieves the User from Database
		Users user = users.findByEmail(email);
		
		if(user != null) {
			//Generates the token
			String token = generateToken();
			
			//Creates HTML Text for User
			UserForgotPass userforgot = new UserForgotPass(
					user.getId(), user.getEmail(), token, new Date());
			String fullHtml = htmlTextForForgotPassword(userforgot, token);
			
			//Sends an email to the user requesting forget password
			sendEmail("Password Recovery Tool", email, fullHtml);
			
			userforgotPass.save(userforgot);
		}
		resp.setStatus(202);
		return null;
	}
	
	/**
	 * Hashes the password with BCrypt Algorithm
	 * @param password
	 * @return
	 */
	private String hashPassword(String password){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(password);
	}
	
	/**
	 * Sends an email
	 * @param subject
	 * @param to
	 * @param content
	 * @throws IOException
	 */
	private void sendEmail(String subject, String to, String content) throws IOException{
		//For SendGrid API
		SendGrid sendgrid = buildSendGrid();
		
		//Setting properties for email
		Email sendEmail = new Email();
		sendEmail.addTo(to);
		sendEmail.setFrom("no-reply@springrestmongodb.com");
		sendEmail.setFromName("SpringRestMongoDB");
		sendEmail.setSubject(subject);
		sendEmail.setHtml(content);
		
		try {
			sendgrid.send(sendEmail);
		} catch (SendGridException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * builds a SendGrid Object using the application.properties
	 * sendgrid.username and sendgrid.pass
	 * @return SendGrid Object
	 * @throws IOException
	 */
	private SendGrid buildSendGrid() throws IOException{
		Properties prop = new Properties();
		String propFileName = "application.properties";
		
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		 
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		
		SendGrid sendgrid = new SendGrid(
				prop.getProperty("sendgrid.username"), 
				prop.getProperty("sendgrid.pass"));
		
		return sendgrid;
	}
	
	/**
	 * Temporary Method creates html text containing the generated pass
	 * @param password
	 * @return
	 */
	private String htmlTextForGeneratedPass(String password){
		StringBuilder fullHtml = new StringBuilder();
		fullHtml.append("<h1>SpringRestMongoDB</h1>");
		fullHtml.append("<p>You changed the password for your Test Account.</p>");
		fullHtml.append("<p>Here are your credentials</p>");
		fullHtml.append("<ul><li>Password: " + password + "</li></ul>");
		fullHtml.append("<p>Now you can login to your account</p><br>");
		
		return fullHtml.toString();
	}
	
	private String htmlTextForAccountActivation(Users user, String token) throws UnsupportedEncodingException{
		StringBuilder fullHtml = new StringBuilder();
		fullHtml.append("<h1>SpringRestMongoDB</h1>");
		fullHtml.append("<p>Hello " + user.getName() + "</p>");
		fullHtml.append("<p>You have just registered an account on SpringRestMongoDB App. " +
				"Please verify that this is your email address by clicking the link below</p>");
		fullHtml.append("<a href=\"" + UsersSvcApi.ACTIVATEACC_URL);
		fullHtml.append("?email=" + URLEncoder.encode(user.getEmail(), "UTF-8")
				+ "&uid=" + user.getId()
				+ "&token=" + token + "\">");
		fullHtml.append("Click here to Verify Email.</a>");
		fullHtml.append("<h4>You didn't create this account?</h4>");
		fullHtml.append("Inform us of this immediately by clicking ");
		fullHtml.append("<a href=\"#\">here</a><br>");
		return fullHtml.toString();
	}
	
	/**
	 * Writes the Html Text for the ForgotPassword email for the user
	 * containing the URL with the user's id, email and generated token.
	 * @param user
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String htmlTextForForgotPassword(UserForgotPass user, String token) throws UnsupportedEncodingException{
		
		StringBuilder fullHtml = new StringBuilder();
		fullHtml.append("<h1>SpringRestMongoDB</h1>");
		fullHtml.append("<p>Someone recently requested a password change in your Account</p>");
		fullHtml.append("<br><a href=\"" + UsersSvcApi.CHANGEPASS_URL 
				+ "?email=" + URLEncoder.encode(user.getEmail(), "UTF-8") 
				+ "&uid=" + user.getUser() 
				+ "&token=" + token);
		fullHtml.append("\">Click here to change your password</a>");
		fullHtml.append("<br><br>");
		fullHtml.append("<h4>You didn't ask for this change?</h4>");
		fullHtml.append("Inform us of this immediately by clicking ");
		fullHtml.append("<a href=\"#\">here</a><br>");
		
		return fullHtml.toString();
	}
	
	/**
	 * Generates a token to be validated when using forgot password
	 * @return Token String
	 */
	private String generateToken(){
		SecureRandom random = new SecureRandom();
		
		return new BigInteger(130, random).toString(32);
	}
	
	/**
	 * Temporary method to be used in forgot password while website
	 * is still being built.
	 * @return a Random Password String.
	 */
	private String generatePassword(){
		Random r = new Random();
		String setOfChars = "abcdefghijklmnopqrstuvwxyz0123456789";
		
		return generateString(r, setOfChars, 15);
	}
	
	/**
	 * Temporary method for generate password
	 * @param rng
	 * @param characters
	 * @param length
	 * @return
	 */
	public static String generateString(Random rng, String characters, int length)
	{
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++)
	    {
	        text[i] = characters.charAt(rng.nextInt(characters.length()));
	    }
	    return new String(text);
	}
	
}
