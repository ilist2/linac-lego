/**
 * 
 */
package org.openepics.discs.linaclego;

import org.openepics.discs.linaclego.structures.elements.beamline.Bend;
import org.openepics.discs.linaclego.structures.elements.beamline.Drift;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlCell;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlDriftTube;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlRfGap;
import org.openepics.discs.linaclego.structures.elements.beamline.FieldMap;
import org.openepics.discs.linaclego.structures.elements.beamline.Ncells;
import org.openepics.discs.linaclego.structures.elements.beamline.Quad;
import org.openepics.discs.linaclego.structures.elements.beamline.RfGap;
import org.openepics.discs.linaclego.structures.elements.beamline.ThinSteering;

/**
 * BeamLineElement visitor
 * 
 * @author Ivo List
 *
 */
public interface BLEVisitor {
	public void visit(Drift drift);
	public void visit(Quad quad);
	public void visit(RfGap rfGap);
	public void visit(Bend bend);
	public void visit(ThinSteering thinSteering);
	public void visit(Ncells ncells);
	public void visit(FieldMap fieldMap);
	public void visit(BLEVisitor bleVisitor);
	public void visit(DtlRfGap dtlRfGap);
	public void visit(DtlDriftTube dtlDriftTube);
	public void visit(DtlCell dtlCell);
}
