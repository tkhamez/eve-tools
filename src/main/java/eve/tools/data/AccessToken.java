package eve.tools.data;

import lombok.Data;

import java.util.List;

@Data
public class AccessToken {

	private Long characterId;

	private String characterName;

	private List<String> scopes;
}
