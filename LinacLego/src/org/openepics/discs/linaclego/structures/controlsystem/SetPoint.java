package org.openepics.discs.linaclego.structures.controlsystem;


import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;

public class SetPoint 
{
	private SimpleXmlReader setPointTag;
	private ControlSetting controlSetting;
	private String section = null;
	private String cell = null;
	private String slot = null;
	private String ble = null;
	private String dataId = null;
	private TransferFunction transferFunction;

	public SimpleXmlReader getSetPointTag() {return setPointTag;}
	public ControlSetting getControlSetting() {return controlSetting;}
	public String getSection() {return section;}
	public String getCell() {return cell;}
	public String getSlot() {return slot;}
	public String getBle() {return ble;}
	public String getDataId() {return dataId;}
	public TransferFunction getTransferFunction() {return transferFunction;}
	
	public SetPoint(SimpleXmlReader setPointTag, ControlSetting controlSetting) throws LinacLegoException
	{
		this.setPointTag = setPointTag;
		this.controlSetting = controlSetting;
		
		try 
		{
			section = setPointTag.attribute("section");
			cell = setPointTag.attribute("cell");
			dataId = setPointTag.attribute("dataId");
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		try {slot = setPointTag.attribute("slot");} catch (SimpleXmlException e) {slot = null;}
		try {ble = setPointTag.attribute("ble");} catch (SimpleXmlException e) {ble = null;}
		SimpleXmlReader transferFunctionTags = setPointTag.tagsByName("tf");
		if (transferFunctionTags.numChildTags() > 0)
		{
			
		}
		else
		{
			transferFunction = new TransferFunction(this);
		}
	}

}
