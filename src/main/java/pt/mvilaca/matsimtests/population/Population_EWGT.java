package pt.mvilaca.matsimtests.population;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.FacilitiesWriter;

public class Population_EWGT {
public static void main(String[] args) throws IOException, ParseException {
	
	Config config = ConfigUtils.createConfig();
	config.network().setInputFile("scenarios/coimbra_ewgt_v3/networkWithTransports.xml");
	
	Scenario scenario = ScenarioUtils.loadScenario(config) ;
	
	File f = Paths.get("data", "population", "Syntheticpopulation_filtro2_simplified_filtro3_EWGT_downsize.tsv").toFile();
	CoimbraQuestionario3 cq = CoimbraQuestionario3.readCoimbraTSV(f);
	
	cq.createFacilities(scenario);
	cq.createQuestionaryPlan(scenario);
	
	
	PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
	populationWriter.write("scenarios/coimbra_ewgt_v3/population_downsize.xml");
	
	FacilitiesWriter facilitiesWriter = new FacilitiesWriter(scenario.getActivityFacilities());
	facilitiesWriter.write("scenarios/coimbra_ewgt_v3/facilities_downsize.xml");
}

public static double convertDate(Date d) {
	return d.getHours()*60.0*60.0 + d.getMinutes()*60;
}

static public List<Coord> getHomeCoordinates(){
	double x = 174826.7;
	double y = 360335.18;
	
		

	return Arrays.asList(new Coord(x,y));
}

static public List<Node> getRandom(List<Node> possibleNodes, int numberOfRandomNodes){
	List<Node> ret = new ArrayList<>();
	Random random = new Random();
	
	for(int i =0; i < numberOfRandomNodes; i++) {
		int indexNextHome = random.nextInt(possibleNodes.size());
		System.out.println(indexNextHome);
		Node node = possibleNodes.remove(indexNextHome);
		ret.add(node);
	}
	
	return ret;
}

static public void printNode(List<Node> possibleNodes) {
	System.out.println("####");
	for(int i =0; i < possibleNodes.size(); i++) {
		System.out.println(possibleNodes.get(i).getCoord());
	}
}

static private Activity createWork(Population population ,Coord workLocation) {
	Activity activity = population.getFactory().createActivityFromCoord("work", workLocation);
	activity.setEndTime(17*60*60);
	return activity;
}

static private Activity createHome(Population population, Coord homeLocation, double seconds) {
	Activity activity = population.getFactory().createActivityFromCoord("home", homeLocation);
	
	
	activity.setEndTime(seconds);
	return activity;
}

static private Id<Person> createId(String source, String sink, int i, String transportMode) {
	return Id.create(transportMode + "_" + source + "_" + sink + "_" + i, Person.class);
}

static private Leg createDriveLeg(Population population) {
	Leg leg = population.getFactory().createLeg(TransportMode.car);
	return leg;
}

}

