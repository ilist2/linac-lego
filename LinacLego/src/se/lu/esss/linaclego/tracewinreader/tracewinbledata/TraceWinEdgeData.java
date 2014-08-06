package se.lu.esss.linaclego.tracewinreader.tracewinbledata;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.tracewinreader.TraceWinCommandReader;

import com.astrofizzbizz.simpleXml.SimpleXmlWriter;

public class TraceWinEdgeData extends TraceWinBleData
{

	public TraceWinEdgeData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "edge";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"poleFaceAngleDeg",	"radOfCurvmm",	"gapmm",	"K1",		"K2",		"aperRadmm",	"HVflag"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"deg",				"mm",			"mm",		"unit",		"unit",		"mm",			"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double",				"double",		"double",	"double",	"double",	"double",		"int"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0],				twd[1],			twd[2],		twd[3],		twd[4],		twd[5],			twd[6]};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "EG";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}
