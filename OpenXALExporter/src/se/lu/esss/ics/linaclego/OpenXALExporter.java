package se.lu.esss.ics.linaclego;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openepics.discs.linaclego.BLEVisitor;
import org.openepics.discs.linaclego.LinacLego;
import org.openepics.discs.linaclego.LinacLegoException;
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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.lu.esss.ics.jels.smf.impl.ESSRfCavity;
import se.lu.esss.ics.jels.smf.impl.ESSRfGap;
import xal.model.IElement;
import xal.smf.Accelerator;
import xal.smf.AcceleratorNode;
import xal.smf.AcceleratorSeq;
import xal.smf.ChannelSuite;
import xal.smf.attr.ApertureBucket;
import xal.smf.attr.SequenceBucket;
import xal.smf.impl.Electromagnet;
import xal.smf.impl.HDipoleCorr;
import xal.smf.impl.Magnet;
import xal.smf.impl.MagnetMainSupply;
import xal.smf.impl.MagnetPowerSupply;
import xal.smf.impl.Marker;
import xal.smf.impl.RfCavity;
import xal.smf.impl.VDipoleCorr;
import xal.smf.impl.qualify.MagnetType;
import xal.tools.data.DataAdaptor;
import xal.tools.xml.XmlDataAdaptor;
import xal.tools.xml.XmlWriter;

public class OpenXALExporter implements BLEVisitor {
	private String fileName;
	private List<MagnetPowerSupply> magnetPowerSupplies;
	private double acceleratorPosition;
	private double sectionPosition;
	public static final double beta_gamma_Er_by_e0_c = -0.08980392292066133;
	
	private Accelerator accelerator;
	private AcceleratorSeq currentSequence;
	
	public OpenXALExporter(String fileName) {
		this.fileName = fileName;
	}
	
	public void export(LinacLego linacLego) throws IOException {
		acceleratorPosition = 0.0;
		sectionPosition = 0.0;
		magnetPowerSupplies = new ArrayList<>();
		
		accelerator = new Accelerator("ESS") {
			  public void write(DataAdaptor adaptor) {
				  super.write(adaptor);
				  // write out power supplies
				  DataAdaptor powerSuppliesAdaptor = adaptor.createChild("powersupplies");				 
				    for ( MagnetPowerSupply mps : magnetPowerSupplies) {
				    	mps.write( powerSuppliesAdaptor.createChild("ps"));				 
				    }				 
			  }
		};
		linacLego.getLinac().accept(this);
		accelerator.setLength(acceleratorPosition + sectionPosition);
		
		
		System.out.println("Writing output");	
		XmlDataAdaptor da = XmlDataAdaptor.newDocumentAdaptor(accelerator, "xdxf.dtd");
		Document document = da.document();
		cleanup(document);
		
		XmlWriter.writeToFile(document, new File(fileName));
	}		
	
	private void add(AcceleratorNode node)
	{
		currentSequence.addNode(node);
		double length = node.getLength();					
		if (node instanceof Magnet) {
			if (node instanceof xal.smf.impl.Bend)
				length = ((xal.smf.impl.Bend) node).getDfltPathLength();
			else
				length = ((Magnet) node).getEffLength();
		} 
		sectionPosition += length;
	}
	
