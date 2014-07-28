package se.lu.esss.linaclego;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import se.lu.esss.linaclego.structures.elements.beamline.BeamLineElement;

import com.astrofizzbizz.simpleXml.SimpleXmlDoc;
import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;

public class FieldProfileBuilder 
{
	private static final String delim = System.getProperty("file.separator");
	private int npts;
	private double zmax;
	private double[] fieldProfile;
	private String fieldUnit = null;
	private String lengthUnit = "mm";
	private File xmlDirectory;
	private File xmlFile;
	private File flatFileDirectory;
	private String storedEnergyUnit = "Joules";
	private String title;
	private double storedEnergy;
	private double scaleFactor;
	
	public FieldProfileBuilder(File xmlDirectory, File flatFileDirectory, String title, double scaleFactor)
	{
		this.title = title;
		this.xmlDirectory = xmlDirectory;
		this.flatFileDirectory = flatFileDirectory;
		this.scaleFactor = scaleFactor;
		xmlFile = new File(getXmlDirectory().getPath() + delim + getTitle() + ".xml");
	}
	public void createTraceWinFile(boolean checkExistence) throws LinacLegoException
	{
		String traceWinFilePath = getFlatFileDirectoryPath() + delim + getTitle() + "." + "edz";
		if (checkExistence && fileExists(traceWinFilePath)) throw new LinacLegoException(traceWinFilePath + " exists.");
		try {
			PrintWriter pw = new PrintWriter(traceWinFilePath);
			pw.println(Integer.toString(npts) + " " + Double.toString(zmax * 0.001));
			pw.println(Double.toString(scaleFactor));
			for (int ii = 0; ii <= npts; ++ii)
			{
				pw.println(Double.toString(fieldProfile[ii]));
			}
			pw.close();
			
		} catch (FileNotFoundException e) 
		{
			throw new LinacLegoException(e);
		}
	}
	public void createDynacFile(double rfFreqMHz, boolean checkExistence) throws LinacLegoException
	{
		String dynacFilePath = getFlatFileDirectoryPath() + delim + getTitle() + "." + "txt";
		if (checkExistence && fileExists(dynacFilePath)) throw new LinacLegoException(dynacFilePath + " exists.");
		try {
			PrintWriter pw = new PrintWriter(dynacFilePath);
			pw.println(BeamLineElement.onePlaces.format(rfFreqMHz * 1.0e+06));
			double z = 0;
			double E0 = fieldProfile[0] * 1.0e+06;
			if (Math.abs(E0) < 0.1) E0 = 0.1;
			pw.println(BeamLineElement.fourPlaces.format(0) + BeamLineElement.space +  BeamLineElement.onePlaces.format(E0));
			for (int ii = 1; ii <= npts; ++ii)
			{
				z = 0.1 * zmax * ((double) ii) / ((double) npts);
				pw.println(BeamLineElement.fourPlaces.format(z) + BeamLineElement.space +  BeamLineElement.onePlaces.format(fieldProfile[ii] * 1.0e+06));
			}
			pw.println ("0.0"+ BeamLineElement.space +  "0.0");
			pw.close();
			
		} catch (FileNotFoundException e) 
		{
			throw new LinacLegoException(e);
		}
	}
	public void readTraceWinFieldProfile(double storedEnergy) throws LinacLegoException
	{
		this.storedEnergy = storedEnergy;
		String traceWinFilePath = getXmlDirectory().getPath() + delim + getTitle() + "." + "edz";
		BufferedReader br;
		ArrayList<String> outputBuffer = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(traceWinFilePath));
			String line;
			while ((line = br.readLine()) != null) 
			{  
				outputBuffer.add(line);
			}
			br.close();
		} 
		catch (FileNotFoundException e) {throw new LinacLegoException(e);}
		catch (IOException e) {throw new LinacLegoException(e);}
		String delims = "[ ,\t]+";
		npts = Integer.parseInt(outputBuffer.get(0).split(delims)[0]);
		zmax = Double.parseDouble(outputBuffer.get(0).split(delims)[1]);
// Read scaleFactor but do not use it.
		double twScaleFactor = Double.parseDouble(outputBuffer.get(1).split(delims)[0]);
		if (twScaleFactor != 1.0 ) throw new LinacLegoException("edz file scale factor not equal to 1.0!");
		this.fieldUnit = "Volt/m";
