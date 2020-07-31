package eve.tools.esi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import eve.tools.entity.User;
import eve.tools.esi.model.assets.Asset;
import eve.tools.esi.model.assets.AssetName;
import eve.tools.esi.model.character.PublicInfo;
import eve.tools.esi.model.contracts.Contract;
import eve.tools.esi.model.contracts.ContractItem;
import eve.tools.esi.model.industry.CorporationMiningExtraction;
import eve.tools.esi.model.oauth.Token;
import eve.tools.esi.model.planetary_interaction.PiPlanet;
import eve.tools.esi.model.planetary_interaction.PlanetDetails;
import eve.tools.esi.model.universe.Moon;
import eve.tools.esi.model.universe.Planet;
import eve.tools.esi.model.universe.Station;
import eve.tools.esi.model.universe.Structure;
import eve.tools.esi.model.universe.Type;
import eve.tools.esi.model.universe.UniverseName;
import eve.tools.service.ApiCache;
import eve.tools.service.EveConfigService;
import eve.tools.service.UserService;

/**
 * https://esi.evetech.net/_latest/swagger.json
 *
 * All methods return null on error, the error is usually logged in the Client class.
 *
 * TODO implement more caches
 */
@Service
public class Api {

	private final Logger log = LoggerFactory.getLogger(Api.class);

	@Autowired
	private Client client;

	@Autowired
	private ApiCache cache;

	@Autowired
	private UserService userService;

	@Autowired
	private Auth auth;

	@Autowired
	private EveConfigService conf;

	public Client getClient() {
		return client;
	}

	/* Assets */

	/**
	 * https://esi.evetech.net/ui/#/Assets/get_characters_character_id_assets
	 * esi-assets.read_assets.v1
	 */
	public List<Asset> characterAssets(Long characterId) {
		String path = "/v5/characters/" + characterId +  "/assets/";
		Asset[] data = getAuthenticated(path, Asset[].class);

		if (data == null) {
			return null;
		}

		List<Asset> dataList = new ArrayList<>(Arrays.asList(data));

		int currentPage = 1;
		Integer pages = client.getPages();
		while (currentPage < pages) {
			currentPage ++;
			Asset[] data2 = getAuthenticated(path + "?page=" + currentPage, Asset[].class);
			if (data2 != null) {
				dataList.addAll(Arrays.asList(data2));
			}
		}

		return dataList;
	}

	/**
	 * https://esi.evetech.net/ui/#/Assets/post_characters_character_id_assets_names
	 * scope esi-assets.read_assets.v1
	 */
	public List<AssetName> characterAssetNames(Long characterId, List<Long> itemIds) {
		String path = "/v1/characters/" + characterId +  "/assets/names/";

		List<AssetName> dataList = new ArrayList<>();

		int processed = 0;
		List<Long> rest = itemIds;
		while (rest.size() > processed) {
			int to = Math.min(rest.size(), 1000);
			List<Long> current = rest.subList(0, to);
			rest = rest.subList(to, rest.size());

			String body = new JSONArray(current).toString();
			AssetName[] data = postAuthenticated(path, AssetName[].class, body);
			if (data != null) {
				dataList.addAll(Arrays.asList(data));
			}
		}

		return dataList.size() > 0 ? dataList : null;
	}

	/* Character */

	/**
	 * https://esi.evetech.net/ui/#/Character/get_characters_character_id
	 */
	public PublicInfo characterPublicInfo(Long characterId) {
		String path = "/v4/characters/" + characterId;

		return get(path, PublicInfo.class);
	}

	/* Contracts */

	/**
	 * https://esi.evetech.net/ui/#/Contracts/get_corporations_corporation_id_contracts
	 * esi-contracts.read_corporation_contracts.v1
	 */
	public List<Contract> corporationContracts(Long corporationId) {
		String path = "/v1/corporations/" + corporationId + "/contracts/";
		Contract[] data = getAuthenticated(path, Contract[].class);

		return data == null ? null : Arrays.asList(data);
	}

	/**
	 * https://esi.evetech.net/ui/#/Contracts/get_corporations_corporation_id_contracts_contract_id_items
	 * esi-contracts.read_corporation_contracts.v1
	 */
	public List<ContractItem> corporationContractItems(Long corporationId, Long contractId) {
		String path = "/v1/corporations/" + corporationId + "/contracts/" + contractId + "/items/";
		ContractItem[] data = getAuthenticated(path, ContractItem[].class);

		return data == null ? null : Arrays.asList(data);
	}

	/**
	 * https://esi.evetech.net/ui/#/Contracts/get_characters_character_id_contracts_contract_id_items
	 * esi-contracts.read_character_contracts.v1
	 */
	public List<ContractItem> characterContractItems(Long characterId, Long contractId) {
		String path = "/v1/characters/" + characterId + "/contracts/" + contractId + "/items/";
		ContractItem[] data = getAuthenticated(path, ContractItem[].class);

		return data == null ? null : Arrays.asList(data);
	}

	/* Industry */

