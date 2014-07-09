package se.lu.esss.linaclego.structures.elements.beamline;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.slot.Slot;

import com.astrofizzbizz.simpleXml.SimpleXmlReader;

public class ThinSteering  extends BeamLineElement
{
	private double xkick;
	private double ykick;
	private double rmm;
	private int kickType;
	public ThinSteering(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("xkick", null, "double","Tm");
		addDataElement("ykick", null, "double","Tm");
		addDataElement("r", null, "double", "mm");
		addDataElement("kickType", null, "double", "unit");
	}
	@Override
	public void readDataElements() throws NumberFormatException, LinacLegoException 
	{
		xkick = Double.parseDouble(getDataElement("xkick").getValue());
		ykick = Double.parseDouble(getDataElement("ykick").getValue());
		rmm = Double.parseDouble(getDataElement("r").getValue());
		kickType = Integer.parseInt(getDataElement("kickType").getValue());
	}
	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "THIN_STEERING";
		command = command + space + Double.toString(xkick);
		command = command + space + Double.toString(ykick);
		command = command + space + Double.toString(rmm);
		command = command + space + Integer.toString(kickType);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
//TODO implement thing steering in DYNAC
		String command = ";Thin Steering not in DYNAC";
		return command;
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		setLength(0.0);
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
	public double getXkick() {return xkick;}
	public double getYkick() {return ykick;}
	public double getRmm() {return rmm;}
	public int getKickType() {return kickType;}
	@Override
	public double characteristicValue() {return Math.abs(xkick) + Math.abs(ykick);}
	@Override
	public String characteristicValueUnit() {return "Tesla-m";}
}
