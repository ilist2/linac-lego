package org.openepics.discs.linaclego.structures.slot;

import java.util.ArrayList;

import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.matcher.SlotMatch;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

public class SlotModel 
{
	private SimpleXmlReader tag;
	private ArrayList<SlotVariable> variables = new ArrayList<SlotVariable>();
	private LinacLego linacLego;
	
	public SimpleXmlReader getTag() {return tag;}
	public ArrayList<SlotVariable> getVariables() {return variables;}
	public LinacLego getLinacLego() {return linacLego;}

	public SlotModel(SimpleXmlReader tag, LinacLego linacLego) throws LinacLegoException
	{
		this.tag = tag;
		this.linacLego = linacLego;
		SimpleXmlReader variableTags = tag.tagsByName("var");
		if (variableTags.numChildTags() > 0)
		{
			for (int ii = 0; ii < variableTags.numChildTags(); ++ii)
			{
				try {variables.add(new SlotVariable(variableTags.tag(ii), this));} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
			}
		}
	}
	public DocumentFragment createSlotFragment(SlotMatch slotMatch, String name) throws LinacLegoException
	{
		Document xdoc = linacLego.getSimpleXmlDoc().getXmlDoc();
		try 
		{
			Element slot = xdoc.createElement("slot");
			slot.setAttribute("model", tag.attribute("id"));
			slot.setAttribute("id",  tag.attribute("id") + name);
			if (variables.size() > 0)
			{
				slot.appendChild(xdoc.createTextNode("\n\t\t\t\t\t"));
				for (int ii = 0; ii < variables.size(); ++ii)
				{
					slot.appendChild(variables.get(ii).getVariableElement(slotMatch, xdoc));
					if (ii < (variables.size() - 1))
					{
						slot.appendChild(xdoc.createTextNode("\n\t\t\t\t\t"));
					}
					else
					{
						slot.appendChild(xdoc.createTextNode("\n\t\t\t\t"));
					}
				}
			}
			DocumentFragment documentFragment = xdoc.createDocumentFragment();
			documentFragment.appendChild(slot);
			documentFragment.appendChild(xdoc.createTextNode("\n\t\t\t\t"));
			return documentFragment;
		} 
		catch (DOMException e) {throw new LinacLegoException(e);} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
}
