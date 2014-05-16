package org.tjumyk.metaview.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import org.tjumyk.metaview.Main;

public class DialogAboutController extends DialogControllerBase {

	@FXML
	Pane root;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.showDialog(root);
	}

	@Override
	public void initData(Object data) {

	}

	@FXML
	private void onCloseAction(ActionEvent event) {
		super.closeDialog(root);
	}

	@FXML
	private void onClose(MouseEvent event) {
		super.closeDialog(root);
	}

	@FXML
	private void onContactByQQ(MouseEvent event) {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI("tencent://message/?uin="
					+ Main.getString("app.contact.qq")));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onContactByGithub(ActionEvent event) {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI(Main.getString("app.contact.github")
					.replace("@", "https://github.com/")));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onContactByEmail(ActionEvent event) {
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI("mailto:"
					+ Main.getString("app.contact.email")));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
