package org.tjumyk.metaview;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.tjumyk.metaview.controller.DialogControllerBase;
import org.tjumyk.metaview.util.DragUtil;

public class Main extends Application {
	private static Stage stage;
	private static Parameters param;

	public static final int STAGE_WIDTH = 1280, STAGE_HEIGHT = 700;
	public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle("metaview");

	public static ResourceBundle getResources() {
		return RESOURCE_BUNDLE;
	}

	public static Image getImage(String file, double width, double height) {
		return new Image(Main.class.getResourceAsStream("images/" + file),
				width, height, true, true);
	}

	public static String getString(String key) {
		if (RESOURCE_BUNDLE.containsKey(key))
			return RESOURCE_BUNDLE.getString(key);
		return "%" + key;
	}

	public static Stage getStage() {
		return stage;
	}

	public static Parameters getParams() {
		return param;
	}

	@Override
	public void init() throws Exception {
		super.init();
		param = this.getParameters();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Platform.setImplicitExit(false);

		stage = primaryStage;
		Parent root = FXMLLoader
				.load(this.getClass().getResource("fxml/frame.fxml"),
						RESOURCE_BUNDLE);
		Scene scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT,
				Color.TRANSPARENT);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(scene);
		DragUtil.setDraggable(stage);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				event.consume();
				quit();
			}
		});

		stage.show();
	}

	public static void openDialog(String fxmlFileName, Object data)
			throws IOException {
		Stage dialogStage = new Stage(StageStyle.TRANSPARENT);
		dialogStage.initOwner(Main.getStage());
		dialogStage.initModality(Modality.WINDOW_MODAL);

		FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/"
				+ fxmlFileName), RESOURCE_BUNDLE);
		Pane root = (Pane) loader.load();
		dialogStage.setScene(new Scene(root, Color.TRANSPARENT));
		DragUtil.setDraggable(dialogStage);

		loader.<DialogControllerBase> getController().initData(data);
		dialogStage.show();
	}

	public static void quit() {
		Platform.exit();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
