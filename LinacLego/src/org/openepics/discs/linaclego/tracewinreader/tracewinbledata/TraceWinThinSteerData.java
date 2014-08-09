package org.openepics.discs.linaclego.tracewinreader.tracewinbledata;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlWriter;
import org.openepics.discs.linaclego.tracewinreader.TraceWinCommandReader;

public class TraceWinThinSteerData extends TraceWinBleData
{

	public TraceWinThinSteerData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "thinSteering";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"xkick",	"ykick",	"r",		"kickType"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"Tm",		"Tm",		"mm",		"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double",	"double",	"double",	"int"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0],	twd[1],		twd[2],	"0"};
		if (twd.length == 4) value[3] = twd[3];
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "TS";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}
