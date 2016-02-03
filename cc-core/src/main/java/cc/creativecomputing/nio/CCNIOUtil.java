package cc.creativecomputing.nio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

import cc.creativecomputing.CCSystem;
import cc.creativecomputing.io.CCIOException;
import cc.creativecomputing.math.CCMath;

public class CCNIOUtil {
	
	/**
	 * Gets the extension of the given file.
	 * @param theFileName file name to check the extension
	 * @return the extension of the file
	 */
	public static String fileExtension(final String theFileName) {
		int i = theFileName.lastIndexOf('.');
		
		if(i < 0)
			return null;
		
		if(i >= theFileName.length() - 1)
			return null;
		
		return theFileName.substring(i + 1).toLowerCase();
	}
	
	/**
	 * Gets the extension of the given file.
	 * @param thePath path of the file to check the extension
	 * @return the extension of the file
	 */
	public static String fileExtension(final Path thePath) {
		return fileExtension(thePath.getFileName());
	}
	
	/**
	 * Gets the name of the given file without an extension.
	 * @param theFile path to get the name
	 * @return name of the given file without an extension.
	 */
	public static String fileName(final String theFile){
		final int myIndex = theFile.lastIndexOf('.');
		final int mySeperator = theFile.lastIndexOf(FileSystems.getDefault().getSeparator());
		return theFile.substring(CCMath.max(0, mySeperator),myIndex);
	}
	
	/**
	 * Gets the name of the given file without an extension.
	 * @param thePath path to get the name
	 * @return name of the given file without an extension.
	 */
	public static String fileName(final Path thePath){
		return fileName(thePath.getFileName());
	}

	/**
	 * Checks if the given file A is newer than the given file B
	 * @param theA path of file a
	 * @param theB path of file b
	 * @return <code>true</code> if A is newer than B otherwise <code>false</code>
	 */
	public static boolean isNewer(final String theA, final String theB){
		return isNewer(Paths.get(theA), Paths.get(theB));
	}
	
