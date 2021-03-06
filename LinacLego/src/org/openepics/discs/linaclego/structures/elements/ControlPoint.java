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
package org.openepics.discs.linaclego.structures.elements;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.simplexml.SimpleXmlWriter;
import org.openepics.discs.linaclego.structures.Linac;
import org.openepics.discs.linaclego.structures.Section;
import org.openepics.discs.linaclego.structures.cell.Cell;
import org.openepics.discs.linaclego.structures.elements.beamline.BeamLineElement;
import org.openepics.discs.linaclego.structures.slot.Slot;

public class ControlPoint 
{
	public static final String newline = System.getProperty("line.separator");
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	public static final String space = "\t";
	private ArrayList<DataElement> dataElementList = new ArrayList<DataElement>();
	private SimpleXmlReader tag;
	private int index;
	private BeamLineElement beamLineElement;
	private double[] endPosVec = {0.0, 0.0, 0.0};
	private double[] endLocalPosVec = {0.0, 0.0, 0.0};
	private String type = null;
	private String id = null;
	private String model = "none";
	
	public ControlPoint(SimpleXmlReader tag, BeamLineElement beamLineElement, int index) throws LinacLegoException
	{
		setTag(tag);
		this.beamLineElement = beamLineElement;
		this.index = index;
		checkAttributes();
		addDataElements();
		readDataElementsFromXml();
		readDataElements();
		beamLineElement.getSlot().getCell().getSection().getLinac().getLinacLego().writeStatus(getEssId());
		calcLocation();	
	}
	public void checkAttributes() throws LinacLegoException
	{
		try {
			String  sectionName = tag.attribute("section");
			String cellName = tag.attribute("cell");
			String slotName = tag.attribute("slot");
			String bleName = tag.attribute("ble");
			if (!sectionName.equals(beamLineElement.getSlot().getCell().getSection().getId())) throw new LinacLegoException(getEssId() + ": Section attribute  does not match section id.");
			if (!cellName.equals(beamLineElement.getSlot().getCell().getId())) throw new LinacLegoException(getEssId() + ": Cell attribute  does not match cell id.");
			if (!slotName.equals(beamLineElement.getSlot().getId())) throw new LinacLegoException(getEssId() + ": slot attribute  does not match slot id.");
			if (!bleName.equals(beamLineElement.getId())) throw new LinacLegoException(getEssId() + ": ble attribute  does not match ble id.");
		} catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
		return;
	}
	public void addDataElements() 
	{
		addDataElement("dxmm", "0.0", "double", "mm");
		addDataElement("dymm", "0.0", "double", "mm");
		addDataElement("dzmm", "0.0", "double", "mm");
	}
	public void readDataElements() throws NumberFormatException, LinacLegoException  
	{
		if (getDataElement("dxmm").getValue() != null) endLocalPosVec[0] = Double.parseDouble(getDataElement("dxmm").getValue()) * 0.001;
		if (getDataElement("dymm").getValue() != null) endLocalPosVec[1] = Double.parseDouble(getDataElement("dymm").getValue()) * 0.001;
		if (getDataElement("dzmm").getValue() != null) endLocalPosVec[2] = Double.parseDouble(getDataElement("dzmm").getValue()) * 0.001;
	}
	public void printTraceWin(PrintWriter pw) throws SimpleXmlException 
	{
		if (!beamLineElement.getSlot().getCell().getSection().getLinac().getLinacLego().isPrintControlPoints()) return;
		String command = ";" + getName().replace(":", "-")
				+ space + "dxmm=" + Double.toString(endLocalPosVec[0] * 1000.0)
				+ space + "dymm=" + Double.toString(endLocalPosVec[1] * 1000.0)
				+ space + "dzmm=" + Double.toString(endLocalPosVec[2] * 1000.0);
		pw.println(command);
	}
	public void printXmlPbs(SimpleXmlWriter xw) throws LinacLegoException   
	{
		for (int idata = 0; idata < dataElementList.size(); ++idata)
		{
			dataElementList.get(idata).writeTag(xw);
		}
	}
	public void printReportTable(PrintWriter pw) throws SimpleXmlException 
	{
		if (!beamLineElement.getSlot().getCell().getSection().getLinac().getLinacLego().isPrintControlPoints()) return;
		double[] surveyCoords = beamLineElement.getSlot().getCell().getSection().getLinac().getSurveyCoords(endPosVec);
		pw.print(getName() + ",");
		pw.print(getEssId().replace("-", ","));
		pw.print(" ," + getType());
		pw.print(" ," + getModel());
		pw.print(" ,");
		pw.print(" ,");
		pw.print(" ,");
		pw.print(" ," + fourPlaces.format(endPosVec[0]));
		pw.print(" ," + fourPlaces.format(endPosVec[1]));
		pw.print(" ," + fourPlaces.format(endPosVec[2]));
		pw.print(" ," + fourPlaces.format(surveyCoords[0]));
		pw.print(" ," + fourPlaces.format(surveyCoords[1]));
		pw.print(" ," + fourPlaces.format(surveyCoords[2]));
		pw.print(" ,");
		pw.print(" ,");
		pw.print(" ,");
		pw.print(" ,");
		pw.println(" , ");
	}
	public String getEssId() 
	{
		String id = "";
		try {
			id = beamLineElement.getSlot().getCell().getSection().getId()
					+ "-" + beamLineElement.getSlot().getCell().getId()
					+ "-" + beamLineElement.getSlot().getId()
					+ "-" + beamLineElement.getId()
					+ "-" + getId();
		} 
		catch (LinacLegoException e) {id = "";} 
		return id;
	}
	private void calcLocation() 
	{
		double[] localOutputVec = {0.0, 0.0, 0.0};
		double[][] centerRotMat = beamLineElement.centerRotMat();
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
				localOutputVec[ir] = localOutputVec[ir] + centerRotMat[ir][ic] * endLocalPosVec[ic];
			endPosVec[ir] = beamLineElement.centerLocation()[ir] + localOutputVec[ir];
		}
	}
	public void addDataElement(String id, String value, String type, String unit)
	{
		dataElementList.add(new DataElement(id, value, type, unit));
	}
	public int numDataElements()
	{
		return dataElementList.size();
	}
	private void readDataElementsFromXml() throws LinacLegoException
	{
		try 
		{
			type = tag.attribute("type");
			id = tag.attribute("id");
			try {model = tag.attribute("model");} catch (SimpleXmlException e) { model = "none";}
			SimpleXmlReader dataElements = tag.tagsByName("d");
			int numDataTags = dataElements.numChildTags();
			if (numDataTags < 1) return;
			for (int ii = 0; ii < numDataElements(); ++ii)
			{
				DataElement a = getDataElement(ii);
				for (int ij = 0; ij < numDataTags; ++ij)
				{
					SimpleXmlReader dataTag = dataElements.tag(ij);
					if (a.getId().equals(dataTag.attribute("id")))
					{
						if (!a.unitMatches(dataTag.attribute("unit"))) throw new LinacLegoException(getEssId() + " " + a.getId()  + " unit does not match required unit of " + a.getUnit());
						a.setValue(dataTag.getCharacterData());
						if (!a.valueMatchsType())
						{
							if (beamLineElement.getSlot() != null) a.setValue(beamLineElement.getSlot().getVariableValue(a.getValue()));
						}
					}
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
	public DataElement getDataElement(String id)
	{
		for (int ii = 0; ii < numDataElements(); ++ii  )
		{
			DataElement a = getDataElement(ii);
			if (a.getId().equals(id)) return a;
		}
		return null;
	}
	public BeamLineElement getBeamLineElement() {return beamLineElement;}
	public Slot getSlot() {return getBeamLineElement().getSlot();}
	public Cell getCell() {return getSlot().getCell();}
	public Section getSection() {return getCell().getSection();}
	public Linac getLinac() {return getSection().getLinac();}
	public LinacLego getLinacLego() {return getLinac().getLinacLego();}
	public SimpleXmlReader getTag() {return tag;}
	public int getIndex() {return index;}
	public double[] getEndLocalPosVec() {return endLocalPosVec;}
	public String getId() {return id;}
	public String getType()  {return type;}
	public String getModel() {return model;}
	public String getName() throws SimpleXmlException {return tag.attribute("devName");}
	public double[] getEndPosVec() {return endPosVec;}
	public ArrayList<DataElement> getDataElementList() {return dataElementList;}

	public void setTag(SimpleXmlReader tag) {this.tag = tag;}

}