	/**
	 * https://esi.evetech.net/ui/#/Industry/get_corporation_corporation_id_mining_extractions
	 * esi-industry.read_corporation_mining.v1
	 */
	public List<CorporationMiningExtraction> corporationMiningExtractions(Long corporationId) {
		String path = "/v1/corporation/" + corporationId + "/mining/extractions/";
		CorporationMiningExtraction[] data = getAuthenticated(path, CorporationMiningExtraction[].class);

		// API may return a 403 "Character does not have required role(s)"

		return data == null ? null : Arrays.asList(data);
	}

	/* Planetary Interaction */

	/**
	 * https://esi.evetech.net/ui/#/Planetary32Interaction/get_characters_character_id_planets
	 * esi-planets.manage_planets.v1
	 */
	public List<PiPlanet> characterPlanet(Long characterId) {
		String path = "/v1/characters/" + characterId + "/planets/";
		PiPlanet[] data = getAuthenticated(path, PiPlanet[].class);

		return data == null ? null : Arrays.asList(data);
	}

	/**
	 * https://esi.evetech.net/ui/#/Planetary32Interaction/get_characters_character_id_planets
	 * esi-planets.manage_planets.v1
	 */
	public PlanetDetails characterPlanetDetail(Long characterId, Integer planetId) {
		String path = "/v3/characters/" + characterId + "/planets/" + planetId + "/";

		return getAuthenticated(path, PlanetDetails.class);
	}

	/* Universe */

	/**
	 * https://esi.evetech.net/ui/#/Universe/get_universe_moons_moon_id
	 *
	 * Result is cached.
	 */
	public Moon universeMoon(Long moonId) {
		String path = "/v1/universe/moons/" + moonId + "/";

		if (cache.getMoon(moonId) == null) {
			Moon data = get(path, Moon.class);
			if (data != null) {
				cache.setMoon(moonId, data);
			}
		}

		return cache.getMoon(moonId);
	}

	/**
	 * https://esi.evetech.net/ui/#/Universe/get_universe_planets_planet_id
	 *
	 * Result is cached.
	 */
	public Planet universePlanet(Integer planetId) {
		String path = "/v1/universe/planets/" + planetId + "/";

		if (cache.getPlanet(planetId) == null) {
			Planet data = get(path, Planet.class);
			if (data != null) {
				cache.setPlanet(planetId, data);
			}
		}

		return cache.getPlanet(planetId);
	}

	/**
	 * https://esi.evetech.net/ui/#/Universe/get_universe_stations_station_id
	 *
	 * Result is cached.
	 */
	public Station universeStation(Integer stationId) {
		String path = "/v2/universe/stations/" + stationId + "/";

		if (cache.getStation(stationId) == null) {
			Station data = get(path, Station.class);
			if (data != null) {
				cache.setStation(stationId, data);
			}
		}

		return cache.getStation(stationId);
	}

	/**
	 * https://esi.evetech.net/ui/#/Universe/get_universe_structures_structure_id
	 * esi-universe.read_structures.v1
	 *
	 * Returns name = (Forbidden) if user has no access to the citadel.
	 */
	public Structure universeStructure(Long structureId) {
		String path = "/v2/universe/structures/" + structureId + "/";

		Structure data = getAuthenticated(path, Structure.class);
		if (data == null && client.getLastError().get_http_status_code() == 403) {
			data = new Structure();
			data.setName("(Forbidden)");
		}

		return data;
	}

	/**
	 * https://esi.evetech.net/ui/#/Universe/get_universe_types_type_id
	 *
	 * Result is cached.
	 */
	public Type universeType(Integer typeId) {
		String path = "/v3/universe/types/" + typeId + "/";

		if (cache.getType(typeId) == null) {
			Type data = get(path, Type.class);
			if (data != null) {
				cache.setType(typeId, data);
			}
		}

		return cache.getType(typeId);
	}

	/**
	 * https://esi.evetech.net/ui/#/Universe/post_universe_names
	 */
	public List<UniverseName> universeNames(List<Long> ids) {
		String path = "/v3/universe/names/";
		String body = new JSONArray(ids).toString();
		UniverseName[] data = post(path, UniverseName[].class, body);

		return data == null ? null : Arrays.asList(data);
	}

	private <T> T get(String path, Class<T> valueType) {
		return client.get(valueType, url(path));
	}

	private <T> T post(String path, Class<T> valueType, String jsonBody) {
		return client.post(valueType, url(path), jsonBody);
	}

	private <T> T getAuthenticated(String path, Class<T> valueType) {
		return client.get(valueType, url(path), authHeaders());
	}

	private <T> T postAuthenticated(String path, Class<T> valueType, String jsonBody) {
		return client.post(valueType, url(path), authHeaders(), jsonBody);
	}

	private String url(String path) {
		//log.info(path);

		if (!path.contains("?")) {
			path += "?datasource=" + conf.dataSource();
		} else {
			path += "&datasource=" + conf.dataSource();
		}

		return "https://esi.evetech.net" + path;
	}

	private HttpHeaders authHeaders() {
		User user = userService.getAuthenticatedUser();

		if (user == null) {
			log.error("SecurityContextHolder: Authentication principal not found");
			return null;
		}

		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		if (user.getExpiresOn().getTimeInMillis() - now.getTimeInMillis() <= 60 * 1000) {
			Token newToken = auth.refresh(user.getRefreshToken());
			if (newToken != null) {
				userService.updateAuthenticatedUser(user, newToken);
			}
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + user.getAccessToken());

		return headers;
	}
}
