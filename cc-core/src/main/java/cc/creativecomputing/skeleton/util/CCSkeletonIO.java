package cc.creativecomputing.skeleton.util;

public class CCSkeletonIO {
	static final String SKELETON_NODE = "skeleton";
	static final String JOINT_NODE = "joint";
	
	static final String EVENT_ATTRIBUTE = "event";
	static final String ID_ATTRIBUTE = "id";
	static final String FRAME_ATTRIBUTE = "frame";
	static final String TIME_ATTRIBUTE = "time";

	static final String JOINT_TYPE_ATTRIBUTE = "type";
	static final String JOINT_X_ATTRIBUTE = "x";
	static final String JOINT_Y_ATTRIBUTE = "y";
	static final String JOINT_Z_ATTRIBUTE = "z";
	
	public static enum CCSkeletonEvent{
		NEW, UPDATE, LOST
	}
}
