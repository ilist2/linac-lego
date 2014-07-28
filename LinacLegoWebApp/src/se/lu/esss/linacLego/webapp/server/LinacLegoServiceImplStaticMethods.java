package se.lu.esss.linacLego.webapp.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.lu.esss.linacLego.webapp.shared.CsvFile;
import se.lu.esss.linacLego.webapp.shared.HtmlTextTree;
import se.lu.esss.linacLego.webapp.shared.LinacLegoViewSerializer;
import se.lu.esss.linacLego.webapp.shared.LinacLegoWebAppException;
import se.lu.esss.linaclego.LinacLego;
import se.lu.esss.linaclego.LinacLegoException;
import se.lu.esss.linaclego.structures.Section;
import se.lu.esss.linaclego.structures.cell.Cell;
import se.lu.esss.linaclego.structures.elements.ControlPoint;
import se.lu.esss.linaclego.structures.elements.beamline.BeamLineElement;
import se.lu.esss.linaclego.structures.slot.Slot;

import com.astrofizzbizz.simpleXml.SimpleXmlDoc;
import com.astrofizzbizz.simpleXml.SimpleXmlException;
import com.astrofizzbizz.simpleXml.SimpleXmlReader;

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
	public static HtmlTextTree createXmlView(File linacLegoXmlFile) throws SimpleXmlException    
	{
		String xmlViewTagStyle = "xmlTagLabel";
		String xmlViewAttLabelStyle = "xmlAttLabel";
		String xmlViewAttValueStyle = "xmlAttValue";
		String xmlViewAttWhiteSpaceStyle = "xmlWhiteSpace";
		SimpleXmlDoc sxd = new SimpleXmlDoc(linacLegoXmlFile);
		return  LinacLegoServiceImplStaticMethods.buildXmlTextTree((Node) sxd.getXmlDoc().getDocumentElement(), xmlViewTagStyle, xmlViewAttLabelStyle, xmlViewAttValueStyle, xmlViewAttWhiteSpaceStyle);
	}
	public static HtmlTextTree createPbsViewHtmlTextTree(File linacLegoXmlFile) throws SimpleXmlException, LinacLegoException   
	{
		String pbsTagStyle = "pbsTagLabel";
		String pbsAttLabelStyle = "pbsAttLabel";
		String pbsAttValueStyle = "pbsAttValue";
		String pbsAttWhiteSpaceStyle = "pbsWhiteSpace";
		SimpleXmlDoc sxd = new SimpleXmlDoc(linacLegoXmlFile);
		LinacLego linacLego = new LinacLego(sxd, null);
		linacLego.setCreateReportDirectory(false);
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
	public static CsvFile readCsvFile(String csvFilePath) throws IOException, LinacLegoWebAppException  
	{
		CsvFile csvFile = new CsvFile();
        BufferedReader br;
        br = new BufferedReader(new FileReader(csvFilePath));
        String line;
        while ((line = br.readLine()) != null) 
        {  
        	csvFile.addLine(line);
        }
        br.close();
        csvFile.close();
		return csvFile;
	}
	public static void writeOutLinacLegoViewSerializer(File linacLegoDir, File outputFile) throws SimpleXmlException, LinacLegoException, IOException, LinacLegoWebAppException 
	{
		String delim = File.separator;
		LinacLegoViewSerializer linacLegoViewSerializer = new LinacLegoViewSerializer();
		File linacLegoXmlFile = new File(linacLegoDir.getPath() + delim + "linacLego.xml");
		linacLegoViewSerializer.setPbsViewHtmlTextTree(createPbsViewHtmlTextTree(linacLegoXmlFile));
		linacLegoViewSerializer.setXmlViewHtmlTextTree(createXmlView(linacLegoXmlFile));
		String partsParentDir = linacLegoDir.getPath() + delim + "linacLegoOutput" + delim;
		linacLegoViewSerializer.setLinacLegoData(readCsvFile(partsParentDir + "linacLegoData.csv"));
		linacLegoViewSerializer.setLinacLegoCellParts(readCsvFile(partsParentDir + "linacLegoCellParts.csv"));
		linacLegoViewSerializer.setLinacLegoSlotParts(readCsvFile(partsParentDir + "linacLegoSlotParts.csv"));
		linacLegoViewSerializer.setLinacLegoBleParts(readCsvFile(partsParentDir + "linacLegoBleParts.csv"));
		linacLegoViewSerializer.setLinacLegoCnptParts(readCsvFile(partsParentDir + "linacLegoCnptParts.csv"));

		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
		objectOutputStream.writeObject(linacLegoViewSerializer);
		objectOutputStream.close();
		bufferedOutputStream.close();
		fileOutputStream.close();
	}
	public static LinacLegoViewSerializer readLinacLegoViewSerializer(File inputFile) throws IOException, ClassNotFoundException
	{
		FileInputStream fileInputStream = new FileInputStream(inputFile);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		ObjectInput objectInputStream = new ObjectInputStream (bufferedInputStream);
		LinacLegoViewSerializer linacLegoViewSerializer = (LinacLegoViewSerializer) objectInputStream.readObject();
		objectInputStream.close();
		bufferedInputStream.close();
		fileInputStream.close();
		return linacLegoViewSerializer;
	}
	public static void main(String[] args) throws LinacLegoWebAppException, IOException, SimpleXmlException, LinacLegoException 
	{
		File linacLegoDir = new File("C:\\Users\\davidmcginnis\\Google Drive\\ESS\\linacLego\\public");
		File outputFile = new File("C:\\EclipseWorkSpace2014\\LinacLegoWebApp\\war\\linacLegoFiles\\linacLegoView.ser");
		writeOutLinacLegoViewSerializer(linacLegoDir, outputFile);
	}	

}
