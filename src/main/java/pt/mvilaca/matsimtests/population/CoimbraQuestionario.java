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

import pt.mvilaca.matsimtests.population.CoimbraQuestionario.QuestionarioIndividual;

public class CoimbraQuestionario {

	public static CoimbraQuestionario readCoimbraTSV(File f) throws IOException, ParseException {
//		Map<Integer, TreeSet<QuestionarioIndividual>> allInfo = new HashMap<>();
		List<QuestionarioIndividual> info = new ArrayList<>();
		DateFormat df =  new SimpleDateFormat("H':'m");
		//		NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt"));


		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);

		//		De forma a ignorar o cabeçalho do ficheiro são lidas duas linhas
		String line = br.readLine();
		line = br.readLine();

		while(line!=null) {
			String[] tokens = line.split("\t");

//			int personId = Integer.parseInt(tokens[0]);
			String idString = tokens[0].replace("_", "");
			int personId = Integer.parseInt(idString);
			


			double x,y;

			//			Origen
			x = Double.parseDouble(tokens[3]);
			y = Double.parseDouble(tokens[4]);
			Coord origen = new Coord(x, y);

			//			Destino
			x = Double.parseDouble(tokens[11]);
			y = Double.parseDouble(tokens[12]);
			Coord destino = new Coord(x, y);

			//			Horas
//			System.out.println(line);
//			System.out.println(tokens[17]);
			Date inicio = df.parse(tokens[17]);
			Date fim = df.parse(tokens[18]);
			Date duration = df.parse(tokens[19]);

			MotivoViagem motivo = convertMotivoViagem(tokens[9]);

			String modo = convertToTrasportMode(tokens[23]);

			QuestionarioIndividual ai = new QuestionarioIndividual(
					personId, 
					origen, 
					destino, 
					inicio.getHours()*60*60+inicio.getMinutes()*60.0, 
					fim.getHours()*60*60+fim.getMinutes()*60.0, 
					duration.getHours()*60*60+duration.getMinutes()*60.0,
					motivo,
					modo);
					

			info.add(ai);
			line = br.readLine();
		}

		br.close();
		fr.close();

		return new CoimbraQuestionario(info);
	}

//TESTS ABOUT DATA ACCURACY	
//	public void solveDurationProblems() {
//		for(QuestionarioIndividual qi : this.getAnsers()) {
//			double inicio = qi.getInicio();
//			double fim = qi.getFim();
//			fim = (fim == 0.0)?24*60*60:fim;
			
//			double calculatedDuration = fim -inicio;
//			
//			if(qi.getDuration() <= 0.0)
//				qi.duration = calculatedDuration;
//				
//		}
//	}
	
//	public void removeModes(Set<String> modos) {
//		List<QuestionarioIndividual> toRemove = new ArrayList<>();
//		for(QuestionarioIndividual qi : this.getAnsers()) {
//			if(modos.contains(qi.getMode())) toRemove.add(qi);
//		}
//		
//		this.getAnsers().removeAll(toRemove);
//	}
	
	
//	public void removeIds(Set<Integer> ids) {
//		List<QuestionarioIndividual> toRemove = new ArrayList<>();
//		for(QuestionarioIndividual qi : this.getAnsers()) {
//			if(ids.contains(qi.getPersonId())) toRemove.add(qi);
//		}
//		
//		this.getAnsers().removeAll(toRemove);
//	}
//	
//	public void removeDurationZero() {
//		
//		List<QuestionarioIndividual> toRemove = new ArrayList<>();
//		for(QuestionarioIndividual qi : this.getAnsers()) {
//			if(qi.getDuration() <= 0.0) toRemove.add(qi);
//		}
//		
//		this.getAnsers().removeAll(toRemove);
//	}

