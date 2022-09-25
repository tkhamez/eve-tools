package eve.tools.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eve.tools.entity.Role;
import eve.tools.entity.User;
import eve.tools.esi.model.oauth.Token;
import eve.tools.data.AccessToken;
import eve.tools.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("user '" + username + "' not found");
		}
		return user;
	}

	public User getAuthenticatedUser() {
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (user instanceof User) {
			return (User) user;
		}

		return null; // user is String "anonymousUser"
	}

	/**
	 * Finds/creates user, adds data and authenticates.
	 */
	public void loginUser(Token token, AccessToken data) {
		String username = String.valueOf(data.getCharacterId());
		List<String> scopes = data.getScopes();

		User user;
		try {
			user = (User) loadUserByUsername(username);
		} catch (UsernameNotFoundException e) {
			user = new User(username);
		}

		user.setCharacterName(data.getCharacterName());
		user.setScopes(scopes);
		user.setAuthorities(scopesToRoles(scopes));
		user.setRefreshToken(token.getRefresh_token());

		updateAuthenticatedUser(user, token);
	}

	/**
	 * Updates access token and expire time in user object and changes the currently
	 * authenticated principal.
	 */
	public void updateAuthenticatedUser(User user, Token token) {
		Calendar expiresOn = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		expiresOn.add(Calendar.SECOND, token.getExpires_in());

		user.setExpiresOn(expiresOn);
		user.setAccessToken(token.getAccess_token());

		userRepository.saveAndFlush(user);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	public List<String> getScopesForRole(String role) {
		List<String> scopes = new ArrayList<>();

		switch (role) {
		case "ROLE_EVE_CORP_CONTRACTS":
			scopes.add("esi-contracts.read_corporation_contracts.v1");
			scopes.add("esi-universe.read_structures.v1");
			break;
		case "ROLE_EVE_MOON_EXTRACTION":
			scopes.add("esi-industry.read_corporation_mining.v1");
			scopes.add("esi-universe.read_structures.v1");
			break;
		case "ROLE_EVE_ASSETS":
			scopes.add("esi-assets.read_assets.v1");
			scopes.add("esi-universe.read_structures.v1");
			break;
		case "ROLE_EVE_PI":
			scopes.add("esi-planets.manage_planets.v1");
			break;
		}

		return scopes;
	}

	public List<String> getScopesForRoles(String[] roles) {
		List<String> scopes = new ArrayList<>();

		for (String role: roles) {
			getScopesForRole(role).forEach((scope) -> {
				if (! scopes.contains(scope)) {
					scopes.add(scope);
				}
			});
		}

		return scopes;
	}

	private List<Role> scopesToRoles(List<String> scopes) {
		List<Role> roles = new ArrayList<>();

		// All roles must exist in DB, see EveDataLoader

		// user is authenticated, add basic eve role
		roles.add(new Role("ROLE_EVE"));

		String[] roleNames = {
			"ROLE_EVE_CORP_CONTRACTS",
			"ROLE_EVE_MOON_EXTRACTION",
			"ROLE_EVE_ASSETS",
			"ROLE_EVE_PI"
		};
		for (String roleName: roleNames) {
			boolean hasRole = true;
			for (String scopeName : getScopesForRole(roleName)) {
				boolean inScopes = false;
				for (String scope : scopes) {
					if (scope.equals(scopeName)) {
						inScopes = true;
						break;
					}
				}
				if (! inScopes) {
					hasRole = false;
					break;
				}
			}
			if (hasRole) {
				roles.add(new Role(roleName));
			}
		}

		return roles;
	}
}
