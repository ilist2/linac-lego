package se.lu.esss.linaclego.structures.elements.beamline;

import se.lu.esss.linaclego.FieldProfileBuilder;
import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.slot.Slot;

import com.astrofizzbizz.simpleXml.SimpleXmlReader;

public class FieldMap extends BeamLineElement
{
	double lengthmm = -1;
	double xelmax = 0.0;
	double radiusmm = 0.0;
	String fieldMapFileName;
	double phisdeg = -361.0;
	double rfpdeg = -361.0;
	FieldProfileBuilder fieldProfileBuilder = null;
	int betaInt = 5; //no of iterations to get average cavity beta
	double[] phiZprofile;
	double scaleFactor = 1.0;
	private boolean newFieldProfileEncountered = true;
	
	public FieldMap(SimpleXmlReader elementTag, Slot slot, int beamLineElementIndex) throws LinacLegoException 
	{
		super(elementTag, slot, beamLineElementIndex);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("rfpdeg", null, "double", "deg");
		addDataElement("xelmax", null, "double", "unit");
		addDataElement("radiusmm", null, "double", "mm");
		addDataElement("lengthmm", null, "double", "mm");
		addDataElement("file", null, "String", "unit");
		addDataElement("scaleFactor", null, "double", "unit");
	}
	@Override
	public void readDataElements() throws LinacLegoException  
	{
		xelmax = Double.parseDouble(getDataElement("xelmax").getValue());
		radiusmm = Double.parseDouble(getDataElement("radiusmm").getValue());
		fieldMapFileName = getDataElement("file").getValue();
		lengthmm = Double.parseDouble(getDataElement("lengthmm").getValue());
		scaleFactor = Double.parseDouble(getDataElement("scaleFactor").getValue());
		rfpdeg = Double.parseDouble(getDataElement("rfpdeg").getValue());
	}
	private boolean lengthOk()
	{
		if (fieldProfileBuilder.getZmax() == lengthmm ) return true;
		return false;
	}

	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "FIELD_MAP 100";
		command = command + space + twoPlaces.format(lengthmm);
		command = command + space + twoPlaces.format(rfpdeg);
		command = command + space + twoPlaces.format(radiusmm);
		command = command + space + "0";
		command = command + space + Double.toString(xelmax);
		command = command + space + "0 0";
		command = command + space + fieldMapFileName.split("\\.")[0];
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		double fxelmax = (xelmax - 1.0) * 100.0;
		if (newFieldProfileEncountered)
		{
			command = command + "FIELD" + newline + fieldProfileBuilder.getTitle() + ".txt" + newline + scaleFactor + newline;
		}
		command = command + "CAVNUM" + newline + "1" + newline + "0.00" + space + twoPlaces.format(phisdeg) + space + eightPlaces.format(fxelmax);
		return command;
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		String xmlFileDirPath = getSlot().getCell().getSection().getLinac().getLinacLego().getXmlFileDirPath();
		String reportFileDirPath = getSlot().getCell().getSection().getLinac().getLinacLego().getReportDir().getPath();
		fieldProfileBuilder = new FieldProfileBuilder(xmlFileDirPath, reportFileDirPath,  fieldMapFileName, scaleFactor);
		FieldProfileBuilder linacFieldProfileBuilder = getSlot().getCell().getSection().getLinac().getFieldProfileBuilder();
		if (fieldProfileBuilder.fieldProfileNameMatches(linacFieldProfileBuilder))
		{
			if (scaleFactor != linacFieldProfileBuilder.getScaleFactor()) throw new LinacLegoException(getEssId() + ": scaleFactor does not match field previous scaleFactor");
			fieldProfileBuilder = getSlot().getCell().getSection().getLinac().getFieldProfileBuilder();
			newFieldProfileEncountered = false;
		}
		else
		{
			linacFieldProfileBuilder = new FieldProfileBuilder(xmlFileDirPath, reportFileDirPath,  fieldMapFileName, scaleFactor);
			getSlot().getCell().getSection().getLinac().setFieldProfileBuilder(linacFieldProfileBuilder);
			fieldProfileBuilder.readXmlFile();
			linacFieldProfileBuilder.readXmlFile();
			newFieldProfileEncountered = true;
			if (getSlot().getCell().getSection().getLinac().getLinacLego().isCreateReportDirectory())
			{
				try {fieldProfileBuilder.createTraceWinFile(true);} catch (LinacLegoException e) { throw new LinacLegoException(getEssId() + ": TraceWin file exists");}
				try {fieldProfileBuilder.createDynacFile(getRfFreqMHz(), true);} catch (LinacLegoException e) { throw new LinacLegoException(getEssId() + ": Dynac file exists");}
			}
		}

		if (!lengthOk()) 
		{
			throw new LinacLegoException(getEssId() + ": length does not match field profile Length");
		}
		setLength(0.001 * lengthmm);
		for (int ii = 0; ii < betaInt; ++ii) updateEvOut();
		updateSynchPhase();
		setSynchronousPhaseDegrees(phisdeg);
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
	private double egain(double phiDeg, boolean cos)
	{
		double dW = 0.0;
		double dz =  lengthmm * 0.001 / ((double) (fieldProfileBuilder.getNpts() - 1));
		for (int ii = 0; ii < fieldProfileBuilder.getNpts(); ++ii)
		{
			if (cos)
			{
				dW = dW + xelmax * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] * Math.cos(phiDeg * degToRad + phiZprofile[ii]) * dz;
			}
			else
			{
				dW = dW +  xelmax * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] * Math.sin(phiDeg * degToRad + phiZprofile[ii]) * dz;
			}
		}
		dW = dW * 1.0e+06; //go from MV to V
		return dW;
	}
	public void updateSynchPhase()
	{
		phisdeg = Math.atan(egain(rfpdeg,false) / egain(rfpdeg,true) ) / degToRad;
	}
	public void updateRFPhase()
	{
		double ss = -egain(-phisdeg, false);
		double cc =  egain(-phisdeg, true);
		rfpdeg = Math.atan2(ss, cc) / degToRad;
		
	}
	public void updateEvOut()
	{
		phiZprofile = new double[fieldProfileBuilder.getNpts()];
		phiZprofile[0] = 0.0;
		double dW = 0.0;
		double dz =  lengthmm * 0.001 / ((double) (fieldProfileBuilder.getNpts() - 1));
		double k0 = TWOPI / getLamda();
		for (int ii = 0; ii < fieldProfileBuilder.getNpts(); ++ii)
		{
			dW = dW + 1.0e+06 * xelmax  * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] 
					* Math.cos(rfpdeg * degToRad + phiZprofile[ii]) * dz;
			seteVout(geteVin() + dW);
			if (ii < (fieldProfileBuilder.getNpts() - 1) )
			{
				phiZprofile[ii + 1] = phiZprofile[ii] + k0 * dz / beta(geteVout());
			}
		}
	}
	@Override
	public double characteristicValue() {return Math.abs(getVoltage());}
	@Override
	public String characteristicValueUnit() {return "MV";}
}
