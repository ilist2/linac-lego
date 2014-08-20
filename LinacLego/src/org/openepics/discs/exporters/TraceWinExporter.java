package org.openepics.discs.exporters;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import org.openepics.discs.linaclego.BLEVisitor;
import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.structures.Section;
import org.openepics.discs.linaclego.structures.elements.ControlPoint;
import org.openepics.discs.linaclego.structures.elements.beamline.BeamLineElement;
import org.openepics.discs.linaclego.structures.elements.beamline.Bend;
import org.openepics.discs.linaclego.structures.elements.beamline.Drift;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlCell;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlDriftTube;
import org.openepics.discs.linaclego.structures.elements.beamline.DtlRfGap;
import org.openepics.discs.linaclego.structures.elements.beamline.Edge;
import org.openepics.discs.linaclego.structures.elements.beamline.FieldMap;
import org.openepics.discs.linaclego.structures.elements.beamline.Ncells;
import org.openepics.discs.linaclego.structures.elements.beamline.Quad;
import org.openepics.discs.linaclego.structures.elements.beamline.RfGap;
import org.openepics.discs.linaclego.structures.elements.beamline.ThinSteering;

public class TraceWinExporter implements BLEVisitor {
	private String fileName;
	private PrintWriter pw;
	
	public static final String space = "\t";
	public static final String newline = System.getProperty("line.separator");
	public static final DecimalFormat zeroPlaces = new DecimalFormat("###");
	public static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	
	public TraceWinExporter(String fileName) {
		this.fileName = fileName;
	}
	
	public void export(LinacLego linacLego) throws FileNotFoundException {
		pw = new PrintWriter(fileName);
		pw.println(";" + linacLego.getLinacLegoTitle());
		linacLego.getLinac().accept(this);
		pw.println("END");
		pw.close();
	}		
	
	@Override
	public void visit(Section section) {
		String command = "";
		if (!section.isPeriodicLatticeSection())
		{
			int index = section.getIndex();
			if (index > 0)
			{
				if (section.getLinac().getSectionList().get(index - 1).isPeriodicLatticeSection())
				{
					command = "LATTICE_END" + newline;
				}
			}
		}
		command = command + "FREQ";
		command = command + space + Double.toString(section.getRfFreqMHz());
		if (section.isPeriodicLatticeSection())
		{
			command = command + newline + "LATTICE";
			command = command + space + Integer.toString(section.getCellList().get(0).getNumBeamLineElements()) + space + "0";
		}
		command = command + newline;
		
		pw.print(command);
	}
	
	public void printTraceWin(String traceWinCommand, BeamLineElement ble) 
	{
		if (ble.getSlot().getCell().getSection().getLinac().getLinacLego().isPrintIdInTraceWin()) 
			traceWinCommand = ble.getEssId() + ":" + space + traceWinCommand;
		pw.println(traceWinCommand);
	}

	@Override
	public void visit(Drift drift) {
		String command = "";
		command = "DRIFT";
		command = command + space + fourPlaces.format(drift.getLengthMM());
		command = command + space + fourPlaces.format(drift.getrMM());
		command = command + space + fourPlaces.format(drift.getRyMM());
		printTraceWin(command, drift);
	}

	@Override
	public void visit(Quad quad) {
		String command = "";
		command = "QUAD";
		command = command + space + Double.toString(quad.getLengthMM());
		command = command + space + Double.toString(quad.getGradTpM());
		command = command + space + Double.toString(quad.getRadius());
		printTraceWin(command, quad);
	}

	@Override
	public void visit(RfGap rfGap) {
		String command = "";
		command = "GAP";
		command = command + space + Double.toString(rfGap.getVoltsT());
		command = command + space + Double.toString(rfGap.getRfPhaseDeg());
		command = command + space + Double.toString(rfGap.getRadApermm());
		command = command + space + Integer.toString(rfGap.getPhaseFlag());
		command = command + space + Double.toString(rfGap.getBetaS());
		command = command + space + Double.toString(rfGap.getTts());
		command = command + space + Double.toString(rfGap.getKtts());
		command = command + space + Double.toString(rfGap.getK2tts());
		command = command + space + Double.toString(rfGap.getKs());
		command = command + space + Double.toString(rfGap.getK2s());
		printTraceWin(command, rfGap);		
	}

