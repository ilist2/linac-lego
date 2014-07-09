package se.lu.esss.linaclego.matcher;

import java.util.ArrayList;

import org.w3c.dom.DocumentFragment;

import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.cell.CellModel;

import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;

public class CellMatch {

	private CellModel cellModel;
	private SimpleXmlReader linacTag;
	private SimpleXmlReader sectionTag;
	private SimpleXmlReader cellTag;
	private ArrayList<SimpleXmlReader> matchingSlots;
	
	public CellModel getCellModel() {return cellModel;}
	public SimpleXmlReader getLinacTag() {return linacTag;}
	public SimpleXmlReader getSectionTag() {return sectionTag;}
	public SimpleXmlReader getCellTag() {return cellTag;}
	public ArrayList<SimpleXmlReader> getMatchingSlotElements() {return matchingSlots;}
	
	public CellMatch(CellModel cellModel, ArrayList<SimpleXmlReader> matchingSlots)
	{
		this.cellModel = cellModel;
		this.matchingSlots = matchingSlots;
		this.cellTag = new SimpleXmlReader(matchingSlots.get(0).getXmlNode().getParentNode());
		this.sectionTag = new SimpleXmlReader(cellTag.getXmlNode().getParentNode());
		this.linacTag = new SimpleXmlReader(sectionTag.getXmlNode().getParentNode());
	}
	public void replaceSlotListWithCell(String cellName) throws LinacLegoException
	{
		DocumentFragment fraggy = createSlotFragment(cellName);
		getCellTag().getXmlNode().insertBefore(fraggy, matchingSlots.get(0).getXmlNode());
		for (int ii = 0; ii < matchingSlots.size(); ++ii)
		{
			getCellTag().getXmlNode().removeChild(matchingSlots.get(ii).getXmlNode().getNextSibling());
			getCellTag().getXmlNode().removeChild(matchingSlots.get(ii).getXmlNode());
		}
	}
	public DocumentFragment createSlotFragment(String name) throws LinacLegoException
	{
		return cellModel.createCellFragment(this, name);
	}
	String getId() throws LinacLegoException
	{
		return getSectionId() + "." + getCellId() + "." + getSlotId();
	}
	String getCellModelId() throws LinacLegoException {try {return cellModel.getTag().attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getSlotId() throws LinacLegoException {try {return matchingSlots.get(0).attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getCellId() throws LinacLegoException {try {return cellTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getSectionId() throws LinacLegoException {try {return sectionTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}

}
