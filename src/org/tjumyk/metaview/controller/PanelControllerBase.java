package org.tjumyk.metaview.controller;

import javafx.fxml.Initializable;

/**
 * The abstract class of the controller of the content panels.
 * 
 * @author 宇锴
 */
public abstract class PanelControllerBase implements Initializable {
	public abstract void execCommand(String id);
}
