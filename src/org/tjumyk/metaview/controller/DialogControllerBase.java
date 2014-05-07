package org.tjumyk.metaview.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class DialogControllerBase implements Initializable {
	public abstract void initData(Object data);

	protected void showDialog(Node root) {
		TranslateTransition translate = new TranslateTransition();
		translate.setFromY(-20);
		translate.setToY(0);
		FadeTransition fade = new FadeTransition();
		fade.setFromValue(0.0);
		fade.setToValue(1.0);
		ParallelTransition pt = new ParallelTransition(root, translate, fade);
		pt.play();
	}

	protected void closeDialog(final Node root) {
		TranslateTransition translate = new TranslateTransition();
		translate.setFromY(0);
		translate.setToY(-20);
		FadeTransition fade = new FadeTransition();
		fade.setFromValue(1.0);
		fade.setToValue(0.0);
		ParallelTransition pt = new ParallelTransition(root, translate, fade);
		pt.setOnFinished(e -> {
			Stage stage = (Stage) root.getScene().getWindow();
			stage.close();
		});
		pt.play();
	}
}
