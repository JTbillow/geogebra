package org.geogebra.common.kernel.geos;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.TextObject;

/**
 * Input box for user input
 *
 * @author Michael
 *
 */
public class GeoInputBox extends GeoButton implements HasSymbolicMode, HasAlignment {

	private static final int defaultLength = 20;

	private int length = defaultLength;
	private int printDecimals = -1;
	private int printFigures = -1;
	private boolean useSignificantFigures = false;
	private StringTemplate tpl = StringTemplate.defaultTemplate;

	private boolean symbolicMode = false;

	private StringTemplate stringTemplateForLaTeX;

	private TextAlignment textAlignment = TextAlignment.LEFT;

	private @Nonnull GeoElementND linkedGeo;
	private @Nonnull InputBoxProcessor inputBoxProcessor;

	/**
	 * Creates new text field
	 *
	 * @param cons
	 *            construction
	 */
	public GeoInputBox(Construction cons) {
		super(cons);
		linkedGeo = new GeoText(cons, "");
		inputBoxProcessor = new InputBoxProcessor(this, linkedGeo);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labelOffsetX
	 *            x offset
	 * @param labelOffsetY
	 *            y offset
	 */
	public GeoInputBox(Construction cons, int labelOffsetX, int labelOffsetY) {
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
	}

	@Override
	public boolean isChangeable() {
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.TEXTFIELD;
	}

	/**
	 * @param geo
	 *            new linked geo
	 */
	public void setLinkedGeo(GeoElementND geo) {
		if (geo == null) {
			linkedGeo = new GeoText(cons, "");
		} else {
			linkedGeo = geo;
		}

		inputBoxProcessor = new InputBoxProcessor(this, linkedGeo);
	}

	/**
	 *
	 * @return text to edit.
	 */
	public String getTextForEditor() {
		if (linkedGeo.isGeoText()) {
			return ((GeoText) linkedGeo).getTextString();
		}

		String linkedGeoText;

		if (linkedGeo.isGeoNumeric()) {
			GeoNumeric numeric = (GeoNumeric) linkedGeo;

			if (!numeric.isDefined() || isSymbolicMode() && numeric.isSymbolicMode()) {
				linkedGeoText = numeric.getRedefineString(true, true);
			} else if (numeric.isSymbolicMode()) {
				linkedGeoText = numeric.getValueForInputBar();
			} else {
				linkedGeoText = numeric.toValueString(tpl);
			}
		} else {
			linkedGeoText = linkedGeo.getRedefineString(true, true);
		}

		if ("?".equals(linkedGeoText)) {
			return "";
		}

		return linkedGeoText;
	}

	/**
	 * Get the text (used for scripting)
	 *
	 * @return the text
	 */
	public String getText() {
		if (linkedGeo.isGeoText()) {
			return ((GeoText) linkedGeo).getTextString();
		}

		String linkedGeoText;

		if (linkedGeo.isGeoNumeric()) {
			if (symbolicMode && ((GeoNumeric) linkedGeo).isSymbolicMode()
					&& !((GeoNumeric) linkedGeo).isSimple()) {
				linkedGeoText = toLaTex();
			} else if (linkedGeo.isDefined() && linkedGeo.isIndependent()) {
				linkedGeoText = linkedGeo.toValueString(tpl);
			} else {
				linkedGeoText = linkedGeo.getRedefineString(true, true);
			}
		} else if (isSymbolicMode()) {
			linkedGeoText = toLaTex();
		} else {
			linkedGeoText = linkedGeo.getRedefineString(true, true);
		}

		if ("?".equals(linkedGeoText)) {
			return "";
		}

		return linkedGeoText;
	}

	private String toLaTex() {
		boolean flatEditableList = !hasEditableMatrix() && linkedGeo.isGeoList();

		if (hasSymbolicFunction() || flatEditableList) {
			return linkedGeo.getRedefineString(true, true,
					getStringtemplateForLaTeX());
		}
		return linkedGeo.toLaTeXString(true, StringTemplate.latexTemplate);
	}

	private boolean hasEditableMatrix() {
		if (!linkedGeo.isGeoList()) {
			return false;
		}

		return ((GeoList) linkedGeo).isEditableMatrix();
	}

	private StringTemplate getStringtemplateForLaTeX() {
		if (stringTemplateForLaTeX == null) {
			stringTemplateForLaTeX = StringTemplate.latexTemplate.makeStrTemplateForEditing();
		}
		return stringTemplateForLaTeX;
	}

	/**
	 * Returns the linked geo
	 *
	 * @return linked geo
	 */
	public GeoElementND getLinkedGeo() {
		return linkedGeo;
	}

	@Override
	public String toValueString(StringTemplate tpl1) {
		return getText();
	}

	@Override
	public boolean isGeoInputBox() {
		return true;
	}

	/**
	 * Sets length of the input box
	 *
	 * @param len
	 *            new length
	 */
	public void setLength(int len) {
		length = len;
		this.updateVisualStyle(GProperty.LENGTH);
	}

	/**
	 * @return length of the input box
	 */
	public int getLength() {
		return length;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		// print decimals
		if (printDecimals >= 0 && !useSignificantFigures) {
			sb.append("\t<decimals val=\"");
			sb.append(printDecimals);
			sb.append("\"/>\n");
		}

		// print significant figures
		if (printFigures >= 0 && useSignificantFigures) {
			sb.append("\t<significantfigures val=\"");
			sb.append(printFigures);
			sb.append("\"/>\n");
		}

		if (isSymbolicMode()) {
			sb.append("\t<symbolic val=\"true\" />\n");
		}

		if (getLength() != defaultLength) {
			sb.append("\t<length val=\"");
			sb.append(getLength());
			sb.append("\"");
			sb.append("/>\n");
		}
		if (getAlignment() != TextAlignment.LEFT) {
			sb.append("\t<textAlign val=\"");
			sb.append(getAlignment().toString());
			sb.append("\"/>\n");
		}
	}

	@Override
	public GeoElement copy() {
		return new GeoInputBox(cons, labelOffsetX, labelOffsetY);
	}

	/**
	 * @param inputText
	 *            new value for linkedGeo
	 */
	public void updateLinkedGeo(String inputText) {
		inputBoxProcessor.updateLinkedGeo(inputText, tpl, printDecimals > -1 || printFigures > -1);
	}

	/**
	 * Called by a Drawable for this object when it is updated
	 *
	 * @param textFieldToUpdate
	 *            the Drawable's text field
	 */
	public void updateText(TextObject textFieldToUpdate) {
		// avoid redraw error
		String linkedText = getText();
		if (!textFieldToUpdate.getText().equals(linkedText)) {
			textFieldToUpdate.setText(linkedText);
		}
	}

	/**
	 * Called by a Drawable when its text object is updated
	 *
	 * @param textFieldToUpdate
	 *            the Drawable's text field
	 */
	public void textObjectUpdated(TextObject textFieldToUpdate) {
		updateLinkedGeo(textFieldToUpdate.getText());
		updateText(textFieldToUpdate);
	}

	/**
	 * Called by a Drawable when the input is submitted (e.g. by pressing ENTER)
	 */
	public void textSubmitted() {
		runClickScripts(getText());
	}

	private void updateTemplate() {
		if (useSignificantFigures() && printFigures > -1) {
			tpl = StringTemplate.printFigures(StringType.GEOGEBRA, printFigures,
					false);
		} else if (!useSignificantFigures && printDecimals > -1) {
			tpl = StringTemplate.printDecimals(StringType.GEOGEBRA,
					printDecimals, false);
		} else {
			tpl = StringTemplate.get(StringType.GEOGEBRA);
		}
	}

	@Override
	public int getPrintDecimals() {
		return printDecimals;
	}

	@Override
	public int getPrintFigures() {
		return printFigures;
	}

	@Override
	public void setPrintDecimals(int printDecimals, boolean update) {
		this.printDecimals = printDecimals;
		printFigures = -1;
		useSignificantFigures = false;
		updateTemplate();
	}

	@Override
	public void setPrintFigures(int printFigures, boolean update) {
		this.printFigures = printFigures;
		printDecimals = -1;
		useSignificantFigures = true;
		updateTemplate();
	}

	@Override
	public boolean useSignificantFigures() {
		return useSignificantFigures;
	}

	@Override
	public void setBackgroundColor(final GColor bgCol) {
		if (bgCol == null) {
			// transparent
			bgColor = null;
			return;
		}

		// default in case alpha = 0 (not allowed for Input Boxes)
		int red = 255, green = 255, blue = 255;

		// fix for files saved with alpha = 0
		if (bgCol.getAlpha() != 0) {

			red = bgCol.getRed();
			green = bgCol.getGreen();
			blue = bgCol.getBlue();
		}

		bgColor = GColor.newColor(red, green, blue);
	}

	@Override
	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		DrawableND draw = ev.getDrawableFor(this);
		if (draw instanceof DrawInputBox) {
			return ((DrawInputBox) draw).getTotalSize().getWidth();
		}
		return getWidth();
	}

