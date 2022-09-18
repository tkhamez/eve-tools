package eve.tools.esi.model.contracts;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Contract {

	private Long contract_id;

	private Long issuer_id;

	private Long issuer_corporation_id;

	private Long assignee_id;

	private Long acceptor_id;

	private String type;

	private String status;

	private Boolean for_corporation;

	private String availability;

	private Calendar date_issued;

	private Calendar date_expired;

	private Long start_location_id;

	private Long end_location_id;

	private Calendar date_accepted;

	private String days_to_complete;

	private Calendar date_completed;

	private Long price;

	private Long reward;

	private Long buyout;

	private Float volume;

	// not documented
	private Long collateral;

	// not documented
	private String title;

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
