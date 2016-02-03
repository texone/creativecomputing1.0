package cc.creativecomputing.exco;

public enum CCExcoRelationType {
	BELONGS_TO_CATEGORY("belongs_to_category"),
	BELONGS_TO_SUBCATEGORY("belongs_to_subcategory"),
	
	COLLECTS_COMPONENTS_IN("collects_components_in"),
	
	IS_ACTIVE_WITH_CONFIGURATION("is_active_with_configuration"),
	IS_CURRENTLY_IN_OPERATIONAL_MODE("is_currently_in_operational_mode"),
	IS_CURRENTLY_IN_RUNNING_MODE("is_currently_in_running_mode"),
	IS_MODIFIED_BY("is_modified_by"),
	IS_MONITORED_AND_CONTROLLED_BY("is_monitored_and_controlled_by"),
	IS_PARAMETERIZABLE_BY("is_parameterizable_by"),
	IS_PART_OF("is_part_of"),

	HAS_COMPONENT("has_component"),
	HAS_COVER_IMAGE("has_cover_image"),
	HAS_DEFAULT_RUNNING_MODE("has_default_running_mode"),
	HAS_EVENT("has_event"),
	HAS_POSSIBLE_RUNNING_MODE("has_possible_running_mode"),
	
	TAKES_PLACE_IN("takes_place_in");
	
	
	private final String _myID;
	
	private CCExcoRelationType(String theID){
		_myID = theID;
	}
	
	public String id(){
		return _myID;
	}
}
