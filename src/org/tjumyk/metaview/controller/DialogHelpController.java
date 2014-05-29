package org.tjumyk.metaview.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.tjumyk.metaview.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Controller of the "Help" dialog.
 * 
 * @author 宇锴
 */
public class DialogHelpController extends DialogControllerBase {
	@FXML
	private AnchorPane root;

	@FXML
	private Pagination pagination_help_image;

	/**
	 * Parent folder of the help images
	 */
	private static final String IMAGE_PATH = "help-images/";

	/**
	 * Total count of the help images
	 */
	private static final int IMAGE_TOTAL = 5;

	/**
	 * All the loaded ImageView list
	 */
	private List<ImageView> imgList = new ArrayList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for (int i = 1; i <= IMAGE_TOTAL; i++) {
			imgList.add(new ImageView(new Image(Main.class
					.getResourceAsStream("images/" + IMAGE_PATH + i + ".png"))));
		}
		pagination_help_image.setPageCount(IMAGE_TOTAL);
		pagination_help_image.setMaxPageIndicatorCount(IMAGE_TOTAL);
		pagination_help_image.setPageFactory(i -> {
			return imgList.get(i);
		});
		super.showDialog(root);
	}

	@Override
	public void initData(Object data) {

	}

	@FXML
	void onClose(MouseEvent event) {
		super.closeDialog(root);
	}

	@FXML
	void onCloseAction(ActionEvent event) {
		super.closeDialog(root);
	}
}
