package se.lu.esss.linacLego.webapp.client;

import se.lu.esss.linacLego.webapp.shared.CsvFile;
import se.lu.esss.linacLego.webapp.shared.LinacLegoViewSerializer;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LinacLegoServiceAsync 
{
	void readCsvFile(String fileLocationPath, AsyncCallback<CsvFile> callback);
	void getLinacLegoViewSerializer(AsyncCallback<LinacLegoViewSerializer> callback);
}
