package org.openepics.discs.linaclego.webapp.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Attribute implements Serializable
{

	private String attributeName;
	private String attributeValue;
	private int valueWidth;

	private String attLabelStyle;
	private String attValueStyle;
	private String attWhiteSpaceStyle;

	public String getAttLabelStyle() {return attLabelStyle;}
	public String getAttValueStyle() {return attValueStyle;}
	public String getAttWhiteSpaceStyle() {return attWhiteSpaceStyle;}

	public String getAttributeName() {return attributeName;}
	public String getAttributeValue() {return attributeValue;}
	public int getValueWidth() {return valueWidth;}

	public void setAttLabelStyle(String attLabelStyle) {this.attLabelStyle = attLabelStyle;}
	public void setAttValueStyle(String attValueStyle) {this.attValueStyle = attValueStyle;}
	public void setAttWhiteSpaceStyle(String attWhiteSpaceStyle) {this.attWhiteSpaceStyle = attWhiteSpaceStyle;}

	public Attribute() 
	{
		attributeName = "";
		attributeValue = "";
		valueWidth = 0;
	}
	public Attribute(String attributeName, String attributeValue, int valueWidth, Attribute attributeStyle) 
	{
		this();
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.valueWidth = valueWidth;
		setAttLabelStyle(attributeStyle.attLabelStyle);
		setAttValueStyle(attributeStyle.attValueStyle);
		setAttWhiteSpaceStyle(attributeStyle.attWhiteSpaceStyle);
	}
	public String getHtml(boolean addPadding, boolean addParagraph)
	{
		String html = "";
		if (addParagraph) html = html + "<br>";
		html = html + textSpan(" " + attributeName + "=", attLabelStyle);
		html = html + textSpan(attributeValue, attValueStyle);
		if (addPadding) html = html + padSpaceEnd(attributeValue, valueWidth);
//		if (addParagraph) html = html + "</p>";
		return html;
	}
	public String getInlineHtmlString(boolean addPadding, boolean addParagraph)
	{
		String html = "<html>";
		html = html + getHtml(addPadding, addParagraph);
		html = html + "</html>";
		return html;
	}
	private String textSpan(String text, String style)
	{
		return "<span class=\"" + style + "\">" + text + "</span>";
	}
	private String padSpaceEnd(String refString, int stringLength)
	{
		String spanHtml = "<span class=\"" + attWhiteSpaceStyle + "\">";
		int numSpace = stringLength - refString.length();
		String padString = "";
		if (numSpace <= 0) return spanHtml + "</span>";
		for (int ii = 0; ii < numSpace; ++ii) padString = padString + "_";
		return spanHtml + padString + "</span>";
	}

	public void setStyles(String attLabelStyle, String attValueStyle, String attWhiteSpaceStyle)
	{
		setAttLabelStyle(attLabelStyle);
		setAttValueStyle(attValueStyle);
		setAttWhiteSpaceStyle(attWhiteSpaceStyle);
	}

}
