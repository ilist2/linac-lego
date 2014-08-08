package se.lu.esss.linacLego.webapp.client;

import se.lu.esss.linacLego.webapp.client.panels.CsvFilePanel;
import se.lu.esss.linacLego.webapp.client.panels.InfoPanel;
import se.lu.esss.linacLego.webapp.client.panels.PbsLayoutPanel;
import se.lu.esss.linacLego.webapp.client.panels.TreeViewPanel;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutPanel;
import se.lu.esss.linacLego.webapp.shared.CsvFile;
import se.lu.esss.linacLego.webapp.shared.HtmlTextTree;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LinacLegoWebApp implements EntryPoint 
{
	String version = "v1.2";
	String versionDate = "August 8, 2014";

	private int statusTextAreaHeight = 150;
	private int myTabLayoutPanelHeightBarHeightPx = 30;
	private int logoPanelWidth = 200;

	public LinacLegoServiceAsync getLinacLegoService() {return linacLegoService;}
	public String getVersion() {return version;}
	public String getVersionDate() {return versionDate;}

	private final LinacLegoServiceAsync linacLegoService = GWT.create(LinacLegoService.class);
	public final String linacLegoMasterLink      = "https://86507de6de3a79731230d3352847d54aa02f4388.googledrive.com/host/0B3Hieedgs_7FVFVxY3lZdmd0bkU";
	public final String linacLegoDevelopmentLink = "https://298ed9597c2db31e14ecef5e65fbb78dcae48463.googledrive.com/host/0B3Hieedgs_7FYUdpNk5kTVlNdlk";
	public final String linacLegoPrevVersionLink = "https://2ea9579f750913284dcd2386dd1cbd606872f73f.googledrive.com/host/0B3Hieedgs_7Fam1KRE9uekFjYlE";
	private String linacLegoLink;
	private String linacLegoXmlLink;

	private StatusTextArea statusTextArea;
	MyTabLayoutPanel myTabLayoutPanel;
	InfoPanel infoPanel;
	TreeViewPanel pbsViewPanel;
	TreeViewPanel xmlViewPanel;
	CsvFilePanel linacDataPanel;
	CsvFilePanel cellPartCountCsvFilePanel;
	CsvFilePanel slotPartCountCsvFilePanel;
	CsvFilePanel blePartCountCsvFilePanel;
	CsvFilePanel cnptPartCountCsvFilePanel;
	PbsLayoutPanel pbsLayoutPanel;
	
	public StatusTextArea getStatusTextArea() {return statusTextArea;}
	public int getMyTabLayoutPanelHeightBarHeightPx() {return myTabLayoutPanelHeightBarHeightPx;}
	public String getLinacLegoLink() {return linacLegoLink;}

	public void setLinacLegoLink(String linacLegoLink) {this.linacLegoLink = linacLegoLink;}

	public void onModuleLoad() 
	{
		statusTextArea = new StatusTextArea(Window.getClientWidth() - 10, statusTextAreaHeight);
	    statusTextArea.setMaxBufferSize(100);
	    statusTextArea.addStatus("Welcome!");
	    statusTextArea.addStatus("Getting data from server..");
		myTabLayoutPanel = new MyTabLayoutPanel(myTabLayoutPanelHeightBarHeightPx, this, myTabLayoutPanelWidth(), myTabLayoutPanelHeight());
		infoPanel  = new InfoPanel(myTabLayoutPanel);
		pbsViewPanel = new TreeViewPanel(myTabLayoutPanel, "PBS Tree", "PBS");
		pbsLayoutPanel = new PbsLayoutPanel(myTabLayoutPanel, "PBS Layout", "PBS");
		xmlViewPanel = new TreeViewPanel(myTabLayoutPanel, "XML Tree", "XML");
		linacDataPanel = new CsvFilePanel(myTabLayoutPanel, "Linac data", "Linac data", 2);
		cellPartCountCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Cell Parts", "Cell Parts", 1);
		slotPartCountCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Slot Parts", "Slot Parts", 1);
		blePartCountCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Beam-line Parts", "Beam-line Parts", 1);
		cnptPartCountCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Control Pt. Parts", "Control Pt.  Parts", 1);

		HorizontalPanel hp1 = new HorizontalPanel();
		VerticalPanel logoPanel = new VerticalPanel();
		logoPanel.setWidth(logoPanelWidth + "px");
		Image image = new Image("images/essLogo.png");
		logoPanel.add(image);
	    Label titleLabel = new Label("LinacLego Viewer");
	    titleLabel.setStyleName("titleLabel");
	    logoPanel.add(titleLabel);
	    
		hp1.add(logoPanel);
		hp1.add(statusTextArea);
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(myTabLayoutPanel);
	    vp1.add(hp1);
		RootLayoutPanel.get().add(vp1);
		
		Window.addResizeHandler(new MyResizeHandler());
		setLinks(linacLegoMasterLink);
		loadDataPanels();
	}
	public void setLinks(String linacLegoLink)
	{
		this.linacLegoLink = linacLegoLink;
		String helpLink = linacLegoMasterLink + "/doc/LinacLegoManual.pdf";
		String linacLegoAppLink = linacLegoMasterLink + "/dist/LinacLegoApp.jar";

		String linacLegoWebSitePartsDirectoryLink = linacLegoLink + "/public/linacLegoOutput";
		String downloadXmlLink = linacLegoLink + "/public/linacLego.zip";

		linacLegoXmlLink = linacLegoLink + "/public/linacLego.xml";
		linacDataPanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoData.csv");
		cellPartCountCsvFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoCellParts.csv");
		slotPartCountCsvFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoSlotParts.csv");
		blePartCountCsvFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoBleParts.csv");
		cnptPartCountCsvFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoCnptParts.csv");
		infoPanel.setLinks(downloadXmlLink, helpLink, linacLegoAppLink, linacLegoLink);
	}
	public void loadDataPanels()
	{
		infoPanel.getMessageImage().setUrl("/images/Scarecrow.jpg");
		infoPanel.getMessageLabel().setText("Loading data from the server...");
		infoPanel.getMessagePanel().setVisible(true);
		infoPanel.getLinkButtonCaptionPanel().setVisible(false);
		infoPanel.getLatticeVersionCaptionPanel().setVisible(false);
		getLinacLegoService().getXmlViewHtmlTextTree(linacLegoXmlLink, new LoadXmlViewPanelsCallback(this));
		getLinacLegoService().getPbsViewHtmlTextTree(linacLegoXmlLink, new LoadPbsViewPanelsCallback(this));	
		getLinacLegoService().getCsvFile(linacDataPanel.getSourceFileLink(), new LoadCsvFileCallback(this, linacDataPanel));
		getLinacLegoService().getCsvFile(cellPartCountCsvFilePanel.getSourceFileLink(), new LoadCsvFileCallback(this, cellPartCountCsvFilePanel));
		getLinacLegoService().getCsvFile(slotPartCountCsvFilePanel.getSourceFileLink(), new LoadCsvFileCallback(this, slotPartCountCsvFilePanel));
		getLinacLegoService().getCsvFile(blePartCountCsvFilePanel.getSourceFileLink(), new LoadCsvFileCallback(this, blePartCountCsvFilePanel));
		getLinacLegoService().getCsvFile(cnptPartCountCsvFilePanel.getSourceFileLink(), new LoadCsvFileCallback(this, cnptPartCountCsvFilePanel));
	}
	public int myTabLayoutPanelWidth()
	{
		return Window.getClientWidth() + 10 - 15;
	}
	public int myTabLayoutPanelHeight()
	{
		return Window.getClientHeight() - statusTextAreaHeight - 15;
	}
	public class MyResizeHandler implements ResizeHandler
	{
		@Override
		public void onResize(ResizeEvent event) 
		{
			statusTextArea.setSize(Window.getClientWidth() - 10 - logoPanelWidth, statusTextAreaHeight);
			myTabLayoutPanel.setSize(myTabLayoutPanelWidth(), myTabLayoutPanelHeight());			
		}
	}
	public static class LoadXmlViewPanelsCallback implements AsyncCallback<HtmlTextTree>
	{
		LinacLegoWebApp linacLegoWebApp;
		LoadXmlViewPanelsCallback(LinacLegoWebApp linacLegoWebApp)
		{
			this.linacLegoWebApp = linacLegoWebApp;
		}
		@Override
		public void onFailure(Throwable caught) 
		{			
			linacLegoWebApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			linacLegoWebApp.infoPanel.getMessageImage().setUrl("/images/dagnabbit.jpg");
			linacLegoWebApp.infoPanel.getMessageLabel().setText("Failed to load Xml View data from server.");
			linacLegoWebApp.infoPanel.getMessagePanel().setVisible(true);
			linacLegoWebApp.infoPanel.getLinkButtonCaptionPanel().setVisible(true);
			linacLegoWebApp.infoPanel.setCurrentSource(linacLegoWebApp.infoPanel.getPreviousSource());
			linacLegoWebApp.infoPanel.getSourceViewLabel().setText("Currently viewing " + linacLegoWebApp.infoPanel.getCurrentSource() + " Source");
		}
		@Override
		public void onSuccess(HtmlTextTree result) 
		{
			linacLegoWebApp.infoPanel.getLatticeVersionInlineHTML().setHTML(result.getInlineHtmlString(false, true));
			linacLegoWebApp.infoPanel.getLatticeVersionCaptionPanel().setVisible(true);
			linacLegoWebApp.xmlViewPanel.addTree(result);
			linacLegoWebApp.infoPanel.getMessagePanel().setVisible(false);
			linacLegoWebApp.infoPanel.getLinkButtonCaptionPanel().setVisible(true);
			linacLegoWebApp.infoPanel.getSourceViewLabel().setText("Currently viewing " + linacLegoWebApp.infoPanel.getCurrentSource() + " Source");
		}
	}
	public static class LoadPbsViewPanelsCallback implements AsyncCallback<HtmlTextTree>
	{
		LinacLegoWebApp linacLegoWebApp;
		LoadPbsViewPanelsCallback(LinacLegoWebApp linacLegoWebApp)
		{
			this.linacLegoWebApp = linacLegoWebApp;
		}
		@Override
		public void onFailure(Throwable caught) 
		{			
			linacLegoWebApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			linacLegoWebApp.infoPanel.getMessageImage().setUrl("/images/dagnabbit.jpg");
			linacLegoWebApp.infoPanel.getMessageLabel().setText("Failed to load PBS View data from server.");
			linacLegoWebApp.infoPanel.getMessagePanel().setVisible(true);
			linacLegoWebApp.infoPanel.getLinkButtonCaptionPanel().setVisible(true);
		}
		@Override
		public void onSuccess(HtmlTextTree result) 
		{
			linacLegoWebApp.pbsViewPanel.addTree(result);
			linacLegoWebApp.pbsLayoutPanel.addTree(result);
		}
	}
	public static class LoadCsvFileCallback implements AsyncCallback<CsvFile>
	{
		LinacLegoWebApp linacLegoWebApp;
		CsvFilePanel csvFilePanel;
		LoadCsvFileCallback(LinacLegoWebApp linacLegoWebApp, CsvFilePanel csvFilePanel)
		{
			this.linacLegoWebApp = linacLegoWebApp;
			this.csvFilePanel = csvFilePanel;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			linacLegoWebApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			linacLegoWebApp.infoPanel.getMessageImage().setUrl("/images/dagnabbit.jpg");
			linacLegoWebApp.infoPanel.getMessageLabel().setText("Failed to load CSV data " + csvFilePanel.getCsvFileType() + " from server.");
			linacLegoWebApp.infoPanel.getMessagePanel().setVisible(true);
			linacLegoWebApp.infoPanel.getLinkButtonCaptionPanel().setVisible(true);
		}
		@Override
		public void onSuccess(CsvFile result) 
		{
			csvFilePanel.setCsvFile(result);
		}
	}

}
