package pt.mvilaca.matsimtests.allpipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.contrib.edrt.run.EDrtControlerCreator;
import org.matsim.contrib.otfvis.OTFVis;
import org.matsim.contrib.otfvis.RunOTFVis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup.AccessEgressType;
import org.matsim.core.config.groups.QSimConfigGroup.EndtimeInterpretation;
import org.matsim.core.config.groups.QSimConfigGroup.LinkDynamics;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.QSimConfigGroup.StarttimeInterpretation;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.QSimConfigGroup.VehicleBehavior;
import org.matsim.core.config.groups.QSimConfigGroup.VehiclesSource;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;
import org.matsim.facilities.FacilitiesWriter;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt2matsim.config.PublicTransitMappingConfigGroup;
import org.matsim.pt2matsim.gtfs.GtfsConverter;
import org.matsim.pt2matsim.mapping.PTMapper;
import org.matsim.pt2matsim.plausibility.PlausibilityCheck;
import org.matsim.pt2matsim.run.Gtfs2TransitSchedule;
import org.matsim.pt2matsim.tools.NetworkTools;
import org.matsim.pt2matsim.tools.ScheduleTools;
import org.matsim.pt2matsim.tools.ShapeTools;
import org.matsim.pt2matsim.tools.lib.RouteShape;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.Vehicles;

import ch.qos.logback.classic.Level;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario3;
import pt.mvilaca.matsimtests.population.trip.IndividualRandomSelection;
import pt.mvilaca.matsimtests.population.trip.TripsPlan;
import utils.LogUtils;

public class RunAllPipeline {

	//SELECT WHAT I WANT THE CODE TO DO
	public static boolean generateNetwork=true; 
	public static boolean doSimulation= true;
	public static boolean doVisualization= false;
	
	//This variable indicates the time window that the replicated population can vary depending on the original
	public static Double timeWindowInSeconds = 10.0*60;
	
	//Full synthetic means that all the trips are generated (based on the synthetic population rules) there is no replication of the cases that already exist but we can select a population number. Ideal for tests.
	public static boolean fullSynthetic = false;
	public static int numberSyntheticPersons = 0;
	

	public static void main(String[] args) throws IOException, ParseException {

		LogUtils.changeLog(TripsPlan.class, Level.INFO);
		LogUtils.changeLog(IndividualRandomSelection.class, Level.OFF);
		
		//Input/Output path
		String coimbra_file_path ="data/osm/CMBR.osm";
		String gtfsFolder = "data/transport/coimbra/pt-two.xml";
		String scenarioFolder = "scenarios/Regional-testpt-two/";

		(new File(scenarioFolder)).mkdir();
		if(generateNetwork) generateNetwork(coimbra_file_path, scenarioFolder,gtfsFolder);
	
		//Specification of the survey information (coimbra2) is the original survey with some mistakes solved
		if(doSimulation) {
			File f = Paths.get("data", "population", "Region.tsv").toFile();
			CoimbraQuestionario3 cq = generatePop(scenarioFolder, f, fullSynthetic, numberSyntheticPersons);
			simulation(scenarioFolder, cq);
		}
		
		
		if(doVisualization) {
			String[] a = new String[] { 
					"-convert",
					scenarioFolder+ "outputs/output_events.xml.gz",
					scenarioFolder+ "outputs/output_network.xml.gz",
					scenarioFolder+ "outputs/mvi_output.mvi",
					"60"
					
					};
			RunOTFVis.main(a);
			
			RunOTFVis.main(new String[] {scenarioFolder+ "outputs/mvi_output.mvi"});

		}
	}

	
	
