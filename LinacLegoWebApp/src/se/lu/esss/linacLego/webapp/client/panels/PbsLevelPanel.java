package se.lu.esss.linacLego.webapp.client.panels;

import java.util.ArrayList;

import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutScrollPanel;
import se.lu.esss.linacLego.webapp.shared.HtmlTextTree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PbsLevelPanel extends VerticalPanel
{
	HtmlTextTree textTree;
	MyTabLayoutScrollPanel myTabLayoutScrollPanel;
	ArrayList<PbsLevelPanel> childrenPbsLevelPanelList = new ArrayList<PbsLevelPanel>();
	HorizontalPanel childrenPanel;
	boolean expanded = false;
	Grid dataPanel;
	boolean dataPanelExpanded = false;
	HorizontalPanel tagAndButtonWrapperPanel;
	Button expandCollapseButton = new Button("+");
	HorizontalPanel arrowLine1Panel;
	HorizontalPanel arrowLine2Panel;
	FocusPanel focusPanel;
	VerticalPanel mainWrapperPanel;
	Image arrowLine1Image;
	boolean odd;
	int iconWidth = 50;
	int iconHeight = 50;
	int arrowHeight = 16;
	int arrowLine1Width = 64;
	int preExpansionWidth = -1;
	int oldWidth = 0;
	int ilevel = -1;
	PbsLevelPanel parentPbsLevelPanel;
	PbsLevelPanelTimer pbsLevelPanelTimer;
	
	public PbsLevelPanel(int ilevel, HtmlTextTree textTree, boolean odd, PbsLevelPanel parentPbsLevelPanel, MyTabLayoutScrollPanel myTabLayoutScrollPanel)
	{
		super();
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.textTree = textTree;
		this.odd = odd;
		this.parentPbsLevelPanel = parentPbsLevelPanel;
		this.myTabLayoutScrollPanel = myTabLayoutScrollPanel;
		this.ilevel = ilevel;
		mainWrapperPanel = new VerticalPanel();
		childrenPbsLevelPanelList = new ArrayList<PbsLevelPanel>();

		mainWrapperPanel.add(setupElementPanel());
		focusPanel = new FocusPanel();
		focusPanel.add(mainWrapperPanel);
		add(focusPanel);
		focusPanel.setFocus(false);

		pbsLevelPanelTimer = new PbsLevelPanelTimer(this);
		pbsLevelPanelTimer.scheduleRepeating(100);

	}
	private void addChildrenPanels()
	{
		childrenPanel = new HorizontalPanel();
		if (textTree.hasChildren())
		{
			boolean oddChild = true;
			for (int ichild = 0; ichild < textTree.getTextTreeArrayList().size(); ++ichild)
			{
				PbsLevelPanel childPanel = new PbsLevelPanel(ilevel + 1, textTree.getTextTreeArrayList().get(ichild), oddChild, this, myTabLayoutScrollPanel);
				childrenPbsLevelPanelList.add(childPanel);
				oddChild = !oddChild;
				childrenPanel.add(childPanel);
			}
			mainWrapperPanel.add(childrenPanel);
		}
	}
	private void expandAllIconPanel()
	{
		if (getOffsetWidth() < 1) return;
		arrowLine1Width = (getOffsetWidth() - iconWidth - arrowHeight) / 2;
		if (arrowLine1Width < 1) return;
		int arrowLine2Width = getOffsetWidth() - arrowLine1Width - iconWidth - arrowHeight;
		if (arrowLine2Width < 1) return;
		arrowLine1Panel.setWidth(arrowLine1Width + "px");
		arrowLine2Panel.setWidth(arrowLine2Width + "px");
		arrowLine1Image.setWidth(arrowLine1Width + "px");
	}
	private void removeChildren()
	{
		if (childrenPbsLevelPanelList.size() > 0)
		{
			for (int ichild = 0; ichild < childrenPbsLevelPanelList.size(); ++ichild)
			{
				childrenPbsLevelPanelList.get(ichild).pbsLevelPanelTimer.cancel();
				childrenPbsLevelPanelList.get(ichild).removeChildren();
				childrenPanel.remove(childrenPbsLevelPanelList.get(ichild));
			}
		}
		childrenPbsLevelPanelList.clear();
	}
	private void collapseAllIconPanel()
	{
		arrowLine1Panel.setWidth(1 + "px");
		arrowLine2Panel.setWidth(1 + "px");
		arrowLine1Image.setWidth(1 + "px");
		if (parentPbsLevelPanel != null ) parentPbsLevelPanel.collapseAllIconPanel();
	}
	private VerticalPanel setupElementPanel()
	{
		HorizontalPanel expandCollapseButtonPanel = new HorizontalPanel();
		expandCollapseButtonPanel.add(expandCollapseButton);
		
		arrowLine1Image  = new Image("images/blueLine.png");
		arrowLine1Image.setHeight(arrowHeight + "px");
		arrowLine1Image.setWidth(arrowLine1Width + "px");
		arrowLine1Panel = new HorizontalPanel();
		arrowLine1Panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		arrowLine1Panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		arrowLine1Panel.setWidth(arrowLine1Width + "px");
		arrowLine1Panel.setHeight("100%");
		arrowLine1Panel.add(arrowLine1Image);
		
		Image arrowLine2Image  = new Image("images/blueLine.png");
		arrowLine2Image.setHeight(arrowHeight + "px");
		arrowLine2Image.setWidth("100%");
		arrowLine2Panel = new HorizontalPanel();
		arrowLine2Panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		arrowLine2Panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		arrowLine2Panel.setWidth("100%");
		arrowLine2Panel.setHeight("100%");
		arrowLine2Panel.add(arrowLine2Image);
		
		Image iconImage = new Image(textTree.getIconImageLocation());
		iconImage.setSize(iconWidth + "px", iconHeight + "px");
		Image arrowHeadImage = new Image("images/blueArrowHead.png");
		arrowHeadImage.setSize(arrowHeight + "px", arrowHeight + "px");

		HorizontalPanel iconAndArrowHeadPanel = new HorizontalPanel();
		iconAndArrowHeadPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		iconAndArrowHeadPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		iconAndArrowHeadPanel.setWidth(iconWidth + arrowHeight + "px");
		iconAndArrowHeadPanel.add(arrowHeadImage);
		iconAndArrowHeadPanel.add(iconImage);
		
		HorizontalPanel allIconPanel = new HorizontalPanel();
		allIconPanel.setWidth("100%");
		allIconPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LOCALE_START);
		allIconPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		allIconPanel.add(arrowLine1Panel);
		allIconPanel.add(iconAndArrowHeadPanel);
		allIconPanel.add(arrowLine2Panel);

		HorizontalPanel tagAndButtonPanel = new HorizontalPanel();
		tagAndButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		tagAndButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		tagAndButtonPanel.add(expandCollapseButtonPanel);
		tagAndButtonPanel.add(new InlineHTML("<html>" + textTree.getTag() + "</html>"));
		tagAndButtonWrapperPanel = new HorizontalPanel();
		tagAndButtonWrapperPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		tagAndButtonWrapperPanel.setWidth("100%");
		tagAndButtonWrapperPanel.add(tagAndButtonPanel);
		if (odd) tagAndButtonWrapperPanel.setStyleName("pbsElementOdd" + ilevel);
		if (!odd) tagAndButtonWrapperPanel.setStyleName("pbsElementEven" + ilevel);

		VerticalPanel iconAndTag = new VerticalPanel();
		iconAndTag.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		iconAndTag.setWidth("100%");
		iconAndTag.add(allIconPanel);
		iconAndTag.add(tagAndButtonWrapperPanel);

		VerticalPanel wrapperPanel = new VerticalPanel();
		wrapperPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		wrapperPanel.add(iconAndTag);
		if (textTree.hasChildren())
		{
			expandCollapseButton.addClickHandler(new ExpandCollapseButtonClickHandler(this));
			expandCollapseButton.setVisible(true);
		}
		else
		{
			expandCollapseButton.setVisible(false);
		}
		
		Grid attributePanel = new Grid(textTree.numAttributes(), 1);
		 
		for (int ia = 0; ia < textTree.numAttributes(); ++ia)
		{
			attributePanel.setWidget(ia, 0, new InlineHTML(textTree.getAttribute(ia).getInlineHtmlString(false, false)));
		}
		wrapperPanel.add(attributePanel);
		if (textTree.hasDataFolder())
		{
			Image dataIcon = new Image(textTree.getDataFolder().getIconImageLocation());
			dataIcon.setSize("32px", "32px");
			wrapperPanel.add(dataIcon);
			dataPanel = new Grid(textTree.getDataFolder().numChildren(), 1);
			for (int ia = 0; ia < textTree.getDataFolder().numChildren(); ++ia)
			{
				String html = textTree.getDataFolder().getTextTreeArrayList().get(ia).getInlineHtmlString(false, false);
				dataPanel.setWidget(ia, 0, new InlineHTML(html));
			}
			wrapperPanel.add(dataPanel);
			dataIcon.addClickHandler(new DataFolderClickHandler(this));
			dataPanel.setVisible(false);
			dataPanelExpanded = false;
		}
		return wrapperPanel;
	}
	class ExpandCollapseButtonClickHandler implements ClickHandler
	{
		PbsLevelPanel parentPbsLevelPanel;
		
		ExpandCollapseButtonClickHandler(PbsLevelPanel parentPbsLevelPanel)
		{
			this.parentPbsLevelPanel = parentPbsLevelPanel;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			if (!parentPbsLevelPanel.expanded)
			{
				if (parentPbsLevelPanel.preExpansionWidth < 0) parentPbsLevelPanel.preExpansionWidth = getOffsetWidth();
				myTabLayoutScrollPanel.setHorizontalScrollPosition(0);
				myTabLayoutScrollPanel.setVerticalScrollPosition(0);
				expandCollapseButton.setVisible(false);
				expandCollapseButton.setText("-");
				parentPbsLevelPanel.addChildrenPanels();
				parentPbsLevelPanel.expanded = true;
				expandCollapseButton.setVisible(true);
				expandAllIconPanel();
				int hscrollPos = getAbsoluteLeft();
				hscrollPos = hscrollPos + (parentPbsLevelPanel.getOffsetWidth() - myTabLayoutScrollPanel.getPanelWidth()) / 2;
				if (hscrollPos < 0) hscrollPos = 0;
				myTabLayoutScrollPanel.setHorizontalScrollPosition(hscrollPos);
				myTabLayoutScrollPanel.setVerticalScrollPosition(myTabLayoutScrollPanel.getMaximumVerticalScrollPosition());
				parentPbsLevelPanel.focusPanel.setFocus(true);
			}
			else
			{
				myTabLayoutScrollPanel.setHorizontalScrollPosition(0);
				myTabLayoutScrollPanel.setVerticalScrollPosition(0);
				expandCollapseButton.setVisible(false);
				expandCollapseButton.setText("+");
				collapseAllIconPanel();
				parentPbsLevelPanel.removeChildren();
				parentPbsLevelPanel.expanded = false;
				expandCollapseButton.setVisible(true);
				int hscrollPos = getAbsoluteLeft();
				hscrollPos = hscrollPos + (parentPbsLevelPanel.getOffsetWidth() - myTabLayoutScrollPanel.getPanelWidth()) / 2;
				if (hscrollPos < 0) hscrollPos = 0;
				myTabLayoutScrollPanel.setHorizontalScrollPosition(hscrollPos);
				myTabLayoutScrollPanel.setVerticalScrollPosition(myTabLayoutScrollPanel.getMaximumVerticalScrollPosition());
				parentPbsLevelPanel.focusPanel.setFocus(true);
			}
		}
	}
	class DataFolderClickHandler implements ClickHandler
	{
		PbsLevelPanel parentPbsLevelPanel;
		
		DataFolderClickHandler(PbsLevelPanel parentPbsLevelPanel)
		{
			this.parentPbsLevelPanel = parentPbsLevelPanel;
			
		}

		@Override
		public void onClick(ClickEvent event) 
		{
			if (!parentPbsLevelPanel.dataPanelExpanded)
			{
				dataPanel.setVisible(true);
				dataPanelExpanded = true;
			}
			else
			{
				dataPanel.setVisible(false);
				parentPbsLevelPanel.dataPanelExpanded = false;
//				collapseAllIconPanel();
			}
		}
		
	}
	class PbsLevelPanelTimer extends Timer
	{
		PbsLevelPanel parentPbsLevelPanel;
		PbsLevelPanelTimer(PbsLevelPanel parentPbsLevelPanel)
		{
			this.parentPbsLevelPanel = parentPbsLevelPanel;
		}
		@Override
		public void run() 
		{
			if (parentPbsLevelPanel.getOffsetWidth() != parentPbsLevelPanel.oldWidth)
			{
				parentPbsLevelPanel.expandAllIconPanel();
				parentPbsLevelPanel.oldWidth = getOffsetWidth(); 
			}
			
		}
		
	}

}
