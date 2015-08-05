package com.deeplogics.mobilecloud.app.repository;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.deeplogics.mobilecloud.app.client.api.UsersSvcApi;
import com.deeplogics.mobilecloud.app.model.Users;

/**
 * An interface for a repository that can store Users
 * objects and allow them to be searched by Name or Email.
 * 
 * @author Gabriel
 *
 */
// This @RepositoryRestResource annotation tells Spring Data Rest to
// expose the UserRepository through a controller and map it to the 
// "/user" path. This automatically enables you to do the following:
//
// 1. List all Users by sending a GET request to /user 
// 2. Add a User by sending a POST request to /user with the JSON for a User
// 3. Get a specific User by sending a GET request to /User/{UserId}
//    (e.g., /user/1 would return the JSON for the User with id=1)
// 4. Send search requests to our findByXYZ methods to /User/search/findByXYZ
//    (e.g., /user/search/findByName?name=Foo)
//
@RepositoryRestResource(path = UsersSvcApi.USERS_SVC_PATH)
public interface UsersRepository extends MongoRepository<Users, String>{

	// Find all users with a matching name (e.g., Users.name)
	public Collection<Users> findByName(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "name" variable used to
			// search for Users
			@Param(UsersSvcApi.NAME_PARAMETER) String name);
	
	//Finds the user who's email matches
	public Users findByEmail(@Param(UsersSvcApi.EMAIL_PARAMETER) String email);
	
	/*
	 * See: http://docs.spring.io/spring-data/jpa/docs/1.3.0.RELEASE/reference/html/jpa.repositories.html 
	 * for more examples of writing query methods
	 */
	
}