	public static CoimbraQuestionario3 generatePop(String scenarioFolder, File f, boolean fullSynthetic, int numberPersons) throws IOException, ParseException {
		
		
		CoimbraQuestionario3 cq = CoimbraQuestionario3.readCoimbraTSV(f);
		TripsPlan tp = new TripsPlan(cq.calculateIndexed());
	
	
		Random rnd = new Random(System.currentTimeMillis());
	
		if(fullSynthetic)
			cq = tp.generateRandomPopulation(numberPersons, timeWindowInSeconds, rnd);
		else
		    cq = tp.replicatePopulation(1.0, timeWindowInSeconds, rnd);
	
	
		Config config = ConfigUtils.createConfig();
		//		config.network().setInputFile(scenarioFolder+ "networkWithTransports.xml");
	
		Scenario scenario = ScenarioUtils.loadScenario(config) ;
		cq.createFacilities(scenario);
		cq.createQuestionaryPlan(scenario);
	
		PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
		populationWriter.write(scenarioFolder+"population.xml");
	
		FacilitiesWriter facilitiesWriter = new FacilitiesWriter(scenario.getActivityFacilities());
		facilitiesWriter.write(scenarioFolder+"facilities.xml");	
		return cq;
	}
	
	public static void generateNetwork(String coimbra_file_path,String folder, String gtfsFolder) {


		String inputCoordinateSystem = TransformationFactory.WGS84;
		String outputCoordinateSystem = "EPSG:20790"; //"EPSG:4326"; //"EPSG:25832";

		// choose an appropriate coordinate transformation. OSM Data is in WGS84.
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
		reader.parse(coimbra_file_path);


		new NetworkCleaner().run(network);

		new NetworkWriter(network).write(folder+"/network.xml");

		if(gtfsFolder!=null) {
			String coordinate= "EPSG:20790";
			String scheduleFileWithTransports = folder+"/scheduleWithTransports.xml";
			String inputNetwork = folder+"/network.xml";
			String networkWithTransports = folder+"/networkWithTransports.xml";
			String scheduleFile = folder+"/schedule.xml";
			String vehicleFile = folder+"/vehicle.xml";

			if(!gtfsFolder.endsWith(".xml")) {
				String sampleDayParam = GtfsConverter.DAY_WITH_MOST_TRIPS;
				Gtfs2TransitSchedule.run(gtfsFolder, sampleDayParam, coordinate, scheduleFile, vehicleFile);
			}else {
				scheduleFile = gtfsFolder;
			}
				
				
			
			
			runMappingStandard(scheduleFile, inputNetwork, scheduleFileWithTransports, networkWithTransports, coordinate, vehicleFile);


		}
	}
	
	
	//Configuration Details to run the simulation
	public static void simulation(
			String scenarioFolder, CoimbraQuestionario3 cq
			) {
		Config config = ConfigUtils.createConfig();

		
	
		//NETWORK
		config.network().setInputFile(scenarioFolder+"/networkWithTransports.xml");
		

		//PLANS
		config.plans().setInputFile(scenarioFolder+"/population.xml");

		//FACILITIES
		config.facilities().setInputFile(scenarioFolder+"/facilities.xml");
		config.facilities().setInputCRS("EPSG:20790");
		
		
		//TRANSIT
		config.transit().setTransitScheduleFile(scenarioFolder+"/scheduleWithTransports.xml");
		config.transit().setVehiclesFile(scenarioFolder+"/vehicle.xml");
		config.transit().setUseTransit(true);
		config.transit().setUsingTransitInMobsim(true);
		
//		config.transitRouter().setAdditionalTransferTime(900);
		
		
		
		
		//COUNTS
		config.counts().setAverageCountsOverIterations(0);
		config.counts().setInputCRS("EPSG:20790");
		config.counts().setWriteCountsInterval(1);
		config.counts().setFilterModes(true);
		
		config.transit().setTransitScheduleFile(scenarioFolder+"/scheduleWithTransports.xml");
		config.transit().setVehiclesFile(scenarioFolder+"/vehicle.xml");
		config.transit().setUseTransit(true);
		
//		config.jdeqSim().setCarSize(3.5);
//		config.jdeqSim().setMinimumInFlowCapacity(0);
	
		
		//SCORE
		//new_score = (1-learningRate)*old_score + learningRate * score_from_mobsim.  learning rates close to zero emulate score averaging, but slow down initial convergence
		config.planCalcScore().setLearningRate(1.0);
		//logit model scale parameter. default: 1.  Has name and default value for historical reasons (see Bryan Raney's phd thesis)
		config.planCalcScore().setBrainExpBeta(1.0);
		config.planCalcScore().setWriteExperiencedPlans(true);
		
	
		
		cq.defenirActtypeForPlanCalcScore(config.planCalcScore());
		
				
		//GLOBAL
		config.global().setCoordinateSystem("EPSG:20790");
		config.global().setNumberOfThreads(1);
		//Number of random seeds was selected based on CPU cores of the WS
		config.global().setRandomSeed(28);
	
		//LINKSTATS
		config.linkStats().setAverageLinkStatsOverIterations(1);
		config.linkStats().setWriteLinkStatsInterval(1);
		
		
		//HERMES: Communication framework
		config.hermes().setFlowCapacityFactor(1.2);//Try 1.2
		config.hermes().setStorageCapacityFactor(1.2);//try 1.2
		//time in seconds
		config.hermes().setStuckTime(10);
		config.hermes().setEndTime("24:00:00");
		
		
		
		//QSim
		config.qsim().setStartTime(0);
		config.qsim().setEndTime(86400);
		
		config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(true);
		config.qsim().setNumberOfThreads(3);

		config.qsim().setUseLanes(false);
		config.qsim().setVehicleBehavior(VehicleBehavior.wait);
		//change after
		config.qsim().setVehiclesSource(VehiclesSource.defaultVehicle);
		config.qsim().setUsePersonIdForMissingVehicleId(false);
		config.qsim().setSimEndtimeInterpretation(EndtimeInterpretation.onlyUseEndtime);
		config.qsim().setSimStarttimeInterpretation(StarttimeInterpretation.maxOfStarttimeAndEarliestActivityEnd);
		config.qsim().setSnapshotStyle(SnapshotStyle.queue);
		config.qsim().setStuckTime(10);
		
		
		config.qsim().setFlowCapFactor(1.0);
		config.qsim().setStorageCapFactor(1.0);
//		config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(true);
		config.qsim().setLinkDynamics(LinkDynamics.FIFO);
		config.qsim().setRemoveStuckVehicles(false);
		config.qsim().setTrafficDynamics(TrafficDynamics.queue);
//		config.qsim().setTimeStepSize(60);
//		config.qsim().setMainModes(Arrays.asList("car","pt"));
		
//		config.strategy().setFractionOfIterationsToDisableInnovation(0.8);
	
//		config.transitRouter().setAdditionalTransferTime(900);
		
		
		config.parallelEventHandling().setNumberOfThreads(3);
//		config.parallelEventHandling().setEventsQueueSize(300000);
		
		//200meters
		config.transitRouter().setMaxBeelineWalkConnectionDistance(200);
		
		config.plansCalcRoute().setAccessEgressType(AccessEgressType.accessEgressModeToLink);
		
		//STRATEGY
		config.strategy().setMaxAgentPlanMemorySize(5);
		StrategySettings x = new StrategySettings();
		//strategyName of strategy.  Possible default names: SelectRandom BestScore KeepLastSelected ChangeExpBeta SelectExpBeta SelectPathSizeLogit      (selectors), ReRouteTimeAllocationMutatorTimeAllocationMutator_ReRouteChangeSingleTripModeChangeTripModeSubtourModeChoice (innovative strategies).
		x.setStrategyName("BestScore");
		x.setWeight(1);
		
		
		config.strategy().addStrategySettings(x );
		
		Controler controler;
//		//DRT
//		if(isDRT) {
//			controler = EDrtControlerCreator.createControler(config, true);
//
//		}else {
		
		//SCENARIO
		Scenario scenario = ScenarioUtils.loadScenario(config);

		
		
			//CONTROLER
			controler = new Controler( scenario ) ;
//		}
		
		
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		config.controler().setOutputDirectory(scenarioFolder+"/outputs");
		config.controler().setFirstIteration(1);
		config.controler().setLastIteration(1);
		config.controler().setWriteEventsInterval(1);
		config.controler().setWritePlansInterval(1);
		config.controler().setWriteTripsInterval(1);

		config.controler().setMobsim("qsim");
		config.controler().setRoutingAlgorithmType(RoutingAlgorithmType.Dijkstra);
//		config.controler().setRunId("teste_1");
		
		
	
		
		//RUN
		ConfigWriter w = new ConfigWriter(config);
		w.write(scenarioFolder+"/config.xml");
		controler.run();
	}


