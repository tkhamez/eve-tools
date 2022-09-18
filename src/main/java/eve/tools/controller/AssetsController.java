package eve.tools.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eve.tools.esi.Api;
import eve.tools.esi.model.assets.Asset;
import eve.tools.esi.model.assets.AssetName;
import eve.tools.esi.model.universe.UniverseName;
import eve.tools.service.DataService;
import eve.tools.service.UserService;
import eve.tools.ui.Assets;
import eve.tools.ui.AssetsLocation;

@Controller
@Secured("ROLE_EVE_ASSETS")
public class AssetsController {

	@Autowired
	private Api api;

	@Autowired
	private UserService userService;

	@Autowired
	private DataService dataService;

	private Long characterId;

	@RequestMapping("assets")
	String data(Model model) {
		characterId = Long.valueOf(userService.getAuthenticatedUser().getUsername());
		List<Asset> assets = api.characterAssets(characterId);

		if (assets == null && api.getClient().getLastError() != null) {
			model.addAttribute("apiError", "API error: " + api.getClient().getLastError().getMessage());

		} else if (assets != null) {
			model.addAttribute("assetsLocation",
				groupByLocation(assets, structures(assets), types(assets), names(assets)));
		}

		return "assets";
	}

	private Map<Long, String> structures(List<Asset> assets) {
		List<Long> structIds = new ArrayList<>();
		for (Asset asset : assets) {

			// TODO 'for whatever reason, the location flag for things within asset safety containers is "Hangar"'

			if (asset.getLocation_type().equals("station") ||
				asset.getLocation_flag().equals("Hangar") ||
				asset.getLocation_flag().equals("Deliveries")
			) {
				if (! structIds.contains(asset.getLocation_id())) {
					structIds.add(asset.getLocation_id());
				}
			} /*else {
				// location_type = other
				// location_flag = MedSlot0, Cargo etc.
			}*/
		}
		if (structIds.size() == 0) {
			return null;
		}

		return dataService.structureNames(structIds);
	}

	private Map<Long, String> names(List<Asset> assets) {
		List<Long> itemIds = new ArrayList<>();
		for (Asset asset : assets) {
			if (! asset.getIs_singleton()) {
				continue;
			}
			if (! itemIds.contains(asset.getItem_id())) {
				itemIds.add(asset.getItem_id());
			}
		}

		List<AssetName> assetNames = api.characterAssetNames(characterId, itemIds);

		Map<Long, String> names = new HashMap<>();

		if (assetNames != null) {
			assetNames.forEach(an -> names.put(an.getItem_id(), an.getName()));
		}

		return names;
	}

	private List<AssetsLocation> groupByLocation(List<Asset> assets, Map<Long, String> structures,
			Map<Integer, String> types, Map<Long, String> names) {

		// assets in station and structures (level1), remember rest (level2)
		Map<Long, List<Assets>> level1 = new HashMap<>();
		Map<Long, List<Assets>> level2 = new HashMap<>();
		for (Asset asset : assets) {
			Long locId = asset.getLocation_id();
			String typeName = types.getOrDefault(asset.getType_id(), "");
			String itemName = null;
			if (names.containsKey(asset.getItem_id())) {
				itemName = names.get(asset.getItem_id());
			}
			if (structures.containsKey(locId)) {
				if (! level1.containsKey(locId)) {
					level1.put(locId, new ArrayList<>());
				}
				level1.get(locId).add(new Assets(asset, typeName, structures.get(locId), itemName));

			} else {
				if (! level2.containsKey(locId)) {
					level2.put(locId, new ArrayList<>());
				}
				level2.get(locId).add(new Assets(asset, typeName, null, itemName));
			}
		}

		// sort level1's Assets list by type name, e. g. Ares, Damage Control II etc.
		level1.forEach((l1, a1) -> {
			List<Assets> tmpLvl1 = new ArrayList<>();
			a1.stream()
				.sorted(Comparator.comparing(Assets::getTypeName))
				.forEach(tmpLvl1::add);
			level1.replace(l1, tmpLvl1);
		});

		// assets in containers or ships (setChildren), remember rest (level3)
		Map<Long, List<Assets>> level3 = new HashMap<>();
		level2.forEach((locId2, assetList2) -> {
			boolean foundContainer1 = false;
			for (List<Assets> acList1: level1.values()) {
				for (Assets ac1: acList1) {
					if (locId2.equals(ac1.getItemId())) {

						// sort list by location flag, e. g. Cargo, HiSlot0 etc.
						List<Assets> tmpLvl2 = new ArrayList<>();
						assetList2.stream()
							.sorted(Comparator.comparing(Assets::getLocationFlag))
							.forEach(tmpLvl2::add);

						ac1.setChildren(tmpLvl2);
						foundContainer1 = true;
						break;
					}
				}
				if (foundContainer1) {
					break;
				}
			}
			if (! foundContainer1) {
				level3.put(locId2, assetList2);
			}
		});

		// assets in nested containers (e. g. freight container in ships), remember rest (levelX)
		Map<Long, List<Assets>> levelX = new HashMap<>();
		level3.forEach((locId3, assetList3) -> {
			boolean foundContainer2 = false;
			for (List<Assets> acList1: level1.values()) {
				for (Assets ac1: acList1) {
					for (Assets ac2: ac1.getChildren()) {
						if (locId3.equals(ac2.getItemId())) {

							// sort this list by type name again
							List<Assets> tmpLvl3 = new ArrayList<>();
							assetList3.stream()
								.sorted(Comparator.comparing(Assets::getTypeName))
								.forEach(tmpLvl3::add);

							ac2.setChildren(tmpLvl3);
							foundContainer2 = true;
							break;
						}
					}
					if (foundContainer2) {
						break;
					}
				}
				if (foundContainer2) {
					break;
				}
			}
			if (! foundContainer2) {
				levelX.put(locId3, assetList3);
			}
		});

		// create final list, sorted by location name
		List<AssetsLocation> assetsLocation = new ArrayList<>();
		level1.entrySet()
			.stream()
			.sorted(Comparator.comparing(a -> a.getValue().get(0).getLocationName()))
			.forEach(entry -> assetsLocation.add(new AssetsLocation(
				entry.getValue(),
				entry.getKey(),
				entry.getValue().get(0).getLocationName()
			)));

		// append rest to the list TODO where are these? might be an ESI bug
		// they are: lost ships? deleted stuff?
		levelX.forEach((locIdX, assetListX) -> {
			List<Assets> assetsX = new ArrayList<>(assetListX);
			assetsLocation.add(new AssetsLocation(assetsX, locIdX));
		});

		return assetsLocation;
	}

	private Map<Integer, String> types(List<Asset> assets) {
		Map<Integer, String> types = new HashMap<>();

		List<Long> typeIds = new ArrayList<>();
		for (Asset asset : assets) {
			if (! typeIds.contains((long) asset.getType_id())) {
				typeIds.add((long) asset.getType_id());
			}
		}
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
