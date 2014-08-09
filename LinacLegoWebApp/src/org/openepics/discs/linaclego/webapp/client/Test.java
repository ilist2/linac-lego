package org.openepics.discs.linaclego.webapp.client;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Button;

public class Test extends VerticalPanel
{
	public Test() 
	{
		
		Grid grid = new Grid(1, 3);
		add(grid);
		grid.setSize("410px", "127px");
		
		Button btnNewButton = new Button("New button");
		grid.setWidget(0, 0, btnNewButton);
		
		Button btnNewButton1 = new Button("New button1");
		grid.setWidget(0, 1, btnNewButton1);
		
		Button btnNewButton2 = new Button("New button2");
		grid.setWidget(0, 2, btnNewButton2);
//		grid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
//		grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
//		grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		
		
	}
}
