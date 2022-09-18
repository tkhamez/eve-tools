package eve.tools.esi.model.oauth;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Verify {

	@JsonProperty("CharacterID")
	private Long CharacterID;

	@JsonProperty("CharacterName")
	private String CharacterName;

	@JsonProperty("ExpiresOn")
	private String ExpiresOn;

	@JsonProperty("Scopes")
	private String Scopes;

	@JsonProperty("TokenType")
	private String TokenType;

	@JsonProperty("CharacterOwnerHash")
	private String CharacterOwnerHash;

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
