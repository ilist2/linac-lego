package se.lu.esss.linacLego.webapp.client;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class Test extends VerticalPanel
{
	public Test() 
	{
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setBorderWidth(3);
		add(horizontalPanel);
		horizontalPanel.setSize("335px", "170px");
		
		TextBox txtbxBooger = new TextBox();
		txtbxBooger.setText("Booger");
		horizontalPanel.add(txtbxBooger);
		horizontalPanel.setCellVerticalAlignment(txtbxBooger, HasVerticalAlignment.ALIGN_MIDDLE);
		txtbxBooger.setWidth("211px");
		VerticalPanel verticalPanel = new VerticalPanel();
		horizontalPanel.add(verticalPanel);
		
		
		
	}
}
