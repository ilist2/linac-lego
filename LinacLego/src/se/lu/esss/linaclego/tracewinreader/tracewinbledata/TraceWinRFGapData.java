package se.lu.esss.linaclego.tracewinreader.tracewinbledata;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.tracewinreader.TraceWinCommandReader;

import com.astrofizzbizz.simpleXml.SimpleXmlWriter;

public class TraceWinRFGapData extends TraceWinBleData
{

	public TraceWinRFGapData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "rfGap";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"voltsT",	"rfPhaseDeg",	"radApermm",	"phaseFlag",	"betaS",	"tts",		"ktts",		"k2tts",	"ks",		"k2s"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"Volt",	"deg",			"mm",			"unit",			"unit",		"unit",		"unit",		"unit",		"unit",		"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double",	"double",		"double",		"int",			"double",	"double",	"double",	"double",	"double",	"double"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0],	twd[1],			twd[2],			twd[3],			twd[4],		twd[5],		twd[6],		twd[7],		twd[8],		twd[9]};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "RF";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}