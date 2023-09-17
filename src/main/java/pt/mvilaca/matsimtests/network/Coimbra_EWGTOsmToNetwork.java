package pt.mvilaca.matsimtests.network;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.algorithms.NetworkSimplifier;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;

//based on the AveiroV1OSMToNetwork_OtherWay
public class Coimbra_EWGTOsmToNetwork {
	public static void main(String[] args) {
		String coimbra_file_path ="data/osm/network_ewgt.osm";

		//Verificar os sistemas de coordenadas!!
		String inputCoordinateSystem = TransformationFactory.WGS84;
		String outputCoordinateSystem = "EPSG:20790"; //"EPSG:25832";

		// choose an appropriate coordinate transformation. OSM Data is in WGS84. When working in central Germany,
		// EPSG:25832 or EPSG:25833 as target system is a good choice
		CoordinateTransformation transformation = 
				TransformationFactory.getCoordinateTransformation(
						inputCoordinateSystem,
						outputCoordinateSystem
						);

		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		config.network().setInputCRS(inputCoordinateSystem);
		
		/*
		 * Pick the Network from the Scenario for convenience.
		 */
		Network network = scenario.getNetwork();
		
		OsmNetworkReader reader = new OsmNetworkReader(network, transformation);
		reader.parse(coimbra_file_path);
		
		
		new NetworkCleaner().run(network);
		
		new NetworkWriter(network).write("scenarios/coimbra_ewgt/network.xml");
	}
}

