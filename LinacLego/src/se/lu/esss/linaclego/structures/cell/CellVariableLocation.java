package se.lu.esss.linaclego.structures.cell;

public class CellVariableLocation 
{
	private int nslot;
	private String dataId;
	private CellVariable cellVariable;
	
	public int getNslot() {return nslot;}
	public String getDataId() {return dataId;}
	public CellVariable getCellVariable() {return cellVariable;}

	public CellVariableLocation(int nslot, String dataId, CellVariable cellVariable)
	{
		this.nslot = nslot;
		this.dataId = dataId;
		this.cellVariable = cellVariable;
	}

}
