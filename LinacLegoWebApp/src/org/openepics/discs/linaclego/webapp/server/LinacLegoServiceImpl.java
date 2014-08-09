package org.openepics.discs.linaclego.webapp.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;
import org.openepics.discs.linaclego.webapp.client.LinacLegoService;
import org.openepics.discs.linaclego.webapp.shared.CsvFile;
import org.openepics.discs.linaclego.webapp.shared.HtmlTextTree;
import org.openepics.discs.linaclego.webapp.shared.LinacLegoWebAppException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LinacLegoServiceImpl extends RemoteServiceServlet implements LinacLegoService 
{

	@Override
	public HtmlTextTree getPbsViewHtmlTextTree(String linacLegoXmlLink) throws LinacLegoWebAppException 
	{
		try {return  LinacLegoServiceImplStaticMethods.createPbsViewHtmlTextTree(new URL(linacLegoXmlLink));} 
		catch (MalformedURLException | SimpleXmlException | LinacLegoException e) {throw new LinacLegoWebAppException(e);}
	}

	@Override
	public HtmlTextTree getXmlViewHtmlTextTree(String linacLegoXmlLink) throws LinacLegoWebAppException 
	{
		try {return LinacLegoServiceImplStaticMethods.createXmlView(new URL(linacLegoXmlLink));} 
		catch (MalformedURLException | SimpleXmlException e) {throw new LinacLegoWebAppException(e);}
	}

	@Override
	public CsvFile getCsvFile(String csvFileLink) throws LinacLegoWebAppException 
	{
		try {return LinacLegoServiceImplStaticMethods.readCsvFile(new URL(csvFileLink));
		} catch (IOException e) {throw new LinacLegoWebAppException(e);}
	}
}
