package eve.tools.esi.model.universe;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UniverseName {

	private Long id;

	private String name;

	private String category;

	private Map<String, Object> _other = new HashMap<>();

	@JsonAnyGetter
	public Map<String, Object> any() {
		return _other;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		_other.put(name, value);
	}

	public UniverseName() {
	}

	public UniverseName(Long id, String name, String category) {
		this.id = id;
		this.name = name;
		this.category = category;
	}
}
