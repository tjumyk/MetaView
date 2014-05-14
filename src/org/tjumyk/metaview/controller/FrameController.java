package org.tjumyk.metaview.controller;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.tjumyk.metaview.Main;

public class FrameController implements Initializable {

	@FXML
	Pane root, container;

	@FXML
	Pane nav_list;

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
					Rectangle bounds = GraphicsEnvironment
							.getLocalGraphicsEnvironment()
							.getMaximumWindowBounds();
					stage.setX(-10);
					stage.setY(-10);
					stage.setWidth(bounds.getWidth() + 20);
					stage.setHeight(bounds.getHeight() + 20);
				} else {
					stage.setWidth(Main.STAGE_WIDTH);
					stage.setHeight(Main.STAGE_HEIGHT);
					if (stage.getX() == -10 && stage.getY() == -10)
						stage.centerOnScreen();
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
	}
}