package org.tjumyk.metaview.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class DialogLoadController extends DialogControllerBase {

	@FXML
	AnchorPane root;

	@FXML
	ProgressBar progress;

	@FXML
	Label label_message;

	@FXML
	TextArea area_detail;

	private Task<?> task;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.showDialog(root);
	}

	@Override
	public void initData(Object data) {
		task = (Task<?>) data;
		if (task != null) {
			progress.progressProperty().bind(task.progressProperty());
			label_message.textProperty().bind(task.messageProperty());
		}
		task.exceptionProperty().addListener(
				(observable, oldVal, newVal) -> {
					if (newVal != null) {
						if (task.getState() == State.CANCELLED
								&& newVal instanceof IllegalStateException) {
							return;// hot fix
						}
						newVal.printStackTrace();
						StringWriter sw = new StringWriter();
						newVal.printStackTrace(new PrintWriter(sw));
						area_detail.setText(sw.toString());
						showDetail();
					}
				});
		task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, e -> {
			super.closeDialog(root);
		});
		task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, e -> {
			super.closeDialog(root);
		});
	}

	@FXML
	void onCancelAction(ActionEvent event) {
		if (task != null && task.isRunning()) {
			task.cancel();
		} else
			super.closeDialog(root);
	}

	@FXML
	void onCancel(MouseEvent event) {
		if (task != null && task.isRunning()) {
			task.cancel();
		} else
			super.closeDialog(root);
	}

	private void showDetail() {
		root.getScene().getWindow().setHeight(300);
		area_detail.setVisible(true);
	}
}
