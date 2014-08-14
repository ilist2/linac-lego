/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package org.openepics.discs.linaclego;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.openepics.discs.linaclego.matcher.CellSearcher;
import org.openepics.discs.linaclego.matcher.SlotSearcher;
import org.openepics.discs.linaclego.simplexml.SimpleXmlDoc;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlReader;
import org.openepics.discs.linaclego.structures.Section;
import org.openepics.discs.linaclego.structures.cell.Cell;
import org.openepics.discs.linaclego.structures.elements.ControlPoint;
import org.openepics.discs.linaclego.structures.elements.beamline.BeamLineElement;
import org.openepics.discs.linaclego.structures.slot.Slot;
import org.openepics.discs.linaclego.tracewinreader.TraceWinReader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.openepics.discs.linaclego.utilities.DpmSwingUtilities;
import org.openepics.discs.linaclego.utilities.StatusPanel;


@SuppressWarnings("serial")
public class LinacLegoApp extends JFrame
{
	public static final String delim = System.getProperty("file.separator");
	public static final String newline = System.getProperty("line.separator");
	private JMenuBar mainMenuBar;
	private StatusPanel statusBar;
	ClassLoader loader;
	
	static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	protected String version = "v2.5";
	protected String versionDate = "August 14, 2014";
	private String suggestedFileName = "linacLego.xml";
	private String lastDirectoryPath = "./";
	LinacLego linacLego;
	private SimpleXmlDoc simpleXmlDoc = null;
//	File xmlFile = null;
	JTabbedPane mainPane; 
	boolean printControlPoints = true;
	JScrollPane xmlTreeView;
	JScrollPane pbsTreeView;
	JTree xmlTree;
	JTree pbsTree;
	DefaultMutableTreeNode pbsTreeNode;
	WatchService watchService = null;
	WatchKeyRunnable watchKeyRunnable = null;
	Thread watchKeyThread = null;
	File openedXmlFile = null;
	
	public LinacLegoApp(String frametitle, String statusBarTitle, int numStatusLines)
	{
		super(frametitle);
		statusBar = new StatusPanel(numStatusLines, statusBarTitle);
		
		loader  = Thread.currentThread().getContextClassLoader();
		ImageIcon  logoIcon = new ImageIcon(loader.getResource("org/openepics/discs/linaclego/files/lego.jpg"));
        setIconImage(logoIcon.getImage());
        try 
        {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        mainMenuBar = makeMenu();
        setJMenuBar(mainMenuBar);
		DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Parse XML File").setEnabled(false);
		DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "File"), "Save XML File").setEnabled(false);
		DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Match Slot Models").setEnabled(false);
		DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Match Cell Models").setEnabled(false);

		mainPane = new JTabbedPane();
        xmlTree = new JTree(new DefaultMutableTreeNode("LinacLego"));
        pbsTreeNode = new DefaultMutableTreeNode("LinacLego");
        pbsTree = new JTree(pbsTreeNode);
        
        xmlTreeView = new JScrollPane(xmlTree);
        pbsTreeView = new JScrollPane(pbsTree);
        mainPane.addTab("xml Tree", xmlTreeView);
        mainPane.addTab("pbs Tree", pbsTreeView);
      
        getContentPane().setLayout(new BorderLayout(5,5));
		getContentPane().add(mainPane, BorderLayout.CENTER);
		getContentPane().add(statusBar.getScrollPane(), java.awt.BorderLayout.SOUTH);  
		statusBar.setText("Welcome");
        setPreferredSize(new Dimension(800,600));

		pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        
        // Move the window
        this.setLocation(x, y);

		setVisible(true);
		statusBar.getScrollPane().setMinimumSize(statusBar.getScrollPane().getSize());
		statusBar.getScrollPane().setPreferredSize(statusBar.getScrollPane().getSize());
		try {watchService = FileSystems.getDefault().newWatchService();} catch (IOException e) {};
		watchKeyRunnable = new WatchKeyRunnable(this);
		watchKeyThread  = new Thread(watchKeyRunnable);
		watchKeyThread.start();

