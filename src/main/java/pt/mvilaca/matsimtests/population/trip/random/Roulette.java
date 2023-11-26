package pt.mvilaca.matsimtests.population.trip.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.mvilaca.matsimtests.population.trip.IndividualRandomSelection;

public class Roulette<T> {

	private static Logger logger = LoggerFactory.getLogger(IndividualRandomSelection.class);

	Map<T, Double> personFactor;
	Random rnd;
	
	public Roulette(Map<T, Double> personFactor, 
			Random rnd){
		
		this.personFactor = personFactor;
		this.rnd = rnd;
	}

	
	public List<T> pickNValues(int n){
		
		double max = personFactor.values().stream().collect(Collectors.summingDouble(x->x));
		TreeSet<Double> randomNumber = new TreeSet<Double>();
		for(int i =0; i < n ; i++) {
			randomNumber.add(rnd.nextDouble()*max);
		}
			
		logger.info("{}", randomNumber.size());
		List<T> ret = new ArrayList<T>();
		
		Double limit = 0.0;
		Double rn = randomNumber.pollFirst();
		for(Entry<T, Double> e : personFactor.entrySet()) {
			limit+= e.getValue();
//			logger.info("limit {}", limit);
//			logger.info("rnd {}", rn);
			while(rn != null && rn <= limit) {
				
				logger.debug("add {} => {} {}", e.getKey(), rn, limit);
				ret.add(e.getKey());
				
				
				rn = randomNumber.pollFirst();
				
			}
		}
		return ret;
	}
}
