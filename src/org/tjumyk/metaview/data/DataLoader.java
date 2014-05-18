package org.tjumyk.metaview.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Data loader for loading data assets or extracting them to temporary folder.
 * 
 * @author 宇锴
 */
public class DataLoader {
	/**
	 * Load the data asset with the given asset name
	 * 
	 * @param name
	 *            asset name
	 * @return InputStream for reading contents of the asset
	 */
	public static InputStream load(String name) {
		return DataLoader.class.getResourceAsStream(name);
	}

	/**
	 * Load the data asset to temporary folder with a random file name.
	 * 
	 * @param name
	 *            asset name
	 * @return The temporarily extracted asset file
	 * @throws IOException
	 *             if IO error
	 */
	public static File loadToTemp(String name) throws IOException {
		byte[] buffer = IOUtils.toByteArray(load(name));
		File file = File.createTempFile("tmp", name);
		FileUtils.writeByteArrayToFile(file, buffer);
		file.deleteOnExit();
		return file;
	}
}
