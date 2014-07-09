package se.lu.esss.linaclego.structures.elements.beamline;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.slot.Slot;

import com.astrofizzbizz.simpleXml.SimpleXmlReader;

public class RfGap extends BeamLineElement
{
	double voltsT = 0.0;
	double rfPhaseDeg = 0.0;
	double radApermm = 0.0;
	int phaseFlag = 1;
	double betaS = 0.0;
	double tts = 0.0;
	double ktts = 0.0;
	double k2tts = 0.0;
	double ks = 0.0;
	double k2s = 0.0;

	public RfGap(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("voltsT", null, "double", "Volt");
		addDataElement("rfPhaseDeg", null, "double", "deg");
		addDataElement("radApermm", null, "double", "mm");
		addDataElement("phaseFlag", null, "int", "unit");
		addDataElement("betaS", null, "double", "m");
		addDataElement("tts", null, "double", "unit");
		addDataElement("ktts", null, "double", "unit");
		addDataElement("k2tts", null, "double", "unit");
		addDataElement("ks", null, "double", "unit");
		addDataElement("k2s", null, "double", "unit");
	}
	@Override
	public void readDataElements() throws NumberFormatException, LinacLegoException 
	{
		voltsT = Double.parseDouble(getDataElement("voltsT").getValue());
		rfPhaseDeg = Double.parseDouble(getDataElement("rfPhaseDeg").getValue());
		radApermm = Double.parseDouble(getDataElement("radApermm").getValue());
		phaseFlag = Integer.parseInt(getDataElement("phaseFlag").getValue());
		betaS = Double.parseDouble(getDataElement("betaS").getValue());
		tts = Double.parseDouble(getDataElement("tts").getValue());
		ktts = Double.parseDouble(getDataElement("ktts").getValue());
		k2tts = Double.parseDouble(getDataElement("k2tts").getValue());
		ks = Double.parseDouble(getDataElement("ks").getValue());
		k2s = Double.parseDouble(getDataElement("k2s").getValue());
	}
	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "GAP";
		command = command + space + Double.toString(voltsT);
		command = command + space + Double.toString(rfPhaseDeg);
		command = command + space + Double.toString(radApermm);
		command = command + space + Integer.toString(phaseFlag);
		command = command + space + Double.toString(betaS);
		command = command + space + Double.toString(tts);
		command = command + space + Double.toString(ktts);
		command = command + space + Double.toString(k2tts);
		command = command + space + Double.toString(ks);
		command = command + space + Double.toString(k2s);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		command = "BUNCHER\n";
		command = command + space + Double.toString(voltsT / 1.0e6);
		command = command + space + Double.toString(rfPhaseDeg);
		command = command + space + Integer.toString(getSlot().getCell().getSection().getRfHarmonic());
		command = command + space + Double.toString(radApermm / 10.0);
		return command;
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		setLength(0.00);
		seteVout(geteVin() + voltsT * Math.cos(rfPhaseDeg * degToRad));
	}
	@Override
	public void calcLocation() 
	{
		BeamLineElement previousBeamLineElement = getPreviousBeamLineElement();
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)
			{
				if (previousBeamLineElement != null)
					getEndRotMat()[ir][ic] = previousBeamLineElement.getEndRotMat()[ir][ic];
			}
			if (previousBeamLineElement != null)
				getEndPosVec()[ir] = previousBeamLineElement.getEndPosVec()[ir];
		}
	
		double[] localInputVec = {0.0, 0.0, getLength()};
		double[] localOutputVec = {0.0, 0.0, 0.0};
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
				localOutputVec[ir] = localOutputVec[ir] + getEndRotMat()[ir][ic] * localInputVec[ic];
			getEndPosVec()[ir] = getEndPosVec()[ir] + localOutputVec[ir];
		}
	}
	@Override
	public double characteristicValue() {return Math.abs(voltsT);}
	@Override
	public String characteristicValueUnit() {return "Volts";}
}
