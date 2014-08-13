package org.openepics.discs.linaclego.tracewinreader;

import java.util.Scanner;

import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinBendData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinBleData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinDriftData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinDtlCellData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinEdgeData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinFieldMapData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinNcellsData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinQuadData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinRFGapData;
import org.openepics.discs.linaclego.tracewinreader.tracewinbledata.TraceWinThinSteerData;

public class TraceWinCommandReader 
{
	private String traceWinType = "";
	private String[] traceWinData = null;
	private TraceWinBleData traceWinBleData = null;
	private TraceWinReader traceWinReader;
	private int traceWinCommandListIndex;
	
	public int getTraceWinCommandListIndex() {return traceWinCommandListIndex;}
	public String getTraceWinType() {return traceWinType;}
	public String[] getTraceWinData() {return traceWinData;}
	public TraceWinBleData getTraceWinBleData() {return traceWinBleData;}
	public TraceWinReader getTraceWinReader() {return traceWinReader;}

	public void setTraceWinCommandListIndex(int traceWinCommandListIndex) {this.traceWinCommandListIndex = traceWinCommandListIndex;}

	TraceWinCommandReader(String inputString, TraceWinReader traceWinReader)
	{
		this.traceWinReader = traceWinReader;
// Get rid of leading spaces and delimators
		Scanner inputScanner = new Scanner(inputString);
		inputScanner.useDelimiter("[, \t]");
		try{inputString = inputString.substring(inputString.indexOf(inputScanner.next()));}
		catch (java.util.NoSuchElementException e) 
		{
			inputScanner.close();
			return;
		}
		inputScanner.close();
// Get rid of leader description
		if (inputString.indexOf(":") >= 0)
		{
			inputScanner = new Scanner(inputString.substring(inputString.indexOf(":") + 1));
			inputScanner.useDelimiter("[, \t]");
			inputString = inputString.substring(inputString.indexOf(inputScanner.next()));
			inputScanner.close();
		}

		String delims = "[ ,\t]+";
		String[] splitResponse = null;
		
		splitResponse = inputString.split(delims);
		if (splitResponse.length > 0)
		{
			if (splitResponse[0].length() > 0)
			{
				if (splitResponse[0].indexOf(";") != 0)
				{
					traceWinReader.writeStatus("Processing TraceWin Line Command = " + splitResponse[0]);
					traceWinData = new String[splitResponse.length - 1];
					for (int idata = 0; idata < (splitResponse.length - 1); ++idata)
					{
						traceWinData[idata] = splitResponse[idata + 1];
					}
					String bleType = "";
					traceWinBleData = null;
					boolean traceWinTypeFound = false;
					if (splitResponse[0].toUpperCase().equals("BEND")) 				bleType = "BEND";
					if (splitResponse[0].toUpperCase().equals("DRIFT")) 			bleType = "DRIFT";
					if (splitResponse[0].toUpperCase().equals("DTL_CEL")) 			bleType = "DTL_CEL";
					if (splitResponse[0].toUpperCase().equals("EDGE")) 				bleType = "EDGE";
					if (splitResponse[0].toUpperCase().equals("FIELD_MAP")) 		bleType = "FIELD_MAP";
					if (splitResponse[0].toUpperCase().equals("NCELLS")) 			bleType = "NCELLS";
					if (splitResponse[0].toUpperCase().equals("QUAD")) 				bleType = "QUAD";
					if (splitResponse[0].toUpperCase().equals("GAP")) 				bleType = "GAP";
					if (splitResponse[0].toUpperCase().equals("THIN_STEERING"))		bleType = "THIN_STEERING";

					if (splitResponse[0].toUpperCase().equals("END")) traceWinTypeFound = true;
					if (splitResponse[0].toUpperCase().equals("FREQ")) traceWinTypeFound = true;
					if (splitResponse[0].toUpperCase().equals("LATTICE")) traceWinTypeFound = true;
					if (splitResponse[0].toUpperCase().equals("LATTICE_END")) traceWinTypeFound = true;
					if (bleType.length() > 0) 
					{
						traceWinTypeFound = true;
						if (bleType.equals("BEND")) 			traceWinBleData = new TraceWinBendData(traceWinData, this);
						if (bleType.equals("DRIFT")) 			traceWinBleData = new TraceWinDriftData(traceWinData, this);
						if (bleType.equals("DTL_CEL"))			traceWinBleData = new TraceWinDtlCellData(traceWinData, this);
						if (bleType.equals("EDGE"))				traceWinBleData = new TraceWinEdgeData(traceWinData, this);
						if (bleType.equals("FIELD_MAP"))		traceWinBleData = new TraceWinFieldMapData(traceWinData, this);
						if (bleType.equals("NCELLS"))			traceWinBleData = new TraceWinNcellsData(traceWinData, this);
						if (bleType.equals("QUAD"))				traceWinBleData = new TraceWinQuadData(traceWinData, this);
						if (bleType.equals("GAP"))				traceWinBleData = new TraceWinRFGapData(traceWinData, this);
						if (bleType.equals("THIN_STEERING"))	traceWinBleData = new TraceWinThinSteerData(traceWinData, this);
					}
					if (traceWinTypeFound)
					{
						traceWinType = splitResponse[0].toUpperCase();
					}
					else
					{
						traceWinReader.writeStatus("TraceWin Command " + splitResponse[0] + " not understood");
						traceWinType = "";
						traceWinData = null;
					}
				}
			}
		}
	}
}
