package eve.tools.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eve.tools.esi.Api;
import eve.tools.esi.model.character.PublicInfo;
import eve.tools.esi.model.industry.CorporationMiningExtraction;
import eve.tools.esi.model.universe.Moon;
import eve.tools.service.DataService;
import eve.tools.service.UserService;

@Controller
@Secured("ROLE_EVE_MOON_EXTRACTION")
public class MoonExtractionController {

	@Autowired
	private Api api;

	@Autowired
	private DataService dataService;

	@Autowired
	private UserService userService;

	@RequestMapping("moon-extraction")
	String data(Model model) {
		Long characterId = Long.valueOf(userService.getAuthenticatedUser().getUsername());
		PublicInfo publicInfo = api.characterPublicInfo(characterId);

		if (publicInfo == null && api.getClient().getLastError() != null) {
			model.addAttribute("apiError", "API error: " + api.getClient().getLastError().getMessage());

		} else if (publicInfo != null) {
			List<CorporationMiningExtraction> cme = api.corporationMiningExtractions(publicInfo.getCorporation_id());
			if (cme != null) {
				model.addAttribute("extractions", cme);
				model.addAttribute("loc", structures(cme));
				model.addAttribute("moons", moons(cme));
			} else if (api.getClient().getLastError() != null) {
                model.addAttribute("apiError", "API error: " + api.getClient().getLastError().getMessage());
            }
		}

		return "moon-extraction";
	}

	private Map<Long, String> moons(List<CorporationMiningExtraction> cmes) {
		Map<Long, String> moons = new HashMap<>();

		for (CorporationMiningExtraction cme : cmes) {
			if (! moons.containsKey(cme.getMoon_id())) {
				Moon moon = api.universeMoon(cme.getMoon_id());
				if (moon != null) {
					moons.put(cme.getMoon_id(), moon.getName());
				}
			}
		}

		return moons;
	}

	private Map<Long, String> structures(List<CorporationMiningExtraction> cmes) {
		List<Long> structIds = new ArrayList<>();
		for (CorporationMiningExtraction cme : cmes) {
			if (! structIds.contains(cme.getStructure_id())) {
				structIds.add(cme.getStructure_id());
			}
		}
		if (structIds.size() == 0) {
			return null;
		}

		return dataService.structureNames(structIds);
	}
}
