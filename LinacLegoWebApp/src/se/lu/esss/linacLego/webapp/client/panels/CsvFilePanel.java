package se.lu.esss.linacLego.webapp.client.panels;

import se.lu.esss.linacLego.webapp.client.LinacLegoWebApp;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutPanel;
import se.lu.esss.linacLego.webapp.client.tablayout.MyTabLayoutScrollPanel;
import se.lu.esss.linacLego.webapp.shared.CsvFile;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Grid;

public class CsvFilePanel extends VerticalPanel
{
	private String csvFileType = "";
	private Grid dataGrid;
	private Grid headerGrid;
	private int numHeaderRows;
	private MyTabLayoutScrollPanel myTabLayoutScrollPanel;
	private int headerLineHeight = 25;
	private int maxNumDataRowsOnLoad = 50;
	private int lastDataRowLoaded = -1;
	private ScrollPanel dataGridScrollPane;
	private String sourceFileLink = "";
	private int numDataRows;
	private CsvFile csvFile;
	private boolean fileCompletelyLoaded = false;

	
	public boolean isFileCompletelyLoaded() {return fileCompletelyLoaded;}
	public int getLastDataRowLoaded() {return lastDataRowLoaded;}
	public int getNumHeaderRows() {return numHeaderRows;}
	public Grid getDataGrid() {return dataGrid;}
	public int getNumDataRows() {return numDataRows;}
	public CsvFile getCsvFile() {return csvFile;}
	public String getCsvFileType() {return csvFileType;}
	public MyTabLayoutScrollPanel getMyTabLayoutScrollPanel() {return myTabLayoutScrollPanel;}
	public int getMaxNumDataRowsOnLoad() {return maxNumDataRowsOnLoad;}
	public void setFileCompletelyLoaded(boolean fileCompletelyLoaded) {this.fileCompletelyLoaded = fileCompletelyLoaded;}

	public void setLastDataRowLoaded(int lastDataRowLoaded) {this.lastDataRowLoaded = lastDataRowLoaded;}

