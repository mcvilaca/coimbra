package pt.mvilaca.matsimtests.population.trip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Test;

public class IndividualRandomSelectionTest {
	
	@Test
	public void test1() {
		Map<Integer, Double> info = new HashMap<Integer, Double>();
		info.put(1, 1.0);
		info.put(2, 1.0);
		info.put(3, 1.0);
		info.put(4, 1.0);
		info.put(5, 1.0);
		
		
		IndividualRandomSelection irs = new IndividualRandomSelection(info, new Random(0));
		
		List<Integer> generated = irs.pickNValues(1000);
		
		System.out.println(generated.size());
		
		Map<Integer, Long> counts =
				generated.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));


		System.out.println(counts);
		
	}
	
	
	@Test
	public void test2() {
		
		Map<Integer, Double> info = new HashMap<Integer, Double>();
		info.put(1, 10.0);
		info.put(2, 30.0);
		info.put(3, 20.0);
		info.put(4, 10.0);
		info.put(5, 30.0);
		
		IndividualRandomSelection irs = new IndividualRandomSelection(info, new Random(0));
		
		List<Integer> generated = irs.pickNValues(1000);
		
		System.out.println(generated.size());
		
		Map<Integer, Long> counts =
				generated.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));


		System.out.println(counts);
		
	}
}
