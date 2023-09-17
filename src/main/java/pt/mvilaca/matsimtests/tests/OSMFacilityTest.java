package pt.mvilaca.matsimtests.tests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.pbf.seq.PbfIterator;

public class OSMFacilityTest {

	
	public static void main(String[] args) throws IOException
	{
//		Path path = Paths.get("data/osm/coimbra.osm");
		
		Path path = Paths.get("data/osm/aveiro_1.osm");
		InputStream input = Files.newInputStream(path);
		
		BufferedInputStream bis = new BufferedInputStream(input);
		PbfIterator iterator = new PbfIterator(bis, false);

		
		for (EntityContainer object : iterator) {
			System.out.println(object);
		}
//		int n = 0;
//		int w = 0;
//		int r = 0;
//		int noName = 0;
//
//		for (EntityContainer object : iterator) {
//			OsmEntity entity = object.getEntity();
//			Map<String, String> tags = OsmModelUtil.getTagsAsMap(entity);
//			String amenity = tags.get("amenity");
//			
//			System.out.println(amenity);
//			
//			if ("restaurant".equals(amenity)) {
//				String name = tags.get("name");
//				if (name == null) {
//					noName++;
//				} else {
//					System.out.println(name);
//				}
//				if (object.getType() == EntityType.Node) {
//					n++;
//				} else if (object.getType() == EntityType.Way) {
//					w++;
//				} else if (object.getType() == EntityType.Relation) {
//					r++;
//				}
//			}
//		}
//
//		System.out.println("-Summary-");
//		System.out.println(
//				String.format("nodes: %d, ways: %d, relations: %d", n, w, r));
//		System.out.println(String.format("no name: %d", noName));
	}
}
