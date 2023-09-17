//package matsimtests.test;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.text.ParseException;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.geotools.util.factory.GeoTools;
//import org.matsim.api.core.v01.Coord;
//import org.matsim.core.utils.geometry.CoordUtils;
//import org.matsim.core.utils.geometry.transformations.GeotoolsTransformation;
//
//import pt.mvilaca.matsimtests.population.CoimbraQuestionario;
//import pt.mvilaca.matsimtests.population.CoimbraQuestionario.QuestionarioIndividual;
//
//public class TestQuestionario {
//
//	public static void testeDuration(CoimbraQuestionario cq ) {
//		
//		System.out.println(">>>>>>>>>>>>>> Teste Duration <<<<<<<<<<<<<<<<");
//		for(QuestionarioIndividual qi : cq.getAnsers()) {
//			double inicio = qi.getInicio();
//			double fim = qi.getFim();
//			fim = (fim == 0.0)?24*60*60:fim;
//			
//			double calculatedDuration = fim -inicio;
//			
//			if(qi.getDuration() <= 0.0)
//				System.out.println("Problem duration " + qi.getPersonId() +"@"+ qi.getMode() + ";"+ qi.getMotivo()+" => " + qi.getDuration() + "\ti: " + inicio + "\tf:" + fim + "\tcd: " + calculatedDuration);
//		}
//	}
//	
//	public static void testeFim(CoimbraQuestionario cq ) {
//		System.out.println(">>>>>>>>>>>>>> Teste FIM <<<<<<<<<<<<<<<<");
//		for(QuestionarioIndividual qi : cq.getAnsers()) {
//			double inicio = qi.getInicio();
//			double fim = qi.getFim();
////			fim = (fim == 0.0)?24*60*60:fim;
//			
//			double calculatedDuration = fim -inicio;
//			
//			if(fim <= 0.0)
//				System.out.println("Problem duration " + qi.getPersonId() +"@"+ qi.getMode() + ";"+ qi.getMotivo()+" => " + qi.getDuration() + "\ti: " + inicio + "\tf:" + fim + "\tcd: " + calculatedDuration);
//		}
//	}
//	
//	
//	
//	public static void testeModo(CoimbraQuestionario cq ) {
//		System.out.println(">>>>>>>>>>>>>> Teste Modo <<<<<<<<<<<<<<<<");
//		
//		Set<String> modos = new HashSet<>();
//		modos.add("walk");
//		modos.add("non_network_walk");
//		
//		for(QuestionarioIndividual qi : cq.getAnsers()) {
//			double inicio = qi.getInicio();
//			double fim = qi.getFim();
//			Coord destino = qi.getDestino();
//			Coord partida = qi.getOrigem();
//			
//			Double distance = CoordUtils.calcEuclideanDistance(destino, partida);
//			
//			if(modos.contains(qi.getMode()))
//			System.out.println("Problem duration " + qi.getPersonId() +"@"+ qi.getMode() + ";"+ qi.getMotivo()+" => " + qi.getDuration() + "\ti: " + inicio + "\tf:" + fim + "\tdist:"+distance);
//		}
//	}
//	
//	
//	public static void testeId(CoimbraQuestionario cq ) {
//		System.out.println(">>>>>>>>>>>>>> Teste IDS <<<<<<<<<<<<<<<<");
//		
//		Set<Integer> ids = new HashSet<>();
//		ids.add(2032);
//		
//		for(QuestionarioIndividual qi : cq.getAnsers()) {
//			double inicio = qi.getInicio();
//			double fim = qi.getFim();
//			Coord destino = qi.getDestino();
//			Coord partida = qi.getOrigem();
//			
//			Double distance = CoordUtils.calcEuclideanDistance(destino, partida);
//			
//			if(ids.contains(qi.getPersonId()))
//			System.out.println("Problem duration " + qi.getPersonId() +"@"+ qi.getMode() + ";"+ qi.getMotivo()+" => " + qi.getDuration() + "\ti: " + inicio + "\tf:" + fim + "\tdist:"+distance);
//		}
//	}
//	
//	
//	
//	public static void main(String[] args) throws IOException, ParseException {
//		File f = Paths.get("data", "population", "coimbra.tsv").toFile();
//		CoimbraQuestionario cq = CoimbraQuestionario.readCoimbraTSV(f);
//	
//		
//		
//		
//		cq.removeDurationZero();
//		cq.changeFimZero();
//		cq.solveDurationProblems();
//		
//		
//		Set<String> modos = new HashSet<>();
//		modos.add("walk");
//		modos.add("non_network_walk");
//		cq.removeModes(modos);
//		
//		Set<Integer> ids = new HashSet<>();
//		ids.add(2032);
//		cq.removeIds(ids);
//		
//		
//		testeDuration(cq);
//		testeFim(cq);
//		testeModo(cq);
//		testeId(cq);
//	}
//}
