package org.remotercp.errorhandling.ui;

import java.util.logging.Level;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;

public class ErrorMessageSorter extends ViewerSorter {

	private static final int ASCENDING = 0;

	private static final int DESCENDING = 1;

	private int column;

	private int direction;

	public void doSort(int column) {
		if (column == this.column) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.column = column;
			direction = ASCENDING;
		}
	}

	// only used for tests
	protected void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		ViewerComparator comparator = new ViewerComparator();
		int compareResult = 0;
		ErrorMessage error1 = (ErrorMessage) e1;
		ErrorMessage error2 = (ErrorMessage) e2;

		switch (column) {
		case ErrorView.COLUMN_ICON:
			compareResult = compareSeverity(error1, error2);
			break;
		case ErrorView.COLUMN_MESSAGE:
			compareResult = comparator.compare(viewer, error1.getText(), error2
					.getText());
			break;
		case ErrorView.COLUMN_DATE:
			compareResult = comparator.compare(viewer, error1.getDate(), error2
					.getDate());
		default:
			break;
		}

		if (direction == ASCENDING) {
			compareResult = -compareResult;
		}

		return compareResult;

	}

	protected int compareSeverity(ErrorMessage e1, ErrorMessage e2) {
		Level level1 = e1.getSeverity();
		Level level2 = e2.getSeverity();

		final int EQUALS = 0;
		final int LOWER = -1;
		final int GREATER = +1;

		int compare = 0;

		if (level1 == Level.SEVERE) {
			// check, whether the lesel2 severity is lower
			if (level2 == Level.SEVERE) {
				compare = EQUALS;
			} else {
				// e1 > e2
				compare = GREATER;
			}
		}

		if (level1 == Level.WARNING) {
			if (level2 == Level.SEVERE) {
				// e1 < e2
				compare = LOWER;
			}
			if (level2 == Level.WARNING) {
				compare = EQUALS;
			}
			if (level2 == Level.INFO) {
				compare = GREATER;
			}
		}

		if (level1 == Level.INFO) {
			if (level2 == Level.SEVERE || level2 == Level.WARNING) {
				compare = LOWER;
			}
			if (level2 == Level.INFO) {
				compare = EQUALS;
			}
		}

		return compare;
	}

}
