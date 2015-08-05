package com.deeplogics.mobilecloud.app.client.api;

import java.util.Collection;

import com.deeplogics.mobilecloud.app.client.entities.EditUser;
import com.deeplogics.mobilecloud.app.model.Users;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

/**
 * This interface defines an API for a UserSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * 
 * 
 * @author Gabriel
 *
 */
public interface UsersSvcApi {
	
	public static final String HOST_URL = "https://localhost:8443";
	//Full URL to change the password
	public static final String CHANGEPASS_URL = HOST_URL + "/users/rp";
	
	//Full URL to activate the account
	public static final String ACTIVATEACC_URL = HOST_URL + "/users/activate";
	
	//The oAuth token path
	public static final String TOKEN_PATH = "/oauth/token";
	
	// The path where we expect the UserSvc to live
	public static final String USERS_SVC_PATH = "/users";
	
	//Path to add Users to the database
	public static final String USERS_REGISTRATION_PATH = USERS_SVC_PATH + "/register";

	// The path to search Users by title
	public static final String USERS_NAME_SEARCH_PATH = USERS_SVC_PATH + "/search/findByName";
	
	public static final String USERS_EMAIL_SEARCH_PATH = USERS_SVC_PATH + "/search/findByEmail";
	
	public static final String USERS_UPLOAD_PHOTO_PATH = USERS_SVC_PATH + "/upload/photo";
	
	public static final String GET_PHOTO_PATH = USERS_SVC_PATH + "/photo/{id}";
	
	public static final String USERS_EDIT_PATH = USERS_SVC_PATH + "/edit";
	
	public static final String USERS_FORGOTPASS_PATH = USERS_SVC_PATH + "/forgotpassword";
	
	public static final String USERS_CHANGEPASS_PATH = USERS_SVC_PATH + "/rp";
	
	public static final String USERS_ACTIVATEACC_PATH = USERS_SVC_PATH + "/activate";
	
	public static final String USERS_REVIEW_PATH = USERS_SVC_PATH + "/reviews";
	
	public static final String NAME_PARAMETER = "name";
	
	public static final String EMAIL_PARAMETER = "email";
	
	public static final String FILE_PARAMETER = "fileContent";
	
	public static final String USERID_PARAMETER = "uid";

	@GET(USERS_EMAIL_SEARCH_PATH)
	public Users getUserByEmail(@Query(EMAIL_PARAMETER) String email);

	@POST(USERS_REGISTRATION_PATH)
	public Users addUser(@Body Users u);
	
	@GET(USERS_NAME_SEARCH_PATH)
	public Collection<Users> findByName(@Query(NAME_PARAMETER) String name);
	
	@Multipart
	@POST(USERS_UPLOAD_PHOTO_PATH)
	public Void uploadPhoto(@Part(FILE_PARAMETER) TypedFile photo);
	
	@Streaming
	@GET(GET_PHOTO_PATH)
	public Response getPhoto(@Path("id") String id);
	
	@PUT(USERS_EDIT_PATH)
	public Void editUser(@Body EditUser u);
	
	@POST(USERS_FORGOTPASS_PATH)
	public Response forgotPass(@Query(EMAIL_PARAMETER) String email);
}
