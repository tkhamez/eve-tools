package eve.tools.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Helper methods
 */
@Service
public class LoginService {

	private final Logger log = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private EveConfigService conf;

	@Autowired
	private UserService userService;

	public String getUrl(String authState, String role) {
		StringJoiner scopes = new StringJoiner("%20");
		userService.getScopesForRole(role).forEach(scopes::add);

		return url(authState, scopes.toString());
	}

	public String getUrl(String authState) {
		StringJoiner scopes = new StringJoiner("%20");
		String[] allRoles = {
			"ROLE_EVE_CORP_CONTRACTS",
			"ROLE_EVE_MOON_EXTRACTION",
			"ROLE_EVE_ASSETS",
			"ROLE_EVE_PI"
		};
		userService.getScopesForRoles(allRoles).forEach(scopes::add);

		return url(authState, scopes.toString());
	}

	private String url(String authState, String scopes) {
		String redirectUri = "";
		try {
			redirectUri = URLEncoder.encode(conf.callbackUrl(), StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException uee) {
			log.error(uee.getMessage());
		}

		return "https://" + conf.loginDomain() + "/oauth/authorize" +
			"?response_type=code" +
			"&redirect_uri=" + redirectUri +
			"&client_id=" + conf.clientId() +
			"&state=" + authState +
			"&scope=" + scopes;
	}
}