	/**
	 * Cleans up XML OpenXal produces
	 * @param parent node to clean
	 */
	private void cleanup(Node parent) {			
		NodeList children = parent.getChildNodes();
		NamedNodeMap attrs = parent.getAttributes();
		if (attrs != null) {
			// unneeded attributes 
			if (attrs.getNamedItem("s") != null) attrs.removeNamedItem("s");
			if (attrs.getNamedItem("pid") != null) attrs.removeNamedItem("pid");
			if (attrs.getNamedItem("status") != null) attrs.removeNamedItem("status");
			if (attrs.getNamedItem("eid") != null) attrs.removeNamedItem("eid");

			// remove type="sequence" on sequences - import doesn't work otherwise
			if ("sequence".equals(parent.getNodeName()) && attrs.getNamedItem("type") != null && "sequence".equals(attrs.getNamedItem("type").getNodeValue())) 
				attrs.removeNamedItem("type");
		}
		
		for (int i = 0; i<children.getLength(); )
		{
			Node child = children.item(i);			
			attrs = child.getAttributes();
			
			if ("align".equals(child.getNodeName()) || "twiss".equals(child.getNodeName())) 
				// remove twiss and align - not needed
				parent.removeChild(child);
			else if ("channelsuite".equals(child.getNodeName()) && !child.hasChildNodes()) {
				parent.removeChild(child);
			}
			else if ("aperture".equals(child.getNodeName()) && "0.0".equals(attrs.getNamedItem("x").getNodeValue())) 
				// remove empty apertures
				parent.removeChild(child);
			else {			
				cleanup(child);				
				// remove empty attributes
				if ("attributes".equals(child.getNodeName()) && child.getChildNodes().getLength()==0)
				{
					parent.removeChild(child);
				} else
					i++;
			}
		}	
	}
	
	private static class RFCavityChannelSuite extends ChannelSuite 
	{
		private String name;
		
		public RFCavityChannelSuite(String name)
		{
			this.name = name.replace('_', ':');
		}
		
		/**
	     * Write data to the data adaptor for storage.
	     * @param adaptor The adaptor to which the receiver's data is written
	     */
	    public void write( final DataAdaptor adaptor ) {	       	    	
	    	writeChannel(adaptor, RfCavity.CAV_AMP_SET_HANDLE, name+":AmpCtl", true);
	    	writeChannel(adaptor, RfCavity.CAV_PHASE_SET_HANDLE, name+":PhsCtl", true);
	    	writeChannel(adaptor, RfCavity.CAV_AMP_AVG_HANDLE, name+":AmpAvg", false);
	    	writeChannel(adaptor, RfCavity.CAV_PHASE_AVG_HANDLE, name+":PhsAvg", false);
	    }
	    
	    public void writeChannel( final DataAdaptor adaptor, String handle, String signal, boolean settable ) {	       	    	
	    	
	    	final DataAdaptor channelAdaptor = adaptor.createChild("channel");	      	 
	        channelAdaptor.setValue( "handle", handle );
	        channelAdaptor.setValue( "signal", signal );
	        channelAdaptor.setValue( "settable", settable );	        	      
	    }
	}
	
	private static class ElectromagnetChannelSuite extends ChannelSuite 
	{
		private String name, signal = "B";
		
		public ElectromagnetChannelSuite(String name)
		{
			this.name = name.replace('_', ':');
		}
		
		public ElectromagnetChannelSuite(String name, String signal)
		{
			this.name = name.replace('_', ':');
			this.signal = signal;
		}
		
		
		/**
	     * Write data to the data adaptor for storage.
	     * @param adaptor The adaptor to which the receiver's data is written
	     */
	    public void write( final DataAdaptor adaptor ) {	       	    	
	    	final DataAdaptor channelAdaptor = adaptor.createChild("channel");
	            
	        channelAdaptor.setValue( "handle", Electromagnet.FIELD_RB_HANDLE );
	        channelAdaptor.setValue( "signal", name + ":" + signal );
	        channelAdaptor.setValue( "settable", false);	        	      
	    }		
	}
	
	
	private static class MagnetChannelSuite extends ChannelSuite 
	{
		private String name;
		
		public MagnetChannelSuite(String name)
		{
			this.name = name.replace('_', ':');
		}
		
		/**
	     * Write data to the data adaptor for storage.
	     * @param adaptor The adaptor to which the receiver's data is written
	     */
	    public void write( final DataAdaptor adaptor ) {	       	    	
	    	writeChannel(adaptor, MagnetPowerSupply.CURRENT_RB_HANDLE, name+":CurRB", false);
	    	writeChannel(adaptor, MagnetPowerSupply.CURRENT_SET_HANDLE, name+":CurSet", true);
	    	writeChannel(adaptor, MagnetMainSupply.FIELD_RB_HANDLE, name+":FldRB", false);
	    	writeChannel(adaptor, MagnetMainSupply.FIELD_SET_HANDLE, name+":FldSet", true);
	    	writeChannel(adaptor, MagnetPowerSupply.CYCLE_STATE_HANDLE, name+":CycSt", false);
	    	writeChannel(adaptor, MagnetMainSupply.CYCLE_ENABLE_HANDLE, name+":CycEn", true);
	    }
	    
