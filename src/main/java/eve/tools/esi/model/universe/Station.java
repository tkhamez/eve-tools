package eve.tools.esi.model.universe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Station {

	private Integer station_id;

	private String name;

	private Integer type_id;

	private Position position;

	private Long system_id;

	private Float reprocessing_efficiency;

	private Float reprocessing_stations_take;

	private Long max_dockable_ship_volume;

	private Long office_rental_cost;

	private List<String> services;

	private Long owner;

	private Integer race_id;

	private Map<String, Object> _other = new HashMap<>();

	@JsonAnyGetter
	public Map<String, Object> any() {
		return _other;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		_other.put(name, value);
	}
}
