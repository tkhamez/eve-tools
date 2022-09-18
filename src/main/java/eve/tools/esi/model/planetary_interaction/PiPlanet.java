package eve.tools.esi.model.planetary_interaction;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PiPlanet {

	private Integer solar_system_id;

	private Integer upgrade_level;

	private Integer planet_id;

	private Calendar last_update;

	private Integer owner_id;

	private String planet_type;

	private Integer num_pins;

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
