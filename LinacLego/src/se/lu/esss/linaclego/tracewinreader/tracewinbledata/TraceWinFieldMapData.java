package se.lu.esss.linaclego.tracewinreader.tracewinbledata;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.tracewinreader.TraceWinCommandReader;

import com.astrofizzbizz.simpleXml.SimpleXmlWriter;

public class TraceWinFieldMapData extends TraceWinBleData
{

	public TraceWinFieldMapData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "fieldMap";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"rfpdeg",	"xelmax",	"radiusmm",	"lengthmm",	"file",		"scaleFactor"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"deg",	 "unit", 	"mm", 		"mm", 		"unit",		"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double",	 "double", 	"double", 	"double", 	"string",	"double"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[2], 	twd[5],		twd[3],		twd[1],		twd[8],		"1.0"};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "FM";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}
