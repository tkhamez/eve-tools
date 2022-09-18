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
public class Pin {

	private Float longitude;

	private Float latitude;

	private Boolean is_running;

	private Integer type_id;

	private Long pin_id;

	// undocumented
	private Calendar last_cycle_start;

	// undocumented
	private Integer schematic_id;

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
