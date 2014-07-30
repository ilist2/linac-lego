package se.lu.esss.linaclego;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.lu.esss.linaclego.structures.Linac;
import se.lu.esss.linaclego.structures.cell.CellModel;
import se.lu.esss.linaclego.structures.slot.SlotModel;

import com.astrofizzbizz.simpleXml.SimpleXmlDoc;
import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;
import com.astrofizzbizz.utilities.StatusPanel;

public class LinacLego 
{
	static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	static final DecimalFormat zeroPlaces = new DecimalFormat("###");
	private static final String delim = System.getProperty("file.separator");
	public static final String newline = System.getProperty("line.separator");
	private SimpleXmlDoc simpleXmlDoc;
	private SimpleXmlReader linacLegoTag;
	private ArrayList<CellModel> cellModelList;
	private ArrayList<SlotModel> slotModelList;
	private SimpleXmlReader linacTag;
	private String linacLegoTitle;
	private double eVout = -1.0;
	private boolean printControlPoints = true;
	private boolean printIdInTraceWin = true;
	private StatusPanel statusPanel = null;
	private int linacLegoRevNo = -1;
	private File reportDirectory = null;
	private boolean reportDirectoryExists = false;
	
	private Linac linac = null;
	
	public LinacLego(SimpleXmlDoc simpleXmlDoc, StatusPanel statusPanel) throws LinacLegoException   
	{
		this.simpleXmlDoc = simpleXmlDoc;
		this.statusPanel = statusPanel;
		reportDirectoryExists = false;
		try 
		{
			linacLegoTag = new SimpleXmlReader(simpleXmlDoc);
			try {linacLegoRevNo = Integer.parseInt(linacLegoTag.attribute("revNo"));} 
			catch (SimpleXmlException e) {if (e.getMessage().equals("Attribute does not exist")) linacLegoRevNo = 0;}
			cellModelList = new ArrayList<CellModel>();
			SimpleXmlReader cellModelsListTag = linacLegoTag.tagsByName("header").tag(0).tagsByName("cellModels");
			if (cellModelsListTag.numChildTags() > 0)
			{
				for (int icol = 0; icol < cellModelsListTag.numChildTags(); ++icol)
				{
					SimpleXmlReader cellModelListTag = cellModelsListTag.tag(icol).tagsByName("cellModel");
					if (cellModelListTag.numChildTags() > 0)
					{
						for (int itag = 0; itag < cellModelListTag.numChildTags(); ++itag)
						{
							cellModelList.add(new CellModel(cellModelListTag.tag(itag), this));
						}
					}
				}
			}
			slotModelList = new ArrayList<SlotModel>();
			SimpleXmlReader slotModelsListTag = linacLegoTag.tagsByName("header").tag(0).tagsByName("slotModels");
			if (slotModelsListTag.numChildTags() > 0)
			{
				for (int icol = 0; icol < slotModelsListTag.numChildTags(); ++icol)
				{
					SimpleXmlReader slotModelListTag = slotModelsListTag.tag(icol).tagsByName("slotModel");
					if (slotModelListTag.numChildTags() > 0)
					{
						for (int itag = 0; itag < slotModelListTag.numChildTags(); ++itag)
						{
							slotModelList.add(new SlotModel(slotModelListTag.tag(itag), this));
						}
					}
				}
			}
			linacTag =  linacLegoTag.tagsByName("linac").tag(0);
			linacLegoTitle = linacLegoTag.attribute("title");
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		}

	}
	public void setReportDirectory(File parentDirectory)
	{
		reportDirectory = new File(parentDirectory.getPath() + delim + "linacLegoOutput");
		if (reportDirectory.exists()) 
		{
			File[] fileList = reportDirectory.listFiles();
			if (fileList.length > 0) for (int ifile = 0; ifile < fileList.length; ++ifile) fileList[ifile].delete();
		}
		else
		{
			reportDirectory.mkdir();
		}
		reportDirectoryExists = false;
		if (reportDirectory.exists()) reportDirectoryExists = true;
	}
	public void updateLinac() throws LinacLegoException 
	{
		try 
		{
//			linacLegoTitle = linacLegoTag.attribute("title");
			linac = new Linac(this);
			eVout = linac.geteVout();
			writeStatus("Final Energy = " + twoPlaces.format(geteVout() * 1.0e-6) + " MeV");
			writeStatus("Length       = " + twoPlaces.format(linac.getLength() ) + " meters");
			writeStatus("X = " + twoPlaces.format(linac.getBeamLineElements().get(linac.getBeamLineElements().size() - 1).getEndPosVec()[0]) + " meters");
			writeStatus("Y = " + twoPlaces.format(linac.getBeamLineElements().get(linac.getBeamLineElements().size() - 1).getEndPosVec()[1]) + " meters");
			writeStatus("Z = " + twoPlaces.format(linac.getBeamLineElements().get(linac.getBeamLineElements().size() - 1).getEndPosVec()[2]) + " meters");
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		}
	}
	public void createTraceWinFile() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		try 
		{
			linac.printTraceWin(getReportDirectory().getPath() + delim +  getTraceWinFileName());
			
		} catch (FileNotFoundException e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getStackTraceString());
			throw lle;
		} catch (SimpleXmlException e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getStackTraceString());
			throw lle;
		}
	}
	public void createDynacFile() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		try 
		{
			linac.printDynac(getReportDirectory().getPath() + delim +  getDynacFileName());
			
		} catch (FileNotFoundException e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getStackTraceString());
			throw lle;
		} catch (SimpleXmlException e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getStackTraceString());
			throw lle;
		}
	}
	public void printReportTable() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "Data.csv";
		try {
			linac.printReportTable(getReportDirectory().getPath() + delim +  fileName);
		} catch (Exception e) {
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
	}
	public void printPartCounts() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) ;
		try {
			linac.printPartCounts(getReportDirectory().getPath() + delim +  fileName);
		} catch (Exception e) {
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
	}
	public void saveXmlDocument() throws LinacLegoException
	{
		if (simpleXmlDoc == null) throw new LinacLegoException("no xml Docxument");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + ".xml";
		try {
			simpleXmlDoc.saveXmlDocument(getReportDirectory().getPath() + delim +  fileName);
		} catch (Exception e) {
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
	}
	public File getReportDirectory() {return reportDirectory;}
	public boolean isReportDirectoryExists() {return reportDirectoryExists;}
	public String getXmlFileName() {return simpleXmlDoc.getXmlDocName();}
	public SimpleXmlDoc getSimpleXmlDoc() {return simpleXmlDoc;}
	public SimpleXmlReader getLinacLegoTag() {return linacLegoTag;}
	public ArrayList<CellModel> getCellModelList() {return cellModelList;}
	public ArrayList<SlotModel> getSlotModelList() {return slotModelList;}
	public SimpleXmlReader getLinacTag() {return linacTag;}
	public double geteVout() {return eVout;}
	public String getLinacLegoTitle() {return linacLegoTitle;}
	public boolean isPrintControlPoints() {return printControlPoints;}
	public boolean isPrintIdInTraceWin() {return printIdInTraceWin;}
	public int getLinacLegoRevNo() {return linacLegoRevNo;}
	public Linac getLinac() {return linac;}
	
	public void setPrintControlPoints(boolean printControlPoints) {this.printControlPoints = printControlPoints;}
	public void setPrintIdInTraceWin(boolean printIdInTraceWin) {this.printIdInTraceWin = printIdInTraceWin;}
	public void seteVout(double eVout) {this.eVout = eVout;}
	public void setStatusPanel(StatusPanel statusPanel) {this.statusPanel = statusPanel;}
	public String  getTraceWinFileName() 
	{
		return getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + ".dat";
	}
	public String  getDynacFileName() 
	{
		return getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + ".in";
	}
	public void writeStatus(String statusText) 
	{
		if (statusPanel != null)
		{
			statusPanel.setText(statusText);
		}
		else
		{
			System.out.println(statusText);
		}
	}
	public static void main(String[] args) throws LinacLegoException, SimpleXmlException, MalformedURLException, URISyntaxException  
	{
		String linacLegoWebSite = "https://1dd61ea372616aae15dcd04cd29d320453f0cb60.googledrive.com/host/0B3Hieedgs_7FNXg3OEJIREFuUUE";
		URL inputFileUrl = new URL(linacLegoWebSite + "/public/linacLego.xml");
//		inputFileUrl = new File("C:\\Users\\davidmcginnis\\Google Drive\\ESS\\gitRepositories\\EssLinacLatticeRepository\\public\\linacLego.xml").toURI().toURL();
		
		SimpleXmlDoc sxd = new SimpleXmlDoc(inputFileUrl);
		LinacLego linacLego = new LinacLego(sxd, null);
		linacLego.setPrintControlPoints(true);
		linacLego.setPrintIdInTraceWin(true);
//		linacLego.setReportDirectory(new File("C:\\Users\\davidmcginnis\\Google Drive\\ESS\\gitRepositories\\EssLinacLatticeRepository\\public\\test"));
		linacLego.updateLinac();
/*		linacLego.createTraceWinFile();
		linacLego.createDynacFile();
		linacLego.printReportTable();
		linacLego.printPartCounts();
		linacLego.saveXmlDocument();
*/	}

}
