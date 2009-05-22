package org.eclipse.ecf.provider.twitter.ui.hub;

import java.util.Iterator;

import org.eclipse.ecf.internal.provider.twitter.search.TweetItem;
import org.eclipse.ecf.presence.search.IResultList;
import org.eclipse.ecf.presence.search.message.MessageSearchException;
import org.eclipse.ecf.provider.twitter.ui.logic.TwitterController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

public class SearchViewPart extends ViewPart implements Listener {

	public static final String VIEW_ID = "org.eclipse.ecf.provider.twitter.ui.hub.searchView";
	private FormToolkit toolkit; 
	private ScrolledForm form;
	private Composite formComposite;
	private TwitterController controller;
	
	private Text searchQuery;
	
	
	public SearchViewPart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {	
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Twitter Search");
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		formComposite = form.getBody();
		
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		data.grabHorizontal = true;
		
		searchQuery = toolkit.createText(formComposite,"");
		searchQuery.setLayoutData(data);
		
		Button searchBtn = toolkit.createButton(formComposite, "Search",SWT.NONE);
		searchBtn.addListener(SWT.Selection, this);
 
	}
	
	
	public void setController(TwitterController controller)
	{
		this.controller = controller;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(Event event) 
	{	
		//run a search 
		String query = searchQuery.getText();
		try
		{
			IResultList resultList =   controller.runSearch(query);
			
			Iterator<TweetItem> results = resultList.getResults().iterator();
			
			//start adding message composites here.
			while(results.hasNext())
			{
				TweetItem res = results.next();
				
				

				
			}
			
			
		}
		catch(MessageSearchException mse)
		{
			//handle error with search 
			
			
		}
		
		
	}

}