	@Override
	public void visit(Bend bend) {
		String command = "";
		command = "BEND";
		command = command + space + Double.toString(bend.getTWBendAngleDeg());
		command = command + space + Double.toString(bend.getRadOfCurvmm());
		command = command + space + Integer.toString(bend.getFieldIndex());
		command = command + space + Double.toString(bend.getAperRadmm());
		command = command + space + Integer.toString(bend.getHVflag());
		printTraceWin(command, bend);
	}

	@Override
	public void visit(Edge edge) {
		String command = "";
		command = "EDGE";
		command = command + space + Double.toString(edge.getPoleFaceAngleDeg());
		command = command + space + Double.toString(edge.getRadOfCurvmm());
		command = command + space + Double.toString(edge.getGapmm());
		command = command + space + Double.toString(edge.getK1());
		command = command + space + Double.toString(edge.getK2());
		command = command + space + Double.toString(edge.getAperRadmm());
		command = command + space + Integer.toString(edge.getHVflag());
		printTraceWin(command, edge);	
	}
	
	@Override
	public void visit(ThinSteering thinSteering) {
		String command = "";
		command = "THIN_STEERING";
		command = command + space + Double.toString(thinSteering.getXkick());
		command = command + space + Double.toString(thinSteering.getYkick());
		command = command + space + Double.toString(thinSteering.getRmm());
		command = command + space + Integer.toString(thinSteering.getKickType());
		printTraceWin(command, thinSteering);
	}

	@Override
	public void visit(Ncells ncells) {
		String command = "NCELLS";
		command = command + space + Integer.toString(ncells.getMode());
		command = command + space + Integer.toString(ncells.getNcells());
		command = command + space + fourPlaces.format(ncells.getBetag());
		command = command + space + zeroPlaces.format(ncells.getE0tRef());
		command = command + space + twoPlaces.format(ncells.getPhiTWdeg());
		command = command + space + twoPlaces.format(ncells.getRadius());
		command = command + space + Integer.toString(ncells.getPhaseFlag());
		command = command + space + fourPlaces.format(ncells.getKe0t()[0]);
		command = command + space + fourPlaces.format(ncells.getKe0t()[2]);
		command = command + space + fourPlaces.format( ncells.getDz()[0] * 1000.0);
		command = command + space + fourPlaces.format( ncells.getDz()[2] * 1000.0);
		if (ncells.isTtInfo())
		{
			command = command + space + fourPlaces.format(ncells.getBetaRef());
			command = command + space + fourPlaces.format(ncells.getTtRef()[1]);
			command = command + space + fourPlaces.format(-ncells.getKttRef()[1]);
			command = command + space + fourPlaces.format(-ncells.getK2ttRef()[1]);
			command = command + space + fourPlaces.format(ncells.getTtRef()[0]);
			command = command + space + fourPlaces.format(-ncells.getKttRef()[0]);
			command = command + space + fourPlaces.format(-ncells.getK2ttRef()[0]);
			command = command + space + fourPlaces.format(ncells.getTtRef()[2]);
			command = command + space + fourPlaces.format(-ncells.getKttRef()[2]);
			command = command + space + fourPlaces.format(-ncells.getK2ttRef()[2]);
		}
		printTraceWin(command, ncells);
		
	}

	@Override
	public void visit(FieldMap fieldMap) {
		String command = "";
		command = "FIELD_MAP 100";
		command = command + space + fourPlaces.format(fieldMap.getLengthmm());
		command = command + space + fourPlaces.format(fieldMap.getRfpdeg());
		command = command + space + fourPlaces.format(fieldMap.getRadiusmm());
		command = command + space + "0";
		command = command + space + Double.toString(fieldMap.getXelmax());
		command = command + space + "0 0";
		command = command + space + fieldMap.getFieldMapFileName().split("\\.")[0];
		printTraceWin(command, fieldMap);
	}

