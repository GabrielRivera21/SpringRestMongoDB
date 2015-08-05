package com.deeplogics.mobilecloud.integration.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.deeplogics.mobilecloud.app.TestData;
import com.deeplogics.mobilecloud.app.client.SecuredRestBuilder;
import com.deeplogics.mobilecloud.app.client.api.UsersSvcApi;
import com.deeplogics.mobilecloud.app.client.entities.EditUser;
import com.deeplogics.mobilecloud.app.model.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;

/**
 * 
 * This integration test sends a POST request to the UserServlet to add a new user 
 * and then sends a second GET request to check that the User showed up in the list
 * of Users. Actual network communication using HTTP is performed with this test.
 * 
 * The test requires that the UserSvc be running first (see the directions in
 * the README.md file for how to launch the Application).
 * 
 * To run this test, right-click on it in Eclipse and select
 * "Run As"->"JUnit Test"
 * 
 * Pay attention to how this test that actually uses HTTP and the test that just
 * directly makes method calls on a UserSvc object are essentially identical.
 * All that changes is the setup of the UserService variable. Yes, this could
 * be refactored to eliminate code duplication...but the goal was to show how
 * much Retrofit simplifies interaction with our service!
 * 
 * @author Gabriel
 *
 */
public class UsersSvcClientApiTest {
	
	private final String USERNAME = "gabriel@gmail.com"; //"admin";
	private String PASSWORD = "gabriel";
	private final String CLIENT_ID = "mobile-spring";
	
	private final String TEST_URL = "https://localhost:8443";
	
	private Gson gson = new GsonBuilder()
		.setDateFormat("yyyy/MM/dd hh:mm:ss")
		.create();

	private UsersSvcApi usersService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL + UsersSvcApi.TOKEN_PATH)
			.setUsername(USERNAME)
			.setPassword(PASSWORD)
			.setClientId(CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setConverter(new GsonConverter(gson))
			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
			.create(UsersSvcApi.class);

	private UsersSvcApi usersNoTokenService = new RestAdapter.Builder()
			.setEndpoint(TEST_URL)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setConverter(new GsonConverter(gson))
			.setLogLevel(LogLevel.FULL).build()
			.create(UsersSvcApi.class);
	

	private Users user = TestData.randomUser();
	
	@Test
	public void testGetUser(){
		Users u = usersService.getUserByEmail(USERNAME);
		
		assertTrue(u.getPassword() == null);
		assertTrue(u != null);
	}
	
	
	
	@Test
	public void testUploadPhotoAndRetrieve() throws IOException {
		File file = new File("src/test/resources/profile_pic.jpg");
		
		String mimeType = "image/jpeg";
		TypedFile photo = new TypedFile(mimeType, file);
        
		usersService.uploadPhoto(photo);
		
		Users u = usersService.getUserByEmail(USERNAME);
		
		Response resp = usersService.getPhoto(u.getPhotoID());
		assertEquals(200, resp.getStatus());
		
		InputStream photoData = resp.getBody().in();
		byte[] originalFile = IOUtils.toByteArray(new FileInputStream(file));
		byte[] retrievedFile = IOUtils.toByteArray(photoData);
		assertTrue(Arrays.equals(originalFile, retrievedFile));
	}
	
	/**
	 * This test creates a User, adds the User to the UserSvc, and then
	 * checks that the User is retrieved from the database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUserAddAndList() throws Exception {
		
		// Add the User
		Users u = usersNoTokenService.addUser(user);
		
		assertTrue(u.getEmail().equals(user.getEmail()));
	}
	
	@Test
	public void testEditUserAndList() {
		EditUser edit = new EditUser();
		String newName = "NewName" + TestData.randomUser().getName();
		edit.setName(newName);
		
		usersService.editUser(edit);
		
		Users uc = usersService.getUserByEmail(USERNAME);
		String retrievedName = uc.getName();
		assertTrue(retrievedName.equals(newName));
	}
	
	@Test
	public void forgotAndChangePass(){
		Response resp = usersNoTokenService.forgotPass("gabriel.rivera2192@hotmail.com");
		assertEquals(201, resp.getStatus());
	}
	
	

}
