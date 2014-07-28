package se.lu.esss.linacLego.webapp.server;

import java.io.File;
import java.io.IOException;

import se.lu.esss.linacLego.webapp.client.LinacLegoService;
import se.lu.esss.linacLego.webapp.shared.LinacLegoViewSerializer;
import se.lu.esss.linacLego.webapp.shared.LinacLegoWebAppException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LinacLegoServiceImpl extends RemoteServiceServlet implements LinacLegoService 
{
	@Override
	public LinacLegoViewSerializer getLinacLegoViewSerializer() throws LinacLegoWebAppException 
	{
		String fileLocationPath = getServletContext().getRealPath("/linacLegoFiles/linacLegoView.ser");
		try 
		{
//			try {Thread.sleep(10000);} catch (InterruptedException e) {}
//			boolean test = true;
//			if (test) throw new LinacLegoWebAppException();
			return LinacLegoServiceImplStaticMethods.readLinacLegoViewSerializer(new File(fileLocationPath));
		} catch (ClassNotFoundException | IOException e) {throw new LinacLegoWebAppException(e);}
	}
}
