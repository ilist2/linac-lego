package se.lu.esss.linaclego.structures.cell;

import java.util.ArrayList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import se.lu.esss.linaclego.LinacLego;
import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.matcher.CellMatch;

import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;

public class CellModel 
{
	private SimpleXmlReader tag;
	private ArrayList<CellVariable> variables = new ArrayList<CellVariable>();
	private LinacLego linacLego;

	public SimpleXmlReader getTag() {return tag;}
	public ArrayList<CellVariable> getVariables() {return variables;}
	public LinacLego getLinacLego() {return linacLego;}

	public CellModel(SimpleXmlReader tag, LinacLego linacLego) throws LinacLegoException
	{
		this.tag = tag;
		this.linacLego = linacLego;
		SimpleXmlReader variableTags = tag.tagsByName("var");
		if (variableTags.numChildTags() > 0)
		{
			for (int ii = 0; ii < variableTags.numChildTags(); ++ii)
			{
				try {variables.add(new CellVariable(variableTags.tag(ii), this));} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
			}
		}
	}
	public DocumentFragment createCellFragment(CellMatch cellMatch, String name) throws LinacLegoException
	{
		Document xdoc = getLinacLego().getSimpleXmlDoc().getXmlDoc();
		try 
		{
			Element cell = xdoc.createElement("cell");
			cell.setAttribute("model", tag.attribute("id"));
			cell.setAttribute("id",  tag.attribute("id") + name);
			if (variables.size() > 0)
			{
				cell.appendChild(xdoc.createTextNode("\n\t\t\t\t"));
				for (int ii = 0; ii < variables.size(); ++ii)
				{
					cell.appendChild(variables.get(ii).getVariableElement(cellMatch, xdoc));
					if (ii < (variables.size() - 1))
					{
						cell.appendChild(xdoc.createTextNode("\n\t\t\t\t"));
					}
					else
					{
						cell.appendChild(xdoc.createTextNode("\n\t\t\t"));
					}
				}
			}
			DocumentFragment documentFragment = xdoc.createDocumentFragment();
			documentFragment.appendChild(cell);
			documentFragment.appendChild(xdoc.createTextNode("\n\t\t\t"));
			return documentFragment;
		} 
		catch (DOMException e) {throw new LinacLegoException(e);} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
}
