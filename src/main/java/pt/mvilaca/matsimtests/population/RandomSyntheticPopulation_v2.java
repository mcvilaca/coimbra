package pt.mvilaca.matsimtests.population;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.common.collect.Lists;

public class RandomSyntheticPopulation_v2 {

	public static void main(String[] args) {
		int numberOfHomes = 1007;
		int numberOfWorks = 103;
		
		List<Node> homes = new ArrayList<Node>();
		List<Node> works = new ArrayList<Node>();
		
		Config config = ConfigUtils.createConfig();
		config.network().setInputFile("scenarios/aveiro_v2/network.xml");
		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;
		Network network = scenario.getNetwork();
		Population population = scenario.getPopulation();
		
		
		ArrayList<Node> nodes = new ArrayList<>(network.getNodes().values());
		System.out.println("Number of node: " + nodes.size());
		
		homes = getRandom(nodes, numberOfHomes);
		works = getRandom(nodes, numberOfWorks);
		
		
		System.out.println("####");
		printNode(homes);
		printNode(works);
		
		List<List<Node>> splitedHomes = Lists.partition(homes, numberOfHomes/numberOfWorks);
		
		int workidx = 0;
		int personIdx = 0;
		for(List<Node> hs:splitedHomes) {
			Node w = works.get(workidx);
			for(Node h : hs) {
				Coord source = h.getCoord();
				Coord sink = w.getCoord();
				Person person = population.getFactory().createPerson(createId("home", "work", personIdx++, TransportMode.car));
				Plan plan = population.getFactory().createPlan();
				plan.addActivity(createHome(population,source));
				plan.addLeg(createDriveLeg(population));
				plan.addActivity(createWork(population,sink));
				plan.addLeg(createDriveLeg(population));
				plan.addActivity(createHome(population,source));
				person.addPlan(plan);
				population.addPerson(person);
				
			}
			workidx++;
			if(workidx == numberOfWorks) workidx = 0;
		}
		
		PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
		populationWriter.write("scenarios/aveiro_v2/population.xml");
	}
	
	
//	private void generateHomeWorkHomeTrips(String from, String to, int quantity) {
//		for (int i=0; i<quantity; ++i) {
//			Coord source = zoneGeometries.get(from);
//			Coord sink = zoneGeometries.get(to);
//			Person person = population.getFactory().createPerson(createId(from, to, i, TransportMode.car));
//			Plan plan = population.getFactory().createPlan();
//			Coord homeLocation = shoot(ct.transform(source));
//			Coord workLocation = shoot(ct.transform(sink));
//			plan.addActivity(createHome(homeLocation));
//			plan.addLeg(createDriveLeg());
//			plan.addActivity(createWork(workLocation));
//			plan.addLeg(createDriveLeg());
//			plan.addActivity(createHome(homeLocation));
//			person.addPlan(plan);
//			population.addPerson(person);
//		}
//	}
	
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

	static private Activity createHome(Population population, Coord homeLocation) {
		Activity activity = population.getFactory().createActivityFromCoord("home", homeLocation);
		activity.setEndTime(9*60*60);
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
