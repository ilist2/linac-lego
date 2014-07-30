package se.lu.esss.linacLego.webapp.client.panels;

import se.lu.esss.linacLego.webapp.client.LinacLegoWebApp;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutPanel;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutScrollPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoPanel extends MyTabLayoutScrollPanel 
{
	private VerticalPanel messagePanel;
	private Image messageImage;
	private Label messageLabel;
	CaptionPanel latticeVersionCaptionPanel;
	CaptionPanel linkButtonCaptionPanel;
	InlineHTML latticeVersionInlineHTML;
	
	DownLoadClickHandler downloadXmlClickHandler;
	DownLoadClickHandler helpClickHandler;
	DownLoadClickHandler linacLegoAppClickHandler;
	DownLoadClickHandler sourceWebFolderClickHandler;
	
	TextBox sourceLinkTextBox;
	LinacLegoWebApp linacLegoWebApp;
	boolean changeSourceLinkButtonState = true;
	Button changeSourceLinkButton;
	
	public VerticalPanel getMessagePanel() {return messagePanel;}
	public Image getMessageImage() {return messageImage;}
	public Label getMessageLabel() {return messageLabel;}
	public CaptionPanel getLatticeVersionCaptionPanel() {return latticeVersionCaptionPanel;}
	public InlineHTML getLatticeVersionInlineHTML() {return latticeVersionInlineHTML;}
	public CaptionPanel getLinkButtonCaptionPanel() {return linkButtonCaptionPanel;}
	
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
		
		linkButtonCaptionPanel = new CaptionPanel("Source Link");
		linkButtonCaptionPanel.setVisible(false);
		HorizontalPanel sourceLinkHp = new HorizontalPanel();
		changeSourceLinkButton = new Button("Change");
		changeSourceLinkButton.addClickHandler(new ChangeButtonClickHandler(this));
		sourceLinkHp.add(changeSourceLinkButton);
		linkButtonCaptionPanel.add(sourceLinkHp);
		sourceLinkTextBox = new TextBox();
		sourceLinkTextBox.setWidth("400px");
		sourceLinkTextBox.setText(myTabLayoutPanel.getLinacLegoWebApp().getLinacLegoLink());
		sourceLinkHp.add(sourceLinkTextBox);
		sourceLinkHp.setCellVerticalAlignment(sourceLinkTextBox, HasVerticalAlignment.ALIGN_MIDDLE);
		sourceLinkHp.setCellVerticalAlignment(changeSourceLinkButton, HasVerticalAlignment.ALIGN_MIDDLE);
		
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(versionCaptionPanel);
		vp1.add(programmerCaptionPanel);
		vp1.add(downloadsCaptionPanel);
		HorizontalPanel hp1 = new HorizontalPanel();

		VerticalPanel vp2 = new VerticalPanel();
		vp2.add(messagePanel);
		vp2.add(latticeVersionCaptionPanel);
		vp2.add(linkButtonCaptionPanel);
		hp1.add(vp1);
		hp1.add(vp2);
		add(hp1);
		
	}
	public void setLinks(String downloadXmlLink, String helpLink, String linacLegoAppLink, String linacLegoLink)
	{
		downloadXmlClickHandler.setLink(downloadXmlLink);
		helpClickHandler.setLink(helpLink);
		linacLegoAppClickHandler.setLink(linacLegoAppLink);
		sourceLinkTextBox.setText(linacLegoWebApp.getLinacLegoLink());
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
	static class ChangeButtonClickHandler implements ClickHandler
	{
		InfoPanel infoPanel;
		ChangeButtonClickHandler(InfoPanel infoPanel)
		{
			this.infoPanel = infoPanel;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			if (infoPanel.changeSourceLinkButtonState)
			{
				infoPanel.sourceLinkTextBox.getText();
				infoPanel.changeSourceLinkButton.setText("Revert");
				infoPanel.changeSourceLinkButtonState = false;
				infoPanel.linacLegoWebApp.setLinks(infoPanel.sourceLinkTextBox.getText());
				infoPanel.linacLegoWebApp.loadDataPanels();
			}
			else
			{
				infoPanel.sourceLinkTextBox.setText(infoPanel.linacLegoWebApp.linacLegoDefaultLink);
				infoPanel.changeSourceLinkButton.setText("Change");
				infoPanel.changeSourceLinkButtonState = true;
				infoPanel.linacLegoWebApp.setLinks(infoPanel.linacLegoWebApp.linacLegoDefaultLink);
				infoPanel.linacLegoWebApp.loadDataPanels();
			}
			
		}
		
	}
}
