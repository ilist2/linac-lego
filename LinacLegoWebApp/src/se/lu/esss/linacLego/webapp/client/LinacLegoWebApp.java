package se.lu.esss.linacLego.webapp.client;

import se.lu.esss.linacLego.webapp.client.panels.CsvFilePanel;
import se.lu.esss.linacLego.webapp.client.panels.InfoPanel;
import se.lu.esss.linacLego.webapp.client.panels.PbsLayoutPanel;
import se.lu.esss.linacLego.webapp.client.panels.TreeViewPanel;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutPanel;
import se.lu.esss.linacLego.webapp.shared.LinacLegoViewSerializer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
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
	String version = "v1";
	String versionDate = "July 28, 2014";
	String downloadLink = "/linacLegoFiles/downloads/linacLegoXmlFiles.zip";
	String helpLink = "/linacLegoFiles/downloads/LinacLegoManual.pdf";
	String linacLegoAppLink = "/linacLegoFiles/downloads/LinacLegoApp.jar";

	private int statusTextAreaHeight = 150;
	private int myTabLayoutPanelHeightBarHeightPx = 30;
	private int logoPanelWidth = 200;
	private LinacLegoViewSerializer linacLegoViewSerializer;

	public LinacLegoViewSerializer getLinacLegoViewSerializer() {return linacLegoViewSerializer;}
	public String getDownloadLink() {return downloadLink;}
	public String getHelpLink() {return helpLink;}
	public String getLinacLegoAppLink() {return linacLegoAppLink;}

	public LinacLegoServiceAsync getLinacLegoService() {return linacLegoService;}
	public String getVersion() {return version;}
	public String getVersionDate() {return versionDate;}

	private final LinacLegoServiceAsync linacLegoService = GWT.create(LinacLegoService.class);
	private String linacLegoWebSite = "https://cba4504235597b04fef2d0b4e6294cb45a84179e.googledrive.com/host/0B3Hieedgs_7FWlpGRHBXNVA2Rmc";
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
	int iloadPanelCount = 0;
	int maxloadPanelCount = 7;
	
	public StatusTextArea getStatusTextArea() {return statusTextArea;}
	public int getMyTabLayoutPanelHeightBarHeightPx() {return myTabLayoutPanelHeightBarHeightPx;}

	public void setLinacLegoViewSerializer(LinacLegoViewSerializer linacLegoViewSerializer) {this.linacLegoViewSerializer = linacLegoViewSerializer;}

	public void onModuleLoad() 
	{
		String linacLegoWebSitePartsDirectory = linacLegoWebSite + "/public/linacLegoOutput";
		downloadLink = linacLegoWebSite + "/public/linacLego.zip";
		helpLink = linacLegoWebSite + "/doc/LinacLegoManual.pdf";
		linacLegoAppLink = linacLegoWebSite + "/dist/LinacLegoApp.jar";
		
		statusTextArea = new StatusTextArea(Window.getClientWidth() - 10, statusTextAreaHeight);
	    statusTextArea.setMaxBufferSize(100);
	    statusTextArea.addStatus("Welcome!");
	    statusTextArea.addStatus("Getting data from server..");
		myTabLayoutPanel = new MyTabLayoutPanel(myTabLayoutPanelHeightBarHeightPx, this, myTabLayoutPanelWidth(), myTabLayoutPanelHeight());
		infoPanel  = new InfoPanel(myTabLayoutPanel);
		pbsViewPanel = new TreeViewPanel(myTabLayoutPanel, "PBS Tree", "PBS");
		pbsLayoutPanel = new PbsLayoutPanel(myTabLayoutPanel, "PBS Layout", "PBS");
		xmlViewPanel = new TreeViewPanel(myTabLayoutPanel, "XML Tree", "XML");
		linacDataPanel = new CsvFilePanel(myTabLayoutPanel, "Linac data", "Linac data", 2, linacLegoWebSitePartsDirectory + "/linacLegoData.csv");
		cellPartCountCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Cell Parts", "Cell Parts", 1, linacLegoWebSitePartsDirectory + "/linacLegoCellParts.csv");
		slotPartCountCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Slot Parts", "Slot Parts", 1, linacLegoWebSitePartsDirectory + "/linacLegoSlotParts.csv");
		blePartCountCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Beam-line Parts", "Beam-line Parts", 1, linacLegoWebSitePartsDirectory + "/linacLegoBleParts.csv");
		cnptPartCountCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Control Pt. Parts", "Control Pt.  Parts", 1, linacLegoWebSitePartsDirectory + "/linacLegoCnptParts.csv");

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
		getLinacLegoService().getLinacLegoViewSerializer(new LinacLegoViewSerializerCallback(this));
	
	}
	public boolean loadDataPanels()
	{
		if (iloadPanelCount > maxloadPanelCount)
		{
			return false;
		}
		if (iloadPanelCount == 0)   pbsViewPanel.addTree(getLinacLegoViewSerializer().getPbsViewHtmlTextTree());
		if (iloadPanelCount == 1) pbsLayoutPanel.addTree(getLinacLegoViewSerializer().getPbsViewHtmlTextTree());
		if (iloadPanelCount == 2)   xmlViewPanel.addTree(getLinacLegoViewSerializer().getXmlViewHtmlTextTree());
		if (iloadPanelCount == 3) linacDataPanel.setCsvFile(getLinacLegoViewSerializer().getLinacLegoData());
		if (iloadPanelCount == 4) cellPartCountCsvFilePanel.setCsvFile(getLinacLegoViewSerializer().getLinacLegoCellParts());
		if (iloadPanelCount == 5) slotPartCountCsvFilePanel.setCsvFile(getLinacLegoViewSerializer().getLinacLegoSlotParts());
		if (iloadPanelCount == 6) blePartCountCsvFilePanel.setCsvFile(getLinacLegoViewSerializer().getLinacLegoBleParts());
		if (iloadPanelCount == 7) cnptPartCountCsvFilePanel.setCsvFile(getLinacLegoViewSerializer().getLinacLegoCnptParts());
		iloadPanelCount = iloadPanelCount + 1;
		if (iloadPanelCount > maxloadPanelCount)
		{
			return false;
		}
		return true;
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
	public class LinacLegoViewSerializerCallback implements AsyncCallback<LinacLegoViewSerializer>
	{
		LinacLegoWebApp linacLegoWebApp;
		LinacLegoViewSerializerCallback(LinacLegoWebApp linacLegoWebApp)
		{
			this.linacLegoWebApp = linacLegoWebApp;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			infoPanel.getMessageImage().setUrl("/images/dagnabbit.jpg");
			infoPanel.getMessageLabel().setText("Failed to get server data! Try reloading the page.");
		}
		@Override
		public void onSuccess(LinacLegoViewSerializer result) 
		{
		    statusTextArea.addStatus("Recieved data from server.");
		    infoPanel.getMessagePanel().setVisible(false);
			setLinacLegoViewSerializer(result);
			Scheduler.get().scheduleIncremental(new IncrementalPanelLoader(linacLegoWebApp));
		}

	}
	public static class IncrementalPanelLoader implements RepeatingCommand
	{
		LinacLegoWebApp linacLegoWebApp;

		public IncrementalPanelLoader(LinacLegoWebApp linacLegoWebApp) 
		{
			this.linacLegoWebApp = linacLegoWebApp;
		}

		@Override
		public boolean execute() 
		{
			boolean loadMoreDataPanels  = linacLegoWebApp.loadDataPanels();
// Returning true causes command to be executed again			
			return loadMoreDataPanels;
		}

	}

}
