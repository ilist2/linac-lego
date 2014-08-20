package org.openepics.discs.linaclego.structures.controlsystem;

import java.util.ArrayList;

import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.elements.DataElement;

public class ControlSettingMap 
{
	private SimpleXmlReader controlSettingMapTag;
	private ArrayList<SetPoint> setPointList = new ArrayList<SetPoint>();
	private LinacLego linacLego;
	private String devName;
	private String unit;
	private String type;
	
	public SimpleXmlReader getControlSettingTag() {return controlSettingMapTag;}
	public ArrayList<SetPoint> getSetPointList() {return setPointList;}
	public LinacLego getLinacLego() {return linacLego;}
	public String getDevName() {return devName;}
	public String getUnit() {return unit;}
	public String getType() {return type;}
	
	public ControlSettingMap(SimpleXmlReader controlSettingMapTag, LinacLego linacLego) throws LinacLegoException 
	{
		this.controlSettingMapTag = controlSettingMapTag;
		this.linacLego = linacLego;
		try {devName = controlSettingMapTag.attribute("devName");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No deviceName for controlSettingMap.");}
		try {unit = controlSettingMapTag.attribute("unit");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No unit for controlSettingMap for device " + devName);}
		try {type = controlSettingMapTag.attribute("type");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No type for controlSettingMap for device " + devName);}

		SimpleXmlReader setPointTags = controlSettingMapTag.tagsByName("setPoint");
		if (setPointTags.numChildTags() > 0)
		{
			for (int itag = 0; itag < setPointTags.numChildTags(); ++itag)
			{
				try {setPointList.add(new SetPoint(setPointTags.tag(itag), this));} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
			}
			
		}
		
	}
	private String getControlSetting() throws LinacLegoException
	{
		int icount = 0;
		DataElement controlSetting;
		while (icount < linacLego.getControlSettingList().size())
		{
			controlSetting = linacLego.getControlSettingList().get(icount);
			if (devName.equals(controlSetting.getId()))
			{
				if (unit.equals(controlSetting.getUnit()))
				{
					return linacLego.getControlSettingList().get(icount).getValue();
				}
				else
				{
					throw new LinacLegoException("Unit of " + devName + " does not match setting unit");
				}
			}
		}
		return null;
	}
	public void replaceWithControlSetting() throws LinacLegoException
	{
		for (int ii = 0; ii < setPointList.size(); ++ii) setPointList.get(ii).replaceWithControlSetting();
	}

}
