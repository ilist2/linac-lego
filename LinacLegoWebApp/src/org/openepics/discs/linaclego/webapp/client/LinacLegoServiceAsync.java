package org.openepics.discs.linaclego.webapp.client;

import org.openepics.discs.linaclego.webapp.shared.CsvFile;
import org.openepics.discs.linaclego.webapp.shared.HtmlTextTree;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LinacLegoServiceAsync 
{
	void getPbsViewHtmlTextTree(String linacLegoXmlLink, AsyncCallback<HtmlTextTree> callback);
	void getXmlViewHtmlTextTree(String linacLegoXmlLink, AsyncCallback<HtmlTextTree> callback);
	void getCsvFile(String csvFileLink, AsyncCallback<CsvFile> callback);
}
