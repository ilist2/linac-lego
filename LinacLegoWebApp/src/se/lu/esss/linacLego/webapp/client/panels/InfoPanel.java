package se.lu.esss.linacLego.webapp.client.panels;

import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutPanel;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutScrollPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoPanel extends MyTabLayoutScrollPanel 
{
	VerticalPanel waitPanel;
	public VerticalPanel getWaitPanel() {return waitPanel;}
	public InfoPanel(MyTabLayoutPanel myTabLayoutPanel)
	{
		super(myTabLayoutPanel);
		myTabLayoutPanel.add(this, "Info");
		
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

		Anchor downloadXmlAnchor = new Anchor("Download XML files");
		Anchor helpXmlAnchor = new Anchor("LinacLego Manual");
		Anchor linacLegoAppAnchor = new Anchor("LinacLego Application");

		downloadXmlAnchor.addClickHandler(new DownLoadClickHandler(myTabLayoutPanel.getLinacLegoWebApp().getDownloadLink()));
		helpXmlAnchor.addClickHandler(new DownLoadClickHandler(myTabLayoutPanel.getLinacLegoWebApp().getHelpLink()));
		linacLegoAppAnchor.addClickHandler(new DownLoadClickHandler(myTabLayoutPanel.getLinacLegoWebApp().getLinacLegoAppLink()));

		downloadsVerticalPanel.add(downloadXmlAnchor);
		downloadsVerticalPanel.add(helpXmlAnchor);
		downloadsVerticalPanel.add(linacLegoAppAnchor);

		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(versionCaptionPanel);
		vp1.add(programmerCaptionPanel);
		vp1.add(downloadsCaptionPanel);
		HorizontalPanel hp1 = new HorizontalPanel();
		waitPanel = new VerticalPanel();
		Image scareCrowImage = new Image("/images/Scarecrow.jpg");
		Label waitLabel = new Label("Loading data from the server...");
		waitPanel.add(scareCrowImage);
		waitPanel.add(waitLabel);
		hp1.add(vp1);
		hp1.add(waitPanel);
		add(hp1);
		
	}
	class DownLoadClickHandler implements ClickHandler
	{
		String link;
		DownLoadClickHandler(String link)
		{
			this.link = link;
		}
		@Override
		public void onClick(ClickEvent event) {
			
//			Window.open(link, "_blank", "enabled");
			Window.open(link, "_blank", "");
		}
		
	}
}
