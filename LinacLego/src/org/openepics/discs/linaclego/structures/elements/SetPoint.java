package org.openepics.discs.linaclego.structures.elements;

import java.util.ArrayList;

import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.slot.SlotVariable;

public class SetPoint 
{
	private SimpleXmlReader tag;
	private ArrayList<SlotVariable> variables = new ArrayList<SlotVariable>();
	private LinacLego linacLego;
	
	public SimpleXmlReader getTag() {return tag;}
	public ArrayList<SlotVariable> getVariables() {return variables;}
	public LinacLego getLinacLego() {return linacLego;}
	
	public SetPoint(SimpleXmlReader tag, LinacLego linacLego)
	{
		this.tag = tag;
		this.linacLego = linacLego;
		SimpleXmlReader dataTag = tag.tagsByName("d");
		if (dataTag.numChildTags() > 0)
		{
			System.out.println("yipee");
		}
		
	}

}
