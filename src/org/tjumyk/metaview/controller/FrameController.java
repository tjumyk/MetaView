package org.tjumyk.metaview.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.tjumyk.metaview.Main;
import org.tjumyk.metaview.util.ResizeUtil;

public class FrameController implements Initializable {

	@FXML
	Pane root, container, root_wrapper;

	@FXML
	Pane nav_list;

	@FXML
	Pane resize_handler;

	private PanelControllerBase panelController;

	@FXML
	void onWindowMinimize(MouseEvent event) {
		Main.getStage().setIconified(true);
	}

	@FXML
	void onWindowMaxOrRestore(MouseEvent event) {
		toggleWindowMaxOrRestore();
	}

	@FXML
	void onWindowClose(MouseEvent event) {
		Main.quit();
	}

	@FXML
	void onDisableDrag(MouseEvent event) {
		event.consume();
	}

	@FXML
	void onSelectNav(MouseEvent event) throws IOException {
		Node source = (Node) event.getSource();
		selectNav(source);
		event.consume();
	}

	@FXML
	void onDoubleClickNavBar(MouseEvent event) {
		if (event.getClickCount() == 2) {
			toggleWindowMaxOrRestore();
		}
	}

	private void toggleWindowMaxOrRestore() {
		ObservableList<String> classes = root.getStyleClass();
		if (classes.contains("max")) {
			classes.remove("max");
		} else {
			classes.add("max");
		}
	}

	private void selectNav(Node sourceNode) throws IOException {
		String sourceID = sourceNode.getId();
		panelController.execCommand(sourceID.substring(4));
	}

	@Override
	public void initialize(URL url, ResourceBundle res) {
		root.getStyleClass().addListener(new ListChangeListener<String>() {
			@Override
			public void onChanged(Change<? extends String> c) {
				Stage stage = Main.getStage();
				if (c.getList().contains("max")) {
					Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
					stage.setX(0);
					stage.setY(0);
					stage.setWidth(bounds.getWidth());
					stage.setHeight(bounds.getHeight());
					AnchorPane.setLeftAnchor(root_wrapper, 0.0);
					AnchorPane.setRightAnchor(root_wrapper, 0.0);
					AnchorPane.setTopAnchor(root_wrapper, 0.0);
					AnchorPane.setBottomAnchor(root_wrapper, 0.0);
					resize_handler.setDisable(true);
				} else {
					stage.setWidth(Main.STAGE_WIDTH);
					stage.setHeight(Main.STAGE_HEIGHT);
					AnchorPane.setLeftAnchor(root_wrapper, 10.0);
					AnchorPane.setRightAnchor(root_wrapper, 10.0);
					AnchorPane.setTopAnchor(root_wrapper, 10.0);
					AnchorPane.setBottomAnchor(root_wrapper, 10.0);
					if (stage.getX() == 0 && stage.getY() == 0)
						stage.centerOnScreen();
					resize_handler.setDisable(false);
				}
			}
		});

		try {
			container.getChildren().clear();
			FXMLLoader loader = new FXMLLoader(
					Main.class.getResource("fxml/panel_browser.fxml"),
					Main.getResources());
			container.getChildren().add(loader.load());
			panelController = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Platform.runLater(() -> {
			ResizeUtil.setResizeHandler(root.getScene().getWindow(),
					resize_handler);
		});
	}
}