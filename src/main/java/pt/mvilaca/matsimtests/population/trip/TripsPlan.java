package pt.mvilaca.matsimtests.population.trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.Coord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario3;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario3.MotivoViagem;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario3.QuestionarioIndividual;
import pt.mvilaca.matsimtests.population.trip.random.Roulette;

public class TripsPlan {
	
	private static Logger logger = LoggerFactory.getLogger(TripsPlan.class);
	public static Integer START_HOME_CODE = 1;
	public static String TYPE_COORD_HOME = "Home";
	public static String TYPE_COORD_ESCORT = "Escort";
	public static String TYPE_COORD_WORK = "Work";
	public static String TYPE_COORD_EDUCATION = "Education";
	public static String TYPE_COORD_SHOPPING = "Shopping";
	public static String TYPE_COORD_RESTAURANT = "Restaurant";
	public static String TYPE_COORD_LEISURE = "Leisure";
	public static String TYPE_COORD_HEALTH = "Health";
	public static String TYPE_COORD_SERVICES = "Services";
	public static String TYPE_COORD_OTHERS = "Others";
	
	Map<Integer, TreeSet<QuestionarioIndividual>>personTrip;
	
	
	Map<Integer, Double> personFactor;
	Map<Integer, Set<Integer>> personZone; 
	
	Map<Coord, CoordInformation> coordInfo; 
	Map<Integer, Set<CoordInformation>> coordByZone;
	Map<String, Set<CoordInformation>> coordByType;
	
	public TripsPlan(Map<Integer, TreeSet<QuestionarioIndividual>>personTripx) {
		personTrip = personTripx;
		populateInfo();
		
	}
	
	public Map<Integer, TreeSet<QuestionarioIndividual>> getPersonTrip() {
		return personTrip;
	}
	
	public Map<String, Set<CoordInformation>> getCoordByType() {
		return coordByType;
	}
	
	public Map<Integer, Set<CoordInformation>> getCoordByZone() {
		return coordByZone;
	}
	
	public Map<Coord, CoordInformation> getCoordInfo() {
		return coordInfo;
	}

	
	protected Map<CoordInformation, Double> getCoordByTypeAndZone(String type, Integer zone){
		Map<CoordInformation, Double> ret = new HashMap<CoordInformation, Double>();
		
		Set<CoordInformation> cbz = coordByZone.get(zone);
		logger.debug("coordByZone [{}] = {}", zone, cbz);
		for(CoordInformation coord : cbz) {
			Integer typeTime = coord.getTypes().getOrDefault(type, 0);
			if(typeTime >0) ret.put(coord, typeTime.doubleValue()); 
		}
		return ret;
	}
	
	
	public CoimbraQuestionario3 generateRandomPopulation(int nIndividos,Double timeWindowInSeconds, Random rnd) {
		
		IndividualRandomSelection ind = new IndividualRandomSelection(personFactor, rnd);
		
				
		List<Integer> ids = ind.pickNValues(nIndividos);
		Map<Integer, Integer> nextId = new HashMap<Integer, Integer>();
		List<QuestionarioIndividual> allInfo = new ArrayList<>();
		
		
		for(int i : ids) {
			TreeSet<QuestionarioIndividual> trip = personTrip.get(i);
			int id = nextId.getOrDefault(i,0);
			nextId.put(i, id+1);
			id = i*1000  +(id+1);
			
			TreeSet<QuestionarioIndividual> newTrip = generateDayTrips(id , trip, timeWindowInSeconds, rnd);
			allInfo.addAll(newTrip);
		}
		
		return new CoimbraQuestionario3(allInfo);
	}
	
	public CoimbraQuestionario3 replicatePopulation(double factorOfFactor, Double timeWindowInSeconds, Random rnd) {
		
		List<QuestionarioIndividual> allInfo = new ArrayList<>();
		
		for(int i : personFactor.keySet()) {
			Double factor = personFactor.get(i) * factorOfFactor;
			Integer id = i * 10000;
			
			TreeSet<QuestionarioIndividual> trip = personTrip.get(i);
			allInfo.addAll(trip);
			for(int j =0; j < factor; j++) {
				id++;
				allInfo.addAll(generateDayTrips(id, trip, timeWindowInSeconds, rnd));
				
			}
		}
		
		return new CoimbraQuestionario3(allInfo);
	}


	protected TreeSet<QuestionarioIndividual> generateDayTrips(
			Integer personId,
			TreeSet<QuestionarioIndividual> base,
			Double timeWindow,
			Random rnd){
		TreeSet<QuestionarioIndividual> newTrip = new TreeSet<>();

		QuestionarioIndividual qi = base.first();
		Map<CoordInformation, Double> info = null;
		
		
		
		if(qi.getStartAtHome() == START_HOME_CODE){
			info = getCoordByTypeAndZone(TYPE_COORD_HOME, qi.getZonaOrigem());
		}else {
			logger.debug("{} {}",qi.getZonaOrigem(), coordByZone.get(qi.getZonaOrigem()));
			info = coordByZone.get(qi.getZonaOrigem()).stream().collect(Collectors.toMap(x->x,x->1.0));
		}
		Roulette<CoordInformation> roulette = new Roulette<CoordInformation>(info, rnd);
		Coord orig = roulette.pickNValues(1).get(0).getCoord();
		Double timeGap = rnd.nextDouble() * (timeWindow*2) - timeWindow;
		
		for(QuestionarioIndividual b : base) {
			String typeDest = calculateTypeDestiny(b.getMotivo());
			
			QuestionarioIndividual newQi = genQI(personId, timeGap, b, orig, typeDest, rnd);
			orig = newQi.getDestino();
			newTrip.add(newQi);
			
		}
		return newTrip;
	}
	
