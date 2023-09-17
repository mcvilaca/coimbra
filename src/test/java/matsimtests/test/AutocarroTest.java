package matsimtests.test;

import static org.matsim.pt2matsim.gtfs.GtfsConverter.DAY_WITH_MOST_TRIPS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Set;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;
import org.matsim.facilities.FacilitiesUtils;
import org.matsim.facilities.FacilitiesWriter;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt2matsim.config.PublicTransitMappingConfigGroup;
import org.matsim.pt2matsim.gtfs.GtfsConverter;
import org.matsim.pt2matsim.gtfs.GtfsFeed;
import org.matsim.pt2matsim.gtfs.GtfsFeedImpl;
import org.matsim.pt2matsim.mapping.PTMapper;
import org.matsim.pt2matsim.plausibility.PlausibilityCheck;
import org.matsim.pt2matsim.plausibility.log.PlausibilityWarning;
import org.matsim.pt2matsim.tools.NetworkTools;
import org.matsim.pt2matsim.tools.ScheduleTools;
import org.matsim.vehicles.Vehicles;
import org.matsim.vis.otfvis.OTFVisConfigGroup;
import org.matsim.vis.otfvis.OTFVisConfigGroup.ColoringScheme;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario;

public class AutocarroTest {

	
	static String  outputCoordinateSystem = "EPSG:20790"; //"EPSG:4326"; //"EPSG:25832";
	
	
	public static void main(String[] args) throws IOException, ParseException {
		
		File osmFile = Paths.get("data/osm/Coimbra_Region.osm").toFile();
		File questionarioFile = Paths.get("data/population/coimbra_only_tc_coimbra.txt").toFile();
		File gtfsFolder = Paths.get("data/transport/coimbra/gtfs_SMTUC").toFile();
		
		String  scenariosout = "scenarios/test2";
		CoimbraQuestionario qc = CoimbraQuestionario.readCoimbraTSV(questionarioFile);
		
		
		
		PublicTransitMappingConfigGroup configAgg = PublicTransitMappingConfigGroup.createDefaultConfig();

		configAgg.setNLinkThreshold(4);
		configAgg.setMaxLinkCandidateDistance(300);
		
		buildConfigurations(
				osmFile,
				qc,
				gtfsFolder.getAbsolutePath(),
				configAgg,
				scenariosout);
		
		
		
		Config config = loadConfig(scenariosout, qc);
		config.global().setNumberOfThreads(6);
		

		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		

		
		Controler controler = new Controler( scenario ) ;

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		
		// ---
		
		ConfigWriter w = new ConfigWriter(config);
		w.write(scenariosout + "/config.xml");
		controler.run();
	}
	
	
	public static Config loadConfig(String folder, CoimbraQuestionario qc
			
			) {
		OTFVisConfigGroup configOFV = new  OTFVisConfigGroup();
		configOFV.setDrawLinkIds(false);
		configOFV.setDrawNonMovingItems(false);
		configOFV.setDrawOverlays(true);
		configOFV.setDrawTransitFacilityIds(false);
		configOFV.setDrawTransitFacilities(false);
//		configOFV.setDrawTime(false);
		
		configOFV.setColoringScheme(ColoringScheme.bvg2 );
//		
		Config config = ConfigUtils.createConfig(configOFV);
		config.network().setInputFile(folder + "/network.xml");
		config.plans().setInputFile(folder +"/population.xml");
		config.facilities().setInputFile(folder +"/facilities.xml");
		config.transit().setTransitScheduleFile(folder +"/schedule.xml");
		config.transit().setUseTransit(true);
		config.transit().setVehiclesFile(folder +"/vehicle.xml");

		
		
		config.planCalcScore().setLearningRate(1.0);
		config.planCalcScore().setBrainExpBeta(1.0);
		
		
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		config.controler().setOutputDirectory(folder +"/output");
		config.controler().setFirstIteration(1);
		config.controler().setLastIteration(1);
		config.controler().setWriteEventsInterval(10);

		config.strategy().setMaxAgentPlanMemorySize(5);
		StrategySettings x = new StrategySettings();
		x.setStrategyName("BestScore");
		x.setWeight(1);
		
		config.strategy().addStrategySettings(x );
		qc.defenirActtypeForPlanCalcScore(config.planCalcScore());
		
		return config;
	}
	
	
	static public void buildConfigurations(
			File osmFile,
			CoimbraQuestionario qc,
			String gtfsFolder,
			PublicTransitMappingConfigGroup configAgg,
			String folderOutput) throws IOException, ParseException {
		String networkAUXFile = "lixo/network_aux.xml";
		
		
		
		Network network = buildNetwork(osmFile);
		// Nao gosto muito disto mas nao consegui por a network a entrar no metodo buildScenario
		saveNetworkToXML(network, networkAUXFile);
		

		Scenario scenario =  buildScenario(networkAUXFile, qc);
		
		GtfsFeed gtfs = readGTFS(gtfsFolder);
		
		
		TransportAgregationOutput resultAgregation = agregateNetworkWithTransports(gtfs, network, configAgg);
		PlausibilityCheck check = resultAgregation.getChecker(outputCoordinateSystem);
		
		check.runCheck();
		check.printStatisticsLog();
		Set<PlausibilityWarning> artificialLinks = check.getWarnings().get(PlausibilityWarning.Type.ArtificialLinkWarning);
		for(PlausibilityWarning p : artificialLinks)
			System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference());
		
