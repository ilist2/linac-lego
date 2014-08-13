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
