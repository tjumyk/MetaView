package org.tjumyk.metaview.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

import org.controlsfx.dialog.Dialogs;
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
import org.tjumyk.metaview.viewmodel.LogicAction;
import org.tjumyk.metaview.viewmodel.LogicUnit;
import org.tjumyk.metaview.viewmodel.PlayerModel;

public class BrowserController extends PanelControllerBase {

	private static final int FRMAE_IMAGE_HEIGHT = 80;
	private static final int FRMAE_TITLE_WIDTH = 100, FRMAE_TITLE_HEIGHT = 36;

	@FXML
	AnchorPane root;

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

	@FXML
	VBox box_controls;

	@FXML
	ImageView img_play, img_pause, img_replay;

	private MetaVideo video;
	private MediaPlayer player;
	private ContextMenu metaBoxContextMenu;
	private Map<Integer, Image> frameImageMap = new HashMap<>();

	private PlayerTimeListener playerTimeListener = new PlayerTimeListener();
	private GroupSelectListener groupSelectListener = new GroupSelectListener();
	private SegmentSelectListener segmentSelectListener = new SegmentSelectListener();
	private NodeMouseEnterListener nodeMouseEnterListener = new NodeMouseEnterListener();
	private NodeMouseExitListener nodeMouseExitListener = new NodeMouseExitListener();

	private PlayerModel model;
	private Map<Object, Node> nodeMap = new HashMap<>();
	private Group showSegmentsGroup;
	/**
	 * 用于操作视频的控件组的淡入/淡出动画
	 */
	private FadeTransition controlTransition;

	private static BrowserController instance;

