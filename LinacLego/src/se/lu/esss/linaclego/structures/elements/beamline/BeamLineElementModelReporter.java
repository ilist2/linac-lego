package se.lu.esss.linaclego.structures.elements.beamline;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.Linac;
import se.lu.esss.linaclego.structures.Section;

public class BeamLineElementModelReporter 
{
	ArrayList<ModelList> modelListList = new ArrayList<ModelList>();
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	public BeamLineElementModelReporter(Linac linac) throws LinacLegoException
	{
		for (int isec = 0; isec < linac.getSectionList().size(); ++isec)
		{
			for (int icell = 0; icell < linac.getSectionList().get(isec).getCellList().size(); ++icell)
			{
				for (int islot = 0; islot < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().size(); ++islot)
				{
					for (int ible = 0; ible < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot).getBeamLineElementList().size(); ++ible)
					{
						addModel(linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot).getBeamLineElementList().get(ible));
					}
				}
			}
		}
	}
	public ModelList getModel(BeamLineElement element) throws LinacLegoException
	{
		int icollection = 0;
		while (icollection < modelListList.size())
		{
			if (modelListList.get(icollection).matchesModelAndType(element)) return modelListList.get(icollection);
			icollection = icollection + 1;
		}
		return null;
	}
	private void addModel(BeamLineElement element) throws LinacLegoException
	{
		ModelList modelList = getModel(element);
		if (modelList != null)
		{
			modelList.getElementList().add(element);
		}
		else
		{
			modelListList.add(new ModelList(element));
		}
	}
	public void printModels(PrintWriter pw, Linac linac) throws LinacLegoException
	{
		for (int imodel = 0; imodel < modelListList.size(); ++imodel)
		{
			pw.println(modelListList.get(imodel).printRowOfPartCounts(modelListList.get(imodel).sortByLinac(linac)));
		}
	}
	class ModelList 
	{
		String modelId;
		String typeId ;
		ArrayList<BeamLineElement> elementList = new ArrayList<BeamLineElement>();
		ModelList(BeamLineElement element) throws LinacLegoException
		{
			this.modelId = element.getModel();
			this.typeId = element.getType();
			elementList = new ArrayList<BeamLineElement>();
			elementList.add(element);
		}
		private boolean matchesModelAndType(BeamLineElement element) throws LinacLegoException
		{
			if (!this.modelId.equals(element.getModel())) return false;
			if (!this.typeId.equals(element.getType())) return false;
			return true;
		}
		private ArrayList<BeamLineElement> sortBySection(Section section)
		{
			ArrayList<BeamLineElement> elementListForSection = new ArrayList<BeamLineElement>();
			for (int ii = 0; ii < elementList.size(); ++ii)
			{
				if (elementList.get(ii).getSlot().getCell().getSection().equals(section)) elementListForSection.add(elementList.get(ii));
			}
			return elementListForSection;
		}
		private  ArrayList<ArrayList<BeamLineElement>> sortByLinac(Linac linac)
		{
			ArrayList<ArrayList<BeamLineElement>> elementListForLinac = new ArrayList<ArrayList<BeamLineElement>>();
			for (int isection = 0; isection < linac.getNumOfSections(); ++isection)
			{
				elementListForLinac.add(sortBySection(linac.getSectionList().get(isection)));
			}
			elementListForLinac.add(elementList);
			return elementListForLinac;
		}
		public double[] minAvgMaxCharacteristicValues()
		{
			double minValue = 1.0e+33;
			double maxValue = -1.0e+33;
			double averageValue = 0.0;
			double numElements = 0.0;
			for (int ij = 0; ij < getElementList().size(); ++ij)
			{
				double cv = getElementList().get(ij).characteristicValue();
				if (minValue > cv) minValue = cv;
				if (maxValue < cv) maxValue = cv;
				averageValue = averageValue + cv;
				numElements = numElements + 1;
			}
			averageValue = averageValue / numElements;
			double[] minAvgMax = {minValue, averageValue, maxValue};
			return minAvgMax;
		}
		private String printRowOfPartCounts(ArrayList<ArrayList<BeamLineElement>> elementListForLinac)
		{
			String rowString = typeId + "," + modelId;
			for (int isection = 0; isection < elementListForLinac.size(); ++isection)
			{
				rowString = rowString + "," + elementListForLinac.get(isection).size();
			}
			double[] minAvgMax = minAvgMaxCharacteristicValues();
			rowString = rowString + "," + fourPlaces.format(minAvgMax[0]) + "," + fourPlaces.format(minAvgMax[1]) + "," +fourPlaces.format(minAvgMax[2]);
			rowString = rowString + "," + getElementList().get(0).characteristicValueUnit();
			return rowString;
		}
		String getModelId() {return modelId;}
		String getTypeId() {return typeId;}
		ArrayList<BeamLineElement> getElementList() {return elementList;}
	}
}
