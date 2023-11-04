package pt.mvilaca.matsimtests.population;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.ActivityOption;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario.MotivoViagem;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario.QuestionarioIndividual;

/*This classe its developed to read the specific data survey of Coimbra region and classifies transport modes and trip purposes*/
public class CoimbraQuestionario2 {

	public static CoimbraQuestionario2 readCoimbraTSV(File f) throws IOException, ParseException {

		List<QuestionarioIndividual> info = new ArrayList<>();
		DateFormat df =  new SimpleDateFormat("H':'m");

		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);

		//	Read and Ignore the header line 	
		String line = br.readLine();
		line = br.readLine();

		    while ((line = br.readLine()) != null) {
		        String[] tokens = line.split("\t");

			String idString = tokens[0].replace("_", "");
			int personId = Integer.parseInt(idString);
			


			double x,y;

			//			Origen
			x = Double.parseDouble(tokens[3]);
			y = Double.parseDouble(tokens[4]);
			Coord origem = new Coord(x, y);

			//			Destino
			x = Double.parseDouble(tokens[11]);
			y = Double.parseDouble(tokens[12]);
			Coord destino = new Coord(x, y);

			Date inicio = df.parse(tokens[17]);
			
			int startAtHome = Integer.parseInt(tokens[2]);
	        int endAtHome = Integer.parseInt(tokens[10]);

			MotivoViagem motivo = convertMotivoViagem(tokens[9]);
			String mode = convertToTrasportMode(tokens[23]);

			QuestionarioIndividual ai = new QuestionarioIndividual(
					personId, 
					origem, 
					destino, 
					inicio.getHours()*60*60+inicio.getMinutes()*60.0, 
					startAtHome,
					endAtHome,
					motivo,
					mode);
					

			info.add(ai);
			line = br.readLine();
		}

		br.close();
		fr.close();

		return new CoimbraQuestionario2(info);
	}


	private static MotivoViagem convertMotivoViagem(final String motivo) {
		
		switch (motivo) {
		case "1":
			return MotivoViagem.trabalho;
		case "2":
			return MotivoViagem.escola;
		case "3":
			return MotivoViagem.casa;
		case "4":
			return MotivoViagem.compras;
		case "5":
			return MotivoViagem.lazer_regular;
		case "6":
			return MotivoViagem.lazer_ocasional;
		case "7":
			return MotivoViagem.buscar_levar_familiares;
		case "8":
			return MotivoViagem.refeicoes;
		case "9":
			return MotivoViagem.saude;
		case "10":
			return MotivoViagem.assuntos_pessoais;
		case "11":
			return MotivoViagem.motivos_profissionais;
		case "12":
			return MotivoViagem.servicos_publicos;
		case "13":
			return MotivoViagem.outros;
		case "14":
			return MotivoViagem.visitas;
		case "15":
			return MotivoViagem.passear;
		case "16":
			return MotivoViagem.regresso_a_casa_dos_pais;
		case "17":
			return MotivoViagem.trabalhos_agriculas;
		case "99":
			return MotivoViagem.nao_responde;

		}
		throw new RuntimeException("Problem parsing motivo de viagem " + motivo);
	}
	
	
	private static String convertToTrasportMode(final String transport) {
		switch (transport) {
	
		case "Bicicleta":
			return TransportMode.bike;
		case "Pé":
			return TransportMode.walk;
		case "Outros":
//			return TransportMode.other;
//			return TransportMode.motorcycle;
//			return TransportMode.non_network_walk;
			return TransportMode.car;
		case "Táxi":
//			return TransportMode.taxi;
			return TransportMode.car;
		case "TC":
			return TransportMode.pt;
		case "TI":
			return TransportMode.car;
//		Verificar esta conversão, de certeza que nao esta certo. 
//		Ou se tem de fazer um transport mode diferent costumizado. 
		case "TI + TC":
			return TransportMode.car;
		case "drt":
			return TransportMode.drt;
		
//		confirmar
		case "Transporte Empresa/Escola":
//			return TransportMode.drt;
			return TransportMode.ride;
		}
		
		throw new RuntimeException("Problem reading " + transport);
	}


	List<QuestionarioIndividual> info;

