package pt.mvilaca.matsimtests.transport;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import org.matsim.pt2matsim.gtfs.GtfsFeedImpl;
import org.matsim.pt2matsim.gtfs.lib.GtfsDefinitions;
import org.matsim.pt2matsim.gtfs.lib.Service;
import org.matsim.pt2matsim.tools.GtfsTools;

import com.opencsv.CSVWriter;

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
		writeCalendar(getServices().values(), output);
	}
	
	private void writeCalendar(Collection<Service> services, String output) throws IOException {
		GtfsDefinitions.Files defs = GtfsDefinitions.Files.CALENDAR;
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
		
		CSVWriter calendarWriter = new CSVWriter(new FileWriter(output + defs.fileName), ',');
		String[] columns = defs.columns;
		String[] optionalColumns = defs.optionalColumns;
		String[] header = Stream.concat(Arrays.stream(columns), Arrays.stream(optionalColumns)).toArray(String[]::new);
		calendarWriter.writeNext(header, false);
		for(Service service : services) {
			// service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
			String[] line = new String[header.length];
			line[0] = service.getId();
			line[1] = (service.getDays()[0])?"1":"0";
			line[2] = (service.getDays()[1])?"1":"0";
			line[3] = (service.getDays()[2])?"1":"0";
			line[4] = (service.getDays()[3])?"1":"0";
			line[5] = (service.getDays()[4])?"1":"0";
			line[6] = (service.getDays()[5])?"1":"0";
			line[7] = (service.getDays()[6])?"1":"0";
			line[8] = service.getStartDate().format(format);
			line[9] = service.getEndDate().format(format);
			calendarWriter.writeNext(line, false);
		}
		calendarWriter.close();
	}

}
