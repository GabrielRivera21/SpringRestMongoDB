package com.deeplogics.mobilecloud.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.deeplogics.mobilecloud.app.client.api.UsersSvcApi;
import com.deeplogics.mobilecloud.app.model.UserForgotPass;

/**
 * An interface for a repository that can store UserForgotPass
 * objects 
 * 
 * Used to Store in a collection the users who are requesting
 * a new password via forgot password.
 * 
 * @author Gabriel
 *
 */
@RepositoryRestResource(path=UsersSvcApi.USERS_FORGOTPASS_PATH)
public interface UserForgotPassRepository extends MongoRepository<UserForgotPass, String>{}
