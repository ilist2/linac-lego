package org.openepics.discs.linaclego.tracewinreader.tracewinbledata;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlWriter;
import org.openepics.discs.linaclego.tracewinreader.TraceWinCommandReader;

public class TraceWinNcellsData extends TraceWinBleData
{

	public TraceWinNcellsData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "ncells";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"mode",	"ncells",	"betag",	"e0t",		"theta",	"radius",	"p",	"ke0ti",	"ke0to",	"dzi",		"dzo",		"betas",	"ts",		"kts",		"k2ts",		"ti",		"kti",		"k2ti",		"to",		"kto",		"k2to"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"unit",	"unit",		"m",		"Volt/m",	"deg",		"mm",		"unit",	"unit",		"unit",		"mm",		"mm",		"m",		"unit",		"unit",		"unit",		"unit",		"unit",		"unit",		"unit",		"unit", 	"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"int",	"int",		"double",	"double",	"double",	"double",	"int",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double",	"double"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0],	twd[1],		twd[2],		twd[3],		twd[4],		twd[5],		twd[6],	twd[7],		twd[8],		twd[9],		twd[10],	twd[11],	twd[12],	twd[13],	twd[14],	twd[15],	twd[16],	twd[17],	twd[18],	twd[19],	twd[20]};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "NC";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}