	protected QuestionarioIndividual genQI(Integer personId, 
			Double timeGap, 
			QuestionarioIndividual qi, 
			Coord orig, 
			String destinyType,
			Random rnd) {
		

		
		Map<CoordInformation, Double> info = getCoordByTypeAndZone(destinyType, qi.getZonaDestino());
		Roulette<CoordInformation> roulette = new Roulette<CoordInformation>(info, rnd);
		logger.debug("{}", coordInfo.get(qi.getDestino()));
		
		logger.debug("{} {}", destinyType, qi.getZonaDestino());
		logger.debug("pesos {}", info);
		Coord dest = roulette.pickNValues(1).get(0).getCoord();
		
		double start = qi.getInicio() + timeGap;
		
		if(start > 24*60*60) start = qi.getInicio();
		
		
		QuestionarioIndividual ret = new QuestionarioIndividual(
				personId, 
				orig, 
				dest, 
				qi.getZonaOrigem(), 
				qi.getZonaDestino(), 
				qi.getInicio() + timeGap, 
				qi.getMotivo(), 
				qi.getMode(),
				qi.getStartAtHome(), 
				qi.getTripSequence(),
				qi.getCoefExp());
		
		return ret;
	}
	
	private void populateInfo() {
		coordInfo = new HashMap<Coord, CoordInformation>();
		coordByZone = new HashMap<>();
		coordByType = new HashMap<>();
		personFactor = new HashMap<>();
		personZone = new HashMap<Integer, Set<Integer>>();
		
		
		for(Entry<Integer, TreeSet<QuestionarioIndividual>> e : personTrip.entrySet()) {
			
			Integer personId = e.getKey();
			TreeSet<QuestionarioIndividual> trip = e.getValue();
			LinkedHashSet<Integer> zones = new LinkedHashSet<Integer>();
			
			
			QuestionarioIndividual fqi = trip.first();
			Coord firstOrigin = fqi.getOrigem();
			if(fqi.getStartAtHome() == START_HOME_CODE) {
				CoordInformation info = new CoordInformation(
						firstOrigin, fqi.getZonaOrigem());
				info.addType(TYPE_COORD_HOME);
				addCoordOnStructures(personId, info);
			}else {
				CoordInformation info = new CoordInformation(
						firstOrigin, fqi.getZonaOrigem());
				info.addType(TYPE_COORD_OTHERS);
				addCoordOnStructures(personId, info);
			}
			personFactor.put(e.getKey(), fqi.getCoefExp());
			zones.add(fqi.getZonaOrigem());
			
			for(QuestionarioIndividual qi: trip) {
				
				Coord destiny = qi.getDestino();
				CoordInformation info = new CoordInformation(
						destiny, qi.getZonaDestino());
				info.addType(calculateTypeDestiny(qi.getMotivo()));
				addCoordOnStructures(personId, info);
				
				zones.add(fqi.getZonaOrigem());
				zones.add(fqi.getZonaDestino());
			}
			
		}
	}
	

	
	private String calculateTypeDestiny(MotivoViagem motivo) {
		
		switch (motivo) {
		case casa:
		case regresso_a_casa_dos_pais:
		case visitas:
			return TYPE_COORD_HOME;

		case buscar_levar_familiares:
			return TYPE_COORD_ESCORT;
			
		case trabalho:
		case motivos_profissionais:
			return TYPE_COORD_WORK;
			
		case escola:
			return TYPE_COORD_EDUCATION;
		
		case compras:
			return TYPE_COORD_SHOPPING;
			
		case refeicoes:
			return TYPE_COORD_RESTAURANT;
			
		case lazer_ocasional:
		case lazer_regular:
		case passear:
			return TYPE_COORD_LEISURE;
			
		case saude:
			return TYPE_COORD_HEALTH;
			
		case servicos_publicos:
		case assuntos_pessoais:
			return TYPE_COORD_SERVICES;
			
		default:
			
		}
		
		
		return TYPE_COORD_OTHERS;
	}

	private void addCoordOnStructures(Integer personId, CoordInformation info) {
		
		CoordInformation otherCoordInfo = coordInfo.get(info.getCoord());
		
		
		if(otherCoordInfo!=null && !otherCoordInfo.equals(info)) {
			logger.info("Mesma coordenada informação diferente {}:\n{}\n{}",personId, info, otherCoordInfo);
		}
		
		if(otherCoordInfo!=null) {
			
			otherCoordInfo.merge(info);
			info = otherCoordInfo;
			
		}
		
		info.addPerson(personId);
		coordInfo.put(info.getCoord(), info);
		
		
		Set<CoordInformation> byZoneSet = coordByZone.get(info.getZone());
		if(byZoneSet == null) {
			byZoneSet = new HashSet<CoordInformation>();
			coordByZone.put(info.getZone(), byZoneSet);
		}
		byZoneSet.add(info);
		

		for(String type : info.getTypes().keySet()) {
			Set<CoordInformation> byTypeSet = coordByType.get(type);
			if(byTypeSet == null) {
				byTypeSet = new HashSet<CoordInformation>();
				coordByType.put(type, byTypeSet);
			}
			byTypeSet.add(info);
			
		}
	}
	
	public Map<Coord, CoordInformation> filter(Predicate<? super CoordInformation> filter){
		return coordInfo.values().stream().filter(filter).collect(Collectors.toMap(x-> x.getCoord(), x->x));
	}
}
