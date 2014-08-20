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
package org.openepics.discs.linaclego.structures.slot;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.openepics.discs.linaclego.BLEVisitor;
import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.Linac;
import org.openepics.discs.linaclego.structures.Section;
import org.openepics.discs.linaclego.structures.cell.Cell;
import org.openepics.discs.linaclego.structures.elements.DataElement;
import org.openepics.discs.linaclego.structures.elements.beamline.BeamLineElement;
import org.openepics.discs.linaclego.structures.elements.beamline.Bend;
import org.openepics.discs.linaclego.structures.elements.beamline.Drift;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlCell;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlDriftTube;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlRfGap;
import org.openepics.discs.linaclego.structures.elements.beamline.Edge;
import org.openepics.discs.linaclego.structures.elements.beamline.FieldMap;
import org.openepics.discs.linaclego.structures.elements.beamline.Ncells;
import org.openepics.discs.linaclego.structures.elements.beamline.NcellsDblSpokeFp;
import org.openepics.discs.linaclego.structures.elements.beamline.NcellsEllipticalFp;
import org.openepics.discs.linaclego.structures.elements.beamline.Quad;
import org.openepics.discs.linaclego.structures.elements.beamline.RfGap;
import org.openepics.discs.linaclego.structures.elements.beamline.ThinSteering;

public class Slot 
{
	static final String newline = System.getProperty("line.separator");
	private ArrayList<DataElement> variableList = new ArrayList<DataElement>();
	private SimpleXmlReader tag;
	private Cell cell;
	private ArrayList<BeamLineElement> beamLineElementList =  new ArrayList<BeamLineElement>();
	private SimpleXmlReader modelTag = null;

	private double length = 0.0;
	private int numBeamLineElements = 0;
	private int index = -1;
	
