package org.tjumyk.metaview.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DataLoader {
	public static InputStream load(String name){
		return DataLoader.class.getResourceAsStream(name);
	}
	
	public static File loadToTemp(String name) throws IOException{
		byte[] buffer = IOUtils.toByteArray(load(name));
		File file = File.createTempFile("tmp", name);
		FileUtils.writeByteArrayToFile(file, buffer);
		file.deleteOnExit();
		return file;
	}
}
