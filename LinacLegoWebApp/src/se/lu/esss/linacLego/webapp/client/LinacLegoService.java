package se.lu.esss.linacLego.webapp.client;

import se.lu.esss.linacLego.webapp.shared.LinacLegoViewSerializer;
import se.lu.esss.linacLego.webapp.shared.LinacLegoWebAppException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("linacLego")
public interface LinacLegoService extends RemoteService 
{
	LinacLegoViewSerializer getLinacLegoViewSerializer() throws LinacLegoWebAppException;
}
