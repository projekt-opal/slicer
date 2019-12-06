package org.dice_research.opal.slicer.investigation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.dice_research.opal.common.utilities.Hash;

/**
 * I/O utilities
 *
 * @author Adrian Wilke
 */
public abstract class IoUtils {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final File DIRECTORY = new File(System.getProperty("java.io.tmpdir"), "opal-slicer");

	static {
		if (!DIRECTORY.exists()) {
			DIRECTORY.mkdirs();
		}
	}

	/**
	 * Connects socket to the server with a specified timeout value.
	 * 
	 * @see https://stackoverflow.com/a/3584332
	 */
	public static boolean pingHost(String host, int port, int timeout) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), timeout);
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static void serialize(Object object, File file) {
		LOGGER.info("Serializing: " + file.getPath());
		// see https://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object deserialize(File file) {
		LOGGER.info("Deserializing: " + file.getPath());
		// see https://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object object = ois.readObject();
			ois.close();
			fis.close();
			return object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void serialize(Object object, String key, boolean doHashing) {
//		TODO
//		if (doHashing) {
//			key = new Hash().md5(key);
//		}
		serialize(object, keyToFile(key));
	}

	public static Object deserialize(String key, boolean doHashing) {
//		TODO
//		if (doHashing) {
//			key = new Hash().md5(key);
//		}
		return deserialize(keyToFile(key));
	}

	public static boolean fileForKeyExists(String key, boolean doHashing) {
//		TODO
//		if (doHashing) {
//			key = new Hash().md5(key);
//		}
		return keyToFile(key).exists();
	}

	public static File keyToFile(String key) {
		return new File(DIRECTORY, key);
	}
}