package org.eclipse.ecf.provider.twitter.ui.hub;

import org.eclipse.ecf.provider.twitter.ui.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

public class ReplyViewPart extends ViewPart {

	public static final String VIEW_ID = "org.eclipse.ecf.provider.twitter.ui.hub.replyView";
	
	public ReplyViewPart() {
		// TODO Auto-generated constructor stub
	}

	
//	@Override
//	public void init(IViewSite site) 
//	{
//		
//		super.init(site);
//	}
	
	@Override
	public void createPartControl(Composite parent) {

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm tweetForm = toolkit.createScrolledForm(parent);
		
		tweetForm.setText("Mentions");
		
		Composite tweet = tweetForm.getBody();
		tweet.setLayout(new GridLayout(1, false));
		tweet.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