	    public void writeChannel( final DataAdaptor adaptor, String handle, String signal, boolean settable ) {	       	    	
	    	
	    	final DataAdaptor channelAdaptor = adaptor.createChild("channel");	      	 
	        channelAdaptor.setValue( "handle", handle );
	        channelAdaptor.setValue( "signal", signal );        	      
	    }
	}
	
	
	private static class MagnetSupply extends MagnetMainSupply {
		public MagnetSupply(String name) {
			super(null);
			strId = name + "-PS";			
			channelSuite = new MagnetChannelSuite(name);
		}
	}
	
	private static void updateApertureBucket(BeamLineElement element, ApertureBucket aper) {
		/*if (element.getApertureX() != null) aper.setAperX(element.getApertureX());
		if (element.getApertureY() != null) aper.setAperY(element.getApertureY());		
		int apertureCode = element.getApertureType() == null ? 2 : element.getApertureType().getIntegerValue();
		aper.setShape(toOpenXALApertureCode(apertureCode));		*/
	}
	
	
	@Override
	public void visit(Drift drift) {
		sectionPosition += drift.getLength();
	}

	@Override
	public void visit(final Quad iquad) {
		double L = iquad.getLength();		
		double G = iquad.getQuadGradientTpm();
		
		final MagnetSupply ps = new MagnetSupply(iquad.getEssId());
		magnetPowerSupplies.add(ps);
		xal.smf.impl.Quadrupole quad = new xal.smf.impl.Quadrupole(iquad.getEssId()) { // there's no setter for type (you need to extend class)
			{
				_type="Q"; 
				channelSuite = new ElectromagnetChannelSuite(iquad.getEssId());
				mainSupplyId = ps.getId();
			}
		};
		
		quad.setPosition(sectionPosition + L*0.5); //always position on center!
		quad.setLength(L); // effLength below is actually the only one read 
		quad.getMagBucket().setEffLength(L);
					
		quad.setDfltField(G);
		quad.getMagBucket().setPolarity(1);
		updateApertureBucket(iquad, quad.getAper());
		
		add(quad);
	}

	@Override
	public void visit(final RfGap rfGap) {
		double E0TL = rfGap.getVoltsT();
		double Phis = rfGap.getRfPhaseDeg();	
		double betas = rfGap.getBetaS();
		double Ts = rfGap.getTts();
		double kTs = rfGap.getKtts();
		double k2Ts = rfGap.getK2tts();
		double kS = rfGap.getKs();
		double k2S = rfGap.getK2s();		

		// setup		
		xal.smf.impl.RfGap gap = new xal.smf.impl.RfGap(rfGap.getEssId()+":G");
		gap.setFirstGap(true); // this uses only phase for calculations
		gap.getRfGap().setEndCell(0);
		gap.setLength(0.0); // used only for positioning
		
		// following are used to calculate E0TL
		double length = 1.0; // length is not given in TraceWin, but is used only as a factor in E0TL in OpenXal
		gap.getRfGap().setLength(length); 		
		gap.getRfGap().setAmpFactor(1.0);
		/*gap.getRfGap().setGapOffset(dblVal)*/	
		
		ESSRfCavity cavity = new ESSRfCavity(rfGap.getEssId())
		{
			{
				channelSuite = new RFCavityChannelSuite(rfGap.getEssId());
			}
		};
		cavity.addNode(gap);
		cavity.getRfField().setPhase(Phis);		
		cavity.getRfField().setAmplitude(E0TL * 1e-6 / length);
		cavity.getRfField().setFrequency(rfGap.getSection().getRfFreqMHz());		
		/*cavity.getRfField().setStructureMode(dblVal);*/
		gap.getRfGap().setTTF(1.0);		
		
		// TTF		
		if (betas == 0.0) {
			cavity.getRfField().setTTFCoefs(new double[] {});
			cavity.getRfField().setTTF_endCoefs(new double[] {});
		} else {				
			cavity.getRfField().setTTFCoefs(new double[] {betas, Ts, kTs, k2Ts});
			cavity.getRfField().setTTF_endCoefs(new double[] {betas, Ts, kTs, k2Ts});
			cavity.getRfField().setSTFCoefs(new double[] {betas, 0., kS, k2S});
			cavity.getRfField().setSTF_endCoefs(new double[] {betas, 0., kS, k2S});
		}		
		
		updateApertureBucket(rfGap, gap.getAper());		
		
		cavity.setPosition(sectionPosition);
		cavity.setLength(0.0);
		add(cavity);
	}

