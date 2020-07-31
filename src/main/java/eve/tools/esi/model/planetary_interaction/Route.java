package eve.tools.esi.model.planetary_interaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Route {

	private Long source_pin_id;

	private Long destination_pin_id;

	private Integer quantity;

	private Integer content_type_id;

	private Integer route_id;

	// undocumented
	private List<Long> waypoints;

	private Map<String, Object> _other = new HashMap<String, Object>();

	@JsonAnyGetter
	public Map<String, Object> any() {
		return _other;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		_other.put(name, value);
	}
}
