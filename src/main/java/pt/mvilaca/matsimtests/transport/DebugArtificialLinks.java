package pt.mvilaca.matsimtests.transport;

import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt2matsim.plausibility.PlausibilityCheck;
import org.matsim.pt2matsim.plausibility.log.PlausibilityWarning;
import org.matsim.pt2matsim.tools.NetworkTools;
import org.matsim.pt2matsim.tools.ScheduleTools;

public class DebugArtificialLinks {

	public static void main(String[] args) {
		
		String coordSys= "EPSG:20790";
//		String scheduleFile = "scenarios/coimbra/schedule.xml";
		String scheduleFile = "scenarios/coimbra/scheduleWithTransports-debug1.xml";
		String networkFile  = "scenarios/coimbra/networkWithTransports-debug1.xml";

		TransitSchedule schedule = ScheduleTools.readTransitSchedule(scheduleFile);
		Network network = NetworkTools.readNetwork(networkFile);
		
		
		PlausibilityCheck check = new PlausibilityCheck(schedule, network, coordSys);
		
		check.runCheck();
		check.printStatisticsLog();
		Set<PlausibilityWarning> artificialLinks = check.getWarnings().get(PlausibilityWarning.Type.ArtificialLinkWarning);
		System.out.println("=========" + PlausibilityWarning.Type.ArtificialLinkWarning);
		for(PlausibilityWarning p : artificialLinks)
			System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference() + "\t" + p.getFromId() + "\t" + p.getToId() + "\t" + p.getTransitLine().getRoutes());
		
		Set<PlausibilityWarning> directional = check.getWarnings().get(PlausibilityWarning.Type.DirectionChangeWarning);
		System.out.println("=========" + PlausibilityWarning.Type.DirectionChangeWarning);
		for(PlausibilityWarning p : directional)
			System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference() + "\t" + p.getFromId() + "\t" + p.getToId() + "\t" + p.getTransitLine().getRoutes());
		
		
		Node artificial = network.getNodes().get(Id.create("pt_10896", Node.class));
		Node realCloser = network.getNodes().get(Id.create("2341300130", Node.class));
		
		System.out.println(artificial);
		System.out.println(realCloser);
		
	}
}