	public static PlausibilityCheck runMappingStandard(
			String inputScheduleFile, 
			String inputNetworkFile, 
			String outputScheduleFile, 
			String outputNetworkFile,
			String coordSys,
			String vehiclesFile
			//			String shapeout
			) {
		// Load schedule and network
		TransitSchedule schedule = ScheduleTools.readTransitSchedule(inputScheduleFile);
		Vehicles vs = VehicleUtils.createVehiclesContainer();
		
		ScheduleTools.createVehicles(schedule, vs);
		if(vehiclesFile!=null) ScheduleTools.writeVehicles(vs, vehiclesFile);
		
		
		
		Network network = NetworkTools.readNetwork(inputNetworkFile);

		// create PTM config
		PublicTransitMappingConfigGroup config = PublicTransitMappingConfigGroup.createDefaultConfig();

		config.setNLinkThreshold(3);
		config.setMaxLinkCandidateDistance(600);
		//		config.setCandidateDistanceMultiplier(2.5);
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

		//
		if(outputNetworkFile!=null) NetworkTools.writeNetwork(network, outputNetworkFile);
		if(outputScheduleFile!=null)ScheduleTools.writeTransitSchedule(schedule, outputScheduleFile);

		PlausibilityCheck check = new PlausibilityCheck(schedule, network, coordSys);

		check.runCheck();
		check.printStatisticsLog();
		//		Set<PlausibilityWarning> artificialLinks = check.getWarnings().get(PlausibilityWarning.Type.ArtificialLinkWarning);
		//		
		//		for(PlausibilityWarning p : artificialLinks)
		//			System.out.println(p + "\t" + p.getExpected() + "\t" + p.getDifference());

		//		Schedule2ShapeFile.run(coordSys, base + "output/shp/", schedule, network);
		//
		//		// analyse result
		//		return runAnalysis(outputScheduleFile, outputNetworkFile);
		return check;
	}

	/**
	 * 
	 * outputGtfsShapeFile =  base + "output/shp/gtfs.shp" 
	 * @param inputScheduleFile
	 * @param gtfsShapeFile
	 * @param coordSys
	 * @param gtfsShapeFile
	 */
	public static void convertShapes(String inputScheduleFile, String gtfsShapeFile, String coordSys, String outputGtfsShapeFile) {
		TransitSchedule schedule = ScheduleTools.readTransitSchedule(inputScheduleFile);

		Map<Id<RouteShape>, RouteShape> shapes = ShapeTools.readShapesFile(gtfsShapeFile, coordSys);
		Map<Id<RouteShape>, RouteShape> shapesToConvert = new HashMap<>();


		for(TransitLine transitLine : schedule.getTransitLines().values()) {
			for(TransitRoute transitRoute : transitLine.getRoutes().values()) {
				Id<RouteShape> id = ScheduleTools.getShapeId(transitRoute);
				shapesToConvert.put(id, shapes.get(id));
			}
		}


		ShapeTools.writeESRIShapeFile(shapesToConvert.values(), coordSys, outputGtfsShapeFile);
	}
}
