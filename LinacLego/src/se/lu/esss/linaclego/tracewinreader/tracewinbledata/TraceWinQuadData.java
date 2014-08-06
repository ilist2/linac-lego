package se.lu.esss.linaclego.tracewinreader.tracewinbledata;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.tracewinreader.TraceWinCommandReader;

import com.astrofizzbizz.simpleXml.SimpleXmlWriter;

public class TraceWinQuadData extends TraceWinBleData
{

	public TraceWinQuadData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "quad";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"l",		"g",		"r"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"mm",		"T/m",		"mm"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double",	"double",	"double"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0],	twd[1],		twd[2]};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "QU";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}