	public CsvFilePanel(MyTabLayoutPanel myTabLayoutPanel, String tabTitle, String csvFileType, int numHeaderRows, String sourceFileLink) 
	{
		super();
		myTabLayoutScrollPanel = new MyTabLayoutScrollPanel(myTabLayoutPanel);
		myTabLayoutPanel.add(myTabLayoutScrollPanel, tabTitle);
		myTabLayoutScrollPanel.add(this);
		
		this.csvFileType = csvFileType;
		this.numHeaderRows = numHeaderRows;
		this.sourceFileLink = sourceFileLink;
	}
	public LinacLegoWebApp getLinacLegoWebApp()
	{
		return getMyTabLayoutScrollPanel().getMyTabLayoutPanel().getLinacLegoWebApp();
	}
	public void setCsvFile(CsvFile csvFile)
	{
		this.csvFile = csvFile;
		Anchor sourceFileAnchor = new Anchor("Source File");
		sourceFileAnchor.addClickHandler(new DownLoadClickHandler(sourceFileLink));
		add(sourceFileAnchor);
		numDataRows = csvFile.numOfRows() - numHeaderRows;
		
		dataGrid = new Grid(numDataRows, csvFile.numOfCols());
		headerGrid = new Grid(numHeaderRows, csvFile.numOfCols());
		dataGridScrollPane = new ScrollPanel();
		dataGridScrollPane.add(dataGrid);
		add(headerGrid);
		add(dataGridScrollPane);

		for (int irow = 0; irow < numHeaderRows; ++irow)
		{
			for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
			{
				headerGrid.setText(irow, icol, csvFile.getLine(irow).getCell(icol));
			}
		}
		int numDataRowsOnLoad = numDataRows;
		fileCompletelyLoaded = true;
		if (numDataRowsOnLoad > getMaxNumDataRowsOnLoad())
		{
			numDataRowsOnLoad = getMaxNumDataRowsOnLoad();
			fileCompletelyLoaded = false;
			Scheduler.get().scheduleIncremental(new CsvFilePanelIncrementalExtraRowLoader(this));
		}
		for (int irow = 0; irow < numDataRowsOnLoad; ++irow)
		{
			for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
			{
				dataGrid.setText(irow, icol, csvFile.getLine(irow + numHeaderRows).getCell(icol));
			}
		}
		lastDataRowLoaded = numDataRowsOnLoad - 1;
		resizeMe(csvFile);
		Window.addResizeHandler(new MyResizeHandler(csvFile));
		for (int ih = 0; ih < numHeaderRows; ++ih)
			headerGrid.getRowFormatter().addStyleName(ih, "linacParametersHeader");
		headerGrid.setBorderWidth(0);
		headerGrid.setCellSpacing(0);
		headerGrid.setCellPadding(0);
		dataGrid.setBorderWidth(0);
		dataGrid.setCellSpacing(0);
		dataGrid.setCellPadding(0);

		if (fileCompletelyLoaded) myTabLayoutScrollPanel.getMyTabLayoutPanel().getLinacLegoWebApp().getStatusTextArea().addStatus("Finished building " + csvFileType + " Spreadsheet.");
		if (!fileCompletelyLoaded) myTabLayoutScrollPanel.getMyTabLayoutPanel().getLinacLegoWebApp().getStatusTextArea().addStatus("Still building " + csvFileType + " Spreadsheet.");
	}
	public void loadExtraRows()
	{
		if (fileCompletelyLoaded) return;
		int startRow = getLastDataRowLoaded() + 1;
		int stopRow = startRow + getMaxNumDataRowsOnLoad();
		if (stopRow > (getNumDataRows() - 1))  stopRow = getNumDataRows() - 1;
		for (int irow = startRow; irow <= stopRow; ++irow)
		{
			for ( int icol = 0; icol < getCsvFile().numOfCols(); ++icol)
			{
				getDataGrid().setText(irow, icol, getCsvFile().getLine(irow + getNumHeaderRows()).getCell(icol));
			}
		}
		setLastDataRowLoaded(stopRow);
		if (stopRow == (getNumDataRows() - 1))  fileCompletelyLoaded = true;
		if (fileCompletelyLoaded) myTabLayoutScrollPanel.getMyTabLayoutPanel().getLinacLegoWebApp().getStatusTextArea().addStatus("Finished building " + csvFileType + " Spreadsheet.");

	}
	private void resizeMe(CsvFile csvFile)
	{
		int parentWidth = myTabLayoutScrollPanel.getPanelWidth();
		headerGrid.setWidth(parentWidth - 50 + "px");
		dataGrid.setWidth(parentWidth - 50  + "px");
		dataGridScrollPane.setHeight(myTabLayoutScrollPanel.getPanelHeight() - headerLineHeight * numHeaderRows - 20 + "px");
		dataGridScrollPane.setWidth(myTabLayoutScrollPanel.getPanelWidth()   - 20 + "px");
		headerGrid.setHeight(headerLineHeight * numHeaderRows + "px");

		for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
		{
			double colPer = 100.0 * ((double) csvFile.getColWidth(icol) ) / ((double) csvFile.getTableWidth());
			headerGrid.getColumnFormatter().setWidth(icol, NumberFormat.getFormat("0.0").format(colPer) + "%");
			dataGrid.getColumnFormatter().setWidth(icol, NumberFormat.getFormat("0.0").format(colPer) + "%");
		}
	}
	public class MyResizeHandler implements ResizeHandler
	{
		private CsvFile csvFile;
		MyResizeHandler(CsvFile csvFile)
		{
			this.csvFile = csvFile;
		}
		@Override
		public void onResize(ResizeEvent event) 
		{
			resizeMe(csvFile);
		}
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
	public static class CsvFilePanelIncrementalExtraRowLoader implements RepeatingCommand
	{
		CsvFilePanel csvFilePanel;

		public CsvFilePanelIncrementalExtraRowLoader(CsvFilePanel csvFilePanel) 
		{
			this.csvFilePanel = csvFilePanel;
		}

		@Override
		public boolean execute() 
		{
			csvFilePanel.loadExtraRows();
// Returning true causes command to be executed again			
			return !csvFilePanel.isFileCompletelyLoaded();
		}

	}
}