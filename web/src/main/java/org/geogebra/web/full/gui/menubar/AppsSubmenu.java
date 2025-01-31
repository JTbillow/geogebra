package org.geogebra.web.full.gui.menubar;

import com.google.gwt.resources.client.ResourcePrototype;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.exam.ExamStartDialog;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Web implementation of PerspectivesMenu
 */
public class AppsSubmenu extends Submenu {

	/** Application */
	AppW app;

	/**
		 * @param app application
		 */
	public AppsSubmenu(AppW app) {
		super("apps", app);
		this.app = app;
		addExpandableStyleWithColor(true);
		initActions();
	}

	private void initActions() {
		addMenuItem("graphing", "GraphingCalculator",
				MaterialDesignResources.INSTANCE.graphing());
		addMenuItem("geometry", "Geometry",
				MaterialDesignResources.INSTANCE.geometry());
		addMenuItem("3d", "Graphing3D",
				MaterialDesignResources.INSTANCE.graphing3D());
		addMenuItem("calculator", "ScientificCalculator",
				MaterialDesignResources.INSTANCE.scientific());
		addMenuItem("classic", "math_apps",
				MaterialDesignResources.INSTANCE.geogebra_color());
		if (app.getConfig().hasExam() && !app.isExam() && app.getLAF().isOfflineExamSupported()) {
			addExamMenuItem();
		}
	}

	private void addExamMenuItem() {
		addItem(MainMenu.getMenuBarHtml(((AppWFull) app).getActivity().getExamIcon(),
				app.getLocalization().getMenu(app.getConfig().getExamMenuItemText())),
				true, new MenuCommand(getApp()) {

					@Override
					public void execute() {
						if (app.isMenuShowing()) {
							app.toggleMenu();
						}
						((DialogManagerW) app.getDialogManager())
								.getSaveDialog()
								.showIfNeeded(getExamCallback());
					}
				});
	}

	/**
	 * @return callback that shows the start exam dialog
	 */
	AsyncOperation<Boolean> getExamCallback() {
		return new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean active) {
				app.fileNew();
				app.getLAF().toggleFullscreen(true);
				ExamStartDialog examStartDialog = new ExamStartDialog(getApp());
				examStartDialog.show();
				examStartDialog.center();
			}
		};
	}

	private void addMenuItem(String appId, String translationKey,
			ResourcePrototype icon) {
		StringBuilder link = new StringBuilder("https://www.geogebra.org/");
		if (app.has(Feature.TUBE_BETA)) {
			link = new StringBuilder("https://beta.geogebra.org/");
		}
		link.append(appId);
		AriaMenuItem item = addItem(
				getHTMLwithLink(ImgResourceHelper.safeURI(icon),
				app.getLocalization()
						.getMenu(translationKey),
				link.toString()),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						// do nothing
					}
				});
		AriaHelper.setLabel(item,
				app.getLocalization().getMenu(translationKey));
	}

	/**
	 * @param img
	 *            - image of the menu item
	 * @param s
	 *            - title of the menu item
	 * @param link
	 *            -
	 * @return html code
	 */
	private String getHTMLwithLink(String img, String s, String link) {
		String imgHTML = "<img src=\"" + img
				+ "\" draggable=\"false\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>";
		return "<a class=\"menuLink\" href=\" " + link + "\" target=\"_blank\">"
				+ imgHTML + "</a>";
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.geogebra_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "Apps";
	}
}

