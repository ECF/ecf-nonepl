package org.eclipse.ecf.internal.provider.skype.ui.hyperlink;

import java.net.URI;

import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class SkypeHyperlinkDetector extends AbstractURLHyperlinkDetector {

	public static final String SKYPE_PROTOCOL = "skype"; //$NON-NLS-1$
	
	public SkypeHyperlinkDetector() {
		setProtocols(new String [] { SKYPE_PROTOCOL });
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector#createHyperLinksForURI(org.eclipse.jface.text.IRegion, java.net.URI)
	 */
	protected IHyperlink[] createHyperLinksForURI(IRegion region, URI uri) {
		return new IHyperlink[] { new SkypeHyperlink(region, uri) };
	}	

}
