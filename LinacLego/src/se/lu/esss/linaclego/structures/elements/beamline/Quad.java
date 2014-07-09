package se.lu.esss.linaclego.structures.elements.beamline;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.slot.Slot;

import com.astrofizzbizz.simpleXml.SimpleXmlReader;


public class Quad extends BeamLineElement 
{
	private double lengthMM = 0.0;
	private double gradTpM = 0.0;
	private double radius = 0.0;
	
	public Quad(SimpleXmlReader elementTag, Slot slot, int beamLineElementIndex) throws LinacLegoException
	{
		super(elementTag, slot, beamLineElementIndex);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("l", null, "double","mm");
		addDataElement("g", null, "double","T/m");
		addDataElement("r", null, "double","mm");
	}
	@Override
	public void readDataElements() throws NumberFormatException, LinacLegoException 
	{
		lengthMM = Double.parseDouble(getDataElement("l").getValue());
		gradTpM = Double.parseDouble(getDataElement("g").getValue());
		radius = Double.parseDouble(getDataElement("r").getValue());
	}
	@Override
	public void calcParameters() throws NumberFormatException, LinacLegoException 
	{
		setLength(0.001 * Double.parseDouble(getDataElement("l").getValue()));
		setQuadGradientTpm(gradTpM);
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
	public String makeTraceWinCommand()  
	{
		String command = "";
		command = "QUAD";
		command = command + space + Double.toString(lengthMM);
		command = command + space + Double.toString(gradTpM);
		command = command + space + Double.toString(radius);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		command = "QUADRUPO\n";
		command = command + space + fourPlaces.format(lengthMM / 10.0);
		command = command + space + sixPlaces.format(gradTpM * radius * .001 * 10);
		command = command + space + fourPlaces.format(radius / 10.0);
		return command;
	}
	public double getLengthMM() {return lengthMM;}
	public double getGradTpM() {return gradTpM;}
	public double getRadius() {return radius;}
	@Override
	public double characteristicValue() {return Math.abs(gradTpM);}
	@Override
	public String characteristicValueUnit() {return "Tesla/m";}
}