        addWindowListener(new java.awt.event.WindowAdapter() 
        {
            public void windowClosing(WindowEvent winEvt) 
            {
            	quitProgram();            
            }
        });

	}
	protected  JMenuBar makeMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		String menuText[] = {"File", "Actions","Field Builder", "Settings", "PBS Level View", "Help"};
        String subMenuText[][] =
        {
    		{"Open XML File","Save XML File", "Open TraceWin File", "Exit"},
    		{"Parse XML File" ,"Match Slot Models", "Match Cell Models"},
    		{"Build XML Field File"},
    		{"Disable ControlPoints"},
    		{"Section", "Cell", "Slot","BLE","CNPT"},
    		{"Help", "About"}
    	};

        for (int i = 0; i < menuText.length; i++)
        {
            JMenu menu = new JMenu(menuText[i]);
            menuBar.add (menu);
            
            for (int j = 0; j < subMenuText[i].length; j++)
            {
                JMenuItem item = new JMenuItem(subMenuText[i][j]);
                menu.add (item);
                item.addActionListener(new LinacLegoGuiActionListeners(menuText[i] + "." +subMenuText[i][j], this));
        		if (subMenuText[i][j].equals("Open XML File")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        		if (subMenuText[i][j].equals("Save XML File")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
         		if (subMenuText[i][j].equals("Exit")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
         		if (subMenuText[i][j].equals("Help")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
         		if (subMenuText[i][j].equals("About")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
         		if (subMenuText[i][j].equals("Parse XML File")) item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
           }
        }
        
        return menuBar;
    }
	protected void quitProgram()
	{
		dispose();
		System.exit(0);
	}
	private class LinacLegoGuiActionListeners implements ActionListener
	{
		LinacLegoApp myJFrameClass;
		String actionString = "";
		LinacLegoGuiActionListeners(String actionString, LinacLegoApp myJFrameClass)
		{
			this.actionString = actionString;
			this.myJFrameClass = myJFrameClass;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			if (actionString.equals("File.Open XML File"))
			{
				openXmlFile();
			}
			if (actionString.equals("File.Save XML File"))
			{
				saveXmlFile();
			}
			if (actionString.equals("File.Open TraceWin File"))
			{
				openTraceWinFile();
			}
			if (actionString.equals("File.Exit"))
			{
				quitProgram();
			}
			if (actionString.equals("Actions.Parse XML File"))
			{
				parseXmlFile();
			}
			if (actionString.equals("Actions.Match Slot Models"))
			{
				matchSlotModels();
			}
			if (actionString.equals("Actions.Match Cell Models"))
			{
				matchCellModels();
			}
			if (actionString.equals("Field Builder.Build XML Field File"))
			{
				buildXmlField();
			}
			if (actionString.equals("Settings.Disable ControlPoints"))
			{
				if (printControlPoints)
				{
					printControlPoints = false;
					DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Settings"), "Disable ControlPoints").setText("Enable ControlPoints");
				}
				else
				{
					printControlPoints = true;
					DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Settings"), "Enable ControlPoints").setText("Disable ControlPoints");
				}
				 
			}
			if (actionString.equals("PBS Level View.Section")) expandPbsTreeTo(1);
			if (actionString.equals("PBS Level View.Cell")) expandPbsTreeTo(2);
			if (actionString.equals("PBS Level View.Slot")) expandPbsTreeTo(3);
			if (actionString.equals("PBS Level View.BLE")) expandPbsTreeTo(4);
			if (actionString.equals("PBS Level View.CNPT")) expandPbsTreeTo(5);
			if (actionString.equals("Help.About"))
			{
				String info = "LinacLego Parser Graphical User Interface"
						+ "\n" + "Written by Dave McGinnis"
						+  "\n" + version + "\n" + "Last Updated " + versionDate;
				
				DpmSwingUtilities.messageDialog(info, myJFrameClass);
			}
			if (actionString.equals("guiTimer"))
			{
			}
		}
		
	}
	private void loadXmlFile()
	{
		try 
		{
			simpleXmlDoc = new SimpleXmlDoc(openedXmlFile.toURI().toURL());
			linacLego = new LinacLego(simpleXmlDoc, statusBar);
			xmlTree.setModel(new DefaultTreeModel(buildTreeNode((Node) linacLego.getSimpleXmlDoc().getXmlDoc().getDocumentElement())));
			DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Parse XML File").setEnabled(true);
			DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "File"), "Save XML File").setEnabled(true);
			DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Match Slot Models").setEnabled(true);
			DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Match Cell Models").setEnabled(true);
			this.setTitle("LinacLego " + openedXmlFile.getPath());
	        pbsTreeNode = new LinacLegoDefaultMutableTreeNode(linacLego);
			pbsTree.setModel(new DefaultTreeModel(pbsTreeNode));
			mainPane.setSelectedIndex(0);
		} catch (SimpleXmlException e) 
		{
			statusBar.scrollToTop();
			DpmSwingUtilities.messageDialog("Error: " + e.getRootCause(), this);
		} catch (LinacLegoException e) {
			statusBar.scrollToTop();
			DpmSwingUtilities.messageDialog("Error: " + e.getRootCause(), this);
		} catch (MalformedURLException e) {
			statusBar.scrollToTop();
			DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
		}
	}
	private void openXmlFile()
	{
		String[] xmlExtensions = {"xml"};
		File xmlFile = DpmSwingUtilities.chooseFile(lastDirectoryPath, "Open XML File", "", false, xmlExtensions, this);
		if (xmlFile != null)
		{
			openedXmlFile = new File(xmlFile.getPath());
			lastDirectoryPath = xmlFile.getParent();
			suggestedFileName = xmlFile.getName();
			
			try 
			{
				loadXmlFile();
				Path path = Paths.get(lastDirectoryPath);	// Get the directory to be monitored
				path.register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_MODIFY,
						StandardWatchEventKinds.ENTRY_DELETE);	// Register the directory
				
				
			} catch (IOException e) 
			{
				statusBar.scrollToTop();
				DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
			}
		}
	}
	private void saveXmlFile()
	{
		String[] xmlExtensions = {"xml"};
		File xmlFile = DpmSwingUtilities.chooseFile(lastDirectoryPath, "Save XML File", suggestedFileName, true, xmlExtensions, this);
		if (xmlFile != null)
		{
			try 
			{
				lastDirectoryPath = xmlFile.getParent();
				suggestedFileName = xmlFile.getName();
				simpleXmlDoc.saveXmlDocument(xmlFile.getPath());
				openedXmlFile = new File(xmlFile.getPath());
				Path path = Paths.get(lastDirectoryPath);	// Get the directory to be monitored
				path.register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_MODIFY,
						StandardWatchEventKinds.ENTRY_DELETE);	// Register the directory
				this.setTitle("LinacLego " + openedXmlFile.getPath());
			} catch (SimpleXmlException e) 
			{
				statusBar.scrollToTop();
				DpmSwingUtilities.messageDialog("Error: " + e.getRootCause(), this);
			} catch (IOException e) {
				statusBar.scrollToTop();
				DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
			}

		}
	}
	private void openTraceWinFile()
	{
		String[] xmlExtensions = {"dat"};
		File traceWinFile = DpmSwingUtilities.chooseFile(lastDirectoryPath, "Open TraceWin File", "", false, xmlExtensions, this);
		if (traceWinFile != null)
		{
			lastDirectoryPath = traceWinFile.getParent();
			String xmlFilePath = traceWinFile.getPath().substring(0, traceWinFile.getPath().lastIndexOf(".")) + ".xml";
			suggestedFileName = new File(xmlFilePath).getName();
			
			this.setTitle("LinacLego " + traceWinFile.getPath());
			try 
			{
				double ekinMeV = 0.0;
				double beamFreqMHz = 0.0;
				String ekinMeVString = JOptionPane.showInputDialog("Enter Starting Energy in MeV: ");
				if (ekinMeVString != null) ekinMeV = Double.parseDouble(ekinMeVString);
				String beamFreqMHzString = JOptionPane.showInputDialog("Enter Bunch Frequency in MHz: ");
				if (beamFreqMHzString != null) beamFreqMHz = Double.parseDouble(beamFreqMHzString);
				TraceWinReader twr = new TraceWinReader(traceWinFile.getPath(), ekinMeV, beamFreqMHz, statusBar);
				twr.readTraceWinFile();
				simpleXmlDoc = twr.getSimpleXmlDoc();
				openedXmlFile = new File(simpleXmlDoc.getXmlSourceUrl().toURI());
				DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "File"), "Save XML File").setEnabled(true);
				
				linacLego = new LinacLego(simpleXmlDoc, statusBar);
				xmlTree.setModel(new DefaultTreeModel(buildTreeNode((Node) linacLego.getSimpleXmlDoc().getXmlDoc().getDocumentElement())));
				DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Parse XML File").setEnabled(true);
				DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Match Slot Models").setEnabled(true);
				DpmSwingUtilities.findMenuItem(DpmSwingUtilities.findMenu(mainMenuBar, "Actions"), "Match Cell Models").setEnabled(true);
				pbsTreeNode = new LinacLegoDefaultMutableTreeNode(linacLego);
				pbsTree.setModel(new DefaultTreeModel(pbsTreeNode));
				mainPane.setSelectedIndex(0);

			} catch (LinacLegoException e)
			{
				statusBar.scrollToTop();
				DpmSwingUtilities.messageDialog("Error: " + e.getRootCause(), this);
			} 
			catch (RuntimeException e)
			{
				statusBar.scrollToTop();
				DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
			} catch (URISyntaxException e) {
				statusBar.scrollToTop();
				DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
			} 
		}
		
	}
	private void matchSlotModels()
	{
		try 
		{
			statusBar.setText("Searching for slots matching models");
			SlotSearcher slotSearcher = new SlotSearcher(linacLego);
			slotSearcher.replaceSlotsWithMatches();
			xmlTree.setModel(new DefaultTreeModel(buildTreeNode((Node) linacLego.getSimpleXmlDoc().getXmlDoc().getDocumentElement())));
			statusBar.setText("Finished searching for slots matching models");
			statusBar.scrollToTop();
		} catch (LinacLegoException e) 
		{
			statusBar.scrollToTop();
			DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
		}
		
	}
	private void matchCellModels()
	{
		try 
		{
			statusBar.setText("Searching for cells matching models");
			CellSearcher cellSearcher = new CellSearcher(linacLego);
			cellSearcher.replaceCellsWithMatches();
			xmlTree.setModel(new DefaultTreeModel(buildTreeNode((Node) linacLego.getSimpleXmlDoc().getXmlDoc().getDocumentElement())));
			statusBar.setText("Finished searching for cells matching models");
			statusBar.scrollToTop();
		} catch (LinacLegoException e) 
		{
			statusBar.scrollToTop();
			DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
		}
	}
    private DefaultMutableTreeNodeWrapper buildTreeNode(Node root){
    	DefaultMutableTreeNodeWrapper dmtNode;

        dmtNode = new DefaultMutableTreeNodeWrapper(root);
        NodeList nodeList = root.getChildNodes();
        for (int count = 0; count < nodeList.getLength(); count++) 
        {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) 
            {
                if (tempNode.hasChildNodes()) 
                {
                    // loop again if has child nodes
                    dmtNode.add(buildTreeNode(tempNode));
                }
                else
                {
                	dmtNode.add(new DefaultMutableTreeNodeWrapper(tempNode));
                }
            }
        }
        return dmtNode;
    }
    private void buildPbsTreeNew(LinacLego linacLego) throws LinacLegoException
    {
    	LinacLegoDefaultMutableTreeNode linacNode = new LinacLegoDefaultMutableTreeNode(linacLego.getLinac());
     	pbsTreeNode.add(linacNode);
		for (int isec = 0; isec < linacLego.getLinac().getSectionList().size(); ++isec)
		{
			Section section = linacLego.getLinac().getSectionList().get(isec);
			LinacLegoDefaultMutableTreeNode sectionNode = new LinacLegoDefaultMutableTreeNode(section);
			linacNode.add(sectionNode);
			for (int icell = 0; icell < section.getCellList().size(); ++icell)
			{
				Cell cell = section.getCellList().get(icell);
				LinacLegoDefaultMutableTreeNode cellNode = new LinacLegoDefaultMutableTreeNode(cell);
				sectionNode.add(cellNode);
				for (int islot = 0; islot < cell.getSlotList().size(); ++islot)
				{
					Slot slot = cell.getSlotList().get(islot);
					LinacLegoDefaultMutableTreeNode slotNode = new LinacLegoDefaultMutableTreeNode(slot);
					cellNode.add(slotNode);
					for (int ible = 0; ible < slot.getBeamLineElementList().size(); ++ible)
					{
						BeamLineElement ble = slot.getBeamLineElementList().get(ible);
						LinacLegoDefaultMutableTreeNode bleNode = new LinacLegoDefaultMutableTreeNode(ble);
						slotNode.add(bleNode);
						for (int icnpt = 0; icnpt < ble.getControlPointList().size(); ++icnpt)
						{
							ControlPoint cnpt = ble.getControlPointList().get(icnpt);
							LinacLegoDefaultMutableTreeNode cnptNode = new LinacLegoDefaultMutableTreeNode(cnpt);
							bleNode.add(cnptNode);
						}
					}
				}
			}
		}
    }
    private void expandPbsTreeTo(int level)
    {
    	int row = pbsTree.getRowCount() - 1;
    	while (row >= 0) 
    	{
    		pbsTree.collapseRow(row);
          row--;
    	}
    	DefaultMutableTreeNode currentNode = pbsTreeNode.getNextNode();
    	if (currentNode == null) return;
    	do 
    	{
    		if (currentNode.getLevel() == level) 
    		{
    			pbsTree.expandPath(new TreePath(currentNode.getPath()));
    		}
    		currentNode = currentNode.getNextNode();
    	}
    	while (currentNode != null);
    }
	private void parseXmlFile()
	{
		if (linacLego != null)
		{
			try 
			{
				linacLego = new LinacLego(simpleXmlDoc, statusBar);
				xmlTree.setModel(new DefaultTreeModel(buildTreeNode((Node) linacLego.getSimpleXmlDoc().getXmlDoc().getDocumentElement())));
				linacLego.setPrintControlPoints(printControlPoints);
				linacLego.setReportDirectory(new File(openedXmlFile.getParent()));
				linacLego.updateLinac();
				linacLego.createTraceWinFile();
//				linacLego.createDynacFile();
				linacLego.printReportTable();
				linacLego.printPartCounts();
				linacLego.saveXmlDocument();
				buildPbsTreeNew(linacLego);
				statusBar.scrollToTop();
				mainPane.setSelectedIndex(1);

			} catch (LinacLegoException e) 
			{
				statusBar.scrollToTop();
				DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
			} 
		}
	}
	private void buildXmlField()
	{
		String[] xmlExtensions = {"edz"};
		File traceWinFile = DpmSwingUtilities.chooseFile(lastDirectoryPath, "Select TraceWin Field Profile", "", false, xmlExtensions, this);
		if (traceWinFile != null)
		{
			lastDirectoryPath = traceWinFile.getParent();
			String title = traceWinFile.getName().substring(0, traceWinFile.getName().lastIndexOf("."));
			String storeEnergyString = JOptionPane.showInputDialog("Enter Stored Energy in Joules: ");
			double storedEnergy = -1.0;
			if (storeEnergyString != null) storedEnergy = Double.parseDouble(storeEnergyString);
			FieldProfileBuilder fpb = new FieldProfileBuilder();
			try 
			{
				fpb = FieldProfileBuilder.readTraceWinFieldProfile(storedEnergy, traceWinFile);
				File xmlFile = new File(traceWinFile.getParent() + File.separator + title + ".xml");
				fpb.writeXmlFile(xmlFile, title);
				statusBar.setText("Finished building field profile " + xmlFile.getPath());
			} catch (LinacLegoException e) 
			{
				statusBar.scrollToTop();
				DpmSwingUtilities.messageDialog("Error: " + e.getMessage(), this);
			}
		}

	}
	protected void exceptionOccured(Exception error)
	{
		DpmSwingUtilities.messageDialog("Error: " + error.getMessage(), this);
	}
	public static void main(String[] args) 
	{
		new LinacLegoApp("LinacLego", "Info", 10);
	}
	class WatchKeyRunnable implements Runnable
	{
		LinacLegoApp linacLegoGui;
		WatchKeyRunnable(LinacLegoApp linacLegoGui)
		{
			this.linacLegoGui = linacLegoGui;
		}
		@Override
		public void run() 
		{
			Date oldDate = new Date();
			while(true)
			{
				try 
				{
					WatchKey key = linacLegoGui.watchService.take();
					List<WatchEvent<?>> event = key.pollEvents();
					for (int ii = 0; ii < event.size(); ++ii)
					{
						if (event.get(ii).kind().name().equals("ENTRY_MODIFY") && event.get(ii).context().toString().equals(openedXmlFile.getName()))
						{
							Date newDate = new Date();
							if ((newDate.getTime() - oldDate.getTime()) > 5000)
							{
								int ichoice = DpmSwingUtilities.optionDialog("File Modified!", openedXmlFile.getName() + " modified! Reload?", 
										"Reload", "Cancel",1,linacLegoGui);
								if (ichoice == 1) loadXmlFile();
								oldDate.setTime(newDate.getTime());
							}
						}
					}
					key.reset();
				} 
				catch (InterruptedException e) {}	// retrieve the watchkey
			}
		}
	}
	class DefaultMutableTreeNodeWrapper extends DefaultMutableTreeNode
	{
		SimpleXmlReader sxr;
		DefaultMutableTreeNodeWrapper(Node xmlNode)
		{
			super(xmlNode);
			sxr = new SimpleXmlReader(xmlNode);
		}
		@Override
		public String toString()
		{
			return getTagLabel();
		}
		String getTagLabel()
		{
			String tagName = sxr.tagName();
			String id = "";
			try {id = sxr.attribute("id");} catch (SimpleXmlException e) {}
			String html = "<html>";
			html = html + "<font color=\"0000FF\">" + tagName + "</font>";
			if (id.length() > 0)
			{
				html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + id + "\"</font>";
			}
			ArrayList<String[]> attributes = sxr.getAttributes();
			if (attributes != null)
			{
				for (int ii = 0; ii < attributes.size(); ++ii)
				{
					if (!attributes.get(ii)[0].equals("id"))
					{
						if (!attributes.get(ii)[0].equals("xmlns:xi"))
						{
							if (!attributes.get(ii)[0].equals("xml:base"))
							{
									html =  html + "<font color=\"FF0000\">" + " " + attributes.get(ii)[0] 
											+ "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" 
											+ attributes.get(ii)[1] + "\"</font>";
							}
						}
					}
				}
			}
			String cdata = sxr.getCharacterData();
			if (cdata != null)
			{
				html =  html + "<font color=\"000000\">" + " " + cdata + "</font>";
			}
			html = html + "</html>";
			return html;
		}
	}
}
