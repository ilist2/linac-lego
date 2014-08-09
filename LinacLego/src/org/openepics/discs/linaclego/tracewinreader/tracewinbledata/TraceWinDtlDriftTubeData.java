package org.openepics.discs.linaclego.tracewinreader.tracewinbledata;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlWriter;
import org.openepics.discs.linaclego.tracewinreader.TraceWinCommandReader;

public class TraceWinDtlDriftTubeData extends TraceWinBleData
{

	public TraceWinDtlDriftTubeData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "dtlDriftTube";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"noseConeUpLen",	"noseConeDnLen",	"quadLen",	"radius",	"quadGrad"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"mm",				"mm",				"mm",		"mm",		"T/m"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double", 		"double", 			"double",	"double",	"double"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0],			twd[1],				twd[2],		twd[3],		twd[4]};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "DT";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}
