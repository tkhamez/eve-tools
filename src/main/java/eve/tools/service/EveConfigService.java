package eve.tools.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Service
public class EveConfigService {

	@Value("${eve-tools.callback-url}")
	private String callbackUrl;

	private String eveEnv;

	@Autowired
	private Environment env;

	public Boolean sisiEnabled() {
		return env.getProperty("EVE_TOOLS_SISI_CLIENT_ID") != null &&
				env.getProperty("EVE_TOOLS_SISI_SECRET_KEY") != null;
	}

	public String getEveEnv() {
		env();

		return eveEnv;
	}

	public String loginDomain() {
		env();

		if (Objects.equals(eveEnv, "sisi")) {
			return "sisilogin.testeveonline.com";
		} else {
			return "login.eveonline.com";
		}
	}

	public String clientId() {
		env();

		if (Objects.equals(eveEnv, "sisi")) {
			return env.getProperty("eve-tools.sisi.client-id");
		} else {
			return env.getProperty("eve-tools.client-id");
		}
	}

	public String secretKey() {
		env();

		if (Objects.equals(eveEnv, "sisi")) {
			return env.getProperty("eve-tools.sisi.secret-key");
		} else {
			return env.getProperty("eve-tools.secret-key");
		}
	}

	public String callbackUrl() {
		env();

		return callbackUrl;
	}

	public String dataSource() {
		env();

		if (Objects.equals(eveEnv, "sisi")) {
			return "singularity";
		} else {
			return "tranquility";
		}
	}

	private void env() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession httpSession = attr.getRequest().getSession();

		if (httpSession.getAttribute("eveEnv") != null && httpSession.getAttribute("eveEnv").equals("sisi")) {
			eveEnv = "sisi";
		} else {
			eveEnv = "tranq";
		}
	}
}
