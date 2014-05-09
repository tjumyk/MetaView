package org.tjumyk.metaview.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

import org.tjumyk.metaview.Main;
import org.tjumyk.metaview.controll.BlockSequencePane;
import org.tjumyk.metaview.controll.MetaRelationPane;
import org.tjumyk.metaview.media.VideoFrameCapture;
import org.tjumyk.metaview.model.Category;
import org.tjumyk.metaview.model.Group;
import org.tjumyk.metaview.model.MetaVideo;
import org.tjumyk.metaview.model.MetaVideoParser;
import org.tjumyk.metaview.model.Segment;
import org.tjumyk.metaview.util.NodeStyleUtil;
import org.tjumyk.metaview.viewmodel.PlayerModel;

public class BrowserController implements Initializable {

	private static final int FRMAE_IMAGE_HEIGHT = 80;
	private static final int FRMAE_TITLE_WIDTH = 100, FRMAE_TITLE_HEIGHT = 36;

	@FXML
	Accordion accordion_category_list;

	@FXML
	FlowPane flow_shot_list;

	@FXML
	WebView webview_info;

	@FXML
	MediaView media_view;

	@FXML
	StackPane stack_media_view;

	@FXML
	ScrollPane pane_relation, pane_block, pane_zoom_in;

	private MetaVideo video;
	private MediaPlayer player;
	private Map<Integer, Image> frameImageMap = new HashMap<>();

	private PlayerTimeListener playerTimeListener = new PlayerTimeListener();
	private GroupSelectListener groupSelectListener = new GroupSelectListener();
	private SegmentSelectListener segmentSelectListener = new SegmentSelectListener();
	private NodeMouseEnterListener nodeMouseEnterListener = new NodeMouseEnterListener();
	private NodeMouseExitListener nodeMouseExitListener = new NodeMouseExitListener();

	private PlayerModel model = new PlayerModel();
	private Map<Object, Node> nodeMap = new HashMap<>();
	private Group showSegmentsGroup;

	private static BrowserController instance;