	@Override
	public void visit(DtlCell dtlCell) {
		String command = "";
		command = "DTL_CEL";
		command = command + space + Double.toString(dtlCell.getCellLenmm());
		command = command + space + Double.toString(dtlCell.getQ1Lenmm());
		command = command + space + Double.toString(dtlCell.getQ2Lenmm());
		command = command + space + Double.toString(dtlCell.getCellCentermm());
		command = command + space + Double.toString(dtlCell.getGrad1Tpm());
		command = command + space + Double.toString(dtlCell.getGrad2Tpm());
		command = command + space + Double.toString(dtlCell.getVoltsT() * dtlCell.getVoltMult());
		command = command + space + Double.toString(dtlCell.getRfPhaseDeg() + dtlCell.getPhaseAdd());
		command = command + space + Double.toString(dtlCell.getRadApermm());
		command = command + space + Integer.toString(dtlCell.getPhaseFlag());
		command = command + space + Double.toString(dtlCell.getBetaS());
		command = command + space + Double.toString(dtlCell.getTts());
		command = command + space + Double.toString(dtlCell.getKtts());
		command = command + space + Double.toString(dtlCell.getK2tts());
		printTraceWin(command, dtlCell);
	}


	@Override
	public void visit(DtlDriftTube dtlDriftTube) {
		String command = "";
		command = command + "DRIFT";
		command = command + space + fourPlaces.format(dtlDriftTube.getNoseConeUpLen());
		command = command + space + fourPlaces.format(dtlDriftTube.getRadius());
		command = command + space + "0.0";
		command = command + "\n";
		command = command + "QUAD";
		command = command + space + Double.toString(dtlDriftTube.getQuadLen());
		command = command + space + Double.toString(dtlDriftTube.getQuadGrad());
		command = command + space + Double.toString(dtlDriftTube.getRadius());
		command = command + "\n";
		command = command + "DRIFT";
		command = command + space + fourPlaces.format(dtlDriftTube.getNoseConeDnLen());
		command = command + space + fourPlaces.format(dtlDriftTube.getRadius());
		command = command + space + "0.0";
		printTraceWin(command, dtlDriftTube);
	}

	@Override
	public void visit(DtlRfGap dtlRfGap) {
		String command = "";
		command = command + "DRIFT";
		command = command + space + fourPlaces.format(dtlRfGap.getLength() / 2.0);
		command = command + space + fourPlaces.format(dtlRfGap.getRadApermm());
		command = command + space + "0.0";
		command = command + "\n";
		command = command + "GAP";
		command = command + space + Double.toString(dtlRfGap.getVoltsT());
		command = command + space + Double.toString(dtlRfGap.getRfPhaseDeg());
		command = command + space + Double.toString(dtlRfGap.getRadApermm());
		command = command + space + Integer.toString(dtlRfGap.getPhaseFlag());
		command = command + space + Double.toString(dtlRfGap.getBetaS());
		command = command + space + Double.toString(dtlRfGap.getTts());
		command = command + space + Double.toString(dtlRfGap.getKtts());
		command = command + space + Double.toString(dtlRfGap.getK2tts());
		command = command + space + Double.toString(0.0);
		command = command + space + Double.toString(0.0);
		command = command + "\n";
		command = command + "DRIFT";
		command = command + space + fourPlaces.format(dtlRfGap.getLength() / 2.0);
		command = command + space + fourPlaces.format(dtlRfGap.getRadApermm());
		command = command + space + "0.0";
		printTraceWin(command, dtlRfGap);
	}

	@Override
	public void visit(ControlPoint controlPoint) {
		if (!controlPoint.getSlot().getCell().getSection().getLinac().getLinacLego().isPrintControlPoints()) return;
		String command = ";" + controlPoint.getName().replace(":", "-")
				+ space + "dxmm=" + Double.toString(controlPoint.getEndLocalPosVec()[0] * 1000.0)
				+ space + "dymm=" + Double.toString(controlPoint.getEndLocalPosVec()[1] * 1000.0)
				+ space + "dzmm=" + Double.toString(controlPoint.getEndLocalPosVec()[2] * 1000.0);
		pw.println(command);
	}
}