	public static BrowserController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		initKeyBinding();
		initUI();
		parseParam();
		startLoad();
	}

	@Override
	public void execCommand(String id) {
		try {
			switch (id) {
			case "open":
				if (openNewFile())
					loadFrameImages();
				break;
			case "about":
				Main.openDialog("dialog_about.fxml", null);
				break;

			default:
				System.out.println("[Unknown Command] " + id);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initKeyBinding() {
		root.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			KeyCode keyCode = event.getCode();
			if (keyCode == KeyCode.CONTROL) {
				if (!root.getStyleClass().contains("logic-add"))
					root.getStyleClass().add("logic-add");
				event.consume();
			} else if (keyCode == KeyCode.SHIFT) {
				if (!root.getStyleClass().contains("logic-multiply"))
					root.getStyleClass().add("logic-multiply");
				event.consume();
			} else if (keyCode == KeyCode.ALT) {
				if (!root.getStyleClass().contains("logic-subtract"))
					root.getStyleClass().add("logic-subtract");
				event.consume();
			}
		});
		root.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			KeyCode keyCode = event.getCode();
			if (keyCode == KeyCode.CONTROL) {
				root.getStyleClass().remove("logic-add");
				event.consume();
			} else if (keyCode == KeyCode.SHIFT) {
				root.getStyleClass().remove("logic-multiply");
				event.consume();
			} else if (keyCode == KeyCode.ALT) {
				root.getStyleClass().remove("logic-subtract");
				event.consume();
			}
		});
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
						player.seek(Duration.seconds(1.0
								* (newValue.getFrom() - 1) / video.getFps()));
						player.play();
					}
				});
		player.statusProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (newValue == Status.PLAYING) {
						img_play.setVisible(false);
						img_pause.setVisible(true);
					} else {
						img_play.setVisible(true);
						img_pause.setVisible(false);
					}
				});
	}

	private void initUI() {
		accordion_category_list.expandedPaneProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (newValue == null)
						showWebViewInfo(null);
					else {
						Category cat = (Category) newValue.getUserData();
						showWebViewInfo(cat.getInfo());
					}

				});
		ChangeListener<Number> stack_size_listener = (observable, oldValue,
				newValue) -> {
			media_view.setFitWidth(stack_media_view.getWidth());
			media_view.setFitHeight(stack_media_view.getHeight());
		};
		stack_media_view.widthProperty().addListener(stack_size_listener);
		stack_media_view.heightProperty().addListener(stack_size_listener);

		media_view.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.getClickCount() == 2) {
				toggleVideoFullScreen();
			}
		});
		metaBoxContextMenu = buildContextMenu();

		webview_info.getEngine().setUserStyleSheetLocation(
				Main.class.getResource("css/webview.css").toExternalForm());

		Platform.runLater(() -> {
			root.getScene().getWindow().focusedProperty()
					.addListener(new ChangeListener<Boolean>() {
						@Override
						public void changed(
								ObservableValue<? extends Boolean> observable,
								Boolean oldValue, Boolean newValue) {
							if (newValue == false)
								root.getStyleClass().removeAll("logic-add",
										"logic-multiply", "logic-subtract");
						}
					});
		});
	}

	private void showWebViewInfo(String info) {
		if (info == null)
			webview_info.getEngine().loadContent("");
		else if (info.startsWith("http://") || info.startsWith("https://"))
			webview_info.getEngine().load(info);
		else
			webview_info.getEngine().loadContent(info);
	}

	private ContextMenu buildContextMenu() {
		MenuItem itemSelectPlay = new MenuItem(
				Main.getString("metabox.select_and_play"), new ImageView(
						Main.getImage("play.png", 24, 24)));
		MenuItem itemAdd = new MenuItem(Main.getString("logic.add"),
				new ImageView(Main.getImage("operation-add.png", 24, 24)));
		MenuItem itemMultiply = new MenuItem(Main.getString("logic.multiply"),
				new ImageView(Main.getImage("operation-multiply.png", 24, 24)));
		MenuItem itemSubtract = new MenuItem(Main.getString("logic.subtract"),
				new ImageView(Main.getImage("operation-subtract.png", 24, 24)));
		ContextMenu cm = new ContextMenu(itemSelectPlay, itemAdd, itemMultiply,
				itemSubtract);

		itemSelectPlay.setOnAction(e -> {
			Object obj = model.getActiveNode().getValue();
			if (obj instanceof Group)
				selectAndPlay((Group) obj);
			else if (obj instanceof Segment)
				selectAndPlay((Segment) obj);
		});
		itemAdd.setOnAction(e -> {
			model.applyLogic(new LogicUnit(model.getActiveNode().getValue(),
					LogicAction.ADD));
		});
		itemMultiply.setOnAction(e -> {
			model.applyLogic(new LogicUnit(model.getActiveNode().getValue(),
					LogicAction.MULTIPLY));
		});
		itemSubtract.setOnAction(e -> {
			model.applyLogic(new LogicUnit(model.getActiveNode().getValue(),
					LogicAction.SUBTRACT));
		});

		return cm;
	}

	private void toggleVideoFullScreen() {
		// TODO show video in full screen
	}

	private void startLoad() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (video == null) {
					if (openNewFile())
						loadFrameImages();
				} else
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

	private boolean openNewFile() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(Main.getString("filechooser.choose_metavideo"));
		chooser.getExtensionFilters().add(
				new ExtensionFilter("MetaVideo Description File", "*.mvd"));
		chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = chooser.showOpenDialog(Main.getStage());

		if (file != null && file.exists()) {
			try {
				video = MetaVideoParser.parse(file);
				return true;
			} catch (Exception e) {
				Dialogs.create().title(Main.getString("error.title"))
						.masthead(Main.getString("error.parse_error"))
						.showException(e);
				e.printStackTrace();
			}
		}
		return false;
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
		model = new PlayerModel(video);
		accordion_category_list.getPanes().clear();
		flow_shot_list.getChildren().clear();
		nodeMap.clear();
		if (video == null)
			return;

		for (Segment seg : video.getSegments()) {
			VBox box = new VBox(5);
			box.getStyleClass().addAll("box", "segment");
			box.setPadding(new Insets(3));
			box.setAlignment(Pos.CENTER);
			ImageView img = new ImageView(frameImageMap.get(seg.getKey()));
			img.setFitHeight(FRMAE_IMAGE_HEIGHT * 0.7);
			img.setPreserveRatio(true);
			Label label = new Label("Segment " + seg.getIndex());
			label.getStyleClass().add("name");
			label.setWrapText(false);
			label.setContextMenu(metaBoxContextMenu);
			StackPane operation = new StackPane();
			operation.getStyleClass().add("operation");
			StackPane wrapper = new StackPane(img, operation);

			box.getChildren().addAll(wrapper, label);
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
				box.getStyleClass().addAll("box", "group");
				box.setPadding(new Insets(3));
				box.setAlignment(Pos.CENTER);
				Tooltip.install(box, new Tooltip(group.getName()));
				ImageView img = new ImageView(frameImageMap.get(group.getKey()));
				img.setPreserveRatio(true);
				Label label = new Label(group.getName());
				label.getStyleClass().add("name");
				label.setWrapText(true);
				label.setPrefSize(FRMAE_TITLE_WIDTH, FRMAE_TITLE_HEIGHT);
				label.setContextMenu(metaBoxContextMenu);
				StackPane operation = new StackPane();
				operation.getStyleClass().add("operation");
				StackPane wrapper = new StackPane(img, operation);

				box.getChildren().addAll(wrapper, label);
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
		Media media = new Media(video.getMovieFile());
		player = new MediaPlayer(media);
		player.currentTimeProperty().addListener(playerTimeListener);
		model.getCurrentTime().bind(player.currentTimeProperty());
		media_view.setMediaPlayer(player);

		initPlayer();

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

			int index = segmentPlayList.indexOf(playingSegment.getValue());
			if (index == -1) {
				if (segmentPlayList.size() > 0)
					playingSegment.setValue(segmentPlayList.get(0));
				else
					playingSegment.setValue(null);
			} else if (newValue.toSeconds() >= 1.0
					* (playingSegment.getValue().getTo() - 1) / video.getFps()) {
				if (index < segmentPlayList.size() - 1)
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
		showWebViewInfo(group.getInfo());
		flow_shot_list.getChildren().clear();
		for (Segment seg : group.getSegments()) {
			flow_shot_list.getChildren().add(nodeMap.get(seg));
		}
	}

	@FXML
	private void onPlay() {
		Segment playingSegment = model.getPlayingSegment().getValue();
		if (playingSegment == null) {
			if (model.getSegmentPlayList().size() <= 0)
				return;
			model.getPlayingSegment().setValue(
					model.getSegmentPlayList().get(0));
		} else
			player.play();
	}

	@FXML
	private void onPause() {
		player.pause();
	}

	@FXML
	private void onReplay() {
		if (model.getSegmentPlayList().size() <= 0) {
			return;
		}
		model.getPlayingSegment().setValue(null);
		model.getPlayingSegment().setValue(model.getSegmentPlayList().get(0));
	}

	@FXML
	private void onMouseEnterVideo(MouseEvent event) {
		if (controlTransition != null)
			controlTransition.stop();
		controlTransition = new FadeTransition(Duration.seconds(1.0),
				box_controls);
		controlTransition.setToValue(1);
		controlTransition.play();
	}

	@FXML
	private void onMouseExitVideo(MouseEvent event) {
		if (controlTransition != null)
			controlTransition.stop();
		controlTransition = new FadeTransition(Duration.seconds(1.0),
				box_controls);
		controlTransition.setToValue(0);
		controlTransition.play();
	}

	private void selectAndPlay(Segment seg) {
		model.getSegmentPlayList().clear();
		model.getPlayingSegment().setValue(null);
		model.getSegmentPlayList().add(seg);
		model.getPlayingSegment().setValue(seg);
	}

	private void selectAndPlay(Group group) {
		model.getSegmentPlayList().clear();
		model.getPlayingSegment().setValue(null);
		List<Segment> segList = group.getSegments();
		if (segList.size() > 0) {
			model.getSegmentPlayList().addAll(segList);
			model.getPlayingSegment().setValue(segList.get(0));
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
			int clickCount = event.getClickCount();

			if (clickCount == 1) {
				if (root.getStyleClass().contains("logic-add")) {
					model.applyLogic(new LogicUnit(group, LogicAction.ADD));
				} else if (root.getStyleClass().contains("logic-multiply")) {
					model.applyLogic(new LogicUnit(group, LogicAction.MULTIPLY));
				} else if (root.getStyleClass().contains("logic-subtract")) {
					model.applyLogic(new LogicUnit(group, LogicAction.SUBTRACT));
				} else
					model.getActiveNode().setValue(group);

				if (event.getButton() == MouseButton.SECONDARY) {
					metaBoxContextMenu.show(source, Side.RIGHT, 0, 0);
				}
			} else if (clickCount == 2) {
				selectAndPlay(group);
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

			if (count == 1) {
				if (root.getStyleClass().contains("logic-add")) {
					model.applyLogic(new LogicUnit(seg, LogicAction.ADD));
				} else if (root.getStyleClass().contains("logic-multiply")) {
					model.applyLogic(new LogicUnit(seg, LogicAction.MULTIPLY));
				} else if (root.getStyleClass().contains("logic-subtract")) {
					model.applyLogic(new LogicUnit(seg, LogicAction.SUBTRACT));
				} else
					model.getActiveNode().setValue(seg);

				if (event.getButton() == MouseButton.SECONDARY) {
					metaBoxContextMenu.show(source, Side.RIGHT, 0, 0);
				}
			} else {
				if (count == 2) {
					selectAndPlay(seg);
				}
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
			updateMessage(Main.getString("media.loading_frame_images_cache"));
			int cachedCount = 0;
			for (Segment segment : video.getSegments()) {
				int key = segment.getKey();
				double second = 1.0 * segment.getKey() / video.getFps();
				Image img = VideoFrameCapture.getCache(video.getMovieFile(),
						second, FRMAE_IMAGE_HEIGHT);
				if (img != null) {
					frameImageMap.put(key, img);
					Thread.sleep(20);
					cachedCount++;
				}
			}
			if (cachedCount == size) {
				return null;
			}

			if (!video.getMovieFile().startsWith("file:/")) {
				updateMessage(Main
						.getString("media.loading_frame_images_from_server"));
				int count = 0;
				for (Segment segment : video.getSegments()) {
					int key = segment.getKey();
					if (frameImageMap.containsKey(key))
						continue;
					double second = 1.0 * segment.getKey() / video.getFps();
					Image img = VideoFrameCapture.getFromServer(
							video.getMovieFile(), video.getFrameImageFolder(),
							second, FRMAE_IMAGE_HEIGHT);
					if (img != null) {
						frameImageMap.put(key, img);
					}
					count++;
					updateProgress(count, size);
				}
			} else {
				updateMessage(Main.getString("media.loading_from_local_folder"));
				String folder = video.getFrameImageFolder();
				if (folder != null && folder.length() > 0) {
					int loaded = 0;
					for (Segment segment : video.getSegments()) {
						int key = segment.getKey();
						if (frameImageMap.containsKey(key)) {
							loaded++;
							continue;
						}
						double second = 1.0 * segment.getKey() / video.getFps();
						Image img = VideoFrameCapture.getFromFolder(
								video.getMovieFile(), folder, second,
								FRMAE_IMAGE_HEIGHT);
						if (img != null) {
							frameImageMap.put(key, img);
							Thread.sleep(20);
							loaded++;
						}
					}
					if (loaded == size) {
						return null;
					}
				}

				updateMessage(Main.getString("media.loading_ffmpeg"));
				VideoFrameCapture.checkLoadFFmpeg();
				updateProgress(0, size);
				updateMessage(Main.getString("media.loading_frame_images"));
				for (Segment seg : video.getSegments()) {
					double second = 1.0 * seg.getKey() / video.getFps();
					pool.submit(new SubTaskRunnable(second, seg, pool));
				}
				pool.shutdown();

				try {
					boolean terminated = pool.awaitTermination(video
							.getSegments().size(), TimeUnit.MINUTES);
					if (!terminated)
						throw new RuntimeException(
								"ThreadPool timeout, work may be incomplete.");
					if (exception != null)
						throw exception;
				} catch (InterruptedException e) {
					pool.shutdownNow();
				}
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
					int key = seg.getKey();
					if (!frameImageMap.containsKey(key)) {
						Image image = VideoFrameCapture.capture(
								video.getMovieFile(), second,
								FRMAE_IMAGE_HEIGHT);
						frameImageMap.put(key, image);
					}
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
