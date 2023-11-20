package pt.mvilaca.matsimtests.transport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt2matsim.config.PublicTransitMappingConfigGroup;
import org.matsim.pt2matsim.config.PublicTransitMappingConfigGroup.TravelCostType;
import org.matsim.pt2matsim.gtfs.GtfsConverter;
import org.matsim.pt2matsim.gtfs.GtfsFeedImpl;
import org.matsim.pt2matsim.mapping.PTMapper;
import org.matsim.pt2matsim.plausibility.MappingAnalysis;
import org.matsim.pt2matsim.plausibility.PlausibilityCheck;
import org.matsim.pt2matsim.plausibility.log.PlausibilityWarning;
import org.matsim.pt2matsim.run.Gtfs2TransitSchedule;
import org.matsim.pt2matsim.tools.NetworkTools;
import org.matsim.pt2matsim.tools.ScheduleTools;
import org.matsim.pt2matsim.tools.ShapeTools;
import org.matsim.pt2matsim.tools.lib.RouteShape;

/* This class read the pT gtfs files and generates schedules, vehicles and network xml files including the PT**/

public class EWGT_Transport_MV {

	
	public static void main(String[] args) {
//		Logger.getRootLogger().setLevel(Level.ALL);
		Logger.getLogger(GtfsFeedImpl.class).setLevel(Level.ALL);				
		String gtfsFolder = "data/transport/coimbra/gtfs_SMTUC";
//		String gtfsFolder = "data/transport/coimbra/servios-municipalizados-de-transportes-urbanos-de-coimbra_20141110_1636-ordered";
//		String gtfsFolder = "data/transport/addisoncounty-vt-us-gtfs";

//		String gtfsFolder = "data/transport/coimbra/test1";
//		String gtfsFolder = "data/transport/coimbra/test2";
		
		
		String sampleDayParam = GtfsConverter.DAY_WITH_MOST_TRIPS;
		String coordinate= "EPSG:20790";
		String scheduleFile = "scenarios/coimbra_ewgtbaseline_v2/schedule.xml";
		String vehicleFile = "scenarios/coimbra_ewgtbaseline_v2/vehicle.xml";
		
		
		String scheduleFileWithTransports = "scenarios/coimbra_ewgtbaseline_v2/scheduleWithTransports.xml";
		
		String inputNetwork = "scenarios/coimbra_ewgtbaseline_v2/network.xml";
		String networkWithTransports = "scenarios/coimbra_ewgtbaseline_v2/networkWithTransports.xml";
		
		Gtfs2TransitSchedule.run(gtfsFolder, sampleDayParam, coordinate, scheduleFile, vehicleFile);
		
		runMappingStandard(scheduleFile, inputNetwork, scheduleFileWithTransports, networkWithTransports, coordinate);
		
		
	}
	
	
//	public static void convertShapes() {
//		TransitSchedule schedule = ScheduleTools.readTransitSchedule(inputScheduleFile);
//
//		Map<Id<RouteShape>, RouteShape> shapes = ShapeTools.readShapesFile(gtfsShapeFile, coordSys);
//		Map<Id<RouteShape>, RouteShape> shapesToConvert = new HashMap<>();
//
//
//		for(TransitLine transitLine : schedule.getTransitLines().values()) {
//			for(TransitRoute transitRoute : transitLine.getRoutes().values()) {
//				Id<RouteShape> id = ScheduleTools.getShapeId(transitRoute);
//				shapesToConvert.put(id, shapes.get(id));
//			}
//		}
//
//
//		ShapeTools.writeESRIShapeFile(shapesToConvert.values(), coordSys, base + "output/shp/gtfs.shp");
//	}

