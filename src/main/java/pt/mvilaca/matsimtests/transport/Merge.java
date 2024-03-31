package pt.mvilaca.matsimtests.transport;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.matsim.api.core.v01.network.Network;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.utils.TransitScheduleValidator;
import org.matsim.pt.utils.TransitScheduleValidator.ValidationResult;
import org.matsim.pt2matsim.gtfs.GtfsConverter;
import org.matsim.pt2matsim.gtfs.GtfsFeed;
import org.matsim.pt2matsim.gtfs.GtfsFeedImpl;
import org.matsim.pt2matsim.plausibility.PlausibilityCheck;
import org.matsim.pt2matsim.plausibility.log.PlausibilityWarning;
import org.matsim.pt2matsim.tools.NetworkTools;
import org.matsim.pt2matsim.tools.ScheduleTools;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.Vehicles;

import pt.mvilaca.matsimtests.allpipeline.RunAllPipeline;

public class Merge {




	public static void main(String[] args) throws IOException {
		//		Gtfs2TransitSchedule.

		//		String gtfsFolder1 = "data/transport/coimbra/GTFS_ETAC_04032024";
		//		String gtfsFolder2= "data/transport/coimbra/GTFS_TI_04032024";
		//		GtfsFeed gtfsFeed1 = new GtfsFeedImpl(gtfsFolder1);
		//		GtfsFeed gtfsFeed2 = new GtfsFeedImpl(gtfsFolder2);
		//		
		//		
		//		
		//		
		//		System.out.println(gtfsFeed1.getStops().size());
		//		System.out.println(gtfsFeed2.getStops().size());



		List<String> files = Arrays.asList(
				"data/transport/coimbra/gtfs_SMTUC"
				//				"data/transport/coimbra/GTFS_ETAC_04032024",
//				"data/transport/coimbra/GTFS_TI_04032024"
				);

		//		GTFSFeedXPTO gtfs = new GTFSFeedXPTO(files.get(0));
		//		for(int i = 1; i<files.size(); i++)
		//			gtfs.mergeData(files.get(i));
		//		
		//		
		//		Path output = Paths.get("data/transport/coimbra/GTFS-ALL/");
		//
		//		try {
		//			Files.createDirectory(output);
		//		} catch (Exception e) {
		//			// TODO: handle exception
		//		}
		//		
		//		
		//		gtfs.save(output.toString()+"/");
		//		System.out.println(gtfs.getStops().size());


		//		for(Service s: gtfs.getServices().values()) {
		//			System.out.println(s.getId() + "\t" + join("\t", s.getDays()));
		//			
		//		}
		//		
		//		System.out.println(gtfs.getServices().get("E-2"));


		TransitSchedule ts = ScheduleTools.createSchedule(); 
		Vehicles vs = VehicleUtils.createVehiclesContainer();

		for(int i = 0; i<files.size(); i++) { 
			GtfsFeed gtfsFeed = new GtfsFeedImpl(files.get(i));
			GtfsConverter c = new GtfsConverter(gtfsFeed);

			TransitSchedule toMerge = c.convert("all", "EPSG:20790");
			System.out.println(toMerge);
			ScheduleTools.mergeSchedules(ts, c.getSchedule());
		}
//		ScheduleCleaner.removeNotUsedStopFacilities(ts);
//		ScheduleCleaner.removeTransitRoutesWithoutLinkSequences(ts);
//		ScheduleCleaner.removeNotUsedStopFacilities(ts);
//		ScheduleCleaner.cleanDepartures(ts);
//		ScheduleCleaner.combineIdenticalTransitRoutes(ts);
//		ScheduleCleaner.combineChildStopsToParentStop(ts);
//		ScheduleCleaner.removeInvalidTransitRoutes(null, ts);
		

		
		
		
		String scheduleFile = "data/transport/coimbra/pt-two.xml";
		ScheduleTools.writeTransitSchedule(ts, scheduleFile);

		String folder = "scenarios/coimbraRegion";
		String inputNetwork = folder+"/network.xml";
		PlausibilityCheck check = RunAllPipeline.runMappingStandard(scheduleFile, inputNetwork, null, null, "EPSG:20790", null);

		Set<PlausibilityWarning> artificialLinks = check.getWarnings().get(PlausibilityWarning.Type.ArtificialLinkWarning);

		for(PlausibilityWarning p : artificialLinks)
			System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference());
		
		Set<PlausibilityWarning> tt = check.getWarnings().get(PlausibilityWarning.Type.TravelTimeWarning);
		for(PlausibilityWarning p : tt)
			System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference());
		
		Set<PlausibilityWarning> dc = check.getWarnings().get(PlausibilityWarning.Type.DirectionChangeWarning);
		for(PlausibilityWarning p : dc)
			System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference());
		
//		Set<PlausibilityWarning> lw = check.getWarnings().get(PlausibilityWarning.Type.LoopWarning);
//		for(PlausibilityWarning p : lw)
//			try {
//				System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
		
		
		
		Network network = NetworkTools.readNetwork(inputNetwork);
		
		ValidationResult vr = TransitScheduleValidator.validateAll(ts, network);
		List<String> errors = vr.getErrors();
		for(String e : errors) System.out.println(e);
		
		System.out.println("######################");
		for(String w : vr.getWarnings()) System.out.println(w);
		
	}

	private static String join(String sep, boolean... days) {
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < days.length;i++) {

			s.append(days[i]).append(sep); 
		}
		return s.toString();
	}


}
