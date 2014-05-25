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

/**
 * Main class providing entry point of this program.
 * 
 * @author 宇锴
 */
public class Main extends Application {
	/**
	 * Stage instance
	 */
	private static Stage stage;

	/**
	 * program parameters
	 */
	private static Parameters param;

	/**
	 * Default stage width
	 */
	public static final int STAGE_WIDTH = 1280;
	/**
	 * Default stage height
	 */
	public static final int STAGE_HEIGHT = 700;

	/**
	 * Temporary folder
	 */
	public static String TEMP_DIR = System.getProperty("java.io.tmpdir")
			+ "metaview/";

	/**
	 * Resource bundle for localization. This is loaded dynamically depends on
	 * the locale of the running OS.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(Main.class.getPackage().getName() + ".i18n.metaview");

	/**
	 * Get the stage instance
	 * 
	 * @return stage instance
	 */
	public static Stage getStage() {
		return stage;
	}

	/**
	 * Get the parameters
	 * 
	 * @return parameters
	 */
	public static Parameters getParams() {
		return param;
	}

	/**
	 * Get the resource bundle
	 * 
	 * @return resource bundle instance
	 */
	public static ResourceBundle getResources() {
		return RESOURCE_BUNDLE;
	}

	/**
	 * Get localized string from resource bundle.
	 * 
	 * @param key
	 *            string key
	 * @return localized string
	 */
	public static String getString(String key) {
		if (RESOURCE_BUNDLE.containsKey(key))
			return RESOURCE_BUNDLE.getString(key);
		return "%" + key;
	}

	/**
	 * Get image with given file path and image size.
	 * 
	 * @param imageFile
	 *            file path relative to the {@link org.tjumyk.metaview.images}
	 *            package
	 * @param width
	 *            requested image width
	 * @param height
	 *            requested image height
	 * @return the Image object
	 */
	public static Image getImage(String imageFile, double width, double height) {
		return new Image(Main.class.getResourceAsStream("images/" + imageFile),
				width, height, true, true);
	}

	/**
	 * Open modal dialog with given FXML and data argument to pass.<br>
	 * <br>
	 * 
	 * <b>Note:</b> The FXML for modal dialog must has a controller inheriting
	 * from {@link DialogControllerBase} so as to pass the data argument.
	 * 
	 * @param fxmlFile
	 *            FXML file path relative to the
	 *            {@link org.tjumyk.metaview.fxml} package
	 * @param data
	 *            data argument to pass into the controller of the modal dialog.
	 *            Data is passed by
	 *            {@link DialogControllerBase#initData(Object)} after the dialog
	 *            is loaded and before it shows up.
	 * @throws IOException
	 *             if parse error
	 */
	public static void openDialog(String fxmlFile, Object data)
			throws IOException {
		Stage dialogStage = new Stage(StageStyle.TRANSPARENT);
		dialogStage.initOwner(Main.getStage());
		dialogStage.initModality(Modality.WINDOW_MODAL);

		FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/"
				+ fxmlFile), RESOURCE_BUNDLE);
		Pane root = (Pane) loader.load();
		dialogStage.setScene(new Scene(root, Color.TRANSPARENT));
		DragUtil.setDraggable(dialogStage);

		loader.<DialogControllerBase> getController().initData(data);
		dialogStage.show();
	}

	/**
	 * Quit application
	 */
	public static void quit() {
		Platform.exit();
	}

	/**
	 * Initialize application, invoked by JavaFx runtime after instantiation,
	 * before {@link #start(Stage)}.
	 */
	@Override
	public void init() throws Exception {
		super.init();
		param = this.getParameters();
	}

	/**
	 * Main entry point for JavaFx application, invoked by JavaFx runtime.
	 * 
	 * @param primaryStage
	 *            primary stage of the application
	 */
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
		stage.setTitle(getString("app.title") + " " + getString("app.version"));
		setIcons(stage);

		stage.show();
	}

	/**
	 * Set the icons of the stage
	 * 
	 * @param stage
	 *            target stage
	 */
	private void setIcons(Stage stage) {
		String[] icons = { "logo_16.png", "logo_32.png", "logo_48.png",
				"logo_128.png", "logo_256.png" };
		for (String icon : icons) {
			stage.getIcons()
					.addAll(new Image(Main.class.getResourceAsStream("images/"
							+ icon)));
		}
	}

	/**
	 * Classic program entry for java, but not called in jar for JavaFx.
	 * 
	 * @param args
	 *            program arguments from command line
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
