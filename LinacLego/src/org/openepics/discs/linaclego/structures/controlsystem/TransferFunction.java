package org.openepics.discs.linaclego.structures.controlsystem;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;

public class TransferFunction 
{
	private final static int numCoeff = 5;
	double[] coeff;
	
	public TransferFunction(SetPoint setPoint) throws LinacLegoException
	{
		coeff = new double[numCoeff];
		for (int itf = 0; itf < numCoeff; ++itf) coeff[itf] = 0.0;
		SimpleXmlReader transferFunctionTags = setPoint.getSetPointTag().tagsByName("tf");
		if (transferFunctionTags.numChildTags() > 0)
		{
			for (int itf = 0; itf < transferFunctionTags.numChildTags(); ++itf)
			{
				int ipower = -1;
				try {ipower = Integer.parseInt(transferFunctionTags.tag(itf).attribute("power"));}
				catch (NumberFormatException nfe) {throw new LinacLegoException("Transfer function power attribute not an integer " + setPoint.getControlSettingMap().getDevName());} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
				if ((0 <= ipower) && (ipower < numCoeff))
				{
					try {coeff[ipower] = Double.parseDouble(transferFunctionTags.tag(itf).getCharacterData());}
					catch (NumberFormatException nfe) {throw new LinacLegoException("Transfer function coeff not a number " + setPoint.getControlSettingMap().getDevName());} 
					catch (SimpleXmlException e) {throw new LinacLegoException(e);} 
				}
				else
				{
					throw new LinacLegoException("Transfer function power not in range " + setPoint.getControlSettingMap().getDevName());
				}
			}
		}
		else
		{
			coeff[1] = 1.0;
		}
	}
	public double getValue(double devSetting) throws LinacLegoException
	{
		double tfPow = 1.0;
		double tf = 0.0;
		for (int itf = 0; itf < numCoeff; ++itf)
		{
			tf = tf + coeff[itf] * tfPow;
			tfPow = tfPow * devSetting;
		}
		return tf;
	}

}
