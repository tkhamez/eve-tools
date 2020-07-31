package eve.tools.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eve.tools.esi.Api;
import eve.tools.esi.model.universe.Station;
import eve.tools.esi.model.universe.Structure;

/**
 * Helper methods
 */
@Service
public class DataService {

	@Autowired
	private Api api;

	/**
	 * Station and structure names.
	 *
	 * For ID range see
	 * https://github.com/ccpgames/eve-glue/blob/master/eve_glue/location_type.py
	 *
	 * see for location ID ranges
	 * https://docs.esi.evetech.net/docs/asset_location_id
	 *
	 * @param structIds
	 * @return Map
	 */
	public Map<Long, String> structureNames(List<Long> structIds) {
		Map<Long, String> structures = new HashMap<Long, String>();

		for (Long structId : structIds) {
			if (structId < 64000000) {
				Station station = api.universeStation(structId.intValue());
				if (station != null) {
					structures.put(structId, station.getName());
				}

			} else {
				Structure structure = api.universeStructure(structId);
				if (structure != null) {
					structures.put(structId, structure.getName());
				}
			}
		}

		return structures;
	}
}