	@Override
	public void visit(final Bend ibend) {
		double alpha_deg = ibend.getTWBendAngleDeg();
		double rho = ibend.getRadOfCurvmm();
		double entry_angle_deg = alpha_deg / 2.; //ibend.getEntranceAngle();
		double exit_angle_deg = alpha_deg / 2.; //ibend.getExitAngle();
		double G = 0.;// ibend.getGap();
		
		// TODO put those values into the database
		double entrK1 = 0.45, entrK2 = 2.8, exitK1 = 0.45, exitK2 = 2.8;
		double N = 0;
						
		// mm -> m
		rho *= 1e-3;
		G *= 1e-3;		
		
		// calculations		
		double len = Math.abs(rho*alpha_deg * Math.PI/180.0);
		double quadComp = - N / (rho*rho);
		
		// following are used to calculate field		
	    /*double c  = IConstants.LightSpeed;	      
	    double e = GlobalConst.SpeciesCharge;
	    double Er = probe.getSpeciesRestEnergy();
	    double gamma = probe.getGamma();
	    double b  = probe.getBeta();*/
	    
	    double k /* = b*gamma*Er/(e*c); */ = beta_gamma_Er_by_e0_c;
	    double B0 = k/rho*Math.signum(alpha_deg);
	    //double B0 = b*gamma*Er/(e*c*rho)*Math.signum(alpha);
		
	    final MagnetSupply ps = new MagnetSupply(ibend.getEssId());
		magnetPowerSupplies.add(ps);
	    se.lu.esss.ics.jels.smf.impl.ESSBend bend = new se.lu.esss.ics.jels.smf.impl.ESSBend(ibend.getEssId(), 
				 MagnetType.VERTICAL)
	    {
	    	{				
				channelSuite = new ElectromagnetChannelSuite(ibend.getEssId());
				mainSupplyId = ps.getId();
			}
	    };
		bend.setPosition(sectionPosition+len*0.5); //always position on center!
		bend.setLength(len); // both paths are used in calculation
		bend.getMagBucket().setPathLength(len);
		
		bend.getMagBucket().setDipoleEntrRotAngle(-entry_angle_deg);
		bend.getMagBucket().setBendAngle(alpha_deg);
		bend.getMagBucket().setDipoleExitRotAngle(-exit_angle_deg);		
		bend.setDfltField(B0);		
		bend.getMagBucket().setDipoleQuadComponent(quadComp);
		
		bend.setGap(G);
		bend.setEntrK1(entrK1);
		bend.setEntrK2(entrK2);
		bend.setExitK1(exitK1);
		bend.setExitK2(exitK2);
		
		updateApertureBucket(ibend, bend.getAper());		
				
		add(bend);		
		
	}

