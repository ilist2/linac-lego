package org.openepics.discs.linaclego.structures.controlsystem;

import org.openepics.discs.linaclego.LinacLegoException;

public class TransferFunction 
{
	double[] coeff = {0.0, 1.0, 0.0, 0.0, 0.0};
	private SetPoint setPoint;
	
	public TransferFunction(SetPoint setPoint)
	{
		
	}
	public double getValue() throws LinacLegoException
	{
		double setting;
		try
		{
			setting = Double.parseDouble(setPoint.getControlSetting().getSettingData().getValue());
		}catch (NumberFormatException nfe)
		{
			throw new LinacLegoException("Value does not match type in " + setPoint.getControlSetting().getDevName());
		}
		double tfPow = 1.0;
		double tf = 0.0;
		for (int itf = 0; itf < 5; ++itf)
		{
			tf = tf;
		}
		return 0.0;
	}

}