	/**
	 * Runs a standard mapping
	 */
	public static double runMappingStandard(
			String inputScheduleFile, 
			String inputNetworkFile, 
			String outputScheduleFile, 
			String outputNetworkFile,
			String coordSys
//			String shapeout
			) {
		// Load schedule and network
		TransitSchedule schedule = ScheduleTools.readTransitSchedule(inputScheduleFile);
		Network network = NetworkTools.readNetwork(inputNetworkFile);

		// create PTM config
		PublicTransitMappingConfigGroup config = PublicTransitMappingConfigGroup.createDefaultConfig();

		config.setNLinkThreshold(6);
		config.setMaxLinkCandidateDistance(800);
//		config.setCandidateDistanceMultiplier(2);
//		//or travel time
//		config.setTravelCostType(TravelCostType.linkLength);
		
	
		System.out.println("getNLinkThreshold " + config.getNLinkThreshold());
		System.out.println("getCandidateDistanceMultiplier " + config.getCandidateDistanceMultiplier());
		System.out.println("getMaxLinkCandidateDistance" + config.getMaxLinkCandidateDistance());
		System.out.println("getTransportModeAssignment" + config.getTransportModeAssignment());
		
		System.out.println("getTravelCostType " + config.getTravelCostType());
		System.out.println("getRoutingWithCandidateDistance " + config.getRoutingWithCandidateDistance());
		
		System.out.println("" + config.getMaxTravelCostFactor());
		System.out.println("" + config.getModesToKeepOnCleanUp());
		System.out.println("" + config.getScheduleFreespeedModes());
		System.out.println("" + config.getRemoveNotUsedStopFacilities());
		
		
		// run PTMapper
		PTMapper ptMapper = new PTMapper(schedule, network);
		ptMapper.run(config);

		//
		NetworkTools.writeNetwork(network, outputNetworkFile);
		ScheduleTools.writeTransitSchedule(schedule, outputScheduleFile);

		PlausibilityCheck check = new PlausibilityCheck(schedule, network, coordSys);
		
		check.runCheck();
		check.printStatisticsLog();
//		Set<PlausibilityWarning> artificialLinks = check.getWarnings().get(PlausibilityWarning.Type.ArtificialLinkWarning);
//		
//		for(PlausibilityWarning p : artificialLinks)
//			System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference());
		
//		Schedule2ShapeFile.run(coordSys, base + "output/shp/", schedule, network);
//
//		// analyse result
//		return runAnalysis(outputScheduleFile, outputNetworkFile);
		return 0.0;
	}
	
/**
 * 
 * outputGtfsShapeFile =  base + "output/shp/gtfs.shp" 
 * @param inputScheduleFile
 * @param gtfsShapeFile
 * @param coordSys
 * @param gtfsShapeFile
 */
	public static void convertShapes(String inputScheduleFile, String gtfsShapeFile, String coordSys, String outputGtfsShapeFile) {
		TransitSchedule schedule = ScheduleTools.readTransitSchedule(inputScheduleFile);

		Map<Id<RouteShape>, RouteShape> shapes = ShapeTools.readShapesFile(gtfsShapeFile, coordSys);
		Map<Id<RouteShape>, RouteShape> shapesToConvert = new HashMap<>();


		for(TransitLine transitLine : schedule.getTransitLines().values()) {
			for(TransitRoute transitRoute : transitLine.getRoutes().values()) {
				Id<RouteShape> id = ScheduleTools.getShapeId(transitRoute);
				shapesToConvert.put(id, shapes.get(id));
			}
		}


		ShapeTools.writeESRIShapeFile(shapesToConvert.values(), coordSys, outputGtfsShapeFile);
	}
	
	
	private static double runAnalysis(String scheduleFile, String networkFile, String fileAnalysis,  String gtfsShapeFile, String coordSys) {
		MappingAnalysis analysis = new MappingAnalysis(
				ScheduleTools.readTransitSchedule(scheduleFile),
				NetworkTools.readNetwork(networkFile),
				ShapeTools.readShapesFile(gtfsShapeFile, coordSys)
		);
		analysis.run();
		analysis.writeQuantileDistancesCsv(fileAnalysis);

		return analysis.getQ8585();
	}
}