//	public void changeFimZero() {
//		for(QuestionarioIndividual qi : this.getAnsers()) {
//			double fim = qi.getFim();
//			if(fim <= 0.0)
//				qi.fim = 24*60*60;
//				
//		}
//	}

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
//		case "1":
//			return TransportMode.walk;
//		case "2":
//			return TransportMode.motorcycle;
//		case "3":
//			return TransportMode.car;
//		case "4":
//			return TransportMode.car;
//		case "5":
//			return TransportMode.drt;
//		case "6":
//			return TransportMode.taxi;
//		case "7":
//			return TransportMode.train;
//		case "8":
//			return TransportMode.train;
//		case "9":
//			return TransportMode.train;
//		case "10":
//			return TransportMode.train;
//		case "11":
//			return TransportMode.train;
//		case "12":
//			return TransportMode.pt;
//		case "13":
//			return TransportMode.pt;
//		case "14":
//			return TransportMode.pt;
//		case "15":
//			return TransportMode.pt;
//		case "16":
//			return TransportMode.pt;
//		case "17":
//			return TransportMode.pt;
//		case "18":
//			return TransportMode.other;
//		case "19":
//			return TransportMode.bike;
//		case "20":
//			return TransportMode.other;
//		case "21":
//			return TransportMode.truck;
//		case "99":
//			return TransportMode.other;
		
		
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

	private CoimbraQuestionario(
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
		
//	trabalho,
//		casa,
//		escola,
//		lazer_regular,
//		lazer_ocasional,
//		compras,
//		buscar_levar_familiares,
//		refeicoes,
//		saude,
//		assuntos_pessoais,
//		motivos_profissionais,
//		servicos_publicos,
//		outros,
//		visitas,
//		passear,
//		regresso_a_casa_dos_pais,
//		trabalhos_agriculas,
//		nao_responde,	
		
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
	
	
	public void createFacilities(Scenario scenario){

		Map<MotivoViagem, ActivityOption> map = motivoVsFacility(scenario);
		
//		ActivityOption aoh = scenario.getActivityFacilities().getFactory().createActivityOption("x");
//
		
		Map<Coord, ActivityFacility> facilities = new HashMap<>();
		Set<Coord> set = new HashSet<>();
		int i=0;
		for(QuestionarioIndividual ci : info) {
			
			Coord orig = ci.getOrigem();
			Coord dest = ci.getDestino();
			
			ActivityFacility origf = facilities.get(orig);
			if(origf == null) {
				origf =  scenario.getActivityFacilities().getFactory().createActivityFacility(Id.create("x" + i, ActivityFacility.class), orig);
				i++;
				scenario.getActivityFacilities().addActivityFacility(origf);
				facilities.put(orig, origf);
			}
			
			ActivityFacility destf = facilities.get(dest);
			if(destf == null) {
				destf =  scenario.getActivityFacilities().getFactory().createActivityFacility(Id.create("x" + i, ActivityFacility.class), dest);
				i++;
				scenario.getActivityFacilities().addActivityFacility(destf);
				facilities.put(dest, destf);
			}
			ActivityOption op = map.get(ci.getMotivo());
			
			if(op!= null) {
//				destf.get
				try {
					destf.addActivityOption(op);
				}catch (Exception e) {
					// TODO: handle exception
				}
				
				set.add(dest);
			}
		}

		
	}


	public void createQuestionaryPlan(Scenario scenario) {

		
//		ActivityOption aoh = scenario.getActivityFacilities().getFactory().createActivityOption("home");
//		ActivityOption aow = scenario.getActivityFacilities().getFactory().createActivityOption("work");
		Population population = scenario.getPopulation();

		Map<Integer, TreeSet<QuestionarioIndividual>> personActivity = calculateIndexed();
		
		for(Integer pid : personActivity.keySet()) {

			Person person =population.getFactory().createPerson(Id.create(pid, Person.class));
			
			population.addPerson(person);
//			population.getAttributes(age);
//			population.getAttributes(gender);
			
			
			TreeSet<QuestionarioIndividual> cis = personActivity.get(pid);
			Plan plan = population.getFactory().createPlan();
			plan.setPerson(person);
			for(QuestionarioIndividual ci : cis) {
				
				String motivoViagem = ci.getMotivo().toString();
				Activity inicio = population.getFactory().createActivityFromCoord(motivoViagem, ci.origem);
				Activity fim = population.getFactory().createActivityFromCoord(motivoViagem, ci.destino); 

				inicio.setEndTime(ci.getInicio());
				fim.setEndTime(ci.getFim());

				plan.addActivity(inicio);
				Leg leg = population.getFactory().createLeg(ci.getMode());
				leg.setDepartureTime(ci.getInicio());
				leg.setTravelTime(ci.getDuration());
				plan.addLeg(leg);
				plan.addActivity(fim);

			}
			person.addPlan(plan);

		}
	}


	static public enum MotivoViagem{
		trabalho,
		casa,
		escola,
		lazer_regular,
		lazer_ocasional,
		compras,
		buscar_levar_familiares,
		refeicoes,
		saude,
		assuntos_pessoais,
		motivos_profissionais,
		servicos_publicos,
		outros,
		visitas,
		passear,
		regresso_a_casa_dos_pais,
		trabalhos_agriculas,
		nao_responde,		
	}
	
	
//	Necessita de ser revisto e entendido para o que serve
	public void defenirActtypeForPlanCalcScore(PlanCalcScoreConfigGroup planScore) {
		
		ActivityParams activity = new ActivityParams(MotivoViagem.casa+"");
		activity.setTypicalDuration(12*60*60);
		planScore.addActivityParams(activity);
	
		
		activity = new ActivityParams(MotivoViagem.trabalho+"");
		activity.setTypicalDuration(8*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.escola+"");
		activity.setTypicalDuration(8*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.lazer_regular+"");
		activity.setTypicalDuration(2*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.lazer_ocasional+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.compras+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.buscar_levar_familiares+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.refeicoes+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);
		
		activity = new ActivityParams(MotivoViagem.assuntos_pessoais+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.motivos_profissionais+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.servicos_publicos+"");
		activity.setTypicalDuration(1*60*60);
		activity.setOpeningTime(32400);
		activity.setClosingTime(61200);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.outros+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.visitas+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.passear+"");
		activity.setTypicalDuration(2*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.regresso_a_casa_dos_pais+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.trabalhos_agriculas+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.nao_responde+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);

		activity = new ActivityParams(MotivoViagem.saude+"");
		activity.setTypicalDuration(1*60*60);
		planScore.addActivityParams(activity);
	}
	
	static public class QuestionarioIndividual implements Comparable<QuestionarioIndividual>{

		int personId;
		Coord origem;
		Coord destino;
		double inicio;
		double fim;
		double duration; 
		MotivoViagem motivo;
		String modo;


		public QuestionarioIndividual(
				int personId, 
				Coord origem, 
				Coord destino, 
				Double inicio, 
				Double fim, 
				Double duration,
				MotivoViagem motivo,
				String modo) {
			super();
			this.personId = personId;
			this.origem = origem;
			this.destino = destino;
			this.inicio = inicio;
			this.fim = fim;
			this.motivo = motivo;
			this.modo = modo;
			this.duration = duration;
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


		public Double getFim() {
			return fim;
		}


		public MotivoViagem getMotivo() {
			return motivo;
		}


		public String getMode() {
			return modo;
		}
		
		public Double getDuration() {
			return duration;
		}


		@Override
		public int compareTo(QuestionarioIndividual o) {
			return getInicio().compareTo(o.getInicio());
		}


	}


}
