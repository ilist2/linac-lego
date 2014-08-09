package org.openepics.discs.linaclego.structures.elements.beamline;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.slot.Slot;

public class Edge extends BeamLineElement
{
	private double poleFaceAngleDeg = 0.0;
	private double radOfCurvmm = -1.0;
	private double gapmm = -1.0;
	private double K1 = -1.0;
	private double K2 = -1.0;
	private double aperRadmm = -1.0;
	private int HVflag = 0;
	
	public Edge(SimpleXmlReader elementTag, Slot slot, int beamLineElementIndex) throws LinacLegoException
	{
		super(elementTag, slot, beamLineElementIndex);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("poleFaceAngleDeg", null, "double","deg");
		addDataElement("radOfCurvmm", null, "double","mm");
		addDataElement("gapmm", null, "double","mm");
		addDataElement("K1", null, "double","unit");
		addDataElement("K2", null, "double","unit");
		addDataElement("aperRadmm", null, "double","mm");
		addDataElement("HVflag", null, "int", "unit");
	}
	@Override
	public void readDataElements() throws LinacLegoException
	{
		poleFaceAngleDeg = Double.parseDouble(getDataElement("poleFaceAngleDeg").getValue());
		radOfCurvmm = Double.parseDouble(getDataElement("radOfCurvmm").getValue());
		gapmm = Double.parseDouble(getDataElement("gapmm").getValue());
		K1 = Double.parseDouble(getDataElement("K1").getValue());
		K2 = Double.parseDouble(getDataElement("K2").getValue());
		aperRadmm = Double.parseDouble(getDataElement("aperRadmm").getValue());
		HVflag = Integer.parseInt(getDataElement("HVflag").getValue());
	}
	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "EDGE";
		command = command + space + Double.toString(poleFaceAngleDeg);
		command = command + space + Double.toString(radOfCurvmm);
		command = command + space + Double.toString(gapmm);
		command = command + space + Double.toString(K1);
		command = command + space + Double.toString(K2);
		command = command + space + Double.toString(aperRadmm);
		command = command + space + Integer.toString(HVflag);
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
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = ";Edge Component not in DYNAC";
		return command;
	}
	@Override
	public double characteristicValue() {return 0;}
	@Override
	public String characteristicValueUnit() {return "";}
}
