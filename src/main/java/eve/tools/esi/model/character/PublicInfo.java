package eve.tools.esi.model.character;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PublicInfo {

	private Long corporation_id;

	private String birthday;

	private String name;

	private String gender;

	private Integer race_id;

	private String description;

	private Integer bloodline_id;

	private Integer ancestry_id;

	private Long alliance_id;

	private Float security_status;

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
