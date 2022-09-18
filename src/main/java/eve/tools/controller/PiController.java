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
import eve.tools.esi.model.planetary_interaction.PiPlanet;
import eve.tools.esi.model.planetary_interaction.Pin;
import eve.tools.esi.model.planetary_interaction.PlanetDetails;
import eve.tools.esi.model.universe.Planet;
import eve.tools.esi.model.universe.UniverseName;
import eve.tools.service.UserService;

@Controller
@Secured("ROLE_EVE_PI")
public class PiController {

	@Autowired
	private Api api;

	@Autowired
	private UserService userService;

	@RequestMapping("pi")
	String data(Model model) {
		Long characterId = Long.valueOf(userService.getAuthenticatedUser().getUsername());
		List<PiPlanet> planets = api.characterPlanet(characterId);

		if (planets == null && api.getClient().getLastError() != null) {
			model.addAttribute("apiError", "API error: " + api.getClient().getLastError().getMessage());

		} else if (planets != null) {
			model.addAttribute("planets", planets);
			model.addAttribute("planetNames", planetNames(planets));

			Map<Integer, PlanetDetails> colonies = new HashMap<>();
			planets.forEach(planet -> {
				PlanetDetails colony = api.characterPlanetDetail(characterId, planet.getPlanet_id());
				if (colony != null) {
					colonies.put(planet.getPlanet_id(), colony);
				}
			});
			model.addAttribute("colonies", colonies);
			model.addAttribute("types", types(colonies));
		}

		return "pi";
	}

	private Map<Integer, String> planetNames(List<PiPlanet> cps) {
		Map<Integer, String> names = new HashMap<>();

		cps.forEach(cp -> {
			Planet planet = api.universePlanet(cp.getPlanet_id());
			if (planet != null) {
				names.put(planet.getPlanet_id(), planet.getName());
			}
		});

		return names;
	}

	private Map<Integer, String> types(Map<Integer, PlanetDetails> colonies) {
		Map<Integer, String> types = new HashMap<>();

		List<Long> typeIds = new ArrayList<>();
		colonies.forEach((k, colony) -> {
			for (Pin pin : colony.getPins()) {
				if (! typeIds.contains((long) pin.getType_id())) {
					typeIds.add((long) pin.getType_id());
				}
			}
		});

		if (typeIds.size() == 0) {
			return types;
		}

		List<UniverseName> names = api.universeNames(typeIds);
		if (names != null) {
			names.forEach((name) -> types.put(name.getId().intValue(), name.getName()));
		}

		return types;
	}
}
