package org.dice_research.opal.slicer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * I/O utilities
 *
 * @author Adrian Wilke
 */
public abstract class IoUtils {

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
}