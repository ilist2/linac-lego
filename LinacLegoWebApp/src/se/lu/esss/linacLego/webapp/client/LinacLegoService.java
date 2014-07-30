package se.lu.esss.linacLego.webapp.client;

import se.lu.esss.linacLego.webapp.shared.CsvFile;
import se.lu.esss.linacLego.webapp.shared.HtmlTextTree;
import se.lu.esss.linacLego.webapp.shared.LinacLegoWebAppException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("linacLego")
public interface LinacLegoService extends RemoteService 
{
	HtmlTextTree getPbsViewHtmlTextTree(String linacLegoXmlLink) throws LinacLegoWebAppException;
	HtmlTextTree getXmlViewHtmlTextTree(String linacLegoXmlLink) throws LinacLegoWebAppException;
	CsvFile getCsvFile(String csvFileLink) throws LinacLegoWebAppException;
}
