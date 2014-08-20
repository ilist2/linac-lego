/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package org.openepics.discs.linaclego.structures.elements.beamline;


import org.openepics.discs.linaclego.BLEVisitor;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.slot.Slot;

public class Drift extends BeamLineElement 
{
	private double lengthMM;
	private double rMM;
	private double ryMM;
	
	public Drift(SimpleXmlReader elementTag, Slot slot, int beamLineElementIndex) throws LinacLegoException
	{
		super(elementTag, slot, beamLineElementIndex);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("l", null, "double", "mm");
		addDataElement("r", null, "double", "mm");
		addDataElement("ry", null, "double", "mm");
	}
	@Override
	public void readDataElements() throws LinacLegoException
	{
		lengthMM = Double.parseDouble(getDataElement("l").getValue());
		rMM = Double.parseDouble(getDataElement("r").getValue());
		ryMM = Double.parseDouble(getDataElement("ry").getValue());
	}
	@Override
	public void calcParameters() 
	{
		setLength(0.001 * lengthMM);
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
		command = "DRIFT";
		command = command + space + fourPlaces.format(lengthMM);
		command = command + space + fourPlaces.format(rMM);
		command = command + space + fourPlaces.format(ryMM);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		command = "DRIFT\n";
		command = command + space + fourPlaces.format(lengthMM / 10.0);
		return command;
	}
	public double getLengthMM() {return lengthMM;}
	public double getrMM() {return rMM;}
	public double getRyMM() {return ryMM;}
	@Override
	public double characteristicValue() {return getLengthMM();}
	@Override
	public String characteristicValueUnit() {return "mm";}
	
	/**
	 * Calls visit method on beam line element visitor
	 * @param bleVisitor beam line element visitor
	 */
	@Override
	public void accept(BLEVisitor bleVisitor) {
		bleVisitor.visit(this);
	}
}
