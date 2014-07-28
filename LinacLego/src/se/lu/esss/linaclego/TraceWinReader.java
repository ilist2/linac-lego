package se.lu.esss.linaclego;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.astrofizzbizz.simpleXml.SimpleXmlDoc;
import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlWriter;
import com.astrofizzbizz.utilities.StatusPanel;

public class TraceWinReader 
{
	private String fileLocationPath;
	private double ekinMeV;
	private double beamFrequencyMHz;
	SimpleXmlWriter xw;
	private StatusPanel statusPanel = null;
	
	public TraceWinReader(String fileLocationPath, double ekinMeV, double beamFrequencyMHz, StatusPanel statusPanel)
	{
		this.fileLocationPath = fileLocationPath;
		this.ekinMeV = ekinMeV;
		this.beamFrequencyMHz = beamFrequencyMHz;
		this.statusPanel = statusPanel;
	}
	void readTraceWinFile() throws LinacLegoException 
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
			
			int periodElementCount = 0;
			int numElementsInPeriod = 0;

			
			String delims = "[ ,\t]+";
			String[] splitResponse = null;
			String[] traceWinData = null;
			Double rfFreqMHz = beamFrequencyMHz;
			int elementCount = 0;
			for (int ii = 0; ii < outputBuffer.size(); ++ii)
			{
				splitResponse = outputBuffer.get(ii).split(delims);
				if (splitResponse.length > 0)
				{
					if (splitResponse[0].length() > 0)
					{
						if (splitResponse[0].indexOf(";") != 0)
						{
							writeStatus("Processing TraceWin Line " + Integer.toString(ii + 1) + " Command = " + splitResponse[0]);
							traceWinData = new String[splitResponse.length - 1];
							for (int idata = 0; idata < (splitResponse.length - 1); ++idata)
							{
								traceWinData[idata] = splitResponse[idata + 1];
							}
							
							boolean traceWinTypeFound = false;
							boolean bleFound = false;
							if (splitResponse[0].toUpperCase().equals("BEND")) 				bleFound = true;
							if (splitResponse[0].toUpperCase().equals("DRIFT")) 			bleFound = true;
							if (splitResponse[0].toUpperCase().equals("DTL_CEL")) 			bleFound = true;
							if (splitResponse[0].toUpperCase().equals("EDGE")) 				bleFound = true;
							if (splitResponse[0].toUpperCase().equals("FIELD_MAP")) 		bleFound = true;
							if (splitResponse[0].toUpperCase().equals("NCELLS")) 			bleFound = true;
							if (splitResponse[0].toUpperCase().equals("QUAD")) 				bleFound = true;
							if (splitResponse[0].toUpperCase().equals("GAP")) 				bleFound = true;
							if (splitResponse[0].toUpperCase().equals("THIN_STEERING"))		bleFound = true;

//							if (splitResponse[0].toUpperCase().equals("MATCH_FAM_GRAD")) traceWinTypeFound = true;
//							if (splitResponse[0].toLowerCase().equals("min_phase_variation")) traceWinTypeFound = true;
//							if (splitResponse[0].toLowerCase().equals("match_fam_phase")) traceWinTypeFound = true;
							if (splitResponse[0].toLowerCase().equals("end")) traceWinTypeFound = true;
							if (splitResponse[0].toUpperCase().equals("FREQ")) 
							{
								rfFreqMHz = Double.parseDouble(splitResponse[1]);
								traceWinTypeFound = true;
							}
							if (splitResponse[0].toUpperCase().equals("LATTICE")) 
							{
								numElementsInPeriod = Integer.parseInt(splitResponse[1]);
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
								traceWinTypeFound = true;
							}
							if (splitResponse[0].toUpperCase().equals("LATTICE_END")) 
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
								traceWinTypeFound = true;
							}
							if (bleFound)
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
								String traceWinElementType = splitResponse[0].toUpperCase();
								createBleTagFromTraceWin(traceWinElementType, ecstring, traceWinData);

								if (numElementsInPeriod > 0)
								{
									periodElementCount = periodElementCount + 1;
								}
							}
							else if (!traceWinTypeFound)
							{
								writeStatus("TraceWin Command " + splitResponse[0] + " not understood");
//								throw new LinacLegoException("TraceWin Command " + splitResponse[0] + " not understood");
							}
						}
					}
				}
			}
			xw.closeXmlTag("slot");
			xw.closeXmlTag("cell");
			xw.closeXmlTag("section");
			xw.closeXmlTag("linac");
			xw.closeDocument();
//			String xmlFilePath = new File(fileLocationPath).getPath().substring(0, new File(fileLocationPath).getPath().lastIndexOf(".")) + ".xml";
//			xw.getSimpleXmlDoc().setXmlSourceFile(new File(xmlFilePath));
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
	private void createBleTag(SimpleXmlWriter xw, 
			String legoId, 
			String legoType, 
			String[] dataValue, 
			String[] dataName, 
			String[] dataUnit, 
			String[] dataType) throws LinacLegoException
	{
		try
		{
			xw.openXmlTag("ble");
			xw.setAttribute("id", legoId);
			xw.setAttribute("type", legoType);
			for (int ii = 0; ii < dataValue.length; ++ii)
			{
				xw.openXmlTag("d");
				xw.setAttribute("id", dataName[ii]);
				xw.setAttribute("unit", dataUnit[ii]);
				xw.setAttribute("type", dataType[ii]);
				xw.writeCharacterData(dataValue[ii]);
				xw.closeXmlTag("d");
			}
			xw.closeXmlTag("ble");
		}
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	private void createBleTagFromTraceWin(String traceWinElementType, String legoIdIndex, String[] twd) throws LinacLegoException
	{
		if (traceWinElementType.equals("BEND"))
		{
			
			String legoType = "bend";
			String[] dataName  = {"bendAngleDeg",	"radOfCurvmm",	"fieldIndex",	"aperRadmm",	"HVflag",	"K1in",		"K2in",		"K1out",		"K2out"};
			String[] dataUnit  = {"deg", 			"mm", 			"unit", 		"mm", 			"unit", 	"unit",		"unit",		"unit",			"unit"};
			String[] dataType  = {"double", 		"double", 		"int", 			"double", 		"int", 		"double",	"double",	"double",		"double"};
			String[] dataValue = {twd[0], 			twd[1], 		twd[2], 		twd[3], 		twd[4],		"0.0",		"0.0",		"0.0",			"0.0"};
			createBleTag(xw, "BD" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
		if (traceWinElementType.equals("DRIFT"))
		{
			String legoType = "drift";
			String[] dataName  = {"l",		"r",		"ry"};
			String[] dataUnit  = {"mm", 	"mm", 		"mm"};
			String[] dataType  = {"double", "double", 	"double"};
			String[] dataValue = {twd[0],	twd[1],		twd[2]};
			createBleTag(xw, "DR" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
		if (traceWinElementType.equals("DTL_CEL"))
		{
			String legoType = "dtlCell";
			String[] dataName  = {"cellLenmm",	"q1Lenmm",	"q2Lenmm",	"cellCentermm",	"grad1Tpm",	"grad2Tpm",	"voltsT",	"voltMult",	"rfPhaseDeg",	"phaseAdd",	"radApermm",	"phaseFlag",	"betaS",	"tts",		"ktts",		"k2tts"};
			String[] dataUnit  = {"mm",			"mm",		"mm",		"mm",			"T/m",		"T/m",		"Volt",		"unit",		"deg",			"deg",		"mm",			"unit",			"m",		"unit",		"unit",		"unit"};
			String[] dataType  = {"double",		"double",	"double",	"double",		"double",	"double",	"double",	"double",	"double",		"double",	"double",		"int",			"double",	"double",	"double",	"double"};
			String[] dataValue = {twd[0], 		 twd[1],	twd[2],		twd[3],			twd[4],		twd[5],		twd[6],		"1.0",		twd[7],			"0.0",		twd[8],			twd[9],			twd[10],	twd[11],	twd[12],	twd[13]};
			createBleTag(xw, "DT" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
		if (traceWinElementType.equals("EDGE"))
		{
			String legoType = "edge";
			String[] dataName  = {"poleFaceAngleDeg",	"radOfCurvmm",	"gapmm",	"K1",		"K2",		"aperRadmm",	"HVflag"};
			String[] dataUnit  = {"deg",				"mm",			"mm",		"unit",		"unit",		"mm",			"unit"};
			String[] dataType  = {"double",				"double",		"double",	"double",	"double",	"double",		"int"};
			String[] dataValue = {twd[0],				twd[1],			twd[2],		twd[3],		twd[4],		twd[5],			twd[6]};
			createBleTag(xw, "EG" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
		if (traceWinElementType.equals("FIELD_MAP"))
		{
			String legoType = "fieldMap";
			String[] dataName  = {"rfpdeg",	"xelmax",	"radiusmm",	"lengthmm",	"file",		"scaleFactor"};
			String[] dataUnit  = {"deg",	 "unit", 	"mm", 		"mm", 		"unit",		"unit"};
			String[] dataType  = {"double",	 "double", 	"double", 	"double", 	"string",	"double"};
			String[] dataValue = {twd[2], 	twd[5],		twd[3],		twd[1],		twd[8],		"1.0"};
			createBleTag(xw, "FM" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
		if (traceWinElementType.equals("NCELLS"))
		{
			String legoType = "ncells";
			String[] dataName  = {"mode",	"ncells",	"betag",	"e0t",		"theta",	"radius",	"p",	"ke0ti",	"ke0to",	"dzi",		"dzo",		"betas",	"ts",		"kts",		"k2ts",		"ti",		"kti",		"k2ti",		"to",		"kto",		"k2to"};
			String[] dataUnit  = {"unit",	"unit",		"m",		"Volt/m",	"deg",		"mm",		"unit",	"unit",		"unit",		"mm",		"mm",		"m",		"unit",		"unit",		"unit",		"unit",		"unit",		"unit",		"unit",		"unit", 	"unit"};
			String[] dataType  = {"int",	"int",		"double",	"double",	"double",	"double",	"int",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double"};
			String[] dataValue = {twd[0],	twd[1],		twd[2],		twd[3],		twd[4],		twd[5],		twd[6],	twd[7],		twd[8],		twd[9],		twd[10],	twd[11],	twd[12],	twd[13],	twd[14],	twd[15],	twd[16],	twd[17],	twd[18],	twd[19],	twd[20]};
			createBleTag(xw, "NC" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
		if (traceWinElementType.equals("QUAD"))
		{
			String legoType = "quad";
			String[] dataName  = {"l",		"g",		"r"};
			String[] dataUnit  = {"mm",		"T/m",		"mm"};
			String[] dataType  = {"double",	"double",	"double"};
			String[] dataValue = {twd[0],	twd[1],		twd[2]};
			createBleTag(xw, "QD" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
		if (traceWinElementType.equals("GAP"))
		{
			String legoType = "rfGap";
			String[] dataName  = {"voltsT",	"rfPhaseDeg",	"radApermm",	"phaseFlag",	"betaS",	"tts",		"ktts",		"k2tts",	"ks",		"k2s",};
			String[] dataUnit  = {"Volt",	"deg",			"mm",			"unit",			"m",		"unit",		"unit",		"unit",		"unit",		"unit"};
			String[] dataType  = {"double",	"double",		"double",		"int",			"double",	"double",	"double",	"double",	"double",	"double"};
			String[] dataValue = {twd[0],	twd[1],			twd[2],			twd[3],			twd[4],		twd[5],		twd[6],		twd[7],		twd[8],		twd[9]};
			createBleTag(xw, "RF" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
		if (traceWinElementType.equals("THIN_STEERING"))
		{
			String legoType = "thinSteering";
			String[] dataName  = {"xkick",	"ykick",	"r",		"kickType"};
			String[] dataUnit  = {"Tm",		"Tm",		"mm",		"unit"};
			String[] dataType  = {"double",	"double",	"double",	"int"};
			String[] dataValue = {twd[0],	twd[1],		twd[2],	"0"};
			if (twd.length == 4) dataValue[3] = twd[3];
			createBleTag(xw, "TS" + legoIdIndex, legoType, dataValue, dataName, dataUnit, dataType);
		}
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
		String path = "C:\\EclipseWorkSpace2014\\LinacLego\\EssLinacXmlFiles\\SpokeOptimus.dat";
		TraceWinReader twr = new TraceWinReader(path, 89.0, 352.21, null);
		twr.readTraceWinFile();
		twr.saveXmlFile("C:\\EclipseWorkSpace2014\\LinacLego\\EssLinacXmlFiles\\SpokeOptimus.xml");
	}

}
