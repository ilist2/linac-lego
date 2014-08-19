package org.openepics.discs.linaclego.structures.controlsystem;

import java.util.ArrayList;

import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.elements.DataElement;

public class ControlSetting 
{
	private SimpleXmlReader controlSettingTag;
	private ArrayList<SetPoint> setPointList = new ArrayList<SetPoint>();
	private LinacLego linacLego;
	private String devName;
	private DataElement settingData;
	
	public SimpleXmlReader getControlSettingTag() {return controlSettingTag;}
	public ArrayList<SetPoint> getSetPointList() {return setPointList;}
	public LinacLego getLinacLego() {return linacLego;}
	public String getDevName() {return devName;}
	public DataElement getSettingData() {return settingData;}
	
	public ControlSetting(SimpleXmlReader controlSettingTag, LinacLego linacLego) throws LinacLegoException 
	{
		this.controlSettingTag = controlSettingTag;
		this.linacLego = linacLego;
		try {devName = controlSettingTag.attribute("devName");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No deviceName for cntrlSet");}
		SimpleXmlReader dataTags = controlSettingTag.tagsByName("d");
		if (dataTags.numChildTags() > 0)
		{
			SimpleXmlReader dataTag = null;
			String settingId;
			try 
			{
				dataTag = dataTags.tag(0);
				settingId = dataTag.attribute("id");
				if (settingId.equals("setting"))
				{
					settingData = new DataElement(dataTag);
				}
				else
				{
					throw new LinacLegoException("No setting data for cntrlSet " + devName);
				}
			} 
			catch (SimpleXmlException e) {throw new LinacLegoException("No setting data for cntrlSet " + devName);} 
			catch (LinacLegoException e) {throw new LinacLegoException("No setting data for cntrlSet " + devName);}
		}
		else
		{
			throw new LinacLegoException("No data setting tag for cntrlSet " + devName);
		}
		SimpleXmlReader setPointTags = controlSettingTag.tagsByName("setPnt");
		if (setPointTags.numChildTags() > 0)
		{
			for (int itag = 0; itag < setPointTags.numChildTags(); ++itag)
			{
				try {setPointList.add(new SetPoint(setPointTags.tag(itag), this));} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
			}
			
		}
		
	}

}
