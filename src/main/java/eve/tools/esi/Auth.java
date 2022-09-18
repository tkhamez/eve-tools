package eve.tools.esi;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import eve.tools.esi.model.oauth.Token;
import eve.tools.esi.model.oauth.Verify;
import eve.tools.service.EveConfigService;

/**
 * https://eveonline-third-party-documentation.readthedocs.io/en/latest/sso/index.html
 *
 * All methods return null on error, the error is logged in the Client class.
 */
@Service
public class Auth {

	@Autowired
	private Client client;

	@Autowired
	private EveConfigService conf;

	public Token token(String code) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("code", code);

		return tokenRequest(body);
	}

	public Token refresh(String refreshToken) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "refresh_token");
		body.add("refresh_token", refreshToken);

		return tokenRequest(body);
	}

	public Verify verify(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		String url = "https://" + conf.loginDomain() + "/oauth/verify";

		return client.get(Verify.class, url, headers);
	}

	private Token tokenRequest(MultiValueMap<String, String> body) {
		String encodedAuth = Base64.getEncoder().encodeToString(
				(conf.clientId() + ":" + conf.secretKey()).getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + encodedAuth);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String url = "https://" + conf.loginDomain() + "/oauth/token";

		return client.post(Token.class, url, headers, body);
	}
}
