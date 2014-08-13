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
package org.openepics.discs.linaclego.webapp.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class CsvFile implements Serializable
{
	private ArrayList<CsvLine> lineList;
	private boolean fileOpen = false;
	private int numOfCols = 0;
	private int[] colWidth = null;
	private int tableWidth = 0;
	
	public int getTableWidth() {return tableWidth;}
	public CsvFile()
	{
		lineList = new ArrayList<CsvLine>();
		fileOpen = true;
	}
	public void addLine(String line) throws LinacLegoWebAppException
	{
		if (!fileOpen) throw new LinacLegoWebAppException("CSV File closed");
		lineList.add(new CsvLine(line));
	}
	public void addLine(CsvLine line) throws LinacLegoWebAppException
	{
		if (!fileOpen) throw new LinacLegoWebAppException("CSV File closed");
		lineList.add(line);
	}
	public int numOfRows()
	{
		return lineList.size();
	}
	public int numOfCols()
	{
		return numOfCols;
	}
	public int getColWidth(int icol)
	{
		if (icol < 0 ) return 0;
		if (icol >= numOfCols) return 0;
		return colWidth[icol];
	}
	public CsvLine getLine(int iline)
	{
		if (iline >= numOfRows()) return null;
		if (iline < 0) return null;
		return lineList.get(iline);
	}
	public void close()
	{
		fileOpen = false;
		if (numOfRows() < 1)
		{
			numOfCols = 0;
			return;
		}
		numOfCols = getLine(0).numCells();
		for (int irow = 1; irow < numOfRows(); ++ irow)
			if (getLine(irow).numCells() > numOfCols) numOfCols = getLine(irow).numCells();
		colWidth = new int[numOfCols];
		for (int icol = 0; icol < numOfCols; ++icol)
			colWidth[icol] = lineList.get(0).getCell(icol).length();
		for (int irow = 1; irow < numOfRows(); ++ irow)
		{
			for (int icol = 0; icol < numOfCols; ++icol)
				if (colWidth[icol] < lineList.get(irow).getCell(icol).length())
					colWidth[icol] = lineList.get(irow).getCell(icol).length();
		}
		tableWidth = 0;
		for (int icol = 0; icol < numOfCols; ++icol) tableWidth = tableWidth + colWidth[icol];
		
	}
}
