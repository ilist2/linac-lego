package org.openepics.discs.linaclego.structures.controlsystem;


import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;

public class SetPoint 
{
	private SimpleXmlReader setPointTag;
	private ControlSettingMap controlSettingMap;
	private String section = null;
	private String cell = null;
	private String slot = null;
	private String ble = null;
	private String dataId = null;
	private TransferFunction transferFunction;

	public SimpleXmlReader getSetPointTag() {return setPointTag;}
	public ControlSettingMap getControlSettingMap() {return controlSettingMap;}
	public String getSection() {return section;}
	public String getCell() {return cell;}
	public String getSlot() {return slot;}
	public String getBle() {return ble;}
	public String getDataId() {return dataId;}
	public TransferFunction getTransferFunction() {return transferFunction;}
	
	public SetPoint(SimpleXmlReader setPointTag, ControlSettingMap controlSettingMap) throws LinacLegoException
	{
		this.setPointTag = setPointTag;
		this.controlSettingMap = controlSettingMap;
		
		try 
		{
			section = setPointTag.attribute("section");
			cell = setPointTag.attribute("cell");
			dataId = setPointTag.attribute("dataId");
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		try {slot = setPointTag.attribute("slot");} catch (SimpleXmlException e) {slot = null;}
		try {ble = setPointTag.attribute("ble");} catch (SimpleXmlException e) {ble = null;}
		if ((ble != null) && (slot == null)) throw new LinacLegoException("ble tag defined but not slot tag in " + controlSettingMap.getDevName());
		transferFunction = new TransferFunction(this);
	}
	public SimpleXmlReader getSectionTag() throws LinacLegoException
	{
		SimpleXmlReader sectionTag = controlSettingMap.getLinacLego().getLinacTag().tagsByName("section").getTagMatchingAttribute("id", section);
		if (sectionTag == null) throw new LinacLegoException("Could not find section " + section + " in " + controlSettingMap.getDevName());
		return sectionTag;
	}
	public SimpleXmlReader getCellTag() throws LinacLegoException
	{
		SimpleXmlReader cellTag = getSectionTag().tagsByName("cell").getTagMatchingAttribute("id", cell);
		if (cellTag == null) throw new LinacLegoException("Could not find cell " + cell + " in " + controlSettingMap.getDevName());
		return cellTag;
	}
	public SimpleXmlReader getSlotTag() throws LinacLegoException
	{
		if (slot == null) return null;
		SimpleXmlReader slotTag = getCellTag().tagsByName("slot").getTagMatchingAttribute("id", slot);
		if (slotTag == null) throw new LinacLegoException("Could not find slot " + slot + " in " + controlSettingMap.getDevName());
		return slotTag;
	}
	public SimpleXmlReader getBleTag() throws LinacLegoException
	{
		if (ble == null) return null;
		SimpleXmlReader bleTag = getSlotTag().tagsByName("ble").getTagMatchingAttribute("id", ble);
		if (bleTag == null) throw new LinacLegoException("Could not find ble " + ble + " in " + controlSettingMap.getDevName());
		return bleTag;
	}
	public void replaceWithControlSetting() throws LinacLegoException 
	{
		SimpleXmlReader targetTag = getBleTag();
		if (targetTag == null) targetTag = getSlotTag();
		if (targetTag == null) targetTag = getCellTag();
		if (targetTag == null) throw new LinacLegoException("Cannot find SetPoint location in " + controlSettingMap.getDevName());
		SimpleXmlReader dataTag = targetTag.tagsByName("d").getTagMatchingAttribute("id", dataId);
		if (dataTag == null) throw new LinacLegoException("Could not find data " + dataId + " in " + controlSettingMap.getDevName());
		try {
			System.out.println("Found " + targetTag.attribute("id")  + " " + dataTag.getCharacterData());
		} catch (SimpleXmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
