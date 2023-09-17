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
public class CoimbraOsmToNetwork {
	public static void main(String[] args) {
		String coimbra_file_path ="data/osm/coimbra.osm";

		//Verificar os sistemas de coordenadas!!
		String inputCoordinateSystem = TransformationFactory.WGS84;
		String outputCoordinateSystem = "EPSG:20790"; //"EPSG:4326"; //"EPSG:25832";

		// choose an appropriate coordinate transformation. OSM Data is in WGS84. When working in central Germany,
		// EPSG:25832 or EPSG:25833 as target system is a good choice
		CoordinateTransformation transformation = 
				TransformationFactory.getCoordinateTransformation(
						inputCoordinateSystem,
						outputCoordinateSystem
						);

		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		/*
		 * Pick the Network from the Scenario for convenience.
		 */
		Network network = scenario.getNetwork();
		
		OsmNetworkReader reader = new OsmNetworkReader(network, transformation);
		reader.parse(coimbra_file_path);
		
		 /*
         * Clean the Network. Cleaning means removing disconnected components, so that afterwards there is a route from every link
         * to every other link. This may not be the case in the initial network converted from OpenStreetMap.
         */
		new NetworkCleaner().run(network);
	
		new NetworkWriter(network).write("scenarios/Coimbra/network.xml");
	}
}

