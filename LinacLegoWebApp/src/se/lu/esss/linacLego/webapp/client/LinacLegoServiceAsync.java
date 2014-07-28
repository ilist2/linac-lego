package se.lu.esss.linacLego.webapp.client;

import se.lu.esss.linacLego.webapp.shared.LinacLegoViewSerializer;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LinacLegoServiceAsync 
{
	void getLinacLegoViewSerializer(AsyncCallback<LinacLegoViewSerializer> callback);
}
