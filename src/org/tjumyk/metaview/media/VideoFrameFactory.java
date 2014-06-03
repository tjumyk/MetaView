package org.tjumyk.metaview.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;
import org.tjumyk.metaview.Main;
import org.tjumyk.metaview.data.DataLoader;

/**
 * Tool for extracting frame images from video, server, or local folder.
 * 
 * @author 宇锴
 */
public class VideoFrameFactory {
	/**
	 * The File object of "ffmpeg.exe"
	 */
	private static File ffmpeg;

	/**
	 * Get frame image from the cache folder
	 * 
	 * @param video
	 *            video URL
	 * @param second
	 *            frame time in seconds
	 * @param height
	 *            fit height of the frame image
	 * @return Image object
	 * @throws IOException
	 *             if IO error
	 */
	public static Image getCache(String video, double second, int height)
			throws IOException {
		File tmp = new File(Main.TEMP_DIR + video.hashCode() + "_" + second
				+ "_" + height + ".jpg");
		Image img = null;
		if (tmp.exists())
			img = new Image(tmp.toURI().toURL().toExternalForm());
		return img;
	}

	/**
	 * Get frame image from local folder, image file is loaded in the cache
	 * folder for reusing.
	 * 
	 * @param video
	 *            video URL
	 * @param folder
	 *            local folder of frame images
	 * @param second
	 *            frame time in seconds
	 * @param height
	 *            fit height of the frame image
	 * @return Image object
	 * @throws IOException
	 *             if IO error
	 */
	public static Image getFromFolder(String video, String folder,
			double second, int height) throws IOException {
		if (!folder.startsWith("file:/"))
			throw new IllegalArgumentException(
					"folder name must be a valid url started with \"file:/\"!");
		String path = folder.substring(6);
		File tmp = new File(path + second + "_" + height + ".jpg");
		Image img = null;
		if (tmp.exists())
			img = new Image(tmp.toURI().toURL().toExternalForm());
		return img;
	}

	/**
	 * Get frame image from remote server, image file is loaded in the cache
	 * folder for reusing.
	 * 
	 * @param video
	 *            video URL
	 * @param serverBaseUrl
	 *            base URL of frame image folder on the remote server
	 * @param second
	 *            frame time in seconds
	 * @param height
	 *            fit height of the frame image
	 * @return Image object
	 * @throws IOException
	 *             if IO error
	 */
	public static Image getFromServer(String video, String serverBaseUrl,
			double second, int height) throws IOException {
		File tmp = new File(Main.TEMP_DIR + video.hashCode() + "_" + second
				+ "_" + height + ".jpg");
		String url = serverBaseUrl + second + "_" + height + ".jpg";
		byte[] data = Request.Get(url).execute().returnContent().asBytes();
		FileUtils.writeByteArrayToFile(tmp, data);
		Image img = new Image(tmp.toURI().toURL().toExternalForm());
		return img;
	}

	/**
	 * Get frame image from video file, image file is loaded in the cache folder
	 * for reusing.
	 * 
	 * @param video
	 *            video URL
	 * @param second
	 *            frame time in seconds
	 * @param height
	 *            fit height of the frame image
	 * @return Image object
	 * @throws IOException
	 *             if IO error
	 * @throws InterruptedException
	 *             if thread interrupted
	 * @throws FFMpegException
	 *             if FFMpeg reports error
	 */
	public static Image getFromVideo(String video, double second, int height)
			throws IOException, InterruptedException, FFMpegException {
		if (!video.startsWith("file:/"))
			throw new IllegalArgumentException(
					"video name must be a valid url started with \"file:/\"!");
		String videoPath = URLDecoder.decode(video.substring(6),"utf-8");
		File dir = new File(Main.TEMP_DIR);
		if (!dir.exists())
			dir.mkdirs();
		File tmp = new File(Main.TEMP_DIR + video.hashCode() + "_" + second
				+ "_" + height + ".jpg");
		if (!tmp.exists()) {
			checkLoadFFmpeg();
			final ProcessBuilder pb = new ProcessBuilder();
			pb.redirectErrorStream(true);

			List<String> cmdList = new ArrayList<>();
			cmdList.add(ffmpeg.getAbsolutePath());
			cmdList.add("-ss");
			cmdList.add(Double.toString(second));
			cmdList.add("-i");
			cmdList.add("\""+videoPath+"\"");
			cmdList.add("-y");
			cmdList.add("-vframes");
			cmdList.add("1");
			cmdList.add("-filter:v");
			cmdList.add("scale=\"-1:" + height + "\"");
			cmdList.add(tmp.getAbsolutePath());

			pb.command(cmdList);
			Process p = pb.start();
			String output = readFFMpegOutput(p.getInputStream());
			if (p.waitFor() != 0) {
				throw new FFMpegException("FFmpeg error:" + output);
			}
		}
		Image img = new Image(tmp.toURI().toURL().toExternalForm());
		return img;
	}

	/**
	 * Check if "ffmpeg.exe" is loaded. If not, load it to the temporary folder
	 * instantly.
	 * 
	 * @throws IOException
	 *             if IO error
	 */
	public synchronized static void checkLoadFFmpeg() throws IOException {
		if (ffmpeg == null || !ffmpeg.exists()) {
			ffmpeg = DataLoader.loadToTemp("ffmpeg.exe");
			ffmpeg.deleteOnExit();
		}
	}

	/**
	 * Read output of FFMpeg
	 * 
	 * @param inputStream
	 *            input stream for reading output of the FFMpeg process
	 * @return all of the output
	 */
	private static String readFFMpegOutput(InputStream inputStream) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		boolean firstLine = true;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream), 500);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				if (line.startsWith("  "))
					continue;

				if (!firstLine)
					sb.append('\n');
				sb.append(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Exception for FFMpeg
	 * 
	 * @author 宇锴
	 */
	public static class FFMpegException extends Exception {
		private static final long serialVersionUID = -6041667451915158117L;

		/**
		 * Default constructor
		 */
		public FFMpegException() {
			super();
		}

		/**
		 * Constructor with message
		 * 
		 * @param message
		 *            the detailed message
		 */
		public FFMpegException(String message) {
			super(message);
		}
	}
}