	public static BrowserController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		initPlayer();
		initUI();
		parseParam();
		startLoad();
	}

	private void initPlayer() {
		model.getSeekTime().addListener((b, o, n) -> {
			if (player == null) {
				return;
			}
			if (n != null) {
				player.seek(n);
				player.play();
			}
		});
		model.getPlayingSegment().addListener(
				(observable, oldValue, newValue) -> {
					if (player == null) {
						return;
					}
					if (newValue == null) {
						player.pause();
					} else {
						player.seek(Duration.seconds(1.0 * (newValue.getFrom()-1)
								/ video.getFps()));
						player.play();
					}
				});
	}

	private void initUI() {
		accordion_category_list.expandedPaneProperty().addListener(
				new ChangeListener<TitledPane>() {
					@Override
					public void changed(
							ObservableValue<? extends TitledPane> observable,
							TitledPane oldValue, TitledPane newValue) {
						if (newValue == null)
							webview_info.getEngine().loadContent("");
						else {
							Category cat = (Category) newValue.getUserData();
							webview_info.getEngine().loadContent(cat.getInfo());
						}

					}
				});
		ChangeListener<Number> stack_size_listener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				media_view.setFitWidth(stack_media_view.getWidth());
				media_view.setFitHeight(stack_media_view.getHeight());
			}
		};
		stack_media_view.widthProperty().addListener(stack_size_listener);
		stack_media_view.heightProperty().addListener(stack_size_listener);

		media_view.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.getClickCount() == 2) {
				toggleVideoFullScreen();
			}
		});
	}

	private void toggleVideoFullScreen() {
		// TODO show video in full screen
	}

	private void startLoad() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (video == null) {
					openNewFile();
				}
				loadFrameImages();
			}
		});
	}

	private void parseParam() {
		List<String> params = Main.getParams().getUnnamed();
		if (params.size() > 0) {
			String param = params.get(0);
			if (param.length() > 0) {
				try {
					video = MetaVideoParser.parse(new File(param));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void openNewFile() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(Main.RESOURCE_BUNDLE
				.getString("filechooser.choose_metavideo"));
		chooser.getExtensionFilters().add(
				new ExtensionFilter("MetaVideo Description File", "*.mvd"));
		chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = chooser.showOpenDialog(Main.getStage());
		try {
			if (file != null && file.exists())
				video = MetaVideoParser.parse(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadFrameImages() {
		frameImageMap.clear();
		if (video == null)
			return;

		Task<Void> loadTask = new LoadFrameImageTask();
		loadTask.setOnSucceeded((e) -> {
			loadVideoUI();
		});
		try {
			Main.openDialog("dialog_load.fxml", loadTask);
			new Thread(loadTask).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadVideoUI() {
		accordion_category_list.getPanes().clear();
		flow_shot_list.getChildren().clear();
		nodeMap.clear();
		if (video == null)
			return;

		for (Segment seg : video.getSegments()) {
			VBox box = new VBox(5);
			box.getStyleClass().add("box");
			box.setPadding(new Insets(3));
			box.setAlignment(Pos.CENTER);
			ImageView img = new ImageView(frameImageMap.get(seg.getKey()));
			img.setFitHeight(FRMAE_IMAGE_HEIGHT * 0.7);
			img.setPreserveRatio(true);
			Label label = new Label("Segment " + seg.getIndex());
			label.setWrapText(false);
			box.getChildren().addAll(img, label);
			box.setUserData(seg);
			box.setOnMouseClicked(segmentSelectListener);
			box.setOnMouseEntered(nodeMouseEnterListener);
			box.setOnMouseExited(nodeMouseExitListener);
			nodeMap.put(seg, box);
		}

		boolean firstPane = true;
		for (Category cat : video.getCategories()) {
			FlowPane flow = new FlowPane(2, 2);
			flow.getStyleClass().add("category_list");
			for (Group group : cat.getGroups()) {
				VBox box = new VBox(5);
				box.getStyleClass().add("box");
				box.setPadding(new Insets(3));
				box.setAlignment(Pos.CENTER);
				ImageView img = new ImageView(frameImageMap.get(group.getKey()));
				img.setPreserveRatio(true);
				Label label = new Label(group.getName());
				label.setWrapText(true);
				label.setPrefSize(FRMAE_TITLE_WIDTH, FRMAE_TITLE_HEIGHT);
				box.getChildren().addAll(img, label);
				box.setUserData(group);
				box.setOnMouseClicked(groupSelectListener);
				box.setOnMouseEntered(nodeMouseEnterListener);
				box.setOnMouseExited(nodeMouseExitListener);
				nodeMap.put(group, box);
				flow.getChildren().add(box);

			}
			ScrollPane scroll = new ScrollPane(flow);
			scroll.setFitToWidth(true);
			scroll.setPadding(new Insets(10));
			TitledPane pane = new TitledPane(cat.getName(), scroll);
			pane.setUserData(cat);
			accordion_category_list.getPanes().add(pane);
			if (firstPane) {
				accordion_category_list.setExpandedPane(pane);
			}
			firstPane = false;
		}

		if (player != null) {
			player.stop();
		}
		File file = new File(video.getMovieFile());
		try {
			Media media = new Media(file.toURI().toURL().toExternalForm());
			player = new MediaPlayer(media);
			player.currentTimeProperty().addListener(playerTimeListener);
			model.getCurrentTime().bind(player.currentTimeProperty());
			media_view.setMediaPlayer(player);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		model.setVideo(video);
		NodeStyleUtil.bindStyle(model, nodeMap);
		model.getActiveNode().addListener(
				(b, o, n) -> {
					if (n == null)
						return;
					if (n instanceof Group) {
						boolean find = false;
						int catIndex = 0;
						outer: for (Category cat : video.getCategories()) {
							for (Group group : cat.getGroups()) {
								if (n == group) {
									find = true;
									break outer;
								}
							}
							catIndex++;
						}
						if (!find)
							return;
						accordion_category_list
								.setExpandedPane(accordion_category_list
										.getPanes().get(catIndex));
						showGroupSegments((Group) n);
					}
				});

		MetaRelationPane relPane = new MetaRelationPane(model);
		pane_relation.setContent(relPane);
		BlockSequencePane blockPane = new BlockSequencePane(model);
		pane_block.setContent(blockPane);
	}

	private class PlayerTimeListener implements ChangeListener<Duration> {
		@Override
		public void changed(ObservableValue<? extends Duration> observable,
				Duration oldValue, Duration newValue) {
			Property<Segment> playingSegment = model.getPlayingSegment();
			ObservableList<Segment> segmentPlayList = model
					.getSegmentPlayList();

			if (playingSegment.getValue() == null)
				return;

			if (newValue.toSeconds() >= 1.0 * playingSegment.getValue().getTo()
					/ video.getFps()) {
				int index = segmentPlayList.indexOf(playingSegment.getValue());
				if (index >= 0 && index < segmentPlayList.size() - 1)
					playingSegment.setValue(segmentPlayList.get(index + 1));
				else
					playingSegment.setValue(null);
			}
		}
	}

	private void showGroupSegments(Group group) {
		if (group == showSegmentsGroup)
			return;
		showSegmentsGroup = group;
		webview_info.getEngine().loadContent(group.getInfo());
		flow_shot_list.getChildren().clear();
		for (Segment seg : group.getSegments()) {
			flow_shot_list.getChildren().add(nodeMap.get(seg));
		}
	}

	private class NodeMouseEnterListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			Node node = (Node) event.getSource();
			model.getHoverNode().setValue(node.getUserData());
		}
	}

	private class NodeMouseExitListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			Node node = (Node) event.getSource();
			if (model.getHoverNode().getValue() == node.getUserData())
				model.getHoverNode().setValue(null);
		}
	}

	private class GroupSelectListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			Pane source = (Pane) event.getSource();
			Group group = (Group) source.getUserData();
			model.getActiveNode().setValue(group);
			if (event.getClickCount() == 2) {
				model.getSegmentPlayList().clear();
				model.getPlayingSegment().setValue(null);
				List<Segment> segList = group.getSegments();
				if (segList.size() > 0) {
					model.getSegmentPlayList().addAll(segList);
					model.getPlayingSegment().setValue(segList.get(0));
				}
			}

			showGroupSegments(group);
		}
	}

	private class SegmentSelectListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			Pane source = (Pane) event.getSource();
			Segment seg = (Segment) source.getUserData();
			int count = event.getClickCount();

			model.getActiveNode().setValue(seg);
			if (count == 2) {
				model.getSegmentPlayList().clear();
				model.getPlayingSegment().setValue(null);
				model.getSegmentPlayList().add(seg);
				model.getPlayingSegment().setValue(seg);
			}
		}
	}

	private class LoadFrameImageTask extends Task<Void> {

		Exception exception;

		@Override
		protected Void call() throws Exception {
			ExecutorService pool = Executors.newFixedThreadPool(Runtime
					.getRuntime().availableProcessors());
			int size = video.getSegments().size();
			updateProgress(-1, size);
			updateMessage(Main.RESOURCE_BUNDLE
					.getString("browse.loading_ffmpeg"));
			VideoFrameCapture.checkLoadFFmpeg();
			updateProgress(0, size);
			updateMessage(Main.RESOURCE_BUNDLE
					.getString("browse.loading_frame_images"));
			for (Segment seg : video.getSegments()) {
				double second = 1.0 * seg.getKey() / video.getFps();
				pool.submit(new SubTaskRunnable(second, seg, pool));
			}
			pool.shutdown();

			try {
				boolean terminated = pool.awaitTermination(video.getSegments()
						.size(), TimeUnit.MINUTES);
				if (!terminated)
					throw new RuntimeException(
							"ThreadPool timeout, work may be incomplete.");
				if (exception != null)
					throw exception;
			} catch (InterruptedException e) {
				pool.shutdownNow();
			}
			return null;
		}

		private synchronized void updateLoadProgress() {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					updateProgress(getWorkDone() + 1, getTotalWork());
				}
			});
		}

		private class SubTaskRunnable implements Runnable {
			double second;
			Segment seg;
			ExecutorService pool;

			public SubTaskRunnable(double second, Segment seg,
					ExecutorService pool) {
				super();
				this.second = second;
				this.seg = seg;
				this.pool = pool;
			}

			@Override
			public void run() {
				try {
					Image image = VideoFrameCapture.capture(
							video.getMovieFile(), second, FRMAE_IMAGE_HEIGHT);
					frameImageMap.put(seg.getKey(), image);
					updateLoadProgress();
				} catch (InterruptedException e) {
					// don't care
				} catch (Exception e) {
					exception = e;
					pool.shutdownNow();
				}
			}

		}
	}
}
