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
package org.openepics.discs.linaclego.structures.cell;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.Linac;
import org.openepics.discs.linaclego.structures.Section;
import org.openepics.discs.linaclego.structures.elements.DataElement;
import org.openepics.discs.linaclego.structures.slot.Slot;

public class Cell 
{
	private SimpleXmlReader tag;
	private Section section;
	
	private ArrayList<DataElement> variableList = new ArrayList<DataElement>();
	ArrayList<Slot> slotList = new ArrayList<Slot>();
	private int numBeamLineElements = 0;
	private double length = 0.0;
	private int index  = -1;
	private SimpleXmlReader modelTag = null;
	
	public Cell(SimpleXmlReader tag, Section section, int index) throws SimpleXmlException, LinacLegoException 
	{
		this.tag = tag;
		this.section = section;
		this.index = index;
		expand();
	}
	public void expand() throws SimpleXmlException, LinacLegoException
	{
		SimpleXmlReader slotTagList = null;
		numBeamLineElements = 0;
		length = 0.0;
		if (getModelId() != null)
		{
			SimpleXmlReader dataElements = tag.tagsByName("d");
			int numDataTags = dataElements.numChildTags();
			modelTag = getMatchingModelTag(tag.attribute("model"));
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
				variableList.add(new DataElement(varId, varValue, varType, null));
			}
			slotTagList =  modelTag.tagsByName("slot");
		}
		else
		{
			slotTagList = tag.tagsByName("slot");
			if ((slotTagList.numChildTags() - tag.numChildTags()) != 0 )
			{
				throw new LinacLegoException("Only slot tags allowed inside cell tag");
			}
		}
		for (int islot = 0; islot < slotTagList.numChildTags(); ++islot)
		{
			SimpleXmlReader slotTag = slotTagList.tag(islot);
			Slot slot  = new Slot(slotTag, this, islot);
			slotList.add(slot);
			numBeamLineElements = numBeamLineElements + slot.getNumBeamLineElements();
			length = length + slot.getLength();
		}
	}
	public String getVariableValue(String variableId) throws LinacLegoException
	{
		String tempVarId = variableId;
		if (variableId.charAt(0) == '#') tempVarId = variableId.substring(1);
		for (int ii = 0; ii < variableList.size(); ++ii  )
		{
			DataElement a =  variableList.get(ii);
			if (a.getId().equals(tempVarId)) return a.getValue();
		}
		return null;
	}
	public void printTraceWin(PrintWriter pw) throws SimpleXmlException 
	{
		for (int islots = 0; islots < slotList.size(); ++islots)
		{
			slotList.get(islots).printTraceWin(pw);
		}
	}
	public void printDynac(PrintWriter pw)  
	{
		for (int islots = 0; islots < slotList.size(); ++islots)
		{
			slotList.get(islots).printDynac(pw);
		}
	}
	public void printReportTable(PrintWriter pw) throws SimpleXmlException 
	{
		for (int islots = 0; islots < slotList.size(); ++islots)
		{
			slotList.get(islots).printReportTable(pw);
		}
	}
	private SimpleXmlReader getMatchingModelTag(String cellModelId) throws SimpleXmlException 
	{
		int numModels = section.getLinac().getLinacLego().getCellModelList().size();
		int imodel = 0;
		while (imodel < numModels)
		{
			SimpleXmlReader cellModelTag = section.getLinac().getLinacLego().getCellModelList().get(imodel).getTag();
			if (cellModelId.equals(cellModelTag.attribute("id")))
			{
				return cellModelTag;
			}
			imodel = imodel + 1;
		}
		return null;
	}
	public double geteVin()
	{
		return slotList.get(0).geteVin();
	}
	public double geteVout()
	{
		return slotList.get(slotList.size() - 1).geteVout();
	}
	public double getLocalBeginZ() 
	{
		return slotList.get(0).getLocalBeginZ();
	}
	public double getLocalEndZ() 
	{
		return slotList.get(slotList.size() - 1).getLocalEndZ();
	}
	public Slot getSlot(String slotId) throws LinacLegoException
	{
		Slot matchingSlot = null;
		for (int islot = 0; islot < slotList.size(); ++islot)
		{
			if (slotList.get(islot).getId().equals(slotId)) 
				matchingSlot = slotList.get(islot);
		}
		return matchingSlot;
	}
	public Section getSection() {return section;}
	public Linac getLinac() {return getSection().getLinac();}
	public LinacLego getLinacLego() {return getLinac().getLinacLego();}
	public String getModelId() 
	{
		try {return tag.attribute("model");} catch (SimpleXmlException e) {return null;}
	}
	public String getId() throws LinacLegoException
	{
		try {return tag.attribute("id");} 
		catch (SimpleXmlException e) { throw new LinacLegoException("Cell: " + e.getMessage());}
	}
	public ArrayList<DataElement> getVariableList() {return variableList;}
	public ArrayList<Slot> getSlotList() {return slotList;}
	public int getNumBeamLineElements() {return numBeamLineElements;}
	public double getLength() {return length;}
	public int getIndex() {return index;}
	public int getNumOfSlots() {return slotList.size();}
	public SimpleXmlReader getModelTag() {return modelTag;}
	public SimpleXmlReader getTag() {return tag;}

}
