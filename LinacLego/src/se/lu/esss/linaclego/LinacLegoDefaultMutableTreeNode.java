package se.lu.esss.linaclego;


import javax.swing.tree.DefaultMutableTreeNode;

import se.lu.esss.linaclego.structures.Linac;
import se.lu.esss.linaclego.structures.Section;
import se.lu.esss.linaclego.structures.cell.Cell;
import se.lu.esss.linaclego.structures.elements.ControlPoint;
import se.lu.esss.linaclego.structures.elements.DataElement;
import se.lu.esss.linaclego.structures.elements.beamline.BeamLineElement;
import se.lu.esss.linaclego.structures.slot.Slot;

@SuppressWarnings("serial")
public class LinacLegoDefaultMutableTreeNode extends DefaultMutableTreeNode
{
	
	public LinacLegoDefaultMutableTreeNode() 
	{
		super();
	}
	public LinacLegoDefaultMutableTreeNode(LinacLego linacLego) 
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "linacLego" + "</font>";
		html =  html + "<font color=\"FF0000\"> title</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + linacLego.getLinacLegoTitle() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "revNo" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + linacLego.getLinacLegoRevNo() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LinacLegoDefaultMutableTreeNode(DataElement dataElement) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "data" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getId() + "\"</font>";
		if (dataElement.getType() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getType() + "\"</font>";
		if (dataElement.getUnit() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "unit" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getUnit() + "\"</font>";
		if (dataElement.getValue() != null)
			html =  html + "<font color=\"000000\">" + " " + dataElement.getValue() + "</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LinacLegoDefaultMutableTreeNode(String string) throws LinacLegoException
	{
		this();
		setUserObject(string); 
	}
	public LinacLegoDefaultMutableTreeNode(Linac linac) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "linac" + "</font>";
		html = html + "</html>";
		setUserObject(html); 
		LinacLegoDefaultMutableTreeNode dataFolder = new LinacLegoDefaultMutableTreeNode("data");
		add(dataFolder);
		for (int idata = 0; idata < linac.getDataElementList().size(); ++idata)
			dataFolder.add(new LinacLegoDefaultMutableTreeNode(linac.getDataElementList().get(idata)));
		
	}
	public LinacLegoDefaultMutableTreeNode(Section section) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "section" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + section.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rfHarmonic" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + section.getRfHarmonic() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LinacLegoDefaultMutableTreeNode(Cell cell) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "cell" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cell.getId() + "\"</font>";
		if (cell.getModelId() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cell.getModelId() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		
	}
	public LinacLegoDefaultMutableTreeNode(Slot slot) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "slot" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + slot.getId() + "\"</font>";
		if (slot.getModelId() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + slot.getModelId() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LinacLegoDefaultMutableTreeNode(BeamLineElement ble) throws LinacLegoException
	{
		this();

		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "ble" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + ble.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + ble.getType() + "\"</font>";
		if (ble.getModel().length() > 0)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + ble.getModel() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		LinacLegoDefaultMutableTreeNode dataFolder = new LinacLegoDefaultMutableTreeNode("data");
		add(dataFolder);
		for (int idata = 0; idata < ble.getDataElementList().size(); ++idata)
			dataFolder.add(new LinacLegoDefaultMutableTreeNode(ble.getDataElementList().get(idata)));
	}
	public LinacLegoDefaultMutableTreeNode(ControlPoint cnpt) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "cnpt" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cnpt.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cnpt.getType() + "\"</font>";
		if (cnpt.getModel().length() > 0)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cnpt.getModel() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		LinacLegoDefaultMutableTreeNode dataFolder = new LinacLegoDefaultMutableTreeNode("data");
		add(dataFolder);
		for (int idata = 0; idata < cnpt.getDataElementList().size(); ++idata)
			dataFolder.add(new LinacLegoDefaultMutableTreeNode(cnpt.getDataElementList().get(idata)));
	}
	
}
