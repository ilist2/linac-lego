package org.openepics.discs.linaclego.structures.elements;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.simplexml.SimpleXmlWriter;

public class DataElement 
{
	private String id = null;
	private String value = null;
	private String type = null;
	private String unit = null;
	
	public DataElement(String id, String value, String type, String unit)
	{
		this.id = id;
		this.value = value;
		this.type = type;
		this.unit = unit;
	}
	public DataElement(SimpleXmlReader dataTag) throws LinacLegoException 
	{
		try 
		{
			this.id = dataTag.attribute("id");
			this.value = dataTag.getCharacterData();
		} catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
		try {this.type = dataTag.attribute("type");} catch (SimpleXmlException e) {this.type = "type";}
		try {this.unit = dataTag.attribute("unit");} catch (SimpleXmlException e) {this.unit = "unit";}
	}
	public void writeTag(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("d");
			xw.setAttribute("id", id);
			if (type != null ) xw.setAttribute("type", type);
			if (unit != null ) xw.setAttribute("unit", unit);
			if (value != null ) xw.writeCharacterData(value);
			xw.closeXmlTag("d");
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public String getId() {return id;}
	public String getValue() throws LinacLegoException 
	{
		if (value != null )
		{
			return value;
		}
		else
		{
			throw new LinacLegoException("Cannot find value of " + id);
		}
	}
	public String getType() {return type;}
	public String getUnit() {return unit;}

	public void setId(String id) {this.id = id;}
	public void setValue(String value) {this.value = value;}
	public void setType(String type) {this.type = type;}
	public void setUnit(String unit) {this.unit = unit;}
	
	public boolean unitMatches(String unit)
	{
		return unit.equals(this.unit);
	}
	
	public boolean valueMatchsType()
	{

		if (type.toLowerCase().equals("double"))
		{
			boolean isDouble = true;
			try
			{
				Double.parseDouble(value);
			}catch (NumberFormatException nfe)
			{
				isDouble = false;
			}
			return isDouble;
		}
		if (type.toLowerCase().equals("int"))
		{
			boolean isInt = true;
			try
			{
				Integer.parseInt(value);
			}catch (NumberFormatException nfe)
			{
				isInt = false;
			}
			return isInt;
		}
		if (type.toLowerCase().equals("string"))
		{
			if (value != null) 
			{
				char test = ' ';
				try {test = value.charAt(0);} catch (java.lang.StringIndexOutOfBoundsException e){return true;}
				if (test == '#') return false;
				return true;
			}
			return false;
		}
		if (type.toLowerCase().equals("boolean"))
		{
			boolean isBoolean = false;
			if (value.toLowerCase().equals("true")) isBoolean = true;
			if (value.toLowerCase().equals("false")) isBoolean = true;
			return isBoolean;
		}
		return false;
	}
	public boolean matchesDataElementModel(DataElement dataElementTemplate) throws LinacLegoException
	{
		boolean matches = true;
		if (!dataElementTemplate.getId().equals(getId())) return false;
		if (dataElementTemplate.getUnit() != null)
			if (!dataElementTemplate.getUnit().equals(getUnit())) return false;
		if (dataElementTemplate.getType() != null)
			if (!dataElementTemplate.getType().equals(getType())) return false;
		if (dataElementTemplate.getType() != null)
		{
			if (dataElementTemplate.valueMatchsType())
			{
				if (!dataElementTemplate.getValue().equals(getValue())) return false;
			}
		}
		else
		{
			if (!dataElementTemplate.getValue().equals(getValue())) return false;
		}
		return matches;
	}
}
