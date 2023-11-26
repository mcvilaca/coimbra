package pt.mvilaca.matsimtests.population.trip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndividualRandomSelection {
	
	private static Logger logger = LoggerFactory.getLogger(IndividualRandomSelection.class);

	Map<Integer, Double> personFactor;
	Random rnd;
	
	public IndividualRandomSelection(Map<Integer, Double> personFactor, 
			Random rnd){
		
		this.personFactor = personFactor;
		this.rnd = rnd;
	}

	
	public List<Integer> pickNValues(int n){
		
		double max = personFactor.values().stream().collect(Collectors.summingDouble(x->x));
		TreeSet<Double> randomNumber = new TreeSet<Double>();
		for(int i =0; i < n ; i++) {
			randomNumber.add(rnd.nextDouble()*max);
		}
			
		logger.info("{}", randomNumber.size());
		List<Integer> ret = new ArrayList<Integer>();
		
		Double limit = 0.0;
		Double rn = randomNumber.pollFirst();
		for(Entry<Integer, Double> e : personFactor.entrySet()) {
			limit+= e.getValue();
//			logger.info("limit {}", limit);
//			logger.info("rnd {}", rn);
			while(rn != null && rn <= limit) {
				
				logger.info("add {} => {} {}", e.getKey(), rn, limit);
				ret.add(e.getKey());
				
				
				rn = randomNumber.pollFirst();
				
			}
		}
		return ret;
	}
}
