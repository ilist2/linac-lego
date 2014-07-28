package se.lu.esss.linacLego.webapp.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LinacLegoViewSerializer implements Serializable
{
	private HtmlTextTree pbsViewHtmlTextTree = null;
	private HtmlTextTree xmlViewHtmlTextTree = null;
	private CsvFile linacLegoData = null;
	private CsvFile linacLegoCellParts = null;
	private CsvFile linacLegoSlotParts = null;
	private CsvFile linacLegoBleParts = null;
	private CsvFile linacLegoCnptParts = null;

	public HtmlTextTree getPbsViewHtmlTextTree() {return pbsViewHtmlTextTree;}
	public HtmlTextTree getXmlViewHtmlTextTree() {return xmlViewHtmlTextTree;}
	public CsvFile getLinacLegoData() {return linacLegoData;}
	public CsvFile getLinacLegoCellParts() {return linacLegoCellParts;}
	public CsvFile getLinacLegoSlotParts() {return linacLegoSlotParts;}
	public CsvFile getLinacLegoBleParts() {return linacLegoBleParts;}
	public CsvFile getLinacLegoCnptParts() {return linacLegoCnptParts;}
	public void setPbsViewHtmlTextTree(HtmlTextTree pbsViewHtmlTextTree) {this.pbsViewHtmlTextTree = pbsViewHtmlTextTree;}
	public void setXmlViewHtmlTextTree(HtmlTextTree xmlViewHtmlTextTree) {this.xmlViewHtmlTextTree = xmlViewHtmlTextTree;}
	public void setLinacLegoData(CsvFile linacLegoData) {this.linacLegoData = linacLegoData;}
	public void setLinacLegoCellParts(CsvFile linacLegoCellParts) {this.linacLegoCellParts = linacLegoCellParts;}
	public void setLinacLegoSlotParts(CsvFile linacLegoSlotParts) {this.linacLegoSlotParts = linacLegoSlotParts;}
	public void setLinacLegoBleParts(CsvFile linacLegoBleParts) {this.linacLegoBleParts = linacLegoBleParts;}
	public void setLinacLegoCnptParts(CsvFile linacLegoCnptParts) {this.linacLegoCnptParts = linacLegoCnptParts;}
	public LinacLegoViewSerializer() 
	{
	}
}
