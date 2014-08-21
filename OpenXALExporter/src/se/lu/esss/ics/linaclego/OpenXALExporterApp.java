package se.lu.esss.ics.linaclego;

import java.io.IOException;
import java.net.URL;

import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
import org.openepics.discs.linaclego.simplexml.SimpleXmlDoc;
import org.openepics.discs.linaclego.simplexml.SimpleXmlException;

public class OpenXALExporterApp {
	public static void main(String args[]) throws LinacLegoException, SimpleXmlException, IOException
	{
		SimpleXmlDoc simpleXmlDoc = new SimpleXmlDoc(new URL(args[0]));
		LinacLego linacLego = new LinacLego(simpleXmlDoc, null);
		linacLego.updateLinac();
		OpenXALExporter exporter = new OpenXALExporter(args[1]);
		exporter.export(linacLego);
	}
}