		(new File(folderOutput)).mkdir();
		
		
		String networkOutputFile = Paths.get(folderOutput, "network.xml").toFile().getAbsolutePath();
		String populationFile = Paths.get(folderOutput,    "population.xml").toFile().getAbsolutePath();
		String facilitieFile = Paths.get(folderOutput,     "facilities.xml").toFile().getAbsolutePath();
		String scheduleFile = Paths.get(folderOutput,      "schedule.xml").toFile().getAbsolutePath();
		String vehicleFile = Paths.get(folderOutput,       "vehicle.xml").toFile().getAbsolutePath();
		
//		saveNetworkToXML(resultAgregation.getNetworkWithTransports(), networkOutputFile);
		PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation(), resultAgregation.getNetworkWithTransports());
		populationWriter.write(populationFile);
		
		FacilitiesWriter facilitiesWriter = new FacilitiesWriter(scenario.getActivityFacilities());
		facilitiesWriter.write(facilitieFile);
		
		NetworkTools.writeNetwork(resultAgregation.getNetworkWithTransports(), networkOutputFile);
		ScheduleTools.writeTransitSchedule(resultAgregation.getScheduleWithTransports(), scheduleFile);
		ScheduleTools.writeVehicles(resultAgregation.getVehicles(), vehicleFile);
		
	}
	
	
	/**
	 * pega no ficheiro osm e transforma numa network matsim.
	 * este metodo corre tambem um cleanner 
	 * @param osm
	 * @return
	 */
	public static Network buildNetwork(File osm) {
		String inputCoordinateSystem = TransformationFactory.WGS84;
		

		// choose an appropriate coordinate transformation. OSM Data is in WGS84. When working in central Germany,
		// EPSG:25832 or EPSG:25833 as target system is a good choice
		CoordinateTransformation transformation = 
				TransformationFactory.getCoordinateTransformation(
						inputCoordinateSystem,
						outputCoordinateSystem
						);

		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		/*
		 * Pick the Network from the Scenario for convenience.
		 */
		Network network = scenario.getNetwork();
		
		OsmNetworkReader reader = new OsmNetworkReader(network, transformation);
		reader.parse(osm.getAbsolutePath());
		
		 /*
         * Clean the Network. Cleaning means removing disconnected components, so that afterwards there is a route from every link
         * to every other link. This may not be the case in the initial network converted from OpenStreetMap.
         */
		new NetworkCleaner().run(network);
		return network;
	}
	
	static public void saveNetworkToXML(Network net, String xmlFile) {
		new NetworkWriter(net).write("scenarios/Coimbra/network.xml");
	}
	
	public static  Scenario buildScenario(
			String networkFile,
			CoimbraQuestionario cq
			) throws IOException, ParseException {
	
		Config config = ConfigUtils.createConfig();
		config.network().setInputFile(networkFile);
		
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		cq.createFacilities(scenario);
		cq.createQuestionaryPlan(scenario);
		return scenario;
	}
	
	static public GtfsFeed readGTFS(String gtfsFolder) {
		GtfsFeed gtfsFeed = new GtfsFeedImpl(gtfsFolder);
		return gtfsFeed;
	}
	
	
	public static TransportAgregationOutput agregateNetworkWithTransports(
			GtfsFeed gtfsFeed, 
			Network network,
			PublicTransitMappingConfigGroup config) {
		
		String param =  DAY_WITH_MOST_TRIPS;

		// load gtfs files

		// convert to transit schedule
		GtfsConverter converter = new GtfsConverter(gtfsFeed);
		converter.convert(param, outputCoordinateSystem);

		TransitSchedule schedule = converter.getSchedule();
		Vehicles vehicles = converter.getVehicles();
//			ScheduleTools.writeVehicles(converter.getVehicles(), vehicleFile);
		
		TransportAgregationOutput ret = runMappingStandard(schedule, network, config);
		ret.setVehicles(vehicles);
		return ret;
		
	}
	
	
	public static TransportAgregationOutput runMappingStandard(
			TransitSchedule schedule,
			Network network,
			PublicTransitMappingConfigGroup config
			) {



		System.out.println("getNLinkThreshold " + config.getNLinkThreshold());
		System.out.println("getCandidateDistanceMultiplier " + config.getCandidateDistanceMultiplier());
		System.out.println("getMaxLinkCandidateDistance" + config.getMaxLinkCandidateDistance());
		System.out.println("getTransportModeAssignment" + config.getTransportModeAssignment());
		
		System.out.println("getTravelCostType " + config.getTravelCostType());
		System.out.println("getRoutingWithCandidateDistance " + config.getRoutingWithCandidateDistance());
		
		System.out.println("" + config.getMaxTravelCostFactor());
		System.out.println("" + config.getModesToKeepOnCleanUp());
		System.out.println("" + config.getScheduleFreespeedModes());
		System.out.println("" + config.getRemoveNotUsedStopFacilities());
		
		
		// run PTMapper
		PTMapper ptMapper = new PTMapper(schedule, network);
		ptMapper.run(config);

		
		TransportAgregationOutput output = new TransportAgregationOutput(network, schedule);
		return output;
	}
	
	
	
	public static void savePopulationAndFacilities(Scenario scenario, String filePopu, String fileFacilities) {
		PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
		populationWriter.write(filePopu);
		
		FacilitiesWriter facilitiesWriter = new FacilitiesWriter(scenario.getActivityFacilities());
		facilitiesWriter.write(fileFacilities);
	}
	
	
	
	

	
	private static class TransportAgregationOutput{
		Network networkWithTransports;
		TransitSchedule scheduleWithTransports;
		Vehicles vehicles;
		
		public TransportAgregationOutput(Network networkWithTransports,
				TransitSchedule scheduleWithTransports) {
			this.networkWithTransports = networkWithTransports;
			this.scheduleWithTransports = scheduleWithTransports;
		}
		
		public Network getNetworkWithTransports() {
			return networkWithTransports;
		}
		
		public TransitSchedule getScheduleWithTransports() {
			return scheduleWithTransports;
		}
		
		public PlausibilityCheck getChecker(String coordSys) {
			return new PlausibilityCheck(scheduleWithTransports, networkWithTransports, coordSys);
		}
		
		public void setVehicles(Vehicles vehicles) {
			this.vehicles = vehicles;
		}
		
		public Vehicles getVehicles() {
			return vehicles;
		}

	}
}