	/**
	 * Checks if the given file A is newer than the given file B
	 * @param theA path of file a
	 * @param theB path of file b
	 * @return <code>true</code> if A is newer than B otherwise <code>false</code>
	 */
	public static boolean isNewer(final Path theA, final Path theB){
		if(!Files.exists(theA))return false;
		if(!Files.exists(theB))return true;
		
		try {
			BasicFileAttributes myAttributesA = Files.readAttributes(theA, BasicFileAttributes.class);
			BasicFileAttributes myAttributesB = Files.readAttributes(theB, BasicFileAttributes.class);
			return myAttributesA.lastModifiedTime().toMillis() > myAttributesB.lastModifiedTime().toMillis();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	/**
	 * Takes a path and creates any in-between folders if they don't
	 * already exist. Useful when trying to save to a sub folder that
	 * may not actually exist.
	 * @param thePath thePath to check
	 */
	static public void createPath(Path thePath){
		try {
			Files.createDirectories(thePath);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}

	
	/**
	 * Takes a path and creates any in-between folders if they don't
	 * already exist. Useful when trying to save to a sub folder that
	 * may not actually exist.
	 * @param thePath thePath to check
	 */
	static public void createPath(String thePath){
		createPath(Paths.get(thePath));
	}
	
	/**
	 * Return a full path to an item in the data folder as a Path object. 
	 * @param thePath source path for query
	 * @return full path to an item in the data folder as a Path object
	 */
	static public Path dataPath(String thePath) {
		// isAbsolute() could throw an access exception, but so will writing
		// to the local disk using the sketch path, so this is safe here.
		Path myFile = Paths.get(thePath);
		if (myFile.isAbsolute())
			return myFile;

		String myJarPath = CCNIOUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
		if (myJarPath.contains("Contents/Resources/Java/")) {
			Path containingFolder  =Paths.get(myJarPath).getParent();
			Path dataFolder = containingFolder.resolve(Paths.get("data"));
			return dataFolder.resolve(Paths.get(thePath));
		}
		// Windows, Linux, or when not using a Mac OS X .app file
		return Paths.get(CCSystem.applicationPath,"data",thePath);
	}
	
	/**
	 * Opens a file, returning an input stream to read from the file. The stream
     * will not be buffered, and is not required to support the {@link
     * InputStream#mark mark} or {@link InputStream#reset reset} methods. The
     * stream will be safe for access by multiple concurrent threads. Reading
     * commences at the beginning of the file. Whether the returned stream is
     * <i>asynchronously closeable</i> and/or <i>interruptible</i> is highly
     * file system provider specific and therefore not specified.
     *
     * <p> The {@code options} parameter determines how the file is opened.
     * If no options are present then it is equivalent to opening the file with
     * the {@link StandardOpenOption#READ READ} option. In addition to the {@code
     * READ} option, an implementation may also support additional implementation
     * specific options.
     *
     * @param   thePath
     *          the path to the file to open
     * @param   theOptions
     *          options specifying how the file is opened
	 * @param thePath
	 * @param theOptions
	 * @return a new input stream
	 */
	static public InputStream createInputStream(Path thePath, OpenOption...theOptions){
		try {
			return Files.newInputStream(thePath, theOptions);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	/**
	 * Opens a file, returning an input stream to read from the file.
	 * @see #createInputStream(Path, OpenOption...)
	 * @param thePath
	 * @param theOptions
	 * @return a new input stream
	 */
	static public InputStream createInputStream(String thePath, OpenOption...theOptions){
		return createInputStream(Paths.get(thePath), theOptions);
	}
	
	/**
	 * Opens or creates a file, returning an output stream that may be used to
     * write bytes to the file. The resulting stream will not be buffered. The
     * stream will be safe for access by multiple concurrent threads. Whether
     * the returned stream is <i>asynchronously closeable</i> and/or
     * <i>interruptible</i> is highly file system provider specific and
     * therefore not specified.
     *
     * <p> This method opens or creates a file in exactly the manner specified
     * by the {@link #newByteChannel(Path,Set,FileAttribute[]) newByteChannel}
     * method with the exception that the {@link StandardOpenOption#READ READ}
     * option may not be present in the array of options. If no options are
     * present then this method works as if the {@link StandardOpenOption#CREATE
     * CREATE}, {@link StandardOpenOption#TRUNCATE_EXISTING TRUNCATE_EXISTING},
     * and {@link StandardOpenOption#WRITE WRITE} options are present. In other
     * words, it opens the file for writing, creating the file if it doesn't
     * exist, or initially truncating an existing {@link #isRegularFile
     * regular-file} to a size of {@code 0} if it exists.
     *
     * <p> <b>Usage Examples:</b>
     * <pre>
     *     Path path = ...
     *
     *     // truncate and overwrite an existing file, or create the file if
     *     // it doesn't initially exist
     *     OutputStream out = Files.newOutputStream(path);
     *
     *     // append to an existing file, fail if the file does not exist
     *     out = Files.newOutputStream(path, APPEND);
     *
     *     // append to an existing file, create file if it doesn't initially exist
     *     out = Files.newOutputStream(path, CREATE, APPEND);
     *
     *     // always create new file, failing if it already exists
     *     out = Files.newOutputStream(path, CREATE_NEW);
     * </pre>
     *
     * @param   thePath
     *          the path to the file to open or create
     * @param   theOptions
     *          options specifying how the file is opened
     *
     * @return  a new output stream
	 */
	static public OutputStream createOutputStream(Path thePath, OpenOption...theOptions){
		try {
			return Files.newOutputStream(thePath, theOptions);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	/**
	 * Opens a file, returning an input stream to read from the file.
	 * @see #createOutputStream(Path, OpenOption...)
	 * @param thePath
	 * @param theOptions
	 * @return a new output stream
	 */
	static public OutputStream createOutputStream(String thePath, OpenOption...theOptions){
		return createOutputStream(Paths.get(thePath), theOptions);
	}
	
	/**
	 * Opens a file for reading, returning a {@code BufferedReader} that may be
	 * used to read text from the file in an efficient manner. Bytes from the
	 * file are decoded into characters using the specified charset. Reading
	 * commences at the beginning of the file.
	 * 
	 * <p>
	 * The {@code Reader} methods that read from the file throw
	 * {@code IOException} if a malformed or unmappable byte sequence is read.
	 * 
	 * @param   thePath
     *          the path to the file
     * @param   theCharset
     *          the charset to use for decoding
     *
     * @return  a new buffered reader, with default buffer size, to read text
     *          from the file
	 */
	static public BufferedReader createReader(Path thePath, Charset theCharset) {
		try {
			return Files.newBufferedReader(thePath, theCharset);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	static public BufferedReader createReader(String thePath, Charset theCharset){
		return createReader(Paths.get(thePath), theCharset);
	}
	
	static public BufferedReader createReader(String thePath){
		return createReader(Paths.get(thePath), StandardCharsets.UTF_8);
	}
	
	/**
     * Opens or creates a file for writing, returning a {@code BufferedWriter}
     * that may be used to write text to the file in an efficient manner.
     * The {@code options} parameter specifies how the the file is created or
     * opened. If no options are present then this method works as if the {@link
     * StandardOpenOption#CREATE CREATE}, {@link
     * StandardOpenOption#TRUNCATE_EXISTING TRUNCATE_EXISTING}, and {@link
     * StandardOpenOption#WRITE WRITE} options are present. In other words, it
     * opens the file for writing, creating the file if it doesn't exist, or
     * initially truncating an existing {@link #isRegularFile regular-file} to
     * a size of {@code 0} if it exists.
     *
     * <p> The {@code Writer} methods to write text throw {@code IOException}
     * if the text cannot be encoded using the specified charset.
     *
     * @param   thePath
     *          the path to the file
     * @param   theCharset
     *          the charset to use for encoding
     * @param   options
     *          options specifying how the file is opened
     *
     * @return  a new buffered writer, with default buffer size, to write text
     *          to the file
     *
     * @see #write(Path,Iterable,Charset,OpenOption[])
     */
	static public BufferedWriter createWriterr(Path thePath, Charset theCharset) {
		try {
			return Files.newBufferedWriter(thePath, theCharset);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	static public BufferedWriter createWriterr(String thePath, Charset theCharset){
		return createWriterr(Paths.get(thePath), theCharset);
	}
	
	static public BufferedWriter createWriterr(String thePath){
		return createWriterr(Paths.get(thePath), StandardCharsets.UTF_8);
	}
}
