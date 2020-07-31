package eve.tools.ui;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import eve.tools.esi.model.assets.Asset;

@Data
public class Assets {

	private Long locationId;

	private String locationName;

	private Long itemId;

	private String itemName;

	private String locationFlag;

	private String locationType;

	private Boolean isSingleton;

	private Integer typeId;

	private String typeName;

	private Integer quantity;

	private List<Assets> children = new ArrayList<>();

	public Assets(Asset asset, String typeName, String locationName, String itemName) {
		locationId = asset.getLocation_id();
		itemId = asset.getItem_id();
		locationFlag = asset.getLocation_flag();
		locationType = asset.getLocation_type();
		isSingleton = asset.getIs_singleton();
		typeId = asset.getType_id();
		quantity = asset.getQuantity();

		this.typeName = typeName;
		this.locationName = locationName;
		this.itemName = itemName;
	}
}
