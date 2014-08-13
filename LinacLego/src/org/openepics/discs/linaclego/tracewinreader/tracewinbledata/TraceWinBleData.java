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
package org.openepics.discs.linaclego.tracewinreader.tracewinbledata;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlWriter;
import org.openepics.discs.linaclego.tracewinreader.TraceWinCommandReader;

public abstract class TraceWinBleData {


	private String legoType = null;
	private String[] dataName = null;
	private String[] dataUnit = null;
	private String[] dataType = null;
	private String[] dataValue = null;
	private String legoIdIndexLabel = null;
	TraceWinCommandReader traceWinCommandReader;
	
	public String getLegoType() {return legoType;}
	public String[] getDataName() {return dataName;}
	public String[] getDataUnit() {return dataUnit;}
	public String[] getDataType() {return dataType;}
	public String[] getDataValue() {return dataValue;}
	public String getLegoIdIndexLabel() {return legoIdIndexLabel;}

	public abstract String setLegoType();
	public abstract String[] setDataName();
	public abstract String[] setDataUnit();
	public abstract String[] setDataType();
	public abstract String[] setDataValue(String[] traceWinData);
	public abstract String setLegoIdIndexLabel();

	public TraceWinBleData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		this.traceWinCommandReader = traceWinCommandReader;
		legoType = setLegoType();
		dataName = setDataName();
		dataUnit = setDataUnit();
		dataType = setDataType();
		dataValue = setDataValue(traceWinData);
		legoIdIndexLabel = setLegoIdIndexLabel();
	}
	String getValue(String name)
	{
		int iname  = 0;
		while (iname < dataName.length)
		{
			if (name.equals(dataName[iname])) return dataValue[iname];
			iname = iname + 1;
		}
		return "0";
	}
	double getDoubleValue(String name)
	{
		int iname  = 0;
		while (iname < dataName.length)
		{
			if (name.equals(dataName[iname])) return Double.parseDouble(dataValue[iname]);
			iname = iname + 1;
		}
		return 0;
	}
	public abstract void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException;
	void createBleTag(SimpleXmlWriter xw, String legoIdIndex, String legoIdIndexLabel, String legoType, String[] dataName, String[] dataUnit, String[] dataType, String[] dataValue) throws LinacLegoException
	{
		try
		{
			xw.openXmlTag("ble");
			xw.setAttribute("id", legoIdIndexLabel + legoIdIndex);
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
	public TraceWinBleData getPrevTraceWinBleData()
	{
		TraceWinBleData prevTraceWinBleData = null;
		int iListIndex = traceWinCommandReader.getTraceWinCommandListIndex() - 1;
		
		while (iListIndex >= 0) 
		{
			prevTraceWinBleData = traceWinCommandReader.getTraceWinReader().getTraceWinCommandList().get(iListIndex).getTraceWinBleData();
			if (prevTraceWinBleData != null) return prevTraceWinBleData;
			iListIndex = iListIndex - 1;
		}
		return null;
	}
	public TraceWinBleData getPrevTraceWinBleData(String legoType)
	{
		TraceWinBleData prevTraceWinBleData = null;
		int iListIndex = traceWinCommandReader.getTraceWinCommandListIndex() - 1;
		
		while (iListIndex >= 0) 
		{
			prevTraceWinBleData = traceWinCommandReader.getTraceWinReader().getTraceWinCommandList().get(iListIndex).getTraceWinBleData();
			if (prevTraceWinBleData != null) 
				if (prevTraceWinBleData.getLegoType().equals(legoType)) return prevTraceWinBleData;
			iListIndex = iListIndex - 1;
		}
		return null;
	}
	public TraceWinBleData getNextTraceWinBleData()
	{
		TraceWinBleData nextTraceWinBleData = null;
		int iListIndex = traceWinCommandReader.getTraceWinCommandListIndex() + 1;
		int listSize = traceWinCommandReader.getTraceWinReader().getTraceWinCommandList().size();
		
		while (iListIndex < listSize) 
		{
			nextTraceWinBleData = traceWinCommandReader.getTraceWinReader().getTraceWinCommandList().get(iListIndex).getTraceWinBleData();
			if (nextTraceWinBleData != null) return nextTraceWinBleData;
			iListIndex = iListIndex + 1;
		}
		return null;
	}
	public TraceWinBleData getNextTraceWinBleData(String legoType)
	{
		TraceWinBleData nextTraceWinBleData = null;
		int iListIndex = traceWinCommandReader.getTraceWinCommandListIndex() + 1;
		int listSize = traceWinCommandReader.getTraceWinReader().getTraceWinCommandList().size();
		
		while (iListIndex < listSize) 
		{
			nextTraceWinBleData = traceWinCommandReader.getTraceWinReader().getTraceWinCommandList().get(iListIndex).getTraceWinBleData();
			if (nextTraceWinBleData != null) 
				if (nextTraceWinBleData.getLegoType().equals(legoType)) return nextTraceWinBleData;
			iListIndex = iListIndex + 1;
		}
		return null;
	}


}