// Convert zmax from meters to mm
		zmax  = zmax * 1000;
		fieldProfile = new double[npts + 1];
		for (int ii = 0; ii <= npts; ++ii)
		{
			fieldProfile[ii] = Double.parseDouble(outputBuffer.get(ii + 2).split(delims)[0]);
		}
	}
	public void createXmlFile(boolean checkExistence) throws LinacLegoException
	{
		if (checkExistence && getXmlFile().exists()) throw new LinacLegoException(getXmlFile().getPath() + " exists.");
		try {
			PrintWriter pw = new PrintWriter(getXmlFile());
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
			pw.println("<!DOCTYPE fieldProfile SYSTEM \"FieldProfile.dtd\" >");
			pw.println(
					"<fieldProfile title=\"" 
							+ getTitle() 
							+ "\" storedEnergy=\"" 
							+ Double.toString(storedEnergy) + "\"" 
							+ " length=\"" + Double.toString(zmax) + "\""
							+ " lengthUnit=\"" + lengthUnit + "\""
							+ " storedEnergyUnit=\"" + storedEnergyUnit + "\""
							+ " fieldUnit=\"" + fieldUnit + "\">");
			for (int ii = 0; ii <= npts; ++ii)
			{
				pw.print("\t<d id=\"" + Integer.toString(ii) + "\">" + Double.toString(fieldProfile[ii]) + "</d>\n");
			}
			pw.println("</fieldProfile>");
			pw.close();
			
		} catch (FileNotFoundException e) 
		{
			throw new LinacLegoException(e);
		}
	}
	public void readXmlFile() throws LinacLegoException
	{
		try 
		{
			SimpleXmlDoc xdoc = new SimpleXmlDoc(getXmlFile());
			SimpleXmlReader fieldProfileTag = new SimpleXmlReader(xdoc);
			zmax = Double.parseDouble(fieldProfileTag.attribute("length"));
			storedEnergy = Double.parseDouble(fieldProfileTag.attribute("storedEnergy"));
			fieldUnit = fieldProfileTag.attribute("fieldUnit");
			lengthUnit = fieldProfileTag.attribute("lengthUnit");
			storedEnergyUnit = fieldProfileTag.attribute("storedEnergyUnit");
			SimpleXmlReader dataTags = fieldProfileTag.tagsByName("d");
			npts = dataTags.numChildTags() - 1;
			fieldProfile = new double[npts + 1];
			for (int ii = 0; ii <= npts; ++ii)
			{
				fieldProfile[ii] = Double.parseDouble(dataTags.tag(ii).getCharacterData());
			}
		} 
		catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
		
	}
	public boolean fieldProfileNameMatches(FieldProfileBuilder fpb)
	{
		if (fpb == null) return false;
		if (!fpb.getXmlDirectory().getPath().equals(getXmlDirectory().getPath())) return false;
		if (!fpb.getTitle().equals(getTitle())) return false;
		return true;
	}
	public File getXmlFile() {return xmlFile;}
	public static boolean fileExists(String path) {return new File(path).exists(); }
	public static boolean  removeFile(String path) 
	{
		if (!fileExists(path)) return true;
		File fileToBeRemoved = new File(path);
		return fileToBeRemoved.delete();
	}

	public int getNpts() {return npts;}
	public double getZmax() {return zmax;}
	public double getStoredEnergy() {return storedEnergy;}
	public double[] getFieldProfile() {return fieldProfile;}
	public String getFieldUnit() {return fieldUnit;}
	public String getLengthUnit() {return lengthUnit;}
	public File getXmlDirectory() {return xmlDirectory;}
	public File getFlatFileDirectoryPath() {return flatFileDirectory;}
	public String getTitle() {return title;}
	public double getScaleFactor() {return scaleFactor;}
	
	public static void main(String[] args) throws LinacLegoException 
	{
		String inputDirectoryPath = "C:\\EclipseWorkSpace2014\\LinacLego\\EssLinacXmlFiles";
		String outputDirectoryPath = "C:\\EclipseWorkSpace2014\\LinacLego\\EssLinacXmlFiles";
		String title = "Test";
		double scaleFactor = 1.0;
		FieldProfileBuilder fpb = new FieldProfileBuilder(new File(inputDirectoryPath), new File(outputDirectoryPath), title, scaleFactor);
//		fpb.readTraceWinFieldProfile(100.0);
		boolean checkExistence = true;
//		fpb.createXmlFile(checkExistence);
		fpb.readXmlFile();
		fpb.createTraceWinFile(checkExistence);
	}

}
