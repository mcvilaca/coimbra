package pt.mvilaca.matsimtests.population.trip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario3;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario3.QuestionarioIndividual;

public class TripsPlanTests {
	
	private static Logger logger = LoggerFactory.getLogger(TripsPlan.class);

	
	
	@Test
	public void test1() throws IOException, ParseException {
		
		File file = new File("data/population/coimbra2.tsv");
		CoimbraQuestionario3 q = CoimbraQuestionario3.readCoimbraTSV(file );
		Map<Integer, TreeSet<QuestionarioIndividual>> info = q.calculateIndexed();
		TripsPlan tp = new TripsPlan(info);
	
		int i =0;
		for(CoordInformation coord : tp.getCoordInfo().values()) {
			i++;
			
			if(coord.getTypes().size()>1) logger.info("#t:{} #p{} {}", coord.getTypes().size(),coord.getPersons().size(), coord);
		}
		
		logger.info("{}", tp.getCoordInfo().size());
		logger.info("{}", i);
	}
	
	
	@Test
	public void test2() throws IOException, ParseException {
		
		File file = new File("data/population/coimbra2.tsv");
		CoimbraQuestionario3 q = CoimbraQuestionario3.readCoimbraTSV(file );
		Map<Integer, TreeSet<QuestionarioIndividual>> info = q.calculateIndexed();
		TripsPlan tp = new TripsPlan(info);
	
		Map<Coord, CoordInformation> test = tp.filter(x-> x.getPersons().size()>20);
		
		int i =0;
		for(CoordInformation coord : test.values()) {
			i++;
			
			if(coord.getTypes().size()>1) logger.info("#t:{} #p{} {}", coord.getTypes().size(),coord.getPersons().size(), coord);
		}
		
		logger.info("{}", tp.getCoordInfo().size());
		logger.info("{}", i);
	}
	
	
	@Test
	public void test3() throws IOException, ParseException {
		
		File file = new File("data/population/coimbra2.tsv");
		CoimbraQuestionario3 q = CoimbraQuestionario3.readCoimbraTSV(file );
		Map<Integer, TreeSet<QuestionarioIndividual>> info = q.calculateIndexed();
		TripsPlan tp = new TripsPlan(info);
		
		for(Entry<Integer, TreeSet<QuestionarioIndividual>> e : tp.getPersonTrip().entrySet()) {
			
			Set<Double> coefs = e.getValue().stream().map(x->x.getCoefExp()).collect(Collectors.toSet());
			if(coefs.size()>0)
				logger.info("Problem in person {} Data: {} ", e.getKey(), e.getValue());
			
		}
	}
	
	@Test
	public void n() throws IOException, ParseException {
		File file = new File("data/population/coimbra2.tsv");
		CoimbraQuestionario3 q = CoimbraQuestionario3.readCoimbraTSV(file );
		Map<Integer, TreeSet<QuestionarioIndividual>> info = q.calculateIndexed();
		TripsPlan tp = new TripsPlan(info);
		
		for(CoordInformation c: tp.getCoordInfo().values()) {
			System.out.println(c);
			
		}
	}
	
	@Test
	public void generate10xPop() throws IOException, ParseException {
		File f = Paths.get("data", "population", "Coimbra2.tsv").toFile();
		CoimbraQuestionario3 cq = CoimbraQuestionario3.readCoimbraTSV(f);
		TripsPlan tp = new TripsPlan(cq.calculateIndexed());
		
		
		Random rnd = new Random(System.currentTimeMillis());
		
		cq = tp.generateRandomPopulation(10, 5.0*60, rnd);
	}
	
	
	@Test
	public void testZoneAndType() throws IOException, ParseException {
		File file = new File("data/population/coimbra2.tsv");
		CoimbraQuestionario3 q = CoimbraQuestionario3.readCoimbraTSV(file );
		Map<Integer, TreeSet<QuestionarioIndividual>> info = q.calculateIndexed();
		TripsPlan tp = new TripsPlan(info);
		
		System.out.println(tp.getCoordByZone().get(46));
		System.out.println(tp.getCoordByTypeAndZone(TripsPlan.TYPE_COORD_SHOPPING, 46));
	}
	
	@Test
	public void generatePlan() throws IOException, ParseException {
		
		File file = new File("data/population/coimbra2.tsv");
		CoimbraQuestionario3 q = CoimbraQuestionario3.readCoimbraTSV(file );
		Map<Integer, TreeSet<QuestionarioIndividual>> info = q.calculateIndexed();
		TripsPlan tp = new TripsPlan(info);
	    TreeSet<QuestionarioIndividual> base = tp.getPersonTrip().get(1);
	    
	    logger.info("{}", base);
	    TreeSet<QuestionarioIndividual> newt = tp.generateDayTrips(9999,base , 5.0*60, new Random());
	    
	    
	    print(base);
	    print(newt);
	}
	
	
	
	public void print(TreeSet<QuestionarioIndividual> dayTrip) {

		int i =0;
		for(QuestionarioIndividual b: dayTrip) {
			
			 logger.info("{}   [{}] - {} -> [{}]  {}",i++,
					 b.getOrigem(),
					 b.getInicio(),
					 b.getDestino(),
					 b.getMotivo());
		}
	}
}
