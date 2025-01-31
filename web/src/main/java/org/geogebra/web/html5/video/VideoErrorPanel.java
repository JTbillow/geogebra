package org.geogebra.web.html5.video;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.util.PersistablePanel;

import com.google.gwt.user.client.ui.Label;

/**
 * Panel to display message is video is not available
 *
 * @author Laszlo
 */
public class VideoErrorPanel extends PersistablePanel {
	private final Localization loc;
	public Label error;
	private String errorId;

	/**
	 * Constructor
	 */
	VideoErrorPanel(Localization loc, String errorId) {
		this.loc = loc;
		this.errorId = errorId;
		createGUI();
		stylePanel();
		setErrorMessage();
	}

	private void createGUI() {
		error = new Label();
		add(error);
	}

	private void stylePanel() {
		setWidth("100%");
		setHeight("100%");
		addStyleName("mowWidget");
		addStyleName("error");
	}

	private void setErrorMessage() {
		error.setText(loc.getError(errorId));
	}
}
