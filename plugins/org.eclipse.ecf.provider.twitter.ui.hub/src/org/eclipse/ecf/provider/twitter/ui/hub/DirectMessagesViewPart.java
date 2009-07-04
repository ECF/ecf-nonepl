package org.eclipse.ecf.provider.twitter.ui.hub;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

public class DirectMessagesViewPart extends ViewPart {

	public static final String VIEW_ID = "org.eclipse.ecf.provider.twitter.ui.hub.directMessagesView";
	
	public DirectMessagesViewPart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm tweetForm = toolkit.createScrolledForm(parent);
		
		tweetForm.setText("Direct Messages");
		
		Composite tweet = tweetForm.getBody();
		tweet.setLayout(new GridLayout(1, false));
		tweet.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
