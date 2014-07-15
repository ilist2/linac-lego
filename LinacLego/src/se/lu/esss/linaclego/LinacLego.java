package se.lu.esss.linaclego;

import java.io.File;
import java.io.FileNotFoundException;
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
	private boolean createReportDirectory = true;
	
	private Linac linac = null;
	
	public LinacLego(SimpleXmlDoc simpleXmlDoc, StatusPanel statusPanel) throws LinacLegoException   
	{
		this.simpleXmlDoc = simpleXmlDoc;
		this.statusPanel = statusPanel;
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
	public void updateLinac() throws LinacLegoException 
	{
		try 
		{
			if (createReportDirectory)
			{
				if (getReportDir().exists()) 
				{
					File[] fileList = getReportDir().listFiles();
					if (fileList.length > 0) for (int ifile = 0; ifile < fileList.length; ++ifile) fileList[ifile].delete();
				}
				else
				{
					getReportDir().mkdir();
				}
			}
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
		if (!getReportDir().exists()) throw new LinacLegoException("Report directory does not exist.");
		if (linac == null) throw new LinacLegoException("no linac data");
		try 
		{
			linac.printTraceWin(getReportDir().getPath() + delim +  getTraceWinFileName());
			
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
		if (!getReportDir().exists()) throw new LinacLegoException("Report directory does not exist.");
		if (linac == null) throw new LinacLegoException("no linac data");
		try 
		{
			linac.printDynac(getReportDir().getPath() + delim +  getDynacFileName());
			
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
		if (!getReportDir().exists()) throw new LinacLegoException("Report directory does not exist.");
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "Data.csv";
		try {
			linac.printReportTable(getReportDir().getPath() + delim +  fileName);
		} catch (Exception e) {
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
	}
	public void printParameterTable() throws LinacLegoException  
	{
		if (!getReportDir().exists()) throw new LinacLegoException("Report directory does not exist.");
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "PartCount.csv";
		try {
			linac.printParameterTable(getReportDir().getPath() + delim +  fileName);
		} catch (Exception e) {
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
	}
	public void saveXmlDocument() throws LinacLegoException
	{
		if (!getReportDir().exists()) throw new LinacLegoException("Report directory does not exist.");
		if (simpleXmlDoc == null) throw new LinacLegoException("no xml Docxument");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + ".xml";
		try {
			simpleXmlDoc.saveXmlDocument(getReportDir().getPath() + delim +  fileName);
		} catch (Exception e) {
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
	}
	public String getXmlFileDirPath() {return simpleXmlDoc.getXmlSourceFile().getParent();}
	public File   getReportDir() {return new File(getXmlFileDirPath() + delim + "linacLegoOutput");}
	public String getXmlFileName() {return simpleXmlDoc.getXmlSourceFile().getName();}
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
	public boolean isCreateReportDirectory() {return createReportDirectory;}
	public void setCreateReportDirectory(boolean createReportDirectory) {this.createReportDirectory = createReportDirectory;}
	
	public void setPrintControlPoints(boolean printControlPoints) {this.printControlPoints = printControlPoints;}
	public void setPrintIdInTraceWin(boolean printIdInTraceWin) {this.printIdInTraceWin = printIdInTraceWin;}
	public void seteVout(double eVout) {this.eVout = eVout;}
//	public void setXmlFileName(String xmlFileName) {this.xmlFileName = xmlFileName;}
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
	public static void main(String[] args) throws LinacLegoException, SimpleXmlException  
	{
		SimpleXmlDoc sxd = new SimpleXmlDoc("test\\AT2.xml");
		LinacLego linacLego = new LinacLego(sxd, null);
		linacLego.setPrintControlPoints(true);
		linacLego.setPrintIdInTraceWin(true);
		linacLego.setCreateReportDirectory(false);
		linacLego.updateLinac();
//		linacLego.createTraceWinFile();
//		linacLego.createDynacFile();
//		linacLego.printReportTable();
//		linacLego.printParameterTable();
//		linacLego.saveXmlDocument();
	}

}
