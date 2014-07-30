package se.lu.esss.linacLego.webapp.client.panels;

import se.lu.esss.linacLego.webapp.client.LinacLegoWebApp;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutPanel;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutScrollPanel;
import se.lu.esss.linacLego.webapp.shared.HtmlTextTree;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PbsLayoutPanel extends VerticalPanel
{
	private MyTabLayoutScrollPanel myTabLayoutScrollPanel;
	private String treeType = "";
	HtmlTextTree textTree;
	
	public String getTreeType() {return treeType;}
	public MyTabLayoutScrollPanel getMyTabLayoutScrollPanel() {return myTabLayoutScrollPanel;}


	public PbsLayoutPanel(MyTabLayoutPanel myTabLayoutPanel, String tabTitle, String treeType) 
	{
		super();
		setWidth("100%");
		setHeight("100%");
		myTabLayoutScrollPanel = new MyTabLayoutScrollPanel(myTabLayoutPanel);
		myTabLayoutPanel.add(myTabLayoutScrollPanel, tabTitle);
		myTabLayoutScrollPanel.add(this);
		myTabLayoutScrollPanel.getPanelWidth();
		this.treeType = treeType;
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

	}
	public LinacLegoWebApp getLinacLegoWebApp()
	{
		return getMyTabLayoutScrollPanel().getMyTabLayoutPanel().getLinacLegoWebApp();
	}
	public void addTree(HtmlTextTree textTree)
	{
		if (getWidgetCount() > 0) clear();
	      myTabLayoutScrollPanel.getMyTabLayoutPanel().getLinacLegoWebApp().getStatusTextArea().addStatus("Finished building " + treeType + " layout view.");
	      PbsLevelPanel pbsLevelPanel = new PbsLevelPanel(0, textTree.getTextTreeArrayList().get(0), true, null, myTabLayoutScrollPanel);
	      add(new InlineHTML(textTree.getInlineHtmlString(false)));
	      add(pbsLevelPanel);
	      pbsLevelPanel.focusPanel.setFocus(true);
	}

}
