/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package org.openepics.discs.linaclego.webapp.client.panels;

import org.openepics.discs.linaclego.webapp.client.LinacLegoWebApp;
import org.openepics.discs.linaclego.webapp.client.tablayout.MyTabLayoutPanel;
import org.openepics.discs.linaclego.webapp.client.tablayout.MyTabLayoutScrollPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoPanel extends MyTabLayoutScrollPanel 
{
	private VerticalPanel messagePanel;
	private Image messageImage;
	private Label messageLabel;
	private String currentSource = "Master";
	private String previousSource = "Master";
	private Label sourceViewLabel = new Label("Loading Master Source...");
	CaptionPanel latticeVersionCaptionPanel;
	CaptionPanel ReloadSourceCaptionPanel;
	InlineHTML latticeVersionInlineHTML;
	
	DownLoadClickHandler downloadXmlClickHandler;
	DownLoadClickHandler helpClickHandler;
	DownLoadClickHandler linacLegoAppClickHandler;
	DownLoadClickHandler sourceWebFolderClickHandler;
	
	LinacLegoWebApp linacLegoWebApp;
	Button masterSourceSelectButton;
	Button developmentSourceSelectButton;
	Button prevSourceSelectButton;
	
	public VerticalPanel getMessagePanel() {return messagePanel;}
	public Image getMessageImage() {return messageImage;}
	public Label getMessageLabel() {return messageLabel;}
	public CaptionPanel getLatticeVersionCaptionPanel() {return latticeVersionCaptionPanel;}
	public InlineHTML getLatticeVersionInlineHTML() {return latticeVersionInlineHTML;}
	public CaptionPanel getLinkButtonCaptionPanel() {return ReloadSourceCaptionPanel;}
	public String getCurrentSource() {return currentSource;}
	public String getPreviousSource() {return previousSource;}
	public Label getSourceViewLabel() {return sourceViewLabel;}
	
	public void setCurrentSource(String currentSource) {this.currentSource = currentSource;}

	public InfoPanel(MyTabLayoutPanel myTabLayoutPanel)
	{
		super(myTabLayoutPanel);
		myTabLayoutPanel.add(this, "Info");
		linacLegoWebApp = myTabLayoutPanel.getLinacLegoWebApp();
		
		CaptionPanel versionCaptionPanel = new CaptionPanel("ESS LinacLego Viewer " + myTabLayoutPanel.getLinacLegoWebApp().getVersion());
		VerticalPanel versionVerticalPanel = new VerticalPanel();
		versionCaptionPanel.setContentWidget(versionVerticalPanel);
		versionVerticalPanel.add(new Label("Last Updated " + myTabLayoutPanel.getLinacLegoWebApp().getVersionDate()));

		CaptionPanel programmerCaptionPanel = new CaptionPanel("Maintained by");
		VerticalPanel programmerVerticalPanel = new VerticalPanel();
		programmerCaptionPanel.setContentWidget(programmerVerticalPanel);
		programmerVerticalPanel.add(new Label("Dave McGinnis"));
		programmerVerticalPanel.add(new Label("email: david.mcginnis@esss.se"));

		CaptionPanel downloadsCaptionPanel = new CaptionPanel("Downloads");
		VerticalPanel downloadsVerticalPanel = new VerticalPanel();
		downloadsCaptionPanel.setContentWidget(downloadsVerticalPanel);

		latticeVersionInlineHTML = new InlineHTML();
		latticeVersionCaptionPanel = new CaptionPanel("Lattice Version");
		latticeVersionCaptionPanel.setContentWidget(latticeVersionInlineHTML);
		latticeVersionCaptionPanel.setVisible(false);

		Anchor downloadXmlAnchor = new Anchor("Download XML files");
		Anchor helpAnchor = new Anchor("LinacLego Manual");
		Anchor linacLegoAppAnchor = new Anchor("LinacLego Application");
		Anchor sourceWebFolderAnchor = new Anchor("Source Web Folder");

		downloadXmlClickHandler = new DownLoadClickHandler();
		helpClickHandler = new DownLoadClickHandler();
		linacLegoAppClickHandler = new DownLoadClickHandler();
		sourceWebFolderClickHandler = new DownLoadClickHandler();

		downloadXmlAnchor.addClickHandler(downloadXmlClickHandler);
		helpAnchor.addClickHandler(helpClickHandler);
		linacLegoAppAnchor.addClickHandler(linacLegoAppClickHandler);
		sourceWebFolderAnchor.addClickHandler(sourceWebFolderClickHandler);

		downloadsVerticalPanel.add(downloadXmlAnchor);
		downloadsVerticalPanel.add(helpAnchor);
		downloadsVerticalPanel.add(linacLegoAppAnchor);
		downloadsVerticalPanel.add(sourceWebFolderAnchor);

		messagePanel = new VerticalPanel();
		messageImage = new Image("/images/Scarecrow.jpg");
		messageLabel = new Label("Loading data from the server...");
		messagePanel.add(messageImage);
		messagePanel.add(messageLabel);
		
		masterSourceSelectButton = new Button("Master");
		masterSourceSelectButton.addClickHandler(new SourceButtonClickHandler(this, "Master"));

		developmentSourceSelectButton = new Button("Development");
		developmentSourceSelectButton.addClickHandler(new SourceButtonClickHandler(this, "Development"));

		prevSourceSelectButton = new Button("Previous");
		prevSourceSelectButton.addClickHandler(new SourceButtonClickHandler(this, "Previous"));

		Grid reloadSourceButtonGrid = new Grid(1, 3);
		reloadSourceButtonGrid.setWidth("100%");

		reloadSourceButtonGrid.setWidget(0, 0, masterSourceSelectButton);
		reloadSourceButtonGrid.setWidget(0, 1, developmentSourceSelectButton);
		reloadSourceButtonGrid.setWidget(0, 2, prevSourceSelectButton);
		reloadSourceButtonGrid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		reloadSourceButtonGrid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		reloadSourceButtonGrid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);

		ReloadSourceCaptionPanel = new CaptionPanel("Reload Source");
		ReloadSourceCaptionPanel.setVisible(false);
		ReloadSourceCaptionPanel.add(reloadSourceButtonGrid);

		CaptionPanel sourceTypeCaptionPanel = new CaptionPanel("Current Source");
		sourceTypeCaptionPanel.add(sourceViewLabel);
		
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(versionCaptionPanel);
		vp1.add(programmerCaptionPanel);
		vp1.add(downloadsCaptionPanel);
		HorizontalPanel hp1 = new HorizontalPanel();

		VerticalPanel vp2 = new VerticalPanel();
		vp2.add(messagePanel);
		vp2.add(latticeVersionCaptionPanel);
		vp2.add(ReloadSourceCaptionPanel);
		vp2.add(sourceTypeCaptionPanel);
		hp1.add(vp1);
		hp1.add(vp2);
		add(hp1);
		
	}
	public void setLinks(String downloadXmlLink, String helpLink, String linacLegoAppLink, String linacLegoLink)
	{
		downloadXmlClickHandler.setLink(downloadXmlLink);
		helpClickHandler.setLink(helpLink);
		linacLegoAppClickHandler.setLink(linacLegoAppLink);
		sourceWebFolderClickHandler.setLink(linacLegoLink);
	}
	static class DownLoadClickHandler implements ClickHandler
	{
		private String link = "";

		public String getLink() {return link;}
		public void setLink(String link) {this.link = link;}

		DownLoadClickHandler()
		{
		}
		@Override
		public void onClick(ClickEvent event) {
			
//			Window.open(link, "_blank", "enabled");
			Window.open(link, "_blank", "");
		}
		
	}
	static class SourceButtonClickHandler implements ClickHandler
	{
		InfoPanel infoPanel;
		String sourceType;
		SourceButtonClickHandler(InfoPanel infoPanel, String sourceType)
		{
			this.infoPanel = infoPanel;
			this.sourceType = sourceType;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			infoPanel.previousSource = infoPanel.currentSource;
			infoPanel.currentSource = sourceType;
			infoPanel.getSourceViewLabel().setText("Loading " + sourceType + " source...");
			if (sourceType.equals("Master"))
			{
				infoPanel.linacLegoWebApp.getStatusTextArea().addStatus("Reloading Master Source...");
				infoPanel.linacLegoWebApp.setLinks(infoPanel.linacLegoWebApp.linacLegoMasterLink);
				infoPanel.linacLegoWebApp.loadDataPanels();
			}
			if (sourceType.equals("Development"))
			{
				infoPanel.previousSource = infoPanel.currentSource;
				infoPanel.linacLegoWebApp.getStatusTextArea().addStatus("Reloading Development Source...");
				infoPanel.linacLegoWebApp.setLinks(infoPanel.linacLegoWebApp.linacLegoDevelopmentLink);
				infoPanel.linacLegoWebApp.loadDataPanels();
			}
			if (sourceType.equals("Previous"))
			{
				infoPanel.previousSource = infoPanel.currentSource;
				infoPanel.linacLegoWebApp.getStatusTextArea().addStatus("Reloading Previous Version Source...");
				infoPanel.linacLegoWebApp.setLinks(infoPanel.linacLegoWebApp.linacLegoPrevVersionLink);
				infoPanel.linacLegoWebApp.loadDataPanels();
			}
		}
		
	}
}