	@Override
	public void visit(final ThinSteering thinSteering) {
		double L = thinSteering.getLength();
		
		final MagnetSupply vcps = new MagnetSupply(thinSteering.getEssId()+"-VC");
		magnetPowerSupplies.add(vcps);

		VDipoleCorr vcorr = new VDipoleCorr(thinSteering.getEssId()+"-VC") {
			{
				channelSuite = new ElectromagnetChannelSuite(thinSteering.getEssId()+"-VC");
				mainSupplyId = vcps.getId();
			}
		};
		vcorr.setPosition(sectionPosition + L/2.);
		vcorr.setLength(L);
		vcorr.getMagBucket().setEffLength(L == 0. ?  1. : L);
		updateApertureBucket(thinSteering, vcorr.getAper());
		add(vcorr);
		
		final MagnetSupply hcps = new MagnetSupply(thinSteering.getEssId()+"-HC");
		magnetPowerSupplies.add(hcps);
		
		HDipoleCorr hcorr = new HDipoleCorr(thinSteering.getEssId()+"-HC") {
			{
				channelSuite = new ElectromagnetChannelSuite(thinSteering.getEssId()+"-HC");
				mainSupplyId = hcps.getId();
			}
		};
		hcorr.setPosition(sectionPosition + L/2.);
		hcorr.setLength(L);
		hcorr.getMagBucket().setEffLength(L == 0. ?  1. : L);
		updateApertureBucket(thinSteering, hcorr.getAper());
		add(hcorr);
	}

	@Override
	public void visit(final Ncells ncells) {
		double frequency = ncells.getSection().getRfFreqMHz();
		
		double Phis = ncells.getPhiSynchCalc();
		double E0T = ncells.getE0tRef();
		double betas = ncells.getBetaRef();
		
		double Ts = ncells.getTtRef()[1];
		double kTs = ncells.getKttRef()[1];
		double k2Ts = ncells.getK2ttRef()[1];		
		
		double Ti = ncells.getTtRef()[0];
		double kTi = ncells.getKttRef()[0];
		double k2Ti = ncells.getK2ttRef()[0];
		
		double To = ncells.getTtRef()[2];
		double kTo = ncells.getKttRef()[2];
		double k2To = ncells.getK2ttRef()[2];
		
		double betag = ncells.getBetag();
		double kE0Ti = ncells.getKe0t()[0];
		double kE0To = ncells.getKe0t()[2];
		double dzi = ncells.getDz()[0];
		double dzo = ncells.getDz()[2];
		
		int n = ncells.getNcells();
		int m = ncells.getMode();
		
		ESSRfCavity cavity = new ESSRfCavity(ncells.getEssId())
		{
			{
				channelSuite = new RFCavityChannelSuite(ncells.getEssId());
			}
		};
		cavity.getRfField().setPhase(Phis);
		cavity.getRfField().setAmplitude(E0T * 1e-6);
		cavity.getRfField().setFrequency(frequency * 1e-6);	

		// TTF		
		if (betas == 0.0) {
			cavity.getRfField().setTTF_startCoefs(new double[] {});
			cavity.getRfField().setTTFCoefs(new double[] {});
			cavity.getRfField().setTTF_endCoefs(new double[] {});
		} else {
			cavity.getRfField().setTTF_startCoefs(new double[] {betas, Ti, kTi, k2Ti});
			cavity.getRfField().setTTFCoefs(new double[] {betas, Ts, kTs, k2Ts});
			cavity.getRfField().setTTF_endCoefs(new double[] {betas, To, kTo, k2To});			
		}		

		
		// setup		
		ESSRfGap firstgap = new ESSRfGap(ncells.getEssId()+":G0");
		
		double lambda = IElement.LightSpeed/frequency;
		double Lc0,Lc,Lcn;
		double amp0,ampn;
		double pos0, posn;
		
		amp0 = (1+kE0Ti)*(Ti/Ts);		
		ampn = (1+kE0To)*(To/Ts);
		if (m==0) {
			Lc = Lc0 = Lcn = betag * lambda;
			pos0 = 0.5*Lc0 + dzi;
			posn = Lc0 + (n-2)*Lc + 0.5*Lcn + dzo;			
		} else if (m==1) {
			Lc = Lc0 = Lcn = 0.5 * betag * lambda;
			pos0 = 0.5*Lc0 + dzi;
			posn = Lc0 + (n-2)*Lc + 0.5*Lcn + dzo;
			cavity.getRfField().setStructureMode(1);
		} else { //m==2
			Lc0 = Lcn = 0.75 * betag * lambda;
			Lc = betag * lambda;			
			pos0 = 0.25 * betag * lambda + dzi;
			posn = Lc0 + (n-2)*Lc + 0.5 * betag * lambda + dzo;
		}
						
		firstgap.setFirstGap(true); // this uses only phase for calculations
		firstgap.getRfGap().setEndCell(0);
		firstgap.setLength(0); // used only for positioning
		firstgap.setPosition(pos0);
		
		// following are used to calculate E0TL		
		firstgap.getRfGap().setLength(Lc0); 		
		firstgap.getRfGap().setAmpFactor(amp0);
		firstgap.getRfGap().setTTF(1);
		
		cavity.addNode(firstgap);
				
		for (int i = 1; i<n-1; i++) {
			ESSRfGap gap = new ESSRfGap(ncells.getEssId()+":G"+i);
			gap.getRfGap().setTTF(1);
			gap.setPosition(Lc0 + (i-0.5)*Lc);
			gap.setLength(0);
			gap.getRfGap().setLength(Lc);
			gap.getRfGap().setAmpFactor(1.0);
			cavity.addNode(gap);
		}
		
		ESSRfGap lastgap = new ESSRfGap(ncells.getEssId()+":G"+(n-1));
		lastgap.getRfGap().setEndCell(1);
		lastgap.setLength(0); // used only for positioning
		lastgap.setPosition(posn);
		
		// following are used to calculate E0TL		
		lastgap.getRfGap().setLength(Lcn); 		
		lastgap.getRfGap().setAmpFactor(ampn);
		lastgap.getRfGap().setTTF(1);
		cavity.addNode(lastgap);		
				
		cavity.setLength(Lc0+(n-2)*Lc+Lcn);
		cavity.setPosition(sectionPosition);		
		add(cavity);		
	}

