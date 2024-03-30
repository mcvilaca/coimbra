package pt.mvilaca.matsimtests.transport;

import java.io.IOException;

import org.matsim.pt2matsim.gtfs.GtfsFeedImpl;
import org.matsim.pt2matsim.tools.GtfsTools;

public class GTFSFeedXPTO extends GtfsFeedImpl{

	
	public GTFSFeedXPTO(String gtfsFolder) {
		super(gtfsFolder);
		// TODO Auto-generated constructor stub
	}
	
	public void mergeData(String gtfsFolder) {
		loadFiles(gtfsFolder);
	}
	
	public void save(String output) throws IOException {
		GtfsTools.writeStopTimes(getTrips().values(), output);
		GtfsTools.writeStops(getStops().values(), output);
		GtfsTools.writeTrips(getTrips().values(), output);
		GtfsTools.writeTransfers(getTransfers(), output);

	}

}
