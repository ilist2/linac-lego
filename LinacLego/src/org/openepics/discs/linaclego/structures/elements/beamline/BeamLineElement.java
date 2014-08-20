/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package org.openepics.discs.linaclego.structures.elements.beamline;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.openepics.discs.linaclego.BLEVisitor;
import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.Linac;
import org.openepics.discs.linaclego.structures.Section;
import org.openepics.discs.linaclego.structures.cell.Cell;
import org.openepics.discs.linaclego.structures.elements.ControlPoint;
import org.openepics.discs.linaclego.structures.elements.DataElement;
import org.openepics.discs.linaclego.structures.slot.Slot;

public abstract class BeamLineElement 
{
	public static final String newline = System.getProperty("line.separator");
	public static final String space = "\t";
	public static final double eVrest = 938.272046e+06;
	public static final double PI = Math.PI;
	public static final double degToRad = PI / 180.0;
	public static final double radToDeg = 180.0 / PI;
	public static final double TWOPI = 2.0 * PI;
	public static final double cvel = 299792458.0;
	public static final DecimalFormat onePlaces = new DecimalFormat("###.#");
	public static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	public static final DecimalFormat sixPlaces = new DecimalFormat("###.######");
	public static final DecimalFormat eightPlaces = new DecimalFormat("###.########");
	public static final DecimalFormat zeroPlaces = new DecimalFormat("###");
	
