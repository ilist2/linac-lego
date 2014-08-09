package org.openepics.discs.linaclego.tracewinreader.tracewinbledata;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlWriter;
import org.openepics.discs.linaclego.tracewinreader.TraceWinCommandReader;

public class TraceWinBendData extends TraceWinBleData
{

	public TraceWinBendData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "bend";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"bendAngleDeg",	"radOfCurvmm",	"fieldIndex",	"aperRadmm",	"HVflag",	"K1in",		"K2in",		"K1out",		"K2out"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"deg", 			"mm", 			"unit", 		"mm", 			"unit", 	"unit",		"unit",		"unit",			"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double", 		"double", 		"int", 			"double", 		"int", 		"double",	"double",	"double",		"double"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0], 			twd[1], 		twd[2], 		twd[3], 		twd[4],		"0.0",		"0.0",		"0.0",			"0.0"};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "BD";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}
