package org.tjumyk.metaview.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;
import org.tjumyk.metaview.Main;
import org.tjumyk.metaview.data.DataLoader;

public class VideoFrameCapture {
	private static File ffmpeg;

	public static Image getCache(String video, double second, int height)
			throws IOException {
		File tmp = new File(Main.TEMP_DIR + video.hashCode() + "_" + second
				+ "_" + height + ".jpg");
		Image img = null;
		if (tmp.exists())
			img = new Image(tmp.toURI().toURL().toExternalForm());
		return img;
	}

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

	public static Image capture(String video, double second, int height)
			throws IOException, InterruptedException,
			VideoFrameCaptureException {
		if (!video.startsWith("file:/"))
			throw new IllegalArgumentException(
					"video name must be a valid url started with \"file:/\"!");
		String videoPath = video.substring(6);
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
			cmdList.add(videoPath);
			cmdList.add("-y");
			cmdList.add("-vframes");
			cmdList.add("1");
			cmdList.add("-filter:v");
			cmdList.add("scale=\"-1:" + height + "\"");
			cmdList.add(tmp.getAbsolutePath());

			pb.command(cmdList);
			Process p = pb.start();
			String output = read(p.getInputStream());
			if (p.waitFor() != 0) {
				throw new VideoFrameCaptureException("FFmpeg error:" + output);
			}
		}
		Image img = new Image(tmp.toURI().toURL().toExternalForm());
		return img;
	}

	public synchronized static void checkLoadFFmpeg() throws IOException {
		if (ffmpeg == null || !ffmpeg.exists()) {
			ffmpeg = DataLoader.loadToTemp("ffmpeg.exe");
			ffmpeg.deleteOnExit();
		}
	}

	private static String read(InputStream is) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		boolean firstLine = true;
		try {
			br = new BufferedReader(new InputStreamReader(is), 500);

			String line = "";
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
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
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
			}
		}
		return sb.toString();
	}

	public static class VideoFrameCaptureException extends Exception {
		private static final long serialVersionUID = -6041667451915158117L;

		public VideoFrameCaptureException() {
			super();
		}

		public VideoFrameCaptureException(String message) {
			super(message);
		}
	}
}