	private ArrayList<ControlPoint> controlPointList =  new ArrayList<ControlPoint>();
	private ArrayList<DataElement> dataElementList = new ArrayList<DataElement>();
	private SimpleXmlReader tag;
	private Slot slot = null;
	private String dynacCommand = "";
	private double eVout = -0.0;
	private double eVin = -0.0;
	private double length = 0.0;
	private double localEndZ = 0.0;
	private double localCenterZ = 0.0;
	private double localBeginZ = 0.0;
	private double[] endPosVec = {0.0, 0.0, 0.0};
	private double[][] endRotMat = { {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
	private int index;
	private int globalIndex = 0;
	private double synchronousPhaseDegrees = 0.0;
	private double quadGradientTpm = 0.0;
	private double dipoleBend = 0.0;
	private String type = null;
	private String id = null;
	private String model = "none";

	public BeamLineElement(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException
	{
		setElementTag(elementTag);
		this.slot = slot;
		this.index = index;
	
		slot.getCell().getSection().getLinac().getBeamLineElements().add(this);
		globalIndex = slot.getCell().getSection().getLinac().getBeamLineElements().size() - 1;
		
		BeamLineElement previousBeamLineElement  = getPreviousBeamLineElement();
		if (previousBeamLineElement != null)
		{
			eVout = previousBeamLineElement.geteVout();
		}
		else
		{
			eVout = slot.getCell().getSection().getLinac().geteVin();
		}
		eVin = eVout;
		addDataElements();
		readDataElementsFromXml();
	}
	public BeamLineElement(SimpleXmlReader elementTag) throws LinacLegoException 
	{
		addDataElements();
		readDataElementsFromXml();
	}
	public BeamLineElement getPreviousBeamLineElement()
	{
		int previousBeamLineElementIndex = globalIndex - 1;
		if (previousBeamLineElementIndex < 0 ) return null;
		return slot.getCell().getSection().getLinac().getBeamLineElements().get(previousBeamLineElementIndex);
	}

	public void addDataElement(String id, String value, String type, String unit)
	{
		dataElementList.add(new DataElement(id, value, type, unit));
	}
	public int numDataElements()
	{
		return dataElementList.size();
	}
	public String getLinacLegoNumber()
	{
		String sec = Integer.toString(slot.getCell().getSection().getIndex() + 1);
		while (sec.length() < 2) sec = "0" + sec;
		String cellNo = Integer.toString(slot.getCell().getIndex() + 1);
		while (cellNo.length() < 2) cellNo = "0" + cellNo;
		String slotNo = Integer.toString(slot.getIndex() + 1);
		while (slotNo.length() < 2) slotNo = "0" + slotNo;
		String elemNo = Integer.toString(getIndex() + 1);
		while (elemNo.length() < 3) elemNo = "0" + elemNo;
		return "n" + sec + "-" + cellNo + "-" + slotNo + "-" + elemNo;
	}
	private void readDataElementsFromXml() throws LinacLegoException
	{
		try 
		{
			id = tag.attribute("id");
			type = tag.attribute("type");
			try {model = tag.attribute("model");} catch (SimpleXmlException e) { model = "none";}
			SimpleXmlReader dataElementTags = tag.tagsByName("d");
			int numDataTags = dataElementTags.numChildTags();
			if (numDataTags < 1) return;
			for (int ii = 0; ii < numDataElements(); ++ii)
			{
				DataElement a = getDataElement(ii);
				int itag = 0;
				while (itag < numDataTags)
				{
					SimpleXmlReader dataTag = dataElementTags.tag(itag);
					if (a.getId().equals(dataTag.attribute("id")))
					{
						if (!a.unitMatches(dataTag.attribute("unit"))) throw new LinacLegoException(getEssId() + " " + a.getId()  + " unit does not match required unit of " + a.getUnit());
						a.setValue(dataTag.getCharacterData());
						if (!a.valueMatchsType())
						{
							if (slot != null) a.setValue(slot.getVariableValue(a.getValue()));
						}
						itag = numDataTags;
					}
					itag = itag + 1;
				}
			}
		} 
		catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(getEssId() + ":" + e.getMessage());
		}
		
	}
	public DataElement getDataElement(int ii)
	{
		return dataElementList.get(ii);
	}
	public DataElement getDataElement(String id) throws LinacLegoException
	{
		boolean found = false;
		int ielem = 0;
		DataElement matchingElement = null;
		while ((ielem < numDataElements()) && !found)
		{
			if (getDataElement(ielem).getId().equals(id))
			{
				matchingElement = getDataElement(ielem);
				found = true;
			}
			else
			{
				ielem = ielem + 1;
			}
		}
		if (!found)
		{
			return null;
			//			throw new LinacLegoException(getEssId() + ": Cannot find data element " + id);
		}
		else
		{
			return matchingElement;
		}
	}
	public double gamma(double eVkin)
	{
		double gamma = (eVkin + eVrest) / eVrest;
		return gamma;
	}
	public double beta(double eVkin)
	{
		double beta = gamma(eVkin);
		beta = Math.sqrt(1.0 - 1.0 / (beta * beta));
		return beta;
	}
	public double pc(double eVkin)
	{
		return beta(eVkin) * gamma(eVkin) * eVrest;
	}
	public double[] centerLocation()
	{
		double[] centerPosVec = {0.0, 0.0, 0.0};
		double[] beginPosVec = {0.0, 0.0, 0.0};
		if (getPreviousBeamLineElement() != null)
		{
			for (int ii = 0; ii < 3; ++ii) beginPosVec[ii] = getPreviousBeamLineElement().getEndPosVec()[ii];
		}
		for (int ii = 0; ii < 3; ++ii) centerPosVec[ii] = 0.5 * (beginPosVec[ii] + endPosVec[ii]);
		return centerPosVec;
	}
	public double[][] centerRotMat()
	{
		double[][] centerRotMat = { {0.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 0.0}};
		double[][] beginRotMat = { {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
		if (getPreviousBeamLineElement() != null)
		{
			for (int ii = 0; ii < 3; ++ii) 
				for (int ij = 0; ij < 3; ++ij) beginRotMat[ii][ij] = getPreviousBeamLineElement().getEndRotMat()[ii][ij];
		}
		for (int ii = 0; ii < 3; ++ii)
			for (int ij = 0; ij < 3; ++ij) centerRotMat[ii][ij] = 0.5 * (beginRotMat[ii][ij] + endRotMat[ii][ij]);
		return centerRotMat;
	}
	public void updateLatticeCommand() throws LinacLegoException
	{
		readDataElements();
		slot.getCell().getSection().getLinac().getLinacLego().writeStatus(getEssId());
		calcParameters();
		calcLocation();
		if (getPreviousBeamLineElement() != null)
		{
			localBeginZ = getPreviousBeamLineElement().getLocalEndZ();
		}
		else
		{
			localBeginZ = 0.0;
		}
		localCenterZ = localBeginZ + length / 2.0;
		localEndZ = localBeginZ + length;
		dynacCommand = makeDynacCommand();
	}
	public String getEssId() 
	{
		String id = "";
		try {
			id = slot.getCell().getSection().getId()
					+ "-" + slot.getCell().getId()
					+ "-" + slot.getId()
					+ "-" + getId();
		} 
		catch (LinacLegoException e) {id = "";} 
		return id;
	}
	public void printDynac(PrintWriter pw)  
	{
		String commmand = dynacCommand;
		pw.println(commmand);
	}
	public void printReportTable(PrintWriter pw) throws SimpleXmlException 
	{
		double[] centerPosVec = centerLocation();
		double[] surveyCoords = getSlot().getCell().getSection().getLinac().getSurveyCoords(centerPosVec);
		pw.print(getLinacLegoNumber());
		pw.print("," + getEssId().replace("-", ",") + ", ");
		pw.print(" ," + getType());
		pw.print(" ," + getModel());
		pw.print(" ," + sixPlaces.format((geteVout() / 1.0e6)));
		pw.print(" ," + sixPlaces.format(beta(geteVout())));
		pw.print(" ," + sixPlaces.format(getLength()));
		pw.print(" ," + sixPlaces.format(centerPosVec[0]));
		pw.print(" ," + sixPlaces.format(centerPosVec[1]));
		pw.print(" ," + sixPlaces.format(centerPosVec[2]));
		pw.print(" ," + sixPlaces.format(surveyCoords[0]));
		pw.print(" ," + sixPlaces.format(surveyCoords[1]));
		pw.print(" ," + sixPlaces.format(surveyCoords[2]));
		pw.print(" ," + sixPlaces.format(getVoltage()));
		pw.print(" ," + sixPlaces.format(getSynchronousPhaseDegrees()));
		pw.print(" ," + sixPlaces.format(getQuadGradientTpm()));
		pw.print(" ," + sixPlaces.format(getDipoleBend()));
		pw.println(" , ");
		int numControlPoints = controlPointList.size();
		if (numControlPoints > 0)
		{
			for (int icpt = 0; icpt < numControlPoints; ++icpt)
			{
				controlPointList.get(icpt).printReportTable(pw);
			}
		}
	}

	public abstract String makeDynacCommand() throws LinacLegoException;
	public abstract void calcParameters() throws LinacLegoException;
	public abstract void calcLocation() ;
	public abstract void addDataElements() ;
	public abstract void readDataElements() throws LinacLegoException ;
	public abstract double characteristicValue();
	public abstract String characteristicValueUnit();
	public Slot getSlot() {return slot;}
	public Cell getCell() {return getSlot().getCell();}
	public Section getSection() {return getCell().getSection();}
	public Linac getLinac() {return getSection().getLinac();}
	public LinacLego getLinacLego() {return getLinac().getLinacLego();}

	public SimpleXmlReader getTag() {return tag;}
	public double geteVout() {return eVout;}
	public double geteVin() {return eVin;}
	public double getLamda() {return slot.getCell().getSection().getLamda();}
	public double getLength() {return length;}
	public double getRfFreqMHz() {return slot.getCell().getSection().getRfFreqMHz();}
	public int getIndex() {return index;}
	public double[] getEndPosVec() {return endPosVec;}
	public double[][] getEndRotMat() {return endRotMat;}
	public double getLocalBeginZ() {return localBeginZ;}
	public double getLocalCenterZ() {return localCenterZ;}
	public double getLocalEndZ() {return localEndZ;}
	public String getId()  {return id;}
	public String getType() {return type;}
	public double getVoltage() {return 	(1e-6 * (geteVout() - geteVin()) / Math.cos(getSynchronousPhaseDegrees() * degToRad));}
	public double getSynchronousPhaseDegrees() {return synchronousPhaseDegrees;}
	public double getQuadGradientTpm() {return quadGradientTpm;}
	public double getDipoleBend() {return dipoleBend;}
	public ArrayList<ControlPoint> getControlPointList() {return controlPointList;}
	public int getNumOfControlPoints() {return controlPointList.size();}
	public String getModel() {return model;}
	public ArrayList<DataElement> getDataElementList() {return dataElementList;}

	public void setSynchronousPhaseDegrees(double synchronousPhaseDegrees) {this.synchronousPhaseDegrees = synchronousPhaseDegrees;}
	public void setQuadGradientTpm(double quadGradientTpm) {this.quadGradientTpm = quadGradientTpm;}
	public void setDipoleBend(double dipoleBend) {this.dipoleBend = dipoleBend;}
	public void setElementTag(SimpleXmlReader elementTag) {this.tag = elementTag;}
	public void seteVout(double eVout) {this.eVout = eVout;}
	public void setLength(double length) {this.length = length;}
	
	/**
	 * This method first visits itself and then visits all control points
	 * @param bleVisitor beam line element visitor
	 */
	public void accept(BLEVisitor bleVisitor)
	{
		acceptBLE(bleVisitor);
		int numControlPoints = controlPointList.size();
		if (numControlPoints > 0)
		{
			for (int icpt = 0; icpt < numControlPoints; ++icpt)
			{
				controlPointList.get(icpt).accept(bleVisitor);
			}
		}
	}
	
	/**
	 * This method should call specific beam line element's visitor visit method for specific beam line element
	 * @param bleVisitor beam line element visitor
	 */
	public abstract void acceptBLE(BLEVisitor bleVisitor);
}
