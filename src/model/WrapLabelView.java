package model;

import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.View;

/**
 * 
 * The class to support view of the text frame it wraps long word within text
 * pane to the next line
 */
public class WrapLabelView extends LabelView {
	public WrapLabelView(Element elem) {
		super(elem);
	}

	public float getMinimumSpan(int axis) {
		switch (axis) {
		case View.X_AXIS:
			return 0;
		case View.Y_AXIS:
			return super.getMinimumSpan(axis);
		default:
			throw new IllegalArgumentException("Invalid axis: " + axis);
		}
	}

}
