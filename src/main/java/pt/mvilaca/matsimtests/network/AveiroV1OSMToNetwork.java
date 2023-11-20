package pt.mvilaca.matsimtests.network;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.osm.networkReader.LinkProperties;
import org.matsim.contrib.osm.networkReader.OsmTags;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.utils.gis.shp2matsim.ShpGeometryUtils;

/* Este código não funciona 'OtherWay' foi a solução**/
public class AveiroV1OSMToNetwork {

	public static void main(String[] args) {
		String aveiro_file_path ="data/osm/aveiro_1.osm";

		//Verificar os sistemas de coordenadas!!
		String inputCoordinateSystem = TransformationFactory.WGS84;
		String outputCoordinateSystem = TransformationFactory.WGS84_UTM33N; //"EPSG:25832";

		// choose an appropriate coordinate transformation. OSM Data is in WGS84. When working in central Germany,
		// EPSG:25832 or EPSG:25833 as target system is a good choice
		CoordinateTransformation transformation = 
				TransformationFactory.getCoordinateTransformation(
						inputCoordinateSystem,
						outputCoordinateSystem
						);

		// load the geometries of the shape file, so they can be used as a filter during network creation
		// using PreparedGeometry instead of Geometry increases speed a lot (usually)
//		List<PreparedGeometry> filterGeometries = ShpGeometryUtils.loadPreparedGeometries(filterShape.toUri().toURL());

	
		
		// create an osm network reader with a filter
		SupersonicOsmNetworkReader reader = new SupersonicOsmNetworkReader.Builder()
				.setCoordinateTransformation(transformation)
//				.setIncludeLinkAtCoordWithHierarchy((coord, hierarchyLevel) -> {
//
//					// take all links which are motorway, trunk, or primary-street regardless of their location
//					if (hierarchyLevel <= LinkProperties.LEVEL_PRIMARY) return true;
//
//					// whithin the shape, take all links which are contained in the osm-file
//					return ShpGeometryUtils.isCoordInPreparedGeometries(coord, filterGeometries);
//				})
				.setAfterLinkCreated((link, osmTags, direction) -> {

					// if the original osm-link contains a cycleway tag, add bicycle as allowed transport mode
					// although for serious bicycle networks use OsmBicycleNetworkReader
					if (osmTags.containsKey(OsmTags.CYCLEWAY)) {
						Set<String> modes = new HashSet<>(link.getAllowedModes());
						modes.add(TransportMode.bike);
						link.setAllowedModes(modes);
					}
				})
				.build();


		Network network = reader.read(aveiro_file_path);
		// clean the network to remove unconnected parts where agents might get stuck
		new NetworkCleaner().run(network);
//
//		// write out the network into a file
		new NetworkWriter(network).write("scenarios/aveiro_v1/network.xml");
	}
}
