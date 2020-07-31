package eve.tools.ui;

import java.util.List;

import lombok.Data;

@Data
public class AssetsLocation {

	private Long locationId;

	private String locationName;

	private List<Assets> children;

	public AssetsLocation(List<Assets> children, Long locationId, String locationName) {
		this.children = children;
		this.locationId = locationId;
		this.locationName = locationName;
	}

	public AssetsLocation(List<Assets> children, Long locationId) {
		this.children = children;
		this.locationId = locationId;
	}
}
