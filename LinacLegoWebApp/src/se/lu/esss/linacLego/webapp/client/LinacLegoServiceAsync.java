package se.lu.esss.linacLego.webapp.client;

import se.lu.esss.linacLego.webapp.shared.CsvFile;
import se.lu.esss.linacLego.webapp.shared.HtmlTextTree;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LinacLegoServiceAsync 
{
	void getPbsViewHtmlTextTree(String linacLegoXmlLink, AsyncCallback<HtmlTextTree> callback);
	void getXmlViewHtmlTextTree(String linacLegoXmlLink, AsyncCallback<HtmlTextTree> callback);
	void getCsvFile(String csvFileLink, AsyncCallback<CsvFile> callback);
}