	@Override
	public void visit(FieldMap fieldMap) {
		Marker m = new Marker(fieldMap.getEssId());
		m.setPosition(sectionPosition);
		m.setLength(fieldMap.getLength());
		add(m);
	}

	@Override
	public void visit(DtlRfGap dtlRfGap) {
		Marker m = new Marker(dtlRfGap.getEssId());
		m.setPosition(sectionPosition);
		add(m);
	}

	@Override
	public void visit(DtlDriftTube dtlDriftTube) {
		Marker m = new Marker(dtlDriftTube.getEssId());
		m.setPosition(sectionPosition);
		add(m);
	}

	@Override
	public void visit(final DtlCell dtlCell) {
		// mm -> m
		double L = dtlCell.getCellLenmm()*1e-3;
		double Lq1 = dtlCell.getQ1Lenmm()*1e-3;
		double Lq2 = dtlCell.getQ2Lenmm()*1e-3;
		double g = dtlCell.getCellCentermm()*1e-3;
		
		double Phis = dtlCell.getRfPhaseDeg() + dtlCell.getPhaseAdd();
		double betas = dtlCell.getBetaS();
		double Ts = dtlCell.getTts();
		double kTs = dtlCell.getKtts();
		double k2Ts = dtlCell.getK2tts();
		double kS = 0;
		double k2S = 0;
		double E0TL = dtlCell.getVoltsT()  * dtlCell.getVoltMult();
		
		double B1 = dtlCell.getGrad1Tpm();
		double B2 = dtlCell.getGrad2Tpm();
		
		// setup		
		// QUAD1,2
		final MagnetSupply ps1 = new MagnetSupply(dtlCell.getEssId()+"A");
		magnetPowerSupplies.add(ps1);
		
		xal.smf.impl.Quadrupole quad1 = new xal.smf.impl.Quadrupole(dtlCell.getEssId()+":Q1") { // there's no setter for type (you need to extend class)
			{_type="Q";
			channelSuite = new ElectromagnetChannelSuite(dtlCell.getEssId(),"B1");
			mainSupplyId = ps1.getId();
			}
		};
		quad1.setPosition(0.5*Lq1); //always position on center!
		quad1.setLength(Lq1); // effLength below is actually the only one read 
		quad1.getMagBucket().setEffLength(Lq1);
		quad1.setDfltField(B1);
		quad1.getMagBucket().setPolarity(1);
		
		final MagnetSupply ps2 = new MagnetSupply(dtlCell.getEssId()+"B");
		magnetPowerSupplies.add(ps2);
		
		xal.smf.impl.Quadrupole quad2 = new xal.smf.impl.Quadrupole(dtlCell.getEssId()+":Q2") { // there's no setter for type (you need to extend class)
			{_type="Q"; 
			channelSuite = new ElectromagnetChannelSuite(dtlCell.getEssId(),"B2");	
			mainSupplyId = ps2.getId();
			}
		};
		quad2.setPosition(L-0.5*Lq2); //always position on center!
		quad2.setLength(Lq2); // effLength below is actually the only one read 
		quad2.getMagBucket().setEffLength(Lq2);
		quad2.setDfltField(B2);
		quad2.getMagBucket().setPolarity(1);
		
		
		// GAP
		xal.smf.impl.RfGap gap = new xal.smf.impl.RfGap(dtlCell.getEssId()+":G");
		gap.setFirstGap(true); // this uses only phase for calculations
		gap.getRfGap().setEndCell(0);
		gap.setLength(0.0); // used only for positioning
		gap.setPosition(0.5*L-g);
		// following are used to calculate E0TL
		double length = L-Lq1-Lq2; // length is not given in TraceWin, but is used only as a factor in E0TL in OpenXal
		gap.getRfGap().setLength(length); 		
		gap.getRfGap().setAmpFactor(1.0);
		gap.getRfGap().setTTF(1.0);		
		/*gap.getRfGap().setGapOffset(dblVal)*/		
		
		ESSRfCavity dtlTank = new ESSRfCavity(dtlCell.getEssId())
		{
			{
				channelSuite = new RFCavityChannelSuite(dtlCell.getEssId());
			}
		};; // this could also be rfcavity, makes no difference
		dtlTank.addNode(quad1);
		dtlTank.addNode(gap);
		dtlTank.addNode(quad2);
		dtlTank.getRfField().setPhase(Phis);		
		dtlTank.getRfField().setAmplitude(E0TL * 1e-6 / length);
		dtlTank.getRfField().setFrequency(dtlCell.getSection().getRfFreqMHz());		
		/*cavity.getRfField().setStructureMode(dblVal);*/
				
		// TTF		
		if (betas == 0.0) {			
			dtlTank.getRfField().setTTFCoefs(new double[] {0.0});
		} else {
			dtlTank.getRfField().setTTFCoefs(new double[] {betas, Ts, kTs, k2Ts});
			dtlTank.getRfField().setTTF_endCoefs(new double[] {betas, Ts, kTs, k2Ts});
			dtlTank.getRfField().setSTFCoefs(new double[] {betas, 0., kS, k2S});
			dtlTank.getRfField().setSTF_endCoefs(new double[] {betas, 0., kS, k2S});
		}		
		dtlTank.setLength(L);
		dtlTank.setPosition(sectionPosition);		
		add(dtlTank);
	}

	@Override
	public void visit(Section section) {
		AcceleratorSeq s;
		try {
			s = new AcceleratorSeq(section.getId());
		} catch (LinacLegoException e) { //TODO
			s = new AcceleratorSeq("");
		}
		
		if (currentSequence != null) {
			SequenceBucket sequenceBucket = new SequenceBucket();
			sequenceBucket.setPredecessors(new String[]{currentSequence.getId()});
			s.setSequence(sequenceBucket);
			currentSequence.setLength(sectionPosition);
			acceleratorPosition += sectionPosition;
			sectionPosition = 0.0;
		}
		s.setPosition(acceleratorPosition);	
		accelerator.addNode(s);
		
		currentSequence = s;
	}
		

	@Override
	public void visit(Edge edge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ControlPoint controlPoint) {
		// TODO Auto-generated method stub
		
	}

}
