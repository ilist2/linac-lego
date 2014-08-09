package org.openepics.discs.linaclego.structures.elements;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.structures.Linac;
import org.openepics.discs.linaclego.structures.Section;

public class ControlPointModelReporter 
{
	ArrayList<ModelList> modelListList = new ArrayList<ModelList>();
	public ControlPointModelReporter(Linac linac) throws LinacLegoException
	{
		for (int isec = 0; isec < linac.getSectionList().size(); ++isec)
		{
			for (int icell = 0; icell < linac.getSectionList().get(isec).getCellList().size(); ++icell)
			{
				for (int islot = 0; islot < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().size(); ++islot)
				{
					for (int ible = 0; ible < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot).getBeamLineElementList().size(); ++ible)
					{
						for (int icnpt = 0; icnpt < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot).getBeamLineElementList().get(ible).getControlPointList().size(); ++icnpt)
						{
							addModel(linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot).getBeamLineElementList().get(ible).getControlPointList().get(icnpt));
						}
					}
				}
			}
		}
	}
	public ModelList getModel(ControlPoint element) throws LinacLegoException
	{
		int icollection = 0;
		while (icollection < modelListList.size())
		{
			if (modelListList.get(icollection).matchesModelAndType(element)) return modelListList.get(icollection);
			icollection = icollection + 1;
		}
		return null;
	}
	private void addModel(ControlPoint element) throws LinacLegoException
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
		ArrayList<ControlPoint> elementList = new ArrayList<ControlPoint>();
		ModelList(ControlPoint element) throws LinacLegoException
		{
			this.modelId = element.getModel();
			this.typeId = element.getType();
			elementList = new ArrayList<ControlPoint>();
			elementList.add(element);
		}
		private boolean matchesModelAndType(ControlPoint element) throws LinacLegoException
		{
			if (!this.modelId.equals(element.getModel())) return false;
			if (!this.typeId.equals(element.getType())) return false;
			return true;
		}
		private ArrayList<ControlPoint> sortBySection(Section section)
		{
			ArrayList<ControlPoint> elementListForSection = new ArrayList<ControlPoint>();
			for (int ii = 0; ii < elementList.size(); ++ii)
			{
				if (elementList.get(ii).getBeamLineElement().getSlot().getCell().getSection().equals(section)) elementListForSection.add(elementList.get(ii));
			}
			return elementListForSection;
		}
		private  ArrayList<ArrayList<ControlPoint>> sortByLinac(Linac linac)
		{
			ArrayList<ArrayList<ControlPoint>> elementListForLinac = new ArrayList<ArrayList<ControlPoint>>();
			for (int isection = 0; isection < linac.getNumOfSections(); ++isection)
			{
				elementListForLinac.add(sortBySection(linac.getSectionList().get(isection)));
			}
			elementListForLinac.add(elementList);
			return elementListForLinac;
		}
		private String printRowOfPartCounts(ArrayList<ArrayList<ControlPoint>> elementListForLinac)
		{
			String rowString = typeId + "," + modelId;
			for (int isection = 0; isection < elementListForLinac.size(); ++isection)
			{
				rowString = rowString + "," + elementListForLinac.get(isection).size();
			}
			return rowString;
		}
		String getModelId() {return modelId;}
		String getTypeId() {return typeId;}
		ArrayList<ControlPoint> getElementList() {return elementList;}
	}
}
