package org.tjumyk.metaview.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import org.tjumyk.metaview.Main;

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
	 * Parent folder of all the help images
	 */
	private static final String IMAGE_BASE_PATH = "help-images/";

	/**
	 * Total count of the help images
	 */
	private static final int IMAGE_TOTAL = 5;

	/**
	 * fit height of help images
	 */
	private static final int IMAGE_FIT_HEIGHT = 300;

	/**
	 * fit width of help images
	 */
	private static final int IMAGE_FIT_WIDTH = 540;

	/**
	 * All the loaded ImageView list
	 */
	private List<ImageView> imgList = new ArrayList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String dir = "images/" + IMAGE_BASE_PATH
				+ Main.getString("help.image_dir") + "/";
		for (int i = 1; i <= IMAGE_TOTAL; i++) {
			ImageView imgView = new ImageView(new Image(
					Main.class.getResourceAsStream(dir + i + ".png")));
			imgView.setFitHeight(IMAGE_FIT_HEIGHT);
			imgView.setFitWidth(IMAGE_FIT_WIDTH);
			imgView.setPreserveRatio(true);
			imgList.add(imgView);
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
