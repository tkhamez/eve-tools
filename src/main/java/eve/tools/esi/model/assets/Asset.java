package eve.tools.esi.model.assets;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Asset {

	private String location_flag;

	private Long location_id;

	private Boolean is_singleton;

	private Integer type_id;

	private Long item_id;

	private String location_type;

	private Integer quantity;

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
