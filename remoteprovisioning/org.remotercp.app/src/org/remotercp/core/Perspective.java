package org.remotercp.core;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		/*
		 * bewirkt, dass obwohl noch kein Editor angezeigt wird, bereits der
		 * Platz dafür reserviert und "grau" belegt wird.
		 */
		layout.setEditorAreaVisible(true);

		// String editorArea = layout.getEditorArea();
		//
		// layout.addView("org.eclipse.pde.runtime.LogView", IPageLayout.RIGHT,
		// 0.8f, editorArea);
	}
}
