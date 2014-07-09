package se.lu.esss.linaclego.matcher;

import java.util.ArrayList;

import org.w3c.dom.DocumentFragment;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.slot.SlotModel;

import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;

public class SlotMatch {

	private SlotModel slotModel;
	private SimpleXmlReader linacTag;
	private SimpleXmlReader sectionTag;
	private SimpleXmlReader cellTag;
	private SimpleXmlReader slotTag;
	private ArrayList<SimpleXmlReader> matchingBleElements;
	
	public SlotModel getSlotModel() {return slotModel;}
	public SimpleXmlReader getLinacTag() {return linacTag;}
	public SimpleXmlReader getSectionTag() {return sectionTag;}
	public SimpleXmlReader getCellTag() {return cellTag;}
	public SimpleXmlReader getSlotTag() {return slotTag;}
	public ArrayList<SimpleXmlReader> getMatchingBleElements() {return matchingBleElements;}
	
	public SlotMatch(SlotModel slotModel, ArrayList<SimpleXmlReader> matchingBleElements)
	{
		this.slotModel = slotModel;
		this.matchingBleElements = matchingBleElements;
		this.slotTag = new SimpleXmlReader(matchingBleElements.get(0).getXmlNode().getParentNode());
		this.cellTag = new SimpleXmlReader(slotTag.getXmlNode().getParentNode());
		this.sectionTag = new SimpleXmlReader(cellTag.getXmlNode().getParentNode());
		this.linacTag = new SimpleXmlReader(sectionTag.getXmlNode().getParentNode());
	}
	public void replaceBleListWithSlot(String slotName) throws LinacLegoException
	{
		DocumentFragment fraggy = createSlotFragment(slotName);
		getSlotTag().getXmlNode().insertBefore(fraggy, matchingBleElements.get(0).getXmlNode());
		for (int ii = 0; ii < matchingBleElements.size(); ++ii)
		{
			getSlotTag().getXmlNode().removeChild(matchingBleElements.get(ii).getXmlNode().getNextSibling());
			getSlotTag().getXmlNode().removeChild(matchingBleElements.get(ii).getXmlNode());
		}
	}
	public DocumentFragment createSlotFragment(String name) throws LinacLegoException
	{
		return slotModel.createSlotFragment(this, name);
	}
	String getId() throws LinacLegoException
	{
		return getSectionId() + "." + getCellId() + "." + getSlotId() + "." + getBleId();
	}
	String getSlotModelId() throws LinacLegoException {try {return slotModel.getTag().attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getBleId() throws LinacLegoException {try {return matchingBleElements.get(0).attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getSlotId() throws LinacLegoException {try {return slotTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getCellId() throws LinacLegoException {try {return cellTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getSectionId() throws LinacLegoException {try {return sectionTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	public static void main(String[] args) 
	{
	}

}
