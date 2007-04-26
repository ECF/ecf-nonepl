package org.eclipse.ecf.internal.provider.yahoo.ui.hyperlink;

import java.net.URI;

import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class YahooHyperlinkDetector extends AbstractURLHyperlinkDetector {

	public static final String YAHOO_PROTOCOL = "yahoo"; //$NON-NLS-1$
	
	public YahooHyperlinkDetector() {
		setProtocols(new String [] { YAHOO_PROTOCOL });
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector#createHyperLinksForURI(org.eclipse.jface.text.IRegion, java.net.URI)
	 */
	protected IHyperlink[] createHyperLinksForURI(IRegion region, URI uri) {
		return new IHyperlink[] { new YahooHyperlink(region, uri) };
	}	

}
