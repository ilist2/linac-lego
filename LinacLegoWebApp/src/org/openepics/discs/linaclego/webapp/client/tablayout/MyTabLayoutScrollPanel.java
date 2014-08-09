package org.openepics.discs.linaclego.webapp.client.tablayout;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;

public class MyTabLayoutScrollPanel extends ScrollPanel
{
	private int panelWidth = 0;
	private int panelHeight = 0;
	private MyTabLayoutPanel myTabLayoutPanel;

	public MyTabLayoutPanel getMyTabLayoutPanel() {return myTabLayoutPanel;}
	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}

	public MyTabLayoutScrollPanel(MyTabLayoutPanel myTabLayoutPanel)
	{
		super();
		this.myTabLayoutPanel = myTabLayoutPanel;

		setAlwaysShowScrollBars(true);
		reSize();
		Window.addResizeHandler(new MyResizeHandler());
	}
	public void reSize()
	{
		panelWidth = myTabLayoutPanel.getLinacLegoWebApp().myTabLayoutPanelWidth() - 15;
		panelHeight = myTabLayoutPanel.getLinacLegoWebApp().myTabLayoutPanelHeight() 
				- myTabLayoutPanel.getLinacLegoWebApp().getMyTabLayoutPanelHeightBarHeightPx() - 15;
		setSize(panelWidth + "px", panelHeight + "px");
	}
	public class MyResizeHandler implements ResizeHandler
	{
		@Override
		public void onResize(ResizeEvent event) 
		{
			reSize();
		}
	}
}
