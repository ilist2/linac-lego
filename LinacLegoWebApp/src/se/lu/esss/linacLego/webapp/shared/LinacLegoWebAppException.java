package se.lu.esss.linacLego.webapp.shared;

@SuppressWarnings("serial")
public class LinacLegoWebAppException extends Exception
{
	public LinacLegoWebAppException() {}
	public LinacLegoWebAppException(Exception e) {super(e);}
	public LinacLegoWebAppException(String message) {super(message);}

}
