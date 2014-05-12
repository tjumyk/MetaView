package org.tjumyk.metaview.controller;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.tjumyk.metaview.Main;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class FrameController implements Initializable {
	private Map<String, Pane> panels = new HashMap<String, Pane>();

	@FXML
	Pane root, container;

	@FXML
	Pane nav_list;

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
		String fxmlID = sourceID.replace("nav_", "panel_");
		Pane panel = panels.get(fxmlID);
		for (Node node : nav_list.getChildren()) {
			node.getStyleClass().remove("active");
		}
		sourceNode.getStyleClass().add("active");
		if (panel == null) {
			panel = FXMLLoader.load(
					Main.class.getResource("fxml/" + fxmlID + ".fxml"),
					Main.getResources());
			AnchorPane.setTopAnchor(panel, 0.0);
			AnchorPane.setBottomAnchor(panel, 0.0);
			AnchorPane.setLeftAnchor(panel, 0.0);
			AnchorPane.setRightAnchor(panel, 0.0);
		}
		if (panel != null) {
			panels.put(fxmlID, panel);
			container.getChildren().clear();
			container.getChildren().add(panel);
		}
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
			selectNav(nav_list.getChildren().get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}