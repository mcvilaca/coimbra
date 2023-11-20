package pt.mvilaca.matsimtests.population;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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

import pt.mvilaca.matsimtests.population.CoimbraQuestionario3.MotivoViagem;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario3.QuestionarioIndividual;

/*This classe its developed to read the specific data survey of Coimbra region and classifies transport modes and trip purposes - Nesta atualização foi corrigida a forma como os planos estavam a ser gerados*/

public class CoimbraQuestionario3 {

	public static CoimbraQuestionario3 readCoimbraTSV(File f) throws IOException, ParseException {
		List<QuestionarioIndividual> info = new ArrayList<>();
		DateFormat df = new SimpleDateFormat("H:mm");

		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);

		// Ignoring the header
		String line = br.readLine();
		line = br.readLine();

		while (line != null) {
			String[] tokens = line.split("\t");

			String idString = tokens[0].replace("_", "");
			int personId = Integer.parseInt(idString);

			double x, y;

			// Origen
			x = Double.parseDouble(tokens[3]);
			y = Double.parseDouble(tokens[4]);
			Coord origen = new Coord(x, y);

			// Destino
			x = Double.parseDouble(tokens[11]);
			y = Double.parseDouble(tokens[12]);
			Coord destino = new Coord(x, y);

			// Horas
			Date inicio = df.parse(tokens[17]);

			MotivoViagem motivo = convertMotivoViagem(tokens[9]);

			String modo = convertToTransportMode(tokens[23]);

			int startAtHome = Integer.parseInt(tokens[2]);

			String firstActivityType;
			if (startAtHome == 1) {
				firstActivityType = "Casa";
			} else {
				firstActivityType = "Unknown";
			}

			// Assign the value of tokens[1] (column 1) to tripSequence
			int tripSequence = Integer.parseInt(tokens[1]);

			QuestionarioIndividual ai = new QuestionarioIndividual(personId, origen, destino,
					inicio.getHours() * 60 * 60 + inicio.getMinutes() * 60.0, motivo, modo, startAtHome, tripSequence);

			info.add(ai);
			line = br.readLine();
		}

		br.close();
		fr.close();

		return new CoimbraQuestionario3(info);
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

	private static String convertToTransportMode(final String transport) {
		switch (transport) {
		case "Bicicleta":
			return TransportMode.bike;
		case "Pé":
			return TransportMode.walk;
		case "Outros":
			return TransportMode.car;
		case "Táxi":
			return TransportMode.car;
		case "TC":
			return TransportMode.pt;
		case "TI":
			return TransportMode.car;
		case "TI + TC":
			return TransportMode.car;
		case "drt":
			return TransportMode.drt;
		case "Transporte Empresa/Escola":
			return TransportMode.ride;
		}

		throw new RuntimeException("Problem reading " + transport);
	}

	List<QuestionarioIndividual> info;

	private CoimbraQuestionario3(List<QuestionarioIndividual> info) {
		this.info = info;
	}

	Map<Integer, TreeSet<QuestionarioIndividual>> calculateIndexed() {
		Map<Integer, TreeSet<QuestionarioIndividual>> allInfo = new HashMap<>();

		for (QuestionarioIndividual ci : info) {

			Integer personId = ci.getPersonId();
			TreeSet<QuestionarioIndividual> percurosoIndividual = allInfo.get(personId);
			if (percurosoIndividual == null) {
				percurosoIndividual = new TreeSet<>();
				allInfo.put(personId, percurosoIndividual);
			}
			percurosoIndividual.add(ci);
		}

		return allInfo;
	}

	public List<QuestionarioIndividual> getAnswers() {
		return info;
	}

	protected Map<MotivoViagem, ActivityOption> motivoVsFacility(Scenario scenario) {

		Map<MotivoViagem, ActivityOption> ret = new HashMap<>();

		ActivityOption ao = scenario.getActivityFacilities().getFactory().createActivityOption("Home");
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
		// Create a map to associate the "MotivoViagem" (reason for the trip) with an
		// "ActivityOption"
		Map<MotivoViagem, ActivityOption> map = motivoVsFacility(scenario);

		// Create a set to keep track of unique destination coordinates
		Set<Coord> uniqueDestCoordinates = new HashSet<>();

		int i = 0;
		// Loop through the QuestionarioIndividual objects in the 'info' collection
		for (QuestionarioIndividual ci : info) {
			// Get the destination coordinate for the individual
			Coord dest = ci.getDestino();

			// Check if the destination coordinate is unique (not processed before)
			if (!uniqueDestCoordinates.contains(dest)) {
				// Create a new activity facility with a unique ID and add it to the scenario
				ActivityFacility destf = scenario.getActivityFacilities().getFactory()
						.createActivityFacility(Id.create("x" + i, ActivityFacility.class), dest);
				i++;
				scenario.getActivityFacilities().addActivityFacility(destf);

				// Get the ActivityOption associated with the MotivoViagem (reason for the trip)
				ActivityOption op = map.get(ci.getMotivo());

				// If an ActivityOption is found, add it to the destination facility
				if (op != null) {
					try {
						destf.addActivityOption(op);
					} catch (Exception e) {
						// Handle any exceptions that may occur
					}
				}

				// Add the destination coordinate to the set to mark it as processed
				uniqueDestCoordinates.add(dest);
			}
		}
	}

	public void createQuestionaryPlan(Scenario scenario) {
		Population population = scenario.getPopulation();

//        // Sort trips by person ID and trip sequence
//        List<QuestionarioIndividual> sortedTrips = new ArrayList<>(info);
//        sortedTrips.sort(Comparator.comparing(QuestionarioIndividual::getPersonId)
//                                    .thenComparing(QuestionarioIndividual::getTripSequence));

		Map<Integer, TreeSet<QuestionarioIndividual>> personActivity = calculateIndexed();

		for (Integer pid : personActivity.keySet()) {
			Person person = population.getFactory().createPerson(Id.create(pid.toString(), Person.class));
			Plan plan = population.getFactory().createPlan();
			plan.setPerson(person);

			Boolean firstTrip = true;
			QuestionarioIndividual actBefore = null;

			

			TreeSet<QuestionarioIndividual> cis = personActivity.get(pid);


			for (QuestionarioIndividual ci : cis) {
				if (firstTrip == true) {
					int startAtHome = ci.getStartAtHome();
					Coord origem = ci.getOrigem();
					Double end_time = ci.getInicio();
					MotivoViagem motivoViagem = MotivoViagem.outros;
					if (startAtHome == 1)
						motivoViagem = MotivoViagem.casa;
					Activity act1 = population.getFactory().createActivityFromCoord(motivoViagem.toString(),
							ci.getOrigem());
					act1.setEndTime(end_time);
					Leg leg = population.getFactory().createLeg(ci.getMode());
				
					plan.addActivity(act1);
					plan.addLeg(leg);
					firstTrip = false;

				} else {
					MotivoViagem motivoViagem = actBefore.getMotivo();
					Coord destino = actBefore.getDestino();
					Activity act = population.getFactory().createActivityFromCoord(motivoViagem.toString(), destino);
					plan.addActivity(act);

					Double end_time = ci.getInicio();
					Leg leg = population.getFactory().createLeg(ci.getMode());
					plan.addLeg(leg);
					act.setEndTime(end_time);

				}

				
				
				actBefore = ci;

				
			}
			
			MotivoViagem motivoViagem = actBefore.getMotivo();
			Coord destino = actBefore.getDestino();
			Activity act = population.getFactory().createActivityFromCoord(motivoViagem.toString(), destino);
			plan.addActivity(act);
			
			person.addPlan(plan);
			population.addPerson(person); // Add the person to the population
		}
	}

	static public enum MotivoViagem {
		trabalho, casa, escola, lazer_regular, lazer_ocasional, compras, buscar_levar_familiares, refeicoes, saude,
		assuntos_pessoais, motivos_profissionais, servicos_publicos, outros, visitas, passear, regresso_a_casa_dos_pais,
		trabalhos_agriculas, nao_responde,
	}

	// Necessita de ser revisto e entendido para o que serve
	public void defenirActtypeForPlanCalcScore(PlanCalcScoreConfigGroup planScore) {

		ActivityParams activity = new ActivityParams(MotivoViagem.casa + "");
		activity.setTypicalDuration(12 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.trabalho + "");
		activity.setTypicalDuration(8 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.escola + "");
		activity.setTypicalDuration(8 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.lazer_regular + "");
		activity.setTypicalDuration(2 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.lazer_ocasional + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.compras + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.buscar_levar_familiares + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.refeicoes + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.assuntos_pessoais + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.motivos_profissionais + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.servicos_publicos + "");
		activity.setTypicalDuration(1 * 60 * 60);
		activity.setOpeningTime(32400);
		activity.setClosingTime(61200);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.outros + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.visitas + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.passear + "");
		activity.setTypicalDuration(2 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.regresso_a_casa_dos_pais + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.trabalhos_agriculas + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.nao_responde + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.saude + "");
		activity.setTypicalDuration(1 * 60 * 60);
		planScore.addActivityParams(activity);
	}

	static public class QuestionarioIndividual implements Comparable<QuestionarioIndividual> {

		int personId;
		Coord origem;
		Coord destino;
		double inicio;
		MotivoViagem motivo;
		String modo;
		int startAtHome;
		int tripSequence;

		public QuestionarioIndividual(int personId, Coord origem, Coord destino, Double inicio, MotivoViagem motivo,
				String modo, int startAtHome, int tripSequence) {
			super();
			this.personId = personId;
			this.origem = origem;
			this.destino = destino;
			this.inicio = inicio;
			this.startAtHome = startAtHome;
			this.motivo = motivo;
			this.modo = modo;
			this.tripSequence = tripSequence;
		}

		public void setTripSequence(int tripSequence) {
			// TODO Auto-generated method stub

		}

		public int getPersonId() {
			return personId;
		}

		public Coord getOrigem() {
			return origem;
		}

		public Coord getDestino() {
			return destino;
		}

		public Double getInicio() {
			return inicio;
		}

		public MotivoViagem getMotivo() {
			return motivo;
		}

		public String getMode() {
			return modo;
		}

		public int getStartAtHome() {
			return startAtHome;
		}

		public int getTripSequence() {
			return tripSequence;
		}

		@Override
		public int compareTo(QuestionarioIndividual o) {
			return Double.compare(getTripSequence(), o.getTripSequence());
		}
	}
}
