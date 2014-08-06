package se.lu.esss.linaclego.tracewinreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import se.lu.esss.linaclego.LinacLegoException;

import com.astrofizzbizz.simpleXml.SimpleXmlDoc;
import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlWriter;
import com.astrofizzbizz.utilities.StatusPanel;

public class TraceWinReader 
{
	private String fileLocationPath;
	private double ekinMeV;
	private double beamFrequencyMHz;
	private SimpleXmlWriter xw;
	private StatusPanel statusPanel = null;
	private ArrayList<TraceWinCommandReader> traceWinCommandList;
	
	public SimpleXmlWriter getXw() {return xw;}
	public StatusPanel getStatusPanel() {return statusPanel;}
	public ArrayList<TraceWinCommandReader> getTraceWinCommandList() {return traceWinCommandList;}

	public TraceWinReader(String fileLocationPath, double ekinMeV, double beamFrequencyMHz, StatusPanel statusPanel)
	{
		this.fileLocationPath = fileLocationPath;
		this.ekinMeV = ekinMeV;
		this.beamFrequencyMHz = beamFrequencyMHz;
		this.statusPanel = statusPanel;
	}
	public void readTraceWinFile() throws LinacLegoException 
	{
		BufferedReader br;
		ArrayList<String> outputBuffer = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(fileLocationPath));
			String line;
			while ((line = br.readLine()) != null) 
			{  
				outputBuffer.add(line);
			}
			br.close();
			String title = new File(fileLocationPath).getName().substring(0, new File(fileLocationPath).getName().lastIndexOf("."));
			xw = new SimpleXmlWriter("linacLego", "LinacLego.dtd");
			xw.setAttribute("title", title);
			xw.openXmlTag("header");
			xw.openXmlTag("slotModels");
			xw.setAttribute("id", title + "SlotModels");
			xw.closeXmlTag("slotModels");
			xw.openXmlTag("cellModels");
			xw.setAttribute("id", title + "CellModels");
			xw.closeXmlTag("cellModels");
			xw.openXmlTag("controlPoints");
			xw.setAttribute("id", title + "ControlPoints");
			xw.closeXmlTag("controlPoints");
			xw.closeXmlTag("header");
			xw.openXmlTag("linac");
			xw.openXmlTag("linacData");

