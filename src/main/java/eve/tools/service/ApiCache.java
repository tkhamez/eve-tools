package eve.tools.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eve.tools.esi.model.universe.Moon;
import eve.tools.esi.model.universe.Planet;
import eve.tools.esi.model.universe.Station;
import eve.tools.esi.model.universe.Type;

/**
 *
 * TODO expire cache
 * TODO add more caches: character names, corporation names
 * TODO cache for authenticated data, like universe/structures
 */
@Service
@Scope(value = "singleton")
public class ApiCache {

	private Map<Integer, Station> station =  new HashMap<>();

	private Map<Integer, Type> types =  new HashMap<>();

	private Map<Long, Moon> moons =  new HashMap<>();

	private Map<Integer, Planet> planets =  new HashMap<>();

	public Station getStation(Integer id) {
		return station.containsKey(id) ? station.get(id) : null;
	}

	public void setStation(Integer id, Station data) {
		station.put(id, data);
	}

	public Type getType(Integer id) {
		return types.containsKey(id) ? types.get(id) : null;
	}

	public void setType(Integer id, Type data) {
		types.put(id, data);
	}

	public Moon getMoon(Long id) {
		return moons.containsKey(id) ? moons.get(id) : null;
	}

	public void setMoon(Long id, Moon data) {
		moons.put(id, data);
	}

	public Planet getPlanet(Integer id) {
		return planets.containsKey(id) ? planets.get(id) : null;
	}

	public void setPlanet(Integer id, Planet data) {
		planets.put(id, data);
	}
}
