package pt.mvilaca.matsimtests.network;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.osm.networkReader.LinkProperties;
import org.matsim.contrib.osm.networkReader.OsmTags;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.NetworkConfigGroup;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;
import org.matsim.utils.gis.shp2matsim.ShpGeometryUtils;


public class AveiroV2OSMToNetwork_OtherWay {

	public static void main(String[] args) {
		String aveiro_file_path ="data/osm/aveiro_big_region_test.osm";

		//Verificar os sistemas de coordenadas, eu nao percebo nada disto!!
		String inputCoordinateSystem = TransformationFactory.WGS84;
		String outputCoordinateSystem = "EPSG:25832";

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
		
		//deprecated!! Next version this may stop working
		OsmNetworkReader reader = new OsmNetworkReader(network, transformation);
		reader.parse(aveiro_file_path);
		
		new NetworkCleaner().run(network);
		new NetworkWriter(network).write("scenarios/aveiro_v2/network.xml");
	}
}
