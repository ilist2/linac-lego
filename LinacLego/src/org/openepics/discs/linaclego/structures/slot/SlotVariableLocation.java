package org.openepics.discs.linaclego.structures.slot;

public class SlotVariableLocation 
{
	private int nble;
	private String dataId;
	private SlotVariable slotVariable;
	
	public int getNble() {return nble;}
	public String getDataId() {return dataId;}
	public SlotVariable getSlotVariable() {return slotVariable;}

	public SlotVariableLocation(int nble, String dataId, SlotVariable slotVariable)
	{
		this.nble = nble;
		this.dataId = dataId;
		this.slotVariable = slotVariable;
	}

}