	public Slot(SimpleXmlReader tag, Cell cell, int index) throws SimpleXmlException, LinacLegoException  
	{
		this.tag = tag;
		this.cell = cell;
		this.index = index;
		expand();
	}
	public void expand() throws SimpleXmlException, LinacLegoException 
	{
		SimpleXmlReader elementTagList = null;
		if (getModelId() != null)
		{
			SimpleXmlReader dataElements = tag.tagsByName("d");
			int numDataTags = dataElements.numChildTags();
			modelTag = getModelTag(tag.attribute("model"));
			SimpleXmlReader variableDef =  modelTag.tagsByName("var");
			for (int ivar = 0; ivar < variableDef.numChildTags(); ++ivar)
			{
				String varId = variableDef.tag(ivar).attribute("id");
				String varType = variableDef.tag(ivar).attribute("type");
				String varValue = null;
				if (numDataTags > 0)
				{
					int itag = 0;
					while (itag < numDataTags)
					{
						SimpleXmlReader dataTag = dataElements.tag(itag);
						if (varId.equals(dataTag.attribute("id")))
						{
							varValue = dataTag.getCharacterData();
							itag = numDataTags;
						}
						itag = itag + 1;
					}
				}
				DataElement a = new DataElement(varId, varValue, varType, null);
				if (!a.valueMatchsType())
				{
					if (cell != null) a.setValue(cell.getVariableValue(a.getValue()));
				}
				variableList.add(a);
			}
			elementTagList =  modelTag.tagsByName("ble");
		}
		else
		{
			elementTagList = tag.tagsByName("ble");
			if ((elementTagList.numChildTags() - tag.numChildTags()) != 0 )
			{
				throw new LinacLegoException("Only beamLineElement tags allowed inside slot tag");
			}
		}

		numBeamLineElements = 0;
		for (int ielem = 0; ielem < elementTagList.numChildTags(); ++ielem)
		{
			expandElements(elementTagList.tag(ielem), ielem);
			numBeamLineElements = numBeamLineElements + 1;
			length = length + beamLineElementList.get(beamLineElementList.size() - 1).getLength();
		}
	}
	public String getVariableValue(String variableId) throws LinacLegoException
	{
		String tempVarId = variableId;
		if (variableId.charAt(0) == '#') tempVarId = variableId.substring(1);
// Was done in case slot is defined in a cellModel block with a variable in a beam line element.
		ArrayList<DataElement> tempVariableList = variableList;
		if (tempVariableList.size() < 1) tempVariableList = cell.getVariableList();
		for (int ii = 0; ii < tempVariableList.size(); ++ii  )
		{
			DataElement a =  tempVariableList.get(ii);
			if (a.getId().equals(tempVarId)) return a.getValue();
		}
		return null;
	}
	private  void expandElements(SimpleXmlReader elementTag, int beamLineElementIndex) throws SimpleXmlException, LinacLegoException
	{
		if (!elementTag.tagName().equals("ble")) return;
		String elementType = elementTag.attribute("type");
		BeamLineElement newElement = null;
		if (elementType.equals("drift")) newElement =  new Drift(elementTag, this, beamLineElementIndex);
		if (elementType.equals("quad")) newElement = new Quad(elementTag, this, beamLineElementIndex);
		if (elementType.equals("ncells")) newElement = new Ncells(elementTag, this, beamLineElementIndex);
		if (elementType.equals("ellipfp")) newElement = new NcellsEllipticalFp(elementTag, this, beamLineElementIndex);
		if (elementType.equals("dblspoke")) newElement = new NcellsDblSpokeFp(elementTag, this, beamLineElementIndex);
		if (elementType.equals("fieldMap")) newElement = new FieldMap(elementTag, this, beamLineElementIndex);
		if (elementType.equals("edge")) newElement = new Edge(elementTag, this, beamLineElementIndex);
		if (elementType.equals("bend")) newElement = new Bend(elementTag, this, beamLineElementIndex);
		if (elementType.equals("thinSteering")) newElement = new ThinSteering(elementTag, this, beamLineElementIndex);
		if (elementType.equals("dtlCell")) newElement = new DtlCell(elementTag, this, beamLineElementIndex);
		if (elementType.equals("rfGap")) newElement = new RfGap(elementTag, this, beamLineElementIndex);
		if (elementType.equals("dtlDriftTube")) newElement = new DtlDriftTube(elementTag, this, beamLineElementIndex);
		if (elementType.equals("dtlRfGap")) newElement = new DtlRfGap(elementTag, this, beamLineElementIndex);
		if (newElement != null)
		{
			newElement.updateLatticeCommand();
			beamLineElementList.add(newElement);
		}
	}
	private SimpleXmlReader getModelTag(String slotModelId) throws SimpleXmlException 
	{
		int numModels = cell.getSection().getLinac().getLinacLego().getSlotModelList().size();
		int imodel = 0;
		while (imodel < numModels)
		{
			SimpleXmlReader slotModelTag = cell.getSection().getLinac().getLinacLego().getSlotModelList().get(imodel).getTag();
			if (slotModelId.equals(slotModelTag.attribute("id")))
			{
				return slotModelTag;
			}
			imodel = imodel + 1;
		}
		return null;
	}
	public void printDynac(PrintWriter pw) 
	{
		for (int ielem = 0; ielem < beamLineElementList.size(); ++ielem)
		{
			beamLineElementList.get(ielem).printDynac(pw);
		}
	}
	public void printReportTable(PrintWriter pw) throws SimpleXmlException 
	{
		for (int ielem = 0; ielem < beamLineElementList.size(); ++ielem)
		{
			beamLineElementList.get(ielem).printReportTable(pw);
		}
	}
	public double geteVin()
	{
		return beamLineElementList.get(0).geteVin();
	}
	public double geteVout()
	{
		return beamLineElementList.get(beamLineElementList.size() - 1).geteVout();
	}
	public double getLocalBeginZ() 
	{
		return beamLineElementList.get(0).getLocalBeginZ();
	}
	public double getLocalEndZ() 
	{
		return beamLineElementList.get(beamLineElementList.size() - 1).getLocalEndZ();
	}
	public double getLength() {return length;}
	public String getModelId() 
	{
		try {return tag.attribute("model");} catch (SimpleXmlException e) {return null;}
	}
	public String getId() throws LinacLegoException
	{
		try {return tag.attribute("id");} 
		catch (SimpleXmlException e) { throw new LinacLegoException("Slot: " + e.getMessage());}
	}
	public Cell getCell() {return cell;}
	public Section getSection() {return getCell().getSection();}
	public Linac getLinac() {return getSection().getLinac();}
	public LinacLego getLinacLego() {return getLinac().getLinacLego();}
	public int getNumBeamLineElements() {return numBeamLineElements;}
	public int getIndex() {return index;}
	public ArrayList<BeamLineElement> getBeamLineElementList() {return beamLineElementList;}
	public SimpleXmlReader getModelTag() {return modelTag;}
	public SimpleXmlReader getTag() {return tag;}
	
	/**
	 * Calls visit method on all beam line elements
	 * @param bleVisitor beam line element visitor
	 */
	public void accept(BLEVisitor bleVisitor) {
		for (int ielem = 0; ielem < beamLineElementList.size(); ++ielem)
		{
			beamLineElementList.get(ielem).accept(bleVisitor);
		}
	}
}
