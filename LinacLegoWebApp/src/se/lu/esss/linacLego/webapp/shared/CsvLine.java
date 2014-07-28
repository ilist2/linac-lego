package se.lu.esss.linacLego.webapp.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class CsvLine implements Serializable
{
	private ArrayList<String> cellList = null;
	public CsvLine()
	{
		cellList = new ArrayList<String>();
	}
	public CsvLine(String line)
	{
		this();
		addLine(line);
	}
	public void addLine(String line)
	{
        String delims = "[,]+";
        String[] splitResponse = null;
        splitResponse = line.split(delims);
        if (splitResponse.length > 0)
        {
        	for (int icell = 0; icell < splitResponse.length; ++ icell)
        		cellList.add(splitResponse[icell]);
        }
	}
	public int numCells()
	{
		return cellList.size();
	}
	public String getCell(int icell)
	{
		if (icell >= numCells()) return "";
		if (icell < 0) return "";
		return cellList.get(icell);
	}
	public void removeLastCell()
	{
		cellList.remove(numCells() - 1);
	}

}
