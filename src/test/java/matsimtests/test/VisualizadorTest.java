package matsimtests.test;

import org.matsim.contrib.otfvis.RunOTFVis;

public class VisualizadorTest {

	public static void main(String[] args) {
//		RunOTFVis.main(new String[] {"scenarios/coimbra/outputsWithTransports_MV/output_network.xml.gz"});
		
		RunOTFVis.main(new String[] {"--help"});
//		RunOTFVis.main(new String[] {"scenarios/coimbraRegion/outputs/output_config_reduced.xml"});
		RunOTFVis.main(args);
	}
}
