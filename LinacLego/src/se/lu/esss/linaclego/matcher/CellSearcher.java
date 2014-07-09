package se.lu.esss.linaclego.matcher;

import java.util.ArrayList;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.lu.esss.linaclego.LinacLego;
import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.cell.CellModel;
import se.lu.esss.linaclego.structures.elements.DataElement;

import com.astrofizzbizz.simpleXml.SimpleXmlDoc;
import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;

public class CellSearcher 
{
	private LinacLego linacLego;
	private ArrayList<CellMatch> cellMatches;
	
	public LinacLego getLinacLego() {return linacLego;}
	public ArrayList<CellMatch> getSlotMatches() {return cellMatches;}
	
	public CellSearcher(LinacLego linacLego) throws LinacLegoException
	{
		this.linacLego = linacLego;
		int numCellModels = linacLego.getCellModelList().size();
		if (numCellModels < 1) throw new LinacLegoException("No cell models defined in header tag");
		cellMatches = new ArrayList<CellMatch>();
		for (int imodel = 0; imodel < numCellModels; ++imodel)
		{
			searchLinacForCells(linacLego.getCellModelList().get(imodel));
		}
	}
	public void replaceCellsWithMatches() throws LinacLegoException
	{
		for (int imatch = 0; imatch < cellMatches.size(); ++imatch)
		{
			cellMatches.get(imatch).replaceSlotListWithCell("xx");
		}
		try 
		{
			SimpleXmlReader linacTag = linacLego.getLinacTag();
			SimpleXmlReader sectionTags = linacTag.tagsByName("section");
			if (sectionTags != null )
			{
				for (int isection = 0; isection < sectionTags.numChildTags(); ++isection)
				{
					SimpleXmlReader cellTags = sectionTags.tag(isection).tagsByName("cell");
					DocumentFragment sectionFragment = linacLego.getSimpleXmlDoc().getXmlDoc().createDocumentFragment();
					int cellCounter = 10;
					if (cellTags != null )
					{
						for (int icell = 0; icell < cellTags.numChildTags(); ++icell)
						{
							String cellModelId = null;
							try {cellModelId = cellTags.tag(icell).attribute("model");} catch (SimpleXmlException e1) {}
							if (cellModelId == null)
							{
								NodeList childNodes  = cellTags.tag(icell).getXmlNode().getChildNodes();
								ArrayList<Node> elementNodes = new ArrayList<Node>();
								for (int ic = 0; ic < childNodes.getLength(); ++ic)
								{
									if (childNodes.item(ic).getNodeType() == Node.ELEMENT_NODE)
									{
										elementNodes.add(childNodes.item(ic));
									}
								}
								int ielementCount = 0;
								boolean openNewCellElementTag = false;
								boolean closeNewCellElementTag = false;
								boolean addSlotTagToSlotList = false;
								boolean addCellToNewCellList = false;
								String previousElement = "";
								String currentElement = "";
								ArrayList<Node> slotList = new ArrayList<Node>();
								Element cell;
								while (ielementCount < elementNodes.size())
								{
									previousElement = currentElement;
									currentElement = elementNodes.get(ielementCount).getNodeName();
									if (currentElement.equals("slot") && !previousElement.equals("slot"))
										openNewCellElementTag = true;
									else
										openNewCellElementTag = false;
									if (!currentElement.equals("slot") && previousElement.equals("slot")) 
										closeNewCellElementTag = true;
									else
										closeNewCellElementTag = false;
									if (currentElement.equals("slot") && (ielementCount == (elementNodes.size() - 1))) 
										closeNewCellElementTag = true;
									if (currentElement.equals("slot") ) 
										addSlotTagToSlotList = true;
									else
										addSlotTagToSlotList = false;
									if (currentElement.equals("cell") ) 
										addCellToNewCellList = true;
									else
										addCellToNewCellList = false;
									if (openNewCellElementTag) slotList = new ArrayList<Node>();
									if (addSlotTagToSlotList) slotList.add(elementNodes.get(ielementCount));
									if (closeNewCellElementTag)
									{
										cell = linacLego.getSimpleXmlDoc().getXmlDoc().createElement("cell");
										cell.setAttribute("id",  "cell" + addLeadingZeros(cellCounter, 3));
										for (int islot  = 0; islot < slotList.size(); ++islot)
										{
											cell.appendChild(linacLego.getSimpleXmlDoc().getXmlDoc().createTextNode("\n\t\t\t\t"));
											cell.appendChild(slotList.get(islot));
										}
										cell.appendChild(linacLego.getSimpleXmlDoc().getXmlDoc().createTextNode("\n\t\t\t"));
										sectionFragment.appendChild(linacLego.getSimpleXmlDoc().getXmlDoc().createTextNode("\n\t\t\t"));
										sectionFragment.appendChild(cell);
										cellCounter = cellCounter + 10;
									}
									if (addCellToNewCellList)
									{
										elementNodes.get(ielementCount).getAttributes().getNamedItem("id").setNodeValue("cell" + addLeadingZeros(cellCounter, 3));
										sectionFragment.appendChild(linacLego.getSimpleXmlDoc().getXmlDoc().createTextNode("\n\t\t\t"));
										sectionFragment.appendChild(elementNodes.get(ielementCount));
										cellCounter = cellCounter + 10;
									}
									ielementCount = ielementCount + 1;
								}
								sectionTags.tag(isection).getXmlNode().replaceChild(sectionFragment, cellTags.tag(icell).getXmlNode());
							}
							else
							{
								cellTags.tag(icell).setAttribute("id", "cell" + addLeadingZeros(cellCounter, 3));
								cellCounter = cellCounter + 10;
							}
						}
						NodeList childNodes = sectionTags.tag(isection).getXmlNode().getChildNodes();
						int inode = childNodes.getLength() - 1;
						while (inode > 0)
						{
							if (childNodes.item(inode).getNodeType() == Node.TEXT_NODE)
							{
								if (childNodes.item(inode - 1).getNodeType() == Node.TEXT_NODE)
								{
									sectionTags.tag(isection).getXmlNode().removeChild(childNodes.item(inode - 1));
								}
							}
							inode = inode - 1;
						}
					}
				}
			}
		}
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	private void searchLinacForCells(CellModel cellModel) throws LinacLegoException
	{
		try 
		{
			SimpleXmlReader linacTag = linacLego.getLinacTag();
			SimpleXmlReader sectionTags = linacTag.tagsByName("section");
			SimpleXmlReader cellModelSlots = cellModel.getTag().tagsByName("slot");
			int numSlots = cellModelSlots.numChildTags();
			if (sectionTags != null )
			{
				for (int isection = 0; isection < sectionTags.numChildTags(); ++isection)
				{
					SimpleXmlReader cellTags = sectionTags.tag(isection).tagsByName("cell");
					if (cellTags != null )
					{
						for (int icell = 0; icell < cellTags.numChildTags(); ++icell)
						{
							String cellModelId = null;
							try {cellModelId = cellTags.tag(icell).attribute("model");} catch (SimpleXmlException e1) {}
							if (cellModelId == null)
							{
								SimpleXmlReader slotTagList = cellTags.tag(icell).tagsByName("slot");
								if (slotTagList != null )
								{
									if (slotTagList != null) 
									{
										int startTag = 0;
										int numTagsLeft = slotTagList.numChildTags() - startTag;
										int islotPos = 0;
										while(numTagsLeft >= numSlots)
										{
											boolean tagsMatch 
												= slotTagsMatch(cellModelSlots.tag(islotPos), slotTagList.tag(islotPos + startTag));
											islotPos = islotPos + 1;
											if (tagsMatch)
											{
												if (islotPos == numSlots)
												{
													ArrayList<SimpleXmlReader> matchingSlots = new ArrayList<SimpleXmlReader>();
													for (int im = 0; im < numSlots; ++im) matchingSlots.add(slotTagList.tag(startTag + im));
													CellMatch cellMatch = new CellMatch(cellModel,  matchingSlots);
													cellMatches.add(cellMatch);
													getLinacLego().writeStatus("Found cellModel match " + cellMatch.getCellModelId() + " at " + cellMatch.getId());
													startTag = islotPos + startTag;
													numTagsLeft = slotTagList.numChildTags() - startTag;
													islotPos = 0;
												}
											}
											else
											{
												startTag = islotPos + startTag;
												numTagsLeft = slotTagList.numChildTags() - startTag;
												islotPos = 0;
											}
										}
									}
									
								}
							}
						}
					}
				}
			}
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public boolean slotTagsMatch(SimpleXmlReader slotTag1, SimpleXmlReader slotTag2) throws LinacLegoException
	{
		boolean matches = true;
		try 
		{
			String slotTagModelId1 = "";
			String slotTagModelId2 = "";
			try{slotTagModelId1 = slotTag1.attribute("model");} catch (SimpleXmlException e){slotTagModelId1 = "";}
			try{slotTagModelId2 = slotTag2.attribute("model");} catch (SimpleXmlException e){slotTagModelId2 = "";}
			if (!slotTagModelId1.equals(slotTagModelId2)) return false;
			if (!slotTagModelId1.equals(""))
			{
				SimpleXmlReader dataListTag1 = slotTag1.tagsByName("d");
				SimpleXmlReader dataListTag2 = slotTag2.tagsByName("d");
				if ((dataListTag1 == null) && (dataListTag2 == null)) return true;
				if (dataListTag1 == null) return false;
				if (dataListTag2 == null) return false;
				if (dataListTag1.numChildTags() != dataListTag2.numChildTags()) return false;
				for (int i1 = 0; i1 < dataListTag1.numChildTags(); ++i1)
				{
					DataElement data1 = new DataElement(dataListTag1.tag(i1));
					int i2 = 0;
					boolean dataMatch = false;
					while ((i2 < dataListTag2.numChildTags()) && !dataMatch)
					{
						DataElement data2 = new DataElement(dataListTag2.tag(i2));
						if (data2.matchesDataElementModel(data1)) dataMatch = true;
						i2 = i2 + 1;
					}
					if (!dataMatch) return false;
				}
			}
			else
			{
				SimpleXmlReader bleTagList1 = slotTag1.tagsByName("ble");
				SimpleXmlReader bleTagList2 = slotTag2.tagsByName("ble");
				if (bleTagList1.numChildTags() != bleTagList2.numChildTags()) return false;
				int ible = 0;
				while (ible < bleTagList1.numChildTags())
				{
					if (!beamLineElementTagsMatch(bleTagList1.tag(ible), bleTagList2.tag(ible))) return false;
					ible = ible + 1;
				}
			}
		} 
		catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
		return matches;
	}
	public boolean beamLineElementTagsMatch(SimpleXmlReader bleTag1, SimpleXmlReader bleTag2) throws LinacLegoException
	{
		boolean matches = true;
		try 
		{
			if (!bleTag1.attribute("type").equals(bleTag2.attribute("type"))) return false;
			SimpleXmlReader dataListTag1 = bleTag1.tagsByName("d");
			SimpleXmlReader dataListTag2 = bleTag2.tagsByName("d");
			if ((dataListTag1 == null) && (dataListTag2 == null)) return true;
			if (dataListTag1 == null) return false;
			if (dataListTag2 == null) return false;
			if (dataListTag1.numChildTags() != dataListTag2.numChildTags()) return false;
			for (int i1 = 0; i1 < dataListTag1.numChildTags(); ++i1)
			{
				DataElement data1 = new DataElement(dataListTag1.tag(i1));
				int i2 = 0;
				boolean dataMatch = false;
				while ((i2 < dataListTag2.numChildTags()) && !dataMatch)
				{
					DataElement data2 = new DataElement(dataListTag2.tag(i2));
					if (data2.matchesDataElementModel(data1)) dataMatch = true;
					i2 = i2 + 1;
				}
				if (!dataMatch) return false;
			}
		} 
		catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
		return matches;
	}
	String addLeadingZeros(int counter, int stringLength)
	{
		String scounter = Integer.toString(counter);
		while (scounter.length() < stringLength) scounter = "0" + scounter;
		return scounter;
	}
	public void saveXmlFile(String filePath) throws LinacLegoException
	{
		try {linacLego.getSimpleXmlDoc().saveXmlDocument(filePath);} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public static void main(String[] args) throws LinacLegoException, SimpleXmlException 
	{
		String xmlFileDirPath = "C:\\EclipseWorkSpace2014\\LinacLego\\EssLinacXmlFiles";
		String xmlFileName = "SpokeOptimus6.xml";
		LinacLego linacLego = new LinacLego(new SimpleXmlDoc(xmlFileDirPath + "\\" + xmlFileName), null);
		CellSearcher cellSearcher = new CellSearcher(linacLego);
		cellSearcher.replaceCellsWithMatches();
		cellSearcher.saveXmlFile(xmlFileDirPath + "\\SpokeOptimus7.xml");
	}

}