	@Override
	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		DrawableND draw = ev.getDrawableFor(this);
		if (draw instanceof DrawInputBox) {
			return ((DrawInputBox) draw).getTotalSize().getHeight();
		}
		return getHeight();
	}

	@Override
	public DescriptionMode needToShowBothRowsInAV() {
		return DescriptionMode.DEFINITION;
	}

	@Override
	public GColor getBackgroundColor() {
		return bgColor;
	}

	/**
	 * @return description for the screen reader
	 */
	public String getAuralText() {
		ScreenReaderBuilder sb = new ScreenReaderBuilder();
		sb.append(getKernel().getLocalization().getMenu("Text Field"));
		sb.appendSpace();
		sb.append(getCaption(StringTemplate.screenReader));
		return sb.toString();
	}

	@Override
	public void setSymbolicMode(boolean mode, boolean updateParent) {
		this.symbolicMode = mode;
	}

	@Override
	public boolean isSymbolicMode() {
		return canBeSymbolic() && symbolicMode;
	}

	/**
	 * @return if linked object can be a symbolic one.
	 */
	public boolean canBeSymbolic() {
		return hasSymbolicNumber() || hasSymbolicFunction()
				|| linkedGeo.isGeoPoint() || linkedGeo.isGeoVector()
				|| linkedGeo.isGeoLine() || linkedGeo.isGeoPlane() || linkedGeo.isGeoList();
	}

	private boolean hasSymbolicFunction() {
		return linkedGeo instanceof GeoFunction || linkedGeo instanceof GeoFunctionNVar;
	}

	private boolean hasSymbolicNumber() {
		if (!linkedGeo.isGeoNumeric()) {
			return false;
		}

		GeoNumeric number = (GeoNumeric) linkedGeo;
		return !number.isAngle();
	}

	@Override
	public void setAlignment(TextAlignment alignment) {
		textAlignment = alignment;
	}

	@Override
	public TextAlignment getAlignment() {
		return textAlignment;
	}

	/**
	 * @return whether the alpha button should be shown
	 */
	public boolean needsSymbolButton() {
		return getLength() >= EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH
				&& !(linkedGeo instanceof GeoText && linkedGeo.isLabelSet());
	}
}
