package org.eclipse.ecf.provider.twitter.ui.hub;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class BrowserViewPart extends ViewPart {

	public static final String VIEW_ID = "org.eclipse.ecf.provider.twitter.ui.hub.browserView";

	private Browser browser; 
	
	public BrowserViewPart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		browser = new Browser(parent,SWT.NONE);
		parent.setLayout(new FillLayout());
		
		
	}
	public void setURL(String url)
	{
		browser.setUrl(url);
	}
	

	@Override
	public void setFocus() {
		
		
	}

}
