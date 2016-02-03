package cc.creativecomputing.util;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;

import jogamp.common.os.PlatformPropsImpl;

import com.jogamp.common.util.IOUtil;
import com.jogamp.common.util.JarUtil;
import com.jogamp.common.util.cache.TempJarCache;

public class CCNativeLibUtil {
	
	private static final String stripName(String name) {
		int idx = name.indexOf(".jar");
		if(0 < idx) {
			return name.substring(0, idx);
		}
		return name;
	}
	
	public static void prepareLibraryForLoading(Class<?> theClass, String theLibName){
		try{
			final URI myClassJarURI = JarUtil.getJarURI(theClass.getName(), theClass.getClassLoader());
	        final String myJarName = JarUtil.getJarBasename(myClassJarURI);
			final String myNativeJarBasename = stripName(myJarName);
			final String myNativeJarName = myNativeJarBasename+"-natives-"+PlatformPropsImpl.os_and_arch+".jar";
	        final URI myJarUriRoot = IOUtil.getURIDirname( JarUtil.getJarSubURI( myClassJarURI ) );
	        final URI myNativeJarURI = JarUtil.getJarFileURI(myJarUriRoot, myNativeJarName);
	        TempJarCache.initSingleton();
	        TempJarCache.addNativeLibs(theClass, myNativeJarURI, null);
        
		}catch(Exception e){
			e.printStackTrace();
		}
        
		String myPath = TempJarCache.findLibrary(theLibName);
		File myFile = new File(myPath).getParentFile();
		System.setProperty( "java.library.path", myFile.getAbsolutePath());

		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
