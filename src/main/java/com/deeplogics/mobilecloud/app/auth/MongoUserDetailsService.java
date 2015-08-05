package com.deeplogics.mobilecloud.app.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.deeplogics.mobilecloud.app.model.Users;
import com.deeplogics.mobilecloud.app.repository.UsersRepository;

/**
 * This is the UserDetailsService of this Spring Application.
 * This class is used to retrieve our Users from our Database and
 * validate them in our OAuth 2.0 Security.
 * 
 * @author Gabriel
 *
 */
@Component
public class MongoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsersRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Users customUser = userRepository.findByEmail(email); 

		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		
		return new User(
				customUser.getId(),
				customUser.getPassword(),
				customUser.isEnabled(),
				accountNonExpired,
				credentialsNonExpired,
				customUser.isAccountNonLocked(),
				getAuthorities(customUser.getRole()));
	}

	/**
	 * 
	 * @param roles
	 * @return
	 */
	private Collection<GrantedAuthority> getAuthorities(Collection<String> roles) {
		Collection<GrantedAuthority> authList = getGrantedAuthorities(roles);
		return authList;
	}

	/**
	 * 
	 * @param roles
	 * @return
	 */
	private static Collection<GrantedAuthority> getGrantedAuthorities(Collection<String> roles) {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		
		
		return authorities;
	}
}