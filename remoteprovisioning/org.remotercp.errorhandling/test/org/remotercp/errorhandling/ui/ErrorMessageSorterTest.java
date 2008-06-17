package org.remotercp.errorhandling.ui;

import static org.junit.Assert.*;

import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

public class ErrorMessageSorterTest {

	private ErrorMessage e1Severe;

	private ErrorMessage e1Warning;

	private ErrorMessage e1Info;

	private ErrorMessage e2Severe;

	private ErrorMessage e2Warning;

	private ErrorMessage e2Info;

	private static final int EQUALS = 0;

	private static final int GREATER = +1;

	private static final int LOWER = -1;

	@Before
	public void setUp() {
		e1Severe = new ErrorMessage("Severe message", null, Level.SEVERE) {

			@Override
			public String getDate() {
				return "17.06.2008 12:00";
			}
		};
		e1Warning = new ErrorMessage("Warning message", null, Level.WARNING) {
			@Override
			public String getDate() {
				return "17.06.2008 13:00";
			}
		};

		e1Info = new ErrorMessage("Info message", null, Level.INFO) {
			@Override
			public String getDate() {
				return "17.06.2008 14:00";
			}
		};

		e2Severe = new ErrorMessage("Severe message", null, Level.SEVERE) {
			@Override
			public String getDate() {
				return "17.06.2008 12:00";
			}
		};
		e2Warning = new ErrorMessage("Warning message", null, Level.WARNING) {
			@Override
			public String getDate() {
				return "17.06.2008 13:00";
			}
		};
		e2Info = new ErrorMessage("Info message", null, Level.INFO) {
			@Override
			public String getDate() {
				return "17.06.2008 14:00";
			}
		};
	}

	@Test
	public void testSorter() {
		ErrorMessageSorter sorter = new ErrorMessageSorter();
		sorter.doSort(ErrorView.COLUMN_ICON);

		// override direction
		sorter.setDirection(0);

		int compare = 0;

		// Column Icons
		compare = sorter.compare(null, e1Severe, e2Severe);
		assertEquals(EQUALS, compare);

		compare = sorter.compare(null, e1Severe, e2Warning);
		assertEquals(GREATER, compare);

		compare = sorter.compare(null, e1Severe, e2Info);
		assertEquals(GREATER, compare);

		compare = sorter.compare(null, e1Warning, e2Severe);
		assertEquals(LOWER, compare);

		compare = sorter.compare(null, e1Warning, e2Warning);
		assertEquals(EQUALS, compare);

		compare = sorter.compare(null, e1Warning, e2Info);
		assertEquals(GREATER, compare);

		compare = sorter.compare(null, e1Info, e2Severe);
		assertEquals(LOWER, compare);

		compare = sorter.compare(null, e1Info, e2Warning);
		assertEquals(LOWER, compare);

		compare = sorter.compare(null, e1Info, e2Info);
		assertEquals(EQUALS, compare);

		// column message
		sorter.doSort(ErrorView.COLUMN_MESSAGE);

		compare = sorter.compare(null, e1Severe, e2Severe);
		// e1 == e2
		assertTrue(compare == 0);

		compare = sorter.compare(null, e1Info, e2Severe);
		assertTrue(compare < 0);
		// assertEquals(GREATER, compare);

		compare = sorter.compare(null, e1Warning, e2Info);
		assertTrue(compare > 0);

		// date
		sorter.doSort(ErrorView.COLUMN_DATE);

		compare = sorter.compare(null, e1Info, e2Info);
		assertEquals(EQUALS, compare);

		compare = sorter.compare(null, e1Severe, e2Warning);
		assertEquals(LOWER, compare);

		compare = sorter.compare(null, e1Info, e2Warning);
		assertEquals(GREATER, compare);
	}
}
