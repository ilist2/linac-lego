package se.lu.esss.linacLego.webapp.server;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.astrofizzbizz.simpleXml.SimpleXmlException;

import se.lu.esss.linacLego.webapp.shared.HtmlTextTree;
import se.lu.esss.linaclego.LinacLego;
import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.Linac;
import se.lu.esss.linaclego.structures.Section;
import se.lu.esss.linaclego.structures.cell.Cell;
import se.lu.esss.linaclego.structures.elements.ControlPoint;
import se.lu.esss.linaclego.structures.elements.DataElement;
import se.lu.esss.linaclego.structures.elements.beamline.BeamLineElement;
import se.lu.esss.linaclego.structures.slot.Slot;

public class LinacLegoPbsHtmlTextTree 
{
	private HtmlTextTree htmlTextTree;
	
	public static final DecimalFormat threePlaces = new DecimalFormat("###.###");
	
	public HtmlTextTree getHtmlTextTree() {return htmlTextTree;}
	public LinacLegoPbsHtmlTextTree()
	{
		htmlTextTree = new HtmlTextTree();
	}
	public void add(LinacLegoPbsHtmlTextTree linacLegoPbsHtmlTextTree)
	{
		htmlTextTree.add(linacLegoPbsHtmlTextTree.getHtmlTextTree());
	}
	public LinacLegoPbsHtmlTextTree(LinacLego linacLego, String tagStyle, String attLabelStyle, String attValueStyle, String attWhiteSpaceStyle) 
	{
		this();
		htmlTextTree.setTagStyle(tagStyle);
		htmlTextTree.setAttLabelStyle(attLabelStyle);
		htmlTextTree.setAttValueStyle(attValueStyle);
		htmlTextTree.setAttWhiteSpaceStyle(attWhiteSpaceStyle);
		
		htmlTextTree.setIconImageLocation("images/lego.jpg");
		htmlTextTree.setTag("linacLego");
		htmlTextTree.addAttribute("title", linacLego.getLinacLegoTitle(), 1);
		htmlTextTree.addAttribute("revNo", Integer.toString(linacLego.getLinacLegoRevNo()), 1);
		
	}
	public void inheritStyles(LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree)
	{
		htmlTextTree.inheritStyles(parentLinacLegoPbsHtmlTextTree.getHtmlTextTree());
	}
	public LinacLegoPbsHtmlTextTree(DataElement dataElement, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation(null);
		htmlTextTree.setTag("data");
		htmlTextTree.addAttribute("id", dataElement.getId(), 15);
		if (dataElement.getType() != null)
		{
			htmlTextTree.addAttribute("type", dataElement.getType(), 10);
		}
		if (dataElement.getUnit() != null)
		{
			htmlTextTree.addAttribute("unit", dataElement.getUnit(), 10);
		}
		if (dataElement.getValue() != null)
		{
			htmlTextTree.addAttribute("value", dataElement.getValue(), 1);
		}
	}
	public LinacLegoPbsHtmlTextTree(Linac linac, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/linac.jpg");
		htmlTextTree.setTag("linac");
		htmlTextTree.addAttribute("energy", threePlaces.format(linac.geteVout() / 1.0e+06)  + " MeV", 1);
		htmlTextTree.addAttribute("length", threePlaces.format(linac.getLength())  + " m"   + " MeV", 1);
		
		makeDataFolder(linac.getDataElementList());
	}
	private void makeDataFolder(ArrayList<DataElement> dataElementList) throws LinacLegoException
	{
		HtmlTextTree dataFolderHtmlTextTree = new HtmlTextTree();
		dataFolderHtmlTextTree.inheritStyles(getHtmlTextTree());
		dataFolderHtmlTextTree.setIconImageLocation("images/data.png");
		dataFolderHtmlTextTree.setTag("data");
		htmlTextTree.setDataFolder(dataFolderHtmlTextTree);

		for (int idata = 0; idata < dataElementList.size(); ++idata)
		{
			LinacLegoPbsHtmlTextTree dataElement = new LinacLegoPbsHtmlTextTree(dataElementList.get(idata), this);
			dataFolderHtmlTextTree.add(dataElement.htmlTextTree);
		}
	}
	public LinacLegoPbsHtmlTextTree(Section section, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{

		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/section.jpg");
		htmlTextTree.setTag("section");
		htmlTextTree.addAttribute("id", section.getId(), 15);
		htmlTextTree.addAttribute("rfHarmonic", Integer.toString(section.getRfHarmonic()), 1);
		htmlTextTree.addAttribute("energy", threePlaces.format(section.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(section.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(section.getLocalEndZ()) + "m", 9);
		
	}
	public LinacLegoPbsHtmlTextTree(Cell cell, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/cell.jpg");
		htmlTextTree.setTag("cell");
		htmlTextTree.addAttribute("id", cell.getId(), 15);
		String cellModelId = cell.getModelId();
		if (cellModelId == null) cellModelId = "none";
		htmlTextTree.addAttribute("model", cellModelId, 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(cell.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(cell.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(cell.getLocalEndZ()) + "m", 9);
		
	}
	public LinacLegoPbsHtmlTextTree(Slot slot, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/slots.jpg");
		htmlTextTree.setTag("slot");
		htmlTextTree.addAttribute("id", slot.getId(), 15);
		String slotModelId = slot.getModelId();
		if (slotModelId == null) slotModelId = "none";
		htmlTextTree.addAttribute("model", slotModelId, 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(slot.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(slot.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(slot.getLocalEndZ()) + "m", 9);
		
	}
	public LinacLegoPbsHtmlTextTree(BeamLineElement ble, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("");
		if (ble.getType().equals("quad")) htmlTextTree.setIconImageLocation("images/quad.jpg");
		if (ble.getType().equals("drift")) htmlTextTree.setIconImageLocation("images/drift.png");
		if (ble.getType().equals("rfGap")) htmlTextTree.setIconImageLocation("images/cavity.jpg");
		if (ble.getType().equals("fieldMap")) htmlTextTree.setIconImageLocation("images/cavity.jpg");
		htmlTextTree.setTag("ble");
		htmlTextTree.addAttribute("id", ble.getId(), 15);
		htmlTextTree.addAttribute("model", ble.getModel(), 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(ble.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(ble.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-cent", threePlaces.format(ble.getLocalCenterZ()) + "m", 9);
		makeDataFolder(ble.getDataElementList());
	}
	public LinacLegoPbsHtmlTextTree(ControlPoint cnpt, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/controlPoint.jpg");
		htmlTextTree.setTag("cnpt");
		htmlTextTree.addAttribute("id", cnpt.getId(), 15);
		try {htmlTextTree.addAttribute("name", cnpt.getName(), 25);
		} catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e) ;
		}
		htmlTextTree.addAttribute("type", cnpt.getType(), 15);
		htmlTextTree.addAttribute("model", cnpt.getModel(), 15);
		makeDataFolder(cnpt.getDataElementList());

	}

}