//	Map<Integer, TreeSet<QuestionarioIndividual>> personActivity;

	private CoimbraQuestionario2(
			List<QuestionarioIndividual> info
			) {
		this.info = info;
//		this.personActivity = personActivity;
	}

	Map<Integer, TreeSet<QuestionarioIndividual>> calculateIndexed(){
		Map<Integer, TreeSet<QuestionarioIndividual>> allInfo = 
				new HashMap<>(); 
		
		for(QuestionarioIndividual ci : info) {
			
			Integer personId = ci.getPersonId();
			TreeSet<QuestionarioIndividual> percurosoIndividual = allInfo.get(personId);
			if(percurosoIndividual == null) {
				percurosoIndividual = new TreeSet<>();
				allInfo.put(personId, percurosoIndividual);
			}
			percurosoIndividual.add(ci);
		}
		
		return allInfo;
	}
	
	
	public List<QuestionarioIndividual> getAnsers(){
		return info;
	}


	protected Map<MotivoViagem, ActivityOption> motivoVsFacility(Scenario scenario){
		
		
		Map<MotivoViagem,ActivityOption> ret = new HashMap<>();
		
		ActivityOption ao = scenario.getActivityFacilities().getFactory().createActivityOption("casa");
		ret.put(MotivoViagem.casa, ao);
		ret.put(MotivoViagem.buscar_levar_familiares, ao);
		ret.put(MotivoViagem.regresso_a_casa_dos_pais, ao);
		ret.put(MotivoViagem.visitas, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Work");
		ret.put(MotivoViagem.trabalho, ao);
		ret.put(MotivoViagem.motivos_profissionais, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Education");
		ret.put(MotivoViagem.escola, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Shopping");
		ret.put(MotivoViagem.compras, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Restaurant");
		ret.put(MotivoViagem.refeicoes, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Leisure");
		ret.put(MotivoViagem.lazer_ocasional, ao);
		ret.put(MotivoViagem.lazer_regular, ao);
		ret.put(MotivoViagem.passear, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Health");
		ret.put(MotivoViagem.saude, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Agriculture");
		ret.put(MotivoViagem.trabalhos_agriculas, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Services");
		ret.put(MotivoViagem.servicos_publicos, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Others");
		ret.put(MotivoViagem.assuntos_pessoais, ao);
		ret.put(MotivoViagem.outros, ao);
		
		ao = scenario.getActivityFacilities().getFactory().createActivityOption("Unknown");
		ret.put(MotivoViagem.nao_responde, ao);
		
		return ret;
	}
	
	
	public void createFacilities(Scenario scenario) {
	    Map<MotivoViagem, ActivityOption> map = motivoVsFacility(scenario);

	    Map<Coord, ActivityFacility> facilities = new HashMap<>();
	    Set<Coord> set = new HashSet<>();
	    int i = 0;

	    for (QuestionarioIndividual ci : info) {
	        Coord orig = ci.getOrigem();
	        Coord dest = ci.getDestino();

	        ActivityFacility origf = facilities.get(orig);
	        if (origf == null) {
	            origf = scenario.getActivityFacilities().getFactory().createActivityFacility(Id.create("x" + i, ActivityFacility.class), orig);
	            i++;
	            scenario.getActivityFacilities().addActivityFacility(origf);
	            facilities.put(orig, origf);
	        }

	        ActivityFacility destf = facilities.get(dest);
	        if (destf == null) {
	            destf = scenario.getActivityFacilities().getFactory().createActivityFacility(Id.create("x" + i, ActivityFacility.class), dest);
	            i++;
	            scenario.getActivityFacilities().addActivityFacility(destf);
	            facilities.put(dest, destf);
	        }

	        ActivityOption op = map.get(ci.getMotivo());

	        if (op != null) {
	            try {
	                destf.addActivityOption(op);
	            } catch (Exception e) {
	                // Handle exceptions
	            }
	            set.add(dest);
	        }
	    }
	}


	public void createQuestionaryPlan(Scenario scenario) {
	    Population population = scenario.getPopulation();
	    Map<Integer, TreeSet<QuestionarioIndividual>> personActivity = calculateIndexed();

	    for (Integer pid : personActivity.keySet()) {
	        Person person = population.getFactory().createPerson(Id.create(pid, Person.class));
	        population.addPerson(person);

	        TreeSet<QuestionarioIndividual> cis = personActivity.get(pid);
	        Plan plan = population.getFactory().createPlan();
	        plan.setPerson(person);

	        boolean firstTrip = true;
	        double previousEndTime = 0.0;

	        for (QuestionarioIndividual ci : cis) {
	            String motivoViagem = ci.getMotivo().toString();
	            String modo = ci.getMode(); // Read the transport mode

	            if (firstTrip) {
	                if (ci.startAtHome == 1) {
	                    // First trip starts at home
	                    Activity casaInicio = population.getFactory().createActivityFromCoord("casa", ci.getOrigem());
	                    casaInicio.setEndTime(ci.getInicio());
	                    plan.addActivity(casaInicio);
	                } else {
	                    // First trip doesn't start at home
	                    Activity unknownInicio = population.getFactory().createActivityFromCoord("unknown", ci.getOrigem());
	                    unknownInicio.setEndTime(ci.getInicio());
	                    plan.addActivity(unknownInicio);
	                }

	                // Add the MATSim Leg with the transport mode for the first trip
	                Leg leg = population.getFactory().createLeg(modo);
	                plan.addLeg(leg);

	                // Create the second activity based on motivo da viagem
	                Activity motivoViagemActivity = population.getFactory().createActivityFromCoord(motivoViagem, ci.getDestino());
	                motivoViagemActivity.setEndTime(ci.getFim());
	                plan.addActivity(motivoViagemActivity);
	                firstTrip = false; // Set firstTrip to false
	            } else {
	                // Handle subsequent trips (B2, B3, etc.) here
	                if (ci.startAtHome == 1) {
	                    // Subsequent trip starts at home
	                    Activity casaInicio = population.getFactory().createActivityFromCoord("casa", ci.getOrigem());
	                    casaInicio.setEndTime(ci.getInicio());
	                    plan.addActivity(casaInicio);
	                } else {
	                    // Subsequent trip doesn't start at home
	                    // Adjust activity creation based on your requirements
	                    Activity unknownInicio = population.getFactory().createActivityFromCoord("unknown", ci.getOrigem());
	                    unknownInicio.setEndTime(ci.getInicio());
	                    plan.addActivity(unknownInicio);
	                }

	                // Add the MATSim Leg with the transport mode for subsequent trips
	                Leg leg = population.getFactory().createLeg(modo);
	                plan.addLeg(leg);

	                // Create an activity for the motivo da viagem
	                Activity motivoViagemActivity = population.getFactory().createActivityFromCoord(motivoViagem, ci.getDestino());
	                motivoViagemActivity.setEndTime(ci.getFim());
	                plan.addActivity(motivoViagemActivity);
	            }

	            // Update previous end time
	            previousEndTime = ci.getFim();
	        }

	        // Check if the last trip ends at home
	        if (cis.last().endAtHome == 1) {
	            // Add the last "casa" activity
	            Activity casaFim = population.getFactory().createActivityFromCoord("casa", cis.last().getDestino());
	            plan.addActivity(casaFim);
	        }

	        person.addPlan(plan);
	    }
	}


}