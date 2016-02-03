package cc.creativecomputing.model.collada;

public class CCColladaSkeletonUtil {
	
	private CCColladaSkeleton _mySkeleton;
	private CCColladaSkeletonSkin _mySkeletonSkin;

	public CCColladaSkeletonUtil(String theColladaFile, String theSceneNode){
		CCColladaLoader myColladaLoader = new CCColladaLoader(theColladaFile);
		
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkinController = myColladaLoader.controllers().element(0).skin();
		
		_mySkeleton = new CCColladaSkeleton(
			mySkinController,
			myScene.node(theSceneNode)
		);
		
		_mySkeletonSkin = new CCColladaSkeletonSkin(mySkinController);
	}
	
	public CCColladaSkeleton skeleton(){
		return _mySkeleton;
	}
	
	public CCColladaSkeletonSkin skin(){
		return _mySkeletonSkin;
	}
}
