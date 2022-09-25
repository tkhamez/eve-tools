package eve.tools.controller;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eve.tools.esi.Auth;
import eve.tools.esi.model.oauth.Token;
import eve.tools.data.AccessToken;
import eve.tools.service.UserService;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
public class LoginController {

	private final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private Auth auth;

	@Autowired
	private UserService userService;

	@Autowired
	private HttpSession httpSession;

	@RequestMapping("login")
	String login() {

		return "login";
	}

	@RequestMapping("sso-callback")
	String ssoCallback(@RequestParam(required=false) String state, @RequestParam(required=false) String code) {
		if (state == null || code == null ) {
			log.error("sso-callback: missing params");

			// could be client login with Implicit Flow,
			// token is appended as an URL hash component. e. g.
			// sso-callback#access_token=...&token_type=Bearer&expires_in=1199

			return "redirect:login";
		}

		String authState = (String) httpSession.getAttribute("authState");
		if (! state.equals(authState)) {
			log.error("sso-callback: OAuth state mismatch");
			return "redirect:login";
		}

		Token token = auth.token(code);
		if (token == null) {
			return "redirect:login";
		}

		AccessToken data = parseToken(token.getAccess_token());
		if (data == null) {
			return "redirect:login";
		}

		userService.loginUser(token, data);

		return "redirect:";
	}

	private AccessToken parseToken(final String accessToken) {
		// TODO signature should be verified.
		AccessToken data = null;
		try {
			String[] chunks = accessToken.split("\\.");
			String payload = new String(Base64.getDecoder().decode(chunks[1]));
			JSONObject jsonObject = new JSONObject(payload);

			JSONArray jsonArray = jsonObject.getJSONArray("scp");
			List<String> scopes = new ArrayList<>();
			for (int i = 0; i < jsonArray.length(); i++) {
				scopes.add(jsonArray.getString(i));
			}

			data = new AccessToken();
			data.setCharacterId(Long.parseLong(jsonObject.getString("sub").replace("CHARACTER:EVE:", "")));
			data.setCharacterName(jsonObject.getString("name"));
			data.setScopes(scopes);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return data;
	}
}
