package se.lu.esss.linacLego.webapp.client.tablayout;

import se.lu.esss.linacLego.webapp.client.LinacLegoWebApp;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class MyTabLayoutPanel extends TabLayoutPanel
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	
	private LinacLegoWebApp linacLegoWebApp;

	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}
	public LinacLegoWebApp getLinacLegoWebApp() {return linacLegoWebApp;}

	public MyTabLayoutPanel(int barHeightPx, LinacLegoWebApp linacLegoWebApp, int panelWidth, int panelHeight) 
	{
		super((double) barHeightPx, Unit.PX);
		this.linacLegoWebApp = linacLegoWebApp;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight + "px");

	    addSelectionHandler(new MyTabLayoutPanelSelectionHandler(this));
	    	    
	}
	public void setSize(int panelWidth, int panelHeight)
	{
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");
	}
	public void showTab(int itab, boolean showTab)
	{
	    getTabWidget(itab).setVisible(showTab);
	    getTabWidget(itab).getParent().setVisible(showTab);
	}
	class MyTabLayoutPanelSelectionHandler implements SelectionHandler<Integer>
	{
		MyTabLayoutPanel myTabLayoutPanel;
		MyTabLayoutPanelSelectionHandler(MyTabLayoutPanel myTabLayoutPanel)
		{
			this.myTabLayoutPanel = myTabLayoutPanel;
		}
		@Override
		public void onSelection(SelectionEvent<Integer> event) 
		{
//			int tabId = event.getSelectedItem();
		}
	}

}
