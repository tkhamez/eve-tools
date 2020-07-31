package eve.tools.esi.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EsiError {

	private Integer _http_status_code;

	private String _body;

	private String _url;

	private String error;

	private String error_description;

	private String sso_status;

	private Map<String, Object> _other = new HashMap<String, Object>();

	@JsonAnyGetter
	public Map<String, Object> any() {
		return _other;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		_other.put(name, value);
	}

	public String getMessage() {
		String message;
    	if (getError() != null) {
    		message = getError();
    	} else {
    		message = "HTTP Status Code: " + String.valueOf(get_http_status_code());
    	}

    	return message;
	}
}
