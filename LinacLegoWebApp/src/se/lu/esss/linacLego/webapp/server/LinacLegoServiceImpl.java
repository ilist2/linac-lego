package se.lu.esss.linacLego.webapp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import se.lu.esss.linacLego.webapp.client.LinacLegoService;
import se.lu.esss.linacLego.webapp.shared.CsvFile;
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
	public CsvFile readCsvFile(String fileName) throws LinacLegoWebAppException 
	{
		String fileLocationPath = getServletContext().getRealPath(fileName);
		CsvFile csvFile = new CsvFile();
        BufferedReader br;
        try 
        {
            br = new BufferedReader(new FileReader(fileLocationPath));
            String line;
            while ((line = br.readLine()) != null) 
            {  
            	csvFile.addLine(line);
            }
            br.close();
            csvFile.close();
        } 
        catch (FileNotFoundException e) {throw new LinacLegoWebAppException(e);}
        catch (IOException e) {throw new LinacLegoWebAppException(e);}
		return csvFile;
	}

	@Override
	public LinacLegoViewSerializer getLinacLegoViewSerializer() throws LinacLegoWebAppException 
	{
		String fileLocationPath = getServletContext().getRealPath("/linacLegoFiles/linacLegoView.ser");
		try 
		{
			return LinacLegoServiceImplStaticMethods.readLinacLegoViewSerializer(new File(fileLocationPath));
		} catch (ClassNotFoundException | IOException e) {throw new LinacLegoWebAppException(e);}
	}
}
