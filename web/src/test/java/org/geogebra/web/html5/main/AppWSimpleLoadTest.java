package org.geogebra.web.html5.main;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.web.html5.euclidian.EuclidianSimplePanelW;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.util.file.FileIO;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({TextAreaElement.class, EuclidianSimplePanelW.class,
		JLMContext2d.class, RootPanel.class})
public class AppWSimpleLoadTest {
	private static final String jsonPath =
			"src/test/java/org/geogebra/web/html5/main/inRegion.json";

	@Test
	public void testLoadApp() {

		TestArticleElement articleElement = new TestArticleElement("prerelease", "simple");
		String json = FileIO.load(jsonPath);
		articleElement.attr("jsonFile", json);
		AppWsimple app = AppMocker.mockAppletSimple(articleElement);
		assertTrue(((GeoBoolean) app.getKernel().lookupLabel("visible")).getBoolean());
	}

}
