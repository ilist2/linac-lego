package org.openepics.discs.linaclego.webapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlDoc;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.Section;
import org.openepics.discs.linaclego.structures.cell.Cell;
import org.openepics.discs.linaclego.structures.elements.ControlPoint;
import org.openepics.discs.linaclego.structures.elements.beamline.BeamLineElement;
import org.openepics.discs.linaclego.structures.slot.Slot;
import org.openepics.discs.linaclego.webapp.shared.CsvFile;
import org.openepics.discs.linaclego.webapp.shared.HtmlTextTree;
import org.openepics.discs.linaclego.webapp.shared.LinacLegoWebAppException;

public class LinacLegoServiceImplStaticMethods 
{
    static HtmlTextTree buildXmlTextTree(Node root, String tagStyle, String attLabelStyle, String attValueStyle, String attWhiteSpaceStyle)
    {
    	HtmlTextTree htmlTextTree = new HtmlTextTree();
		htmlTextTree.setTagStyle(tagStyle);
		htmlTextTree.setAttLabelStyle(attLabelStyle);
		htmlTextTree.setAttValueStyle(attValueStyle);
		htmlTextTree.setAttWhiteSpaceStyle(attWhiteSpaceStyle);

		setXmlHtmlDisplay(htmlTextTree, root);
        NodeList nodeList = root.getChildNodes();
        for (int count = 0; count < nodeList.getLength(); count++) 
        {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) 
            {
            	HtmlTextTree childHtmlTextTree = new HtmlTextTree();
            	childHtmlTextTree.inheritStyles(htmlTextTree);
            	setXmlHtmlDisplay(childHtmlTextTree, tempNode);
          	
                if (tempNode.hasChildNodes()) 
                {
                    // loop again if has child nodes
                	htmlTextTree.add(buildXmlTextTree(tempNode, tagStyle, attLabelStyle, attValueStyle, attWhiteSpaceStyle));
                }
                else
                {
                	htmlTextTree.add(childHtmlTextTree);
                }
            }
        }
        return htmlTextTree;
    }
	private static void setXmlHtmlDisplay(HtmlTextTree htmlTextTree, Node xmlNode)
	{

		SimpleXmlReader sxr = new SimpleXmlReader(xmlNode);
		htmlTextTree.setTag(sxr.tagName());
		
		String id = "";
		try {id = sxr.attribute("id");} catch (SimpleXmlException e) {}
		if (id.length() > 0)
		{
			htmlTextTree.addAttribute("id", id, 0);
		}
		ArrayList<String[]> attributes = sxr.getAttributes();
		if (attributes != null)
		{
			for (int ii = 0; ii < attributes.size(); ++ii)
			{
				if (!attributes.get(ii)[0].equals("id"))
				{
					htmlTextTree.addAttribute(attributes.get(ii)[0], attributes.get(ii)[1], 0);
				}
			}
		}
		String cdata = sxr.getCharacterData();
		if (cdata != null )
		{
			if (!stripWhiteSpaces(cdata).equals(""))
				htmlTextTree.addAttribute("cdata", cdata, 0);
		}
		return;
	}
	public static String stripWhiteSpaces(String whitey)
	{
		int numChar = 0;
		for (int ii = 0; ii < whitey.length(); ++ii)
		{
			if (whitey.charAt(ii) != ' ') 
				if (whitey.charAt(ii) != '\n') 
					numChar = numChar + 1;
		}
		if (numChar == 0) return "";
		char[] slimJimArray = new char[numChar];
		int iChar = 0;
		for (int ii = 0; ii < whitey.length(); ++ii)
		{
			if (whitey.charAt(ii) != ' ') 
			{
				slimJimArray[iChar] = whitey.charAt(ii);
				iChar = iChar + 1;
			}
		}
		return new String(slimJimArray);
	}
	public static HtmlTextTree createXmlView(URL linacLegoXmlURL) throws SimpleXmlException    
	{
		String xmlViewTagStyle = "xmlTagLabel";
		String xmlViewAttLabelStyle = "xmlAttLabel";
		String xmlViewAttValueStyle = "xmlAttValue";
		String xmlViewAttWhiteSpaceStyle = "xmlWhiteSpace";
		SimpleXmlDoc sxd = new SimpleXmlDoc(linacLegoXmlURL);
		return  LinacLegoServiceImplStaticMethods.buildXmlTextTree((Node) sxd.getXmlDoc().getDocumentElement(), xmlViewTagStyle, xmlViewAttLabelStyle, xmlViewAttValueStyle, xmlViewAttWhiteSpaceStyle);
	}
	public static HtmlTextTree createPbsViewHtmlTextTree(URL linacLegoXmlURL) throws SimpleXmlException, LinacLegoException   
	{
		String pbsTagStyle = "pbsTagLabel";
		String pbsAttLabelStyle = "pbsAttLabel";
		String pbsAttValueStyle = "pbsAttValue";
		String pbsAttWhiteSpaceStyle = "pbsWhiteSpace";
		SimpleXmlDoc sxd = new SimpleXmlDoc(linacLegoXmlURL);
		LinacLego linacLego = new LinacLego(sxd, null);
		linacLego.updateLinac();
		LinacLegoPbsHtmlTextTree pbsView = new LinacLegoPbsHtmlTextTree(linacLego, pbsTagStyle, pbsAttLabelStyle, pbsAttValueStyle, pbsAttWhiteSpaceStyle);
		
		LinacLegoPbsHtmlTextTree linacNode = new LinacLegoPbsHtmlTextTree(linacLego.getLinac(), pbsView);
		pbsView.add(linacNode);
		for (int isec = 0; isec < linacLego.getLinac().getSectionList().size(); ++isec)
		{
			Section section = linacLego.getLinac().getSectionList().get(isec);
			LinacLegoPbsHtmlTextTree sectionNode = new LinacLegoPbsHtmlTextTree(section, linacNode);
			linacNode.add(sectionNode);
			for (int icell = 0; icell < section.getCellList().size(); ++icell)
			{
				Cell cell = section.getCellList().get(icell);
				LinacLegoPbsHtmlTextTree cellNode = new LinacLegoPbsHtmlTextTree(cell, sectionNode);
				sectionNode.add(cellNode);
				for (int islot = 0; islot < cell.getSlotList().size(); ++islot)
				{
					Slot slot = cell.getSlotList().get(islot);
					LinacLegoPbsHtmlTextTree slotNode = new LinacLegoPbsHtmlTextTree(slot, cellNode);
					cellNode.add(slotNode);
					for (int ible = 0; ible < slot.getBeamLineElementList().size(); ++ible)
					{
						BeamLineElement ble = slot.getBeamLineElementList().get(ible);
						LinacLegoPbsHtmlTextTree bleNode = new LinacLegoPbsHtmlTextTree(ble, slotNode);
						slotNode.add(bleNode);
						for (int icnpt = 0; icnpt < ble.getControlPointList().size(); ++icnpt)
						{
							ControlPoint cnpt = ble.getControlPointList().get(icnpt);
							LinacLegoPbsHtmlTextTree cnptNode = new LinacLegoPbsHtmlTextTree(cnpt, bleNode);
							bleNode.add(cnptNode);
						}
					}
				}
			}
		}
		return pbsView.getHtmlTextTree();
	}
	public static CsvFile readCsvFile(URL csvFileUrl) throws IOException, LinacLegoWebAppException  
	{
		CsvFile csvFile = new CsvFile();
        BufferedReader br;
        InputStreamReader inputStreamReader = new InputStreamReader(csvFileUrl.openStream());
        br = new BufferedReader(inputStreamReader);
        String line;
        while ((line = br.readLine()) != null) 
        {  
        	csvFile.addLine(line);
        }
        br.close();
        inputStreamReader.close();
        csvFile.close();
		return csvFile;
	}
	public static void main(String[] args) throws LinacLegoWebAppException, IOException, SimpleXmlException, LinacLegoException 
	{
//		String linacLegoWebSite = "https://1dd61ea372616aae15dcd04cd29d320453f0cb60.googledrive.com/host/0B3Hieedgs_7FNXg3OEJIREFuUUE";
//		URL inputFileUrl = new URL(linacLegoWebSite + "/public/linacLegoOutput/" + fileName);
	
	}	

}
