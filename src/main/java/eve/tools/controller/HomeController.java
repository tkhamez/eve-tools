package eve.tools.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eve.tools.service.EveConfigService;
import eve.tools.service.LoginService;

@Controller
public class HomeController {

	@Autowired
	private LoginService loginService;

	@Autowired
	private EveConfigService conf;

	@Autowired
	private HttpSession httpSession;

	@RequestMapping("")
	String home(Model model, @RequestParam(required=false) String env) {
		if (env != null) {
			if (env.equals("sisi")) {
				httpSession.setAttribute("eveEnv", "sisi");
			} else if (env.equals("tranq")) {
				httpSession.setAttribute("eveEnv", "tranq");
			}
		}
		if (httpSession.getAttribute("eveEnv") == null) {
			httpSession.setAttribute("eveEnv", "tranq");
		}
		model.addAttribute("eveEnv", conf.getEveEnv());

		model.addAttribute("sisiEnabled", conf.sisiEnabled());

		String authState = new RandomValueStringGenerator(10).generate();
		httpSession.setAttribute("authState", authState);

		model.addAttribute("corpContractsUrl", loginService.getUrl(authState, "ROLE_EVE_CORP_CONTRACTS"));
		model.addAttribute("moonExtractionsUrl", loginService.getUrl(authState, "ROLE_EVE_MOON_EXTRACTION"));
		model.addAttribute("assetsUrl", loginService.getUrl(authState, "ROLE_EVE_ASSETS"));
		model.addAttribute("piUrl", loginService.getUrl(authState, "ROLE_EVE_PI"));
		model.addAttribute("allUrl", loginService.getUrl(authState));

		return "home";
	}
}
