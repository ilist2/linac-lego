package se.lu.esss.linaclego.tracewinreader;

import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinBendData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinBleData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinDriftData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinDtlCellData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinEdgeData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinFieldMapData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinNcellsData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinQuadData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinRFGapData;
import se.lu.esss.linaclego.tracewinreader.tracewinbledata.TraceWinThinSteerData;

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
