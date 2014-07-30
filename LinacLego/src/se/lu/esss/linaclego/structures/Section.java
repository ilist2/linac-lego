package se.lu.esss.linaclego.structures;

import java.io.PrintWriter;
import java.util.ArrayList;

import se.lu.esss.linaclego.LinacLego;
import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.cell.Cell;

import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;


public class Section 
{
	public static final double cvel = 299792458.0;
	public static final String space = "\t";
	public static final String newline = System.getProperty("line.separator");
	SimpleXmlReader tag;
	Linac linac;
	
	ArrayList<Cell> cellList = new ArrayList<Cell>();
	private double rfFreqMHz = 0.0;
	private int rfHarmonic = 1;
	private double lamda = 0.0;
	private double length = 0.0;
	private int index = -1;
	private boolean periodicLatticeSection = false;
	
	public Section(SimpleXmlReader tag, Linac linac, int index) throws SimpleXmlException, LinacLegoException
	{
		this.tag = tag;
		this.linac = linac;
		this.index = index;
		rfHarmonic = Integer.parseInt(tag.attribute("rfHarmonic"));
		rfFreqMHz = ((double) rfHarmonic) * linac.getBeamFrequencyMHz();
		lamda = cvel / (rfFreqMHz * 1.0e+06);

		length = 0.0;
		if (getType() != null)
		{
			if (getType().equals("periodic"))
			{
				periodicLatticeSection = true;
			}
		}
		for (int icell = 0; icell < tag.numChildTags(); ++icell)
		{
			SimpleXmlReader cellTag = tag.tag(icell);
			
			if (cellTag.tagName().equals("cell")) 
			{
				Cell cell  = new Cell(cellTag, this, icell);
				cellList.add(cell);
				setLength(getLength() + cell.getLength());
			}
			else
			{
				throw new SimpleXmlException("Only cell tags allowed inside latticeSection tag");
			}
		}
	}
	public double geteVin()
	{
		return cellList.get(0).geteVin();
	}
	public double geteVout()
	{
		return cellList.get(cellList.size() - 1).geteVout();
	}
	public double getLocalBeginZ() 
	{
		return cellList.get(0).getLocalBeginZ();
	}
	public double getLocalEndZ() 
	{
		return cellList.get(cellList.size() - 1).getLocalEndZ();
	}
	public String traceWinCommand() 
	{
		String command = "";
		if (!periodicLatticeSection)
		{
			if (index > 0)
			{
				if (linac.getSectionList().get(index - 1).isPeriodicLatticeSection())
				{
					command = "LATTICE_END" + newline;
				}
			}
		}
		command = command + "FREQ";
		command = command + space + Double.toString(getRfFreqMHz());
		if (periodicLatticeSection)
		{
			command = command + newline + "LATTICE";
			command = command + space + Integer.toString(cellList.get(0).getNumBeamLineElements()) + space + "0";
		}
		command = command + newline;
		return command;
	}
	public void printTraceWin(PrintWriter pw) throws SimpleXmlException 
	{
		pw.print(traceWinCommand());
		for (int icell = 0; icell < cellList.size(); ++icell)
		{
			cellList.get(icell).printTraceWin(pw);
		}
	}
	public void printDynac(PrintWriter pw)  
	{
		for (int icell = 0; icell < cellList.size(); ++icell)
		{
			cellList.get(icell).printDynac(pw);
		}
	}
	public void printReportTable(PrintWriter pw) throws SimpleXmlException 
	{
		for (int icell = 0; icell < cellList.size(); ++icell)
		{
			cellList.get(icell).printReportTable(pw);
		}
	}
	public String getType() 
	{
		try {return tag.attribute("type");} catch (SimpleXmlException e) {return null;}
	}
	public String getId() throws LinacLegoException
	{
		try {return tag.attribute("id");} 
		catch (SimpleXmlException e) { throw new LinacLegoException("Section: " + e.getMessage());}
	}
	public double getRfFreqMHz() {return rfFreqMHz;}
	public double getLamda() {return lamda;}
	public ArrayList<Cell> getCellList() {return cellList;}
	public double getLength() {return length;}
	public int getIndex() {return index;}
	public int getNumOfCells() {return cellList.size();}
	public int getRfHarmonic() {return rfHarmonic;}
	public boolean isPeriodicLatticeSection() {return periodicLatticeSection;}
	
	public Cell getCell(String cellId) throws LinacLegoException 
	{
		Cell matchingCell = null;
		for (int icell = 0; icell < cellList.size(); ++icell)
		{
			if (cellList.get(icell).getId().equals(cellId)) 
				matchingCell = cellList.get(icell);
		}
		return matchingCell;
	}
	public Linac getLinac() {return linac;}
	public LinacLego getLinacLego() {return getLinac().getLinacLego();}
	
	public void setLength(double length) {this.length = length;}
	public void setRfFreqMHz(double rfFreqMHz) {this.rfFreqMHz = rfFreqMHz;}
}
