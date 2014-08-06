package se.lu.esss.linaclego.tracewinreader.tracewinbledata;

import java.text.DecimalFormat;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.tracewinreader.TraceWinCommandReader;

import com.astrofizzbizz.simpleXml.SimpleXmlWriter;

public class TraceWinDtlCellData extends TraceWinBleData
{

	public TraceWinDtlCellData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "dtlCell";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"cellLenmm",	"q1Lenmm",	"q2Lenmm",	"cellCentermm",	"grad1Tpm",	"grad2Tpm",	"voltsT",	"voltMult",	"rfPhaseDeg",	"phaseAdd",	"radApermm",	"phaseFlag",	"betaS",	"tts",		"ktts",		"k2tts"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"mm",			"mm",		"mm",		"mm",			"T/m",		"T/m",		"Volt",		"unit",		"deg",			"deg",		"mm",			"unit",			"m",		"unit",		"unit",		"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double",		"double",	"double",	"double",		"double",	"double",	"double",	"double",	"double",		"double",	"double",		"int",			"double",	"double",	"double",	"double"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0], 		 twd[1],	twd[2],		twd[3],			twd[4],		twd[5],		twd[6],		"1.0",		twd[7],			"0.0",		twd[8],			twd[9],			twd[10],	twd[11],	twd[12],	twd[13]};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "DT";}
	public void createBleTagEcho(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException
	{
		TraceWinBleData prevTraceWinBleData = getPrevTraceWinBleData(getLegoType());
		double quadLen = getDoubleValue("q1Lenmm");
		if (prevTraceWinBleData != null ) quadLen = quadLen + prevTraceWinBleData.getDoubleValue("q2Lenmm");
		double totalGapLen = getDoubleValue("cellLenmm") - getDoubleValue("q1Lenmm") - getDoubleValue("q2Lenmm");
		double driftLen1 = 0.5 * getDoubleValue("cellLenmm") - getDoubleValue("q1Lenmm") - getDoubleValue("cellCentermm");
		double driftLen2 = totalGapLen - driftLen1;
		double gapLen = 2.0 * getDoubleValue("radApermm");
		double noseConeDnLen = driftLen1 - gapLen / 2.0;
		double noseCone2 = driftLen2 - gapLen / 2.0;

		double prevTotalGapLen = 0.0;
		double prevDriftLen1 = 0.0;
		double prevDriftLen2 = 0.0;
		double prevGapLen = 0.0;
		double noseConeUpLen = 0.0;
		
		if (prevTraceWinBleData != null)
		{
			prevTotalGapLen = prevTraceWinBleData.getDoubleValue("cellLenmm") 
					- prevTraceWinBleData.getDoubleValue("q1Lenmm") - prevTraceWinBleData.getDoubleValue("q2Lenmm");
			prevDriftLen1 = 0.5 * prevTraceWinBleData.getDoubleValue("cellLenmm") 
					- prevTraceWinBleData.getDoubleValue("q1Lenmm") - prevTraceWinBleData.getDoubleValue("cellCentermm");
			prevDriftLen2 = prevTotalGapLen - prevDriftLen1;
			prevGapLen = 2.0 * prevTraceWinBleData.getDoubleValue("radApermm");
			noseConeUpLen = prevDriftLen2 - prevGapLen / 2.0;
		}
		DecimalFormat fourPlaces = new DecimalFormat("####.####");
		String[] driftTubeTwd = new String[5];
		driftTubeTwd[0] = fourPlaces.format(noseConeUpLen);
		driftTubeTwd[1] = fourPlaces.format(noseConeDnLen);
		driftTubeTwd[2] = fourPlaces.format(quadLen);
		driftTubeTwd[3] = getValue("radApermm");
		driftTubeTwd[4] = getValue("grad1Tpm");
		TraceWinDtlDriftTubeData traceWinDtlDriftTubeData = new TraceWinDtlDriftTubeData(driftTubeTwd, traceWinCommandReader);
		createBleTag(xw, legoIdIndex, 
				traceWinDtlDriftTubeData.getLegoIdIndexLabel(), 
				traceWinDtlDriftTubeData.getLegoType(), 
				traceWinDtlDriftTubeData.getDataName(), 
				traceWinDtlDriftTubeData.getDataUnit(), 
				traceWinDtlDriftTubeData.getDataType(), 
				traceWinDtlDriftTubeData.getDataValue());
		
		String[] rfGaptwd = new String[9];
		rfGaptwd[0] = getValue("voltsT");		
		rfGaptwd[1] = getValue("rfPhaseDeg");		
		rfGaptwd[2] = getValue("radApermm");		
		rfGaptwd[3] = getValue("phaseFlag");		
		rfGaptwd[4] = getValue("betaS");		
		rfGaptwd[5] = getValue("tts");		
		rfGaptwd[6] = getValue("ktts");		
		rfGaptwd[7] = getValue("k2tts");		
		rfGaptwd[8] = fourPlaces.format(gapLen);		
		
		TraceWinDtlRfGapData traceWinDtlRfGapData = new TraceWinDtlRfGapData(rfGaptwd, traceWinCommandReader);
		createBleTag(xw, legoIdIndex, 
				traceWinDtlRfGapData.getLegoIdIndexLabel(), 
				traceWinDtlRfGapData.getLegoType(), 
				traceWinDtlRfGapData.getDataName(), 
				traceWinDtlRfGapData.getDataUnit(), 
				traceWinDtlRfGapData.getDataType(), 
				traceWinDtlRfGapData.getDataValue());
		
		TraceWinBleData nextTraceWinBleData = getNextTraceWinBleData(getLegoType());
		if (nextTraceWinBleData == null)
		{
			driftTubeTwd[0] = fourPlaces.format(noseCone2);
			driftTubeTwd[1] = "0.00";
			driftTubeTwd[2] = getValue("q2Lenmm");
			driftTubeTwd[3] = getValue("radApermm");
			driftTubeTwd[4] = getValue("grad2Tpm");;
			traceWinDtlDriftTubeData = new TraceWinDtlDriftTubeData(driftTubeTwd, traceWinCommandReader);
			createBleTag(xw, legoIdIndex, 
					traceWinDtlDriftTubeData.getLegoIdIndexLabel(), 
					traceWinDtlDriftTubeData.getLegoType(), 
					traceWinDtlDriftTubeData.getDataName(), 
					traceWinDtlDriftTubeData.getDataUnit(), 
					traceWinDtlDriftTubeData.getDataType(), 
					traceWinDtlDriftTubeData.getDataValue());
			
		}

	}


}
