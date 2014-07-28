package se.lu.esss.linaclego.structures;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.lu.esss.linaclego.FieldProfileBuilder;
import se.lu.esss.linaclego.LinacLego;
import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.cell.CellModelReporter;
import se.lu.esss.linaclego.structures.elements.ControlPoint;
import se.lu.esss.linaclego.structures.elements.ControlPointModelReporter;
import se.lu.esss.linaclego.structures.elements.DataElement;
import se.lu.esss.linaclego.structures.elements.beamline.BeamLineElement;
import se.lu.esss.linaclego.structures.elements.beamline.BeamLineElementModelReporter;
import se.lu.esss.linaclego.structures.slot.SlotModelReporter;

import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;


public class Linac 
{
	public static final double PI = Math.PI;
	public static final double degToRad = PI / 180.0;
	public static final DecimalFormat onePlaces = new DecimalFormat("###.#");
	public static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	public static final DecimalFormat sixPlaces = new DecimalFormat("###.######");
	public static final DecimalFormat eightPlaces = new DecimalFormat("###.########");
	public static final DecimalFormat zeroPlaces = new DecimalFormat("###");
	private ArrayList<Section> sectionList = new ArrayList<Section>();
	private double eVin = 0.0;
	private double eVout = 0.0;
	private double beamFrequencyMHz  = -1.0;
	private LinacLego linacLego = null;
	private double length = 0.0;
	private ArrayList<BeamLineElement> beamLineElementList =  new ArrayList<BeamLineElement>();
	private double[][] eulerMatrix   = {{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
	private double[] transVec   = {0.0, 0.0, 0.0}; 
	private FieldProfileBuilder fieldProfileBuilder = null;
	private ArrayList<DataElement> dataElementList = new ArrayList<DataElement>();


	private DataElement[][] twissData = new DataElement[3][3];
	private DataElement[] surveyTranslationData = new DataElement[3];
	private DataElement[] surveyAngleData = new DataElement[3];
	private DataElement beamCurrent =  new DataElement("beamCurrent",  "beamCurrent",  "double", "mA");;
	
	public Linac(LinacLego linacLego) throws Exception
	{
		this.linacLego = linacLego;
		DataElement ekinDataElement  = new DataElement("ekin", "ekin", "double", "MeV");
		DataElement beamFrequencyDataElement  = new DataElement("beamFrequency", "beamFrequency", "double", "MHz");
		readDataElementValue(ekinDataElement);
		readDataElementValue(beamFrequencyDataElement);

		eVin = Double.parseDouble(ekinDataElement.getValue()) * 1.0e+06;
		beamFrequencyMHz = Double.parseDouble(beamFrequencyDataElement.getValue());
		eVout = eVin;
		length = 0.0;
		readInputTwissData();
		readSurveyData();
		SimpleXmlReader sectionTags = linacLego.getLinacTag().tagsByName("section");
		for (int isec = 0; isec < sectionTags.numChildTags(); ++isec)
		{
			Section newSection = new Section(sectionTags.tag(isec), this, isec);
			sectionList.add(newSection);
			length = length + newSection.getLength();
		}
		eVout = beamLineElementList.get(beamLineElementList.size() - 1).geteVout();
		addControlPointsToLattice();
	}
	public void addControlPointsToLattice() throws SimpleXmlException, LinacLegoException
	{
		String sectionName  = null;
		String cellName = null;
		String slotName = null;
		String bleName = null;
		boolean bleFound;
		int numBle = beamLineElementList.size();
		int ible;
		BeamLineElement ble = null;
		
		SimpleXmlReader controlPointsListTag = linacLego.getLinacLegoTag().tagsByName("header").tag(0).tagsByName("controlPoints");
		if (controlPointsListTag.numChildTags() > 0)
		{
			for (int icol = 0; icol < controlPointsListTag.numChildTags(); ++icol)
			{
				SimpleXmlReader controlPointTags = controlPointsListTag.tag(icol).tagsByName("cnpt");
				if (controlPointTags.numChildTags() > 0)
				{
					for (int itag = 0; itag < controlPointTags.numChildTags(); ++itag)
					{
						sectionName = controlPointTags.tag(itag).attribute("section");
						cellName = controlPointTags.tag(itag).attribute("cell");
						slotName = controlPointTags.tag(itag).attribute("slot");
						bleName = controlPointTags.tag(itag).attribute("ble");
						bleFound = false;
						ible = 0;
						while ((ible < numBle) && !bleFound)
						{
							ble = beamLineElementList.get(ible);
							if (ble.getId().equals(bleName))
								if (ble.getSlot().getId().equals(slotName))
									if (ble.getSlot().getCell().getId().equals(cellName))
										if (ble.getSlot().getCell().getSection().getId().equals(sectionName))
										{
											bleFound = true;
											ble.getControlPointList().add(new ControlPoint(controlPointTags.tag(itag), ble, ble.getControlPointList().size()));
										}
							ible = ible + 1;
						}
						if (!bleFound)
						{
							throw new LinacLegoException("Cannot find ControlPoint: " + sectionName + "-" + cellName + "-" + slotName + "-" + bleName);
						}
					}
				}
			}
		}
	}
	public void printTraceWin(String fileName) throws FileNotFoundException, SimpleXmlException
	{
		PrintWriter pw = new PrintWriter(fileName);
		pw.println(";" + linacLego.getLinacLegoTitle());
		for (int isec = 0; isec < sectionList.size(); ++isec)
		{
			sectionList.get(isec).printTraceWin(pw);
		}
		pw.println("END");
		pw.close();
	}
	public void printDynac(String fileName) throws FileNotFoundException, SimpleXmlException, LinacLegoException
	{
		PrintWriter pw = new PrintWriter(fileName);
		pw.print(makeDynacHeader());
		for (int isec = 0; isec < sectionList.size(); ++isec)
		{
			sectionList.get(isec).printDynac(pw);
		}
		pw.println("STOP");
		pw.close();
	}
	public void printReportTable(String fileName) throws FileNotFoundException, SimpleXmlException, LinacLegoException
	{
		PrintWriter pw = new PrintWriter(fileName);
		pw.println("Id,Section,Cell,Slot,BLE,CNPT,Type,Model,eVout,v/c,Length,Xcen,Ycen,Zcen,Xsur,Ysur,Zsur,VT,PhiS,G,Theta");
		pw.println(" , , , , , , , ,(MeV), ,(m),(m),(m),(m),(m),(m),(m),(MV),(deg),(T/m),(deg)");
		for (int isec = 0; isec < getNumOfSections(); ++isec)
		{
			sectionList.get(isec).printReportTable(pw);
		}
		pw.close();
	}
	public void printPartCounts(String fileName) throws FileNotFoundException, LinacLegoException
	{
		PrintWriter pwCells = new PrintWriter(fileName + "CellParts.csv");
		PrintWriter pwSlots = new PrintWriter(fileName + "SlotParts.csv");
		PrintWriter pwBles = new PrintWriter(fileName + "BleParts.csv");
		PrintWriter pwCnpts = new PrintWriter(fileName + "CnptParts.csv");
		BeamLineElementModelReporter beamLineElementModelReporter = new BeamLineElementModelReporter(this);
		ControlPointModelReporter controlPointModelReporter = new ControlPointModelReporter(this);
		SlotModelReporter slotModelReporter = new SlotModelReporter(this);
		CellModelReporter cellModelReporter = new CellModelReporter(this);
		pwCells.print("type" +"," + "model");
		pwSlots.print("type" +"," + "model");
		pwBles.print("type" +"," + "model");
		pwCnpts.print("type" +"," + "model");
		for (int isection = 0; isection < getNumOfSections(); ++isection)
		{
			pwCells.print("," + getSectionList().get(isection).getId());
			pwSlots.print("," + getSectionList().get(isection).getId());
			pwBles.print("," + getSectionList().get(isection).getId());
			pwCnpts.print("," + getSectionList().get(isection).getId());
		}	
		pwCells.println(",Total");
		pwSlots.println(",Total");
		pwBles.println(",Total,minValue, avgValue,maxValue,Unit");
		pwCnpts.println(",Total");
		cellModelReporter.printModels(pwCells, this);
		slotModelReporter.printModels(pwSlots, this);
		beamLineElementModelReporter.printModels(pwBles, this);
		controlPointModelReporter.printModels(pwCnpts, this);
		pwCells.close();
		pwSlots.close();
		pwBles.close();
		pwCnpts.close();
	}
	public Section getLatticeSection(String sectionId) throws LinacLegoException 
	{
		Section matchingSection = null;
		for (int isec = 0; isec < sectionList.size(); ++isec)
		{
			if (sectionList.get(isec).getId().equals(sectionId)) matchingSection = sectionList.get(isec);
		}
		return matchingSection;
	}
	public double[] getSurveyCoords(double[] linacCoords)
	{
		double[] surveyCoords = {0.0, 0.0, 0.0};
		for (int ir = 0; ir < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)
			{
				surveyCoords[ir] = surveyCoords[ir] + eulerMatrix[ir][ic] * linacCoords[ic];
			}
			surveyCoords[ir] = surveyCoords[ir] + transVec[ir];
		}
		return surveyCoords;
	}
	private void readInputTwissData() throws  LinacLegoException
	{
		twissData[0][0] = new DataElement("alphaX", "alphaX", "double", "unit");
		twissData[0][1] = new DataElement("betaX",  "betaX",  "double", "mm/mrad");
		twissData[0][2] = new DataElement("emitX",  "emitX",  "double", "mm-mrad");
		twissData[1][0] = new DataElement("alphaY", "alphaY", "double", "unit");
		twissData[1][1] = new DataElement("betaY",  "betaY",  "double", "mm/mrad");
		twissData[1][2] = new DataElement("emitY",  "emitY",  "double", "mm-mrad");
		twissData[2][0] = new DataElement("alphaZ", "alphaZ", "double", "unit");
		twissData[2][1] = new DataElement("betaZ",  "betaZ",  "double", "deg/keV");
		twissData[2][2] = new DataElement("emitZ",  "emitZ",  "double", "deg-keV");
		for (int ip = 0; ip < 3; ++ip)
		{
			for (int ii = 0; ii < 3; ++ii)
			{
				readDataElementValue(twissData[ip][ii]);
			}
		}
		readDataElementValue(beamCurrent);
		return;
		
	}
	private void readDataElementValue(DataElement dataElement) throws LinacLegoException
	{
		SimpleXmlReader twissTag = null;
		try 
		{
			twissTag = linacLego.getLinacTag().tagsByName("linacData").tag(0).tagsByName("d").getTagMatchingAttribute("id", dataElement.getId());
			dataElementList.add(new DataElement(twissTag));
		} catch (SimpleXmlException e1) 
		{
			throw new LinacLegoException("Linac tag " + dataElement.getId() + ": " + e1.getMessage());
		}
		if (twissTag != null) 
		{
			try 
			{
				if (!twissTag.attribute("unit").equals(dataElement.getUnit())) 
					throw new LinacLegoException("Linac tag " +  dataElement.getId() + " unit does not match required unit of " + dataElement.getUnit());
			} 
			catch (SimpleXmlException e) 
			{
				throw new LinacLegoException("Linac tag " + dataElement.getId() + ": " + e.getMessage());
			}
			dataElement.setValue(twissTag.getCharacterData()); 
		}
	}
	private String makeDynacHeader() throws LinacLegoException
	{
		String command = getLinacLego().getLinacLegoTitle() + "\nGEBEAM\n2\t1\n";
		command = command + zeroPlaces.format(beamFrequencyMHz * 1e-06) + "\t1000\n";
		command = command + "0.0\t0.0\t0.0\t0.0\t0.0\t0.0\n";
		for (int ip = 0; ip < 3; ++ip)
		{
			for (int ii = 0; ii < 3; ++ii) command = command + twissData[ip][ii].getValue() + "\t";
			command = command + "\n";
		}
		command = command + "INPUT\n938.2796\t1.0\t1.0\n";
		command = command + fourPlaces.format(eVin * 1.0e-06) + "\t0.0\n";
		command = command + "REFCOG\n0\n";
//TODO finish this		
		return command;
	}
	private void readSurveyData() throws LinacLegoException
	{
		double pitch = 0.0;
		double roll = 0.0;
		double yaw = 0.0;
		for (int ir = 0; ir < 3; ++ir) transVec[ir] = 0;

		surveyTranslationData[0] = new DataElement("xSurvey", "xSurvey", "double", "m");
		surveyTranslationData[1] = new DataElement("ySurvey", "ySurvey", "double", "m");
		surveyTranslationData[2] = new DataElement("zSurvey", "zSurvey", "double", "m");
		surveyAngleData[0]  = new DataElement("pitchSurvey", "pitchSurvey", "double", "deg");
		surveyAngleData[1]  = new DataElement("rollSurvey", "rollSurvey", "double", "deg");
		surveyAngleData[2]  = new DataElement("yawSurvey", "yawSurvey", "double", "deg");
		for (int ii = 0; ii < 3; ++ii)
		{
			readDataElementValue(surveyTranslationData[ii]);
			if (surveyTranslationData[ii].valueMatchsType()) transVec[ii] = Double.parseDouble(surveyTranslationData[ii].getValue());
			readDataElementValue(surveyAngleData[ii]);
		}
		if (surveyAngleData[0].valueMatchsType()) pitch = Double.parseDouble(surveyAngleData[0].getValue()) * degToRad;
		if (surveyAngleData[1].valueMatchsType()) roll = Double.parseDouble(surveyAngleData[0].getValue()) * degToRad;
		if (surveyAngleData[2].valueMatchsType()) yaw = Double.parseDouble(surveyAngleData[0].getValue()) * degToRad;


		
		double[][] pitchMatrix = {{1.0, 0.0, 0.0}, {0.0, Math.cos(pitch), Math.sin(pitch)}, {0.0, -Math.sin(pitch), Math.cos(pitch)}};
		double[][] yawMatrix  = {{Math.cos(yaw), 0.0, -Math.sin(yaw)}, {0.0, 1.0, 0.0}, {Math.sin(yaw), 0.0, Math.cos(yaw)}};
		double[][] rollMatrix   = {{Math.cos(roll), Math.sin(roll), 0.0}, {-Math.sin(roll), Math.cos(roll), 0.0}, {0.0, 0.0, 1.0}};
		double[][] pyMatrix   = {{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
		
		for (int ir = 0; ir < 3; ++ir)
			for (int ic = 0; ic < 3; ++ic)
			{
				eulerMatrix[ir][ic] = 0.0;
				for (int ik = 0; ik < 3; ++ik)
					pyMatrix[ir][ic] = pyMatrix[ir][ic] + pitchMatrix[ir][ik] * yawMatrix[ik][ic];
			}
		for (int ir = 0; ir < 3; ++ir)
			for (int ic = 0; ic < 3; ++ic)
				for (int ik = 0; ik < 3; ++ik)
					eulerMatrix[ir][ic] = eulerMatrix[ir][ic] + rollMatrix[ir][ik] * pyMatrix[ik][ic];
	}
	public ArrayList<Section> getSectionList() {return sectionList;}
	public double geteVin() {return eVin;}
	public double geteVout() {return eVout;}
	public LinacLego getLinacLego() {return linacLego;}
	public double getLength() {return length;}
	public int getNumOfSections() {return sectionList.size();}
	public ArrayList<BeamLineElement> getBeamLineElements() {return beamLineElementList;}
	public int getNumOfBeamLineElements() {return beamLineElementList.size();}
	public double getBeamFrequencyMHz() {return beamFrequencyMHz;}
	public FieldProfileBuilder getFieldProfileBuilder() {return fieldProfileBuilder;}
	public DataElement[][] getTwissData() {return twissData;}
	public DataElement getBeamCurrent() {return beamCurrent;}
	public ArrayList<DataElement> getDataElementList() {return dataElementList;}

	public void setFieldProfileBuilder(FieldProfileBuilder fieldProfileBuilder) {this.fieldProfileBuilder = fieldProfileBuilder;}


}
