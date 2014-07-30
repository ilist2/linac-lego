package se.lu.esss.linacLego.webapp.client.panels;


import se.lu.esss.linacLego.webapp.client.LinacLegoWebApp;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutPanel;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutScrollPanel;
import se.lu.esss.linacLego.webapp.shared.HtmlTextTree;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.InlineHTML;

public class TreeViewPanel extends VerticalPanel
{
    private Tree rootTree;
    private TreeItem rootTreeItem;
	private MyTabLayoutScrollPanel myTabLayoutScrollPanel;
	private String treeType = "";
	private HtmlTextTree textTree;
	
	public HtmlTextTree getTextTree() {return textTree;}
	public String getTreeType() {return treeType;}
	public MyTabLayoutScrollPanel getMyTabLayoutScrollPanel() {return myTabLayoutScrollPanel;}
	
	public TreeViewPanel(MyTabLayoutPanel myTabLayoutPanel, String tabTitle, String treeType) 
	{
		super();
		myTabLayoutScrollPanel = new MyTabLayoutScrollPanel(myTabLayoutPanel);
		myTabLayoutPanel.add(myTabLayoutScrollPanel, tabTitle);
		myTabLayoutScrollPanel.add(this);
		rootTree = null;
		this.treeType = treeType;
	}
	public LinacLegoWebApp getLinacLegoWebApp()
	{
		return getMyTabLayoutScrollPanel().getMyTabLayoutPanel().getLinacLegoWebApp();
	}
	public void addTree(HtmlTextTree textTree)
	{
		if (getWidgetCount() > 0) clear();
		this.textTree = textTree;
		rootTree = new Tree();
		rootTree.addSelectionHandler(new TreeSelectionHandler());
//	      rootTreeItem = buildTree(pbsViewTextTree);
		rootTreeItem = new MyTreeItem(textTree,  32, 32);
		rootTree.addItem(rootTreeItem);
		myTabLayoutScrollPanel.getMyTabLayoutPanel().getLinacLegoWebApp().getStatusTextArea().addStatus("Finished building " + treeType + " tree view.");
		add(rootTree);
	}
	class MyTreeItem extends TreeItem
	{
		HtmlTextTree textTree;
		boolean beenExpanded = false;
		boolean open = false;
		InlineHTML inlineHTML;
		String origHtml;
		int iconWidthPx;
		int iconHeightPx;
		
		MyTreeItem(HtmlTextTree textTree, int iconWidthPx, int iconHeightPx)
		{
			this(new InlineHTML(textTree.getInlineHtmlString(iconWidthPx, iconHeightPx, true)));
			this.textTree = textTree;
			this.iconWidthPx = iconWidthPx;
			this.iconHeightPx = iconHeightPx;
			beenExpanded = false;
			if (textTree.hasChildren() || textTree.hasDataFolder()) 
			{
				inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FF0000\">* </font>" + inlineHTML.getHTML());
			}
			else
			{
				inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FFFFFF\">* </font>" + inlineHTML.getHTML());
			}
		}
		MyTreeItem(InlineHTML inlineHTML)
		{
			super(inlineHTML);
			this.inlineHTML = inlineHTML;
			beenExpanded = false;
		}
		void expand()
		{
			if (beenExpanded) return;
			if (textTree.hasDataFolder())
			{
				MyTreeItem myTreeItem = new MyTreeItem(textTree.getDataFolder(), iconWidthPx, iconHeightPx);
				addItem(myTreeItem);
			}
			if (textTree.hasChildren())
			{
				for (int ichild = 0; ichild < textTree.getTextTreeArrayList().size(); ++ichild)
				{
					MyTreeItem myTreeItem = new MyTreeItem(textTree.getTextTreeArrayList().get(ichild), iconWidthPx, iconHeightPx);
					addItem(myTreeItem);
				}
			}
			inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FFFFFF\">* </font>" + textTree.getInlineHtmlString(iconWidthPx, iconHeightPx, true));
			beenExpanded = true;
			setState(true);
		}
	}
	class TreeSelectionHandler implements SelectionHandler<TreeItem>
	{
		@Override
		public void onSelection(SelectionEvent<TreeItem> event) 
		{
			MyTreeItem myTreeItem = (MyTreeItem) event.getSelectedItem();
			myTreeItem.expand();
		}
	}

}