			xw.openXmlTag("d");
			xw.setAttribute("id", "ekin");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "MeV");
			xw.writeCharacterData(Double.toString(ekinMeV));
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "beamFrequency");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "MHz");
			xw.writeCharacterData(Double.toString(beamFrequencyMHz));
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "xSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "m");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "ySurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "m");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "zSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "m");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "pitchSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "deg");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "rollSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "deg");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "yawSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "deg");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "alphaX");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "unit");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "alphaY");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "unit");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "alphaZ");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "unit");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "betaX");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mm/mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "betaY");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mm/mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "betaZ");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "deg/keV");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "emitX");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mm-mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "emitY");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mm-mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "emitZ");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "deg-keV");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "beamCurrent");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mA");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.closeXmlTag("linacData");
			xw.openXmlTag("section");
			xw.setAttribute("rfHarmonic", "1");
			int sectionCount = 10;
			xw.setAttribute("id", "section" + addLeadingZeros(sectionCount, 3));
			xw.openXmlTag("cell");
			int cellCount = 10;
			xw.setAttribute("id", "cell" + addLeadingZeros(cellCount, 3));
			xw.openXmlTag("slot");
			int slotCount = 10;
			xw.setAttribute("id", "slot" + addLeadingZeros(slotCount, 3));
			
			traceWinCommandList = new ArrayList<TraceWinCommandReader>();
			int traceWinCommandListIndex = 0;
			for (int ii = 0; ii < outputBuffer.size(); ++ii)
			{
				TraceWinCommandReader traceWinCommand = new TraceWinCommandReader(outputBuffer.get(ii), this);
				if (traceWinCommand.getTraceWinType().length() > 0)
				{
					traceWinCommandList.add(traceWinCommand);
					traceWinCommand.setTraceWinCommandListIndex(traceWinCommandListIndex);
					traceWinCommandListIndex = traceWinCommandListIndex + 1;
				}
			}
			int periodElementCount = 0;
			int numElementsInPeriod = 0;
			
			Double rfFreqMHz = beamFrequencyMHz;
			int elementCount = 0;
			
			for (int ii = 0; ii < traceWinCommandList.size(); ++ii)
			{
							
				if (traceWinCommandList.get(ii).getTraceWinType().equals("FREQ"))				rfFreqMHz = Double.parseDouble(traceWinCommandList.get(ii).getTraceWinData()[0]);
				if (traceWinCommandList.get(ii).getTraceWinType().equals("LATTICE")) 
				{
					numElementsInPeriod = Integer.parseInt(traceWinCommandList.get(ii).getTraceWinData()[0]);
					periodElementCount = 0;
					xw.closeXmlTag("slot");
					xw.closeXmlTag("cell");
					xw.closeXmlTag("section");
					xw.openXmlTag("section");
					int rfharmonic = (int) (rfFreqMHz / beamFrequencyMHz);
					xw.setAttribute("rfHarmonic", Integer.toString(rfharmonic));
					sectionCount = sectionCount + 10;
					xw.setAttribute("id", "section" + addLeadingZeros(sectionCount, 3));
					xw.openXmlTag("cell");
					cellCount = 10;
					xw.setAttribute("id", "cell" + addLeadingZeros(cellCount, 3));
					xw.openXmlTag("slot");
					slotCount = 10;
					xw.setAttribute("id", "slot" + addLeadingZeros(slotCount, 3));
					elementCount = 0;
				}
				if (traceWinCommandList.get(ii).getTraceWinType().equals("LATTICE_END")) 
				{
					numElementsInPeriod = 0;
					periodElementCount = 0;
					xw.closeXmlTag("slot");
					xw.closeXmlTag("cell");
					xw.closeXmlTag("section");
					xw.openXmlTag("section");
					int rfharmonic = (int) (rfFreqMHz / beamFrequencyMHz);
					xw.setAttribute("rfHarmonic", Integer.toString(rfharmonic));
					sectionCount = sectionCount + 10;
					xw.setAttribute("id", "section" + addLeadingZeros(sectionCount, 3));
					xw.openXmlTag("cell");
					cellCount = 10;
					xw.setAttribute("id", "cell" + addLeadingZeros(cellCount, 3));
					xw.openXmlTag("slot");
					slotCount = 10;
					xw.setAttribute("id", "slot" + addLeadingZeros(slotCount, 3));
					elementCount = 0;
				}
				if (traceWinCommandList.get(ii).getTraceWinBleData() != null)
				{
					elementCount = elementCount + 10;
					if (numElementsInPeriod > 0)
					{
						if (periodElementCount == numElementsInPeriod)
						{
							xw.closeXmlTag("slot");
							xw.closeXmlTag("cell");
							xw.openXmlTag("cell");
							cellCount = cellCount + 10;
							xw.setAttribute("id", "cell" + addLeadingZeros(cellCount, 3));
							xw.openXmlTag("slot");
							slotCount = 10;
							xw.setAttribute("id", "slot" + addLeadingZeros(slotCount, 3));
							elementCount = 10;
							periodElementCount = 0;
						}
						
					}
					String ecstring = addLeadingZeros(elementCount, 4);
					traceWinCommandList.get(ii).getTraceWinBleData().createBleTag(xw, ecstring);

					if (numElementsInPeriod > 0)
					{
						periodElementCount = periodElementCount + 1;
					}
				}
			}
			xw.closeXmlTag("slot");
			xw.closeXmlTag("cell");
			xw.closeXmlTag("section");
			xw.closeXmlTag("linac");
			xw.closeDocument();
			String xmlFilePath = new File(fileLocationPath).getPath().substring(0, new File(fileLocationPath).getPath().lastIndexOf(".")) + ".xml";
			xw.getSimpleXmlDoc().setXmlSourceUrl(new File(xmlFilePath).toURI().toURL());
			writeStatus("Finished reading TraceWin File.");
		} 
		catch (FileNotFoundException e) {throw new LinacLegoException(e);}
		catch (IOException e) {throw new LinacLegoException(e);} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	String addLeadingZeros(int counter, int stringLength)
	{
		String scounter = Integer.toString(counter);
		while (scounter.length() < stringLength) scounter = "0" + scounter;
		return scounter;
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
	public void saveXmlFile(String filePath) throws LinacLegoException 
	{
		try {getSimpleXmlDoc().saveXmlDocument(filePath);} catch (SimpleXmlException e) {throw new LinacLegoException();}
	}
	public SimpleXmlDoc getSimpleXmlDoc() {return xw.getSimpleXmlDoc();}
	public static void main(String[] args) throws LinacLegoException 
	{
		String path = "C:\\Users\\davidmcginnis\\Google Drive\\ESS\\gitRepositories\\lattice-repository-development\\aig\\TraceWinInputFiles\\dtl.dat";
		TraceWinReader twr = new TraceWinReader(path, 3.6, 352.21, null);
		twr.readTraceWinFile();
		twr.saveXmlFile("test\\dtlTest.xml");
	}

}
