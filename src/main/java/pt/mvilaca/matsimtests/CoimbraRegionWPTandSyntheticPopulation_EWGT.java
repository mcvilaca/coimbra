package pt.mvilaca.matsimtests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;

import org.matsim.api.core.v01.Scenario;
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
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamicsCorrectionApproach;
import org.matsim.core.config.groups.QSimConfigGroup.VehicleBehavior;
import org.matsim.core.config.groups.QSimConfigGroup.VehiclesSource;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.mobsim.hermes.HermesConfigGroup;
import org.matsim.core.scenario.ScenarioUtils;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario;

public class CoimbraRegionWPTandSyntheticPopulation_EWGT {
public static void main(String[] args) throws IOException, ParseException {
		
		Config config = ConfigUtils.createConfig();

		
		
		File f = Paths.get("data", "population", "Filtro2", "SyntheticPopulationCoimbra_EWGT.tsv").toFile();
		CoimbraQuestionario cq = CoimbraQuestionario.readCoimbraTSV(f);
		
	
		//NETWORK
		config.network().setInputFile("scenarios/coimbra_ewgt_test/networkWithTransports_MV.xml");
		
		

		//PLANS
		config.plans().setInputFile("scenarios/coimbra_ewgt_test/population_filtro4.xml");

		//FACILITIES
		config.facilities().setInputFile("scenarios/coimbra_ewgt_test/facilities.xml");
		config.facilities().setInputCRS("EPSG:20790");
		
		//COUNTS
		config.counts().setAverageCountsOverIterations(0);
		config.counts().setFilterModes(true);
		config.counts().setInputCRS("EPSG:20790");
		config.counts().setWriteCountsInterval(1);
		
		//SCORE
		//new_score = (1-learningRate)*old_score + learningRate * score_from_mobsim.  learning rates close to zero emulate score averaging, but slow down initial convergence
		config.planCalcScore().setLearningRate(1.0);
		//logit model scale parameter. default: 1.  Has name and default value for historical reasons (see Bryan Raney's phd thesis)
		config.planCalcScore().setBrainExpBeta(1.0);
		
	
		
		cq.defenirActtypeForPlanCalcScore(config.planCalcScore());
				
		//GLOBAL
		config.global().setCoordinateSystem("EPSG:20790");
		config.global().setNumberOfThreads(1);
		//Number of random seeds was selected based on CPU cores of the WS
		config.global().setRandomSeed(28);
	
		
		
		//HERMES: Communication framework
		config.hermes().setFlowCapacityFactor(1.2);//Try 1.2
		config.hermes().setStorageCapacityFactor(1.2);//try 1.2
		//time in seconds
		config.hermes().setStuckTime(900);
		
		
		
		//QSim
		config.qsim().setStartTime(0);
		config.qsim().setEndTime(86400);
		
		config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(true);
		
		//to use lanes i have to allow the 
		config.qsim().setUseLanes(false);
		config.qsim().setVehicleBehavior(VehicleBehavior.wait);
		//change after
		config.qsim().setVehiclesSource(VehiclesSource.defaultVehicle);
		config.qsim().setUsePersonIdForMissingVehicleId(false);
		config.qsim().setSimEndtimeInterpretation(EndtimeInterpretation.onlyUseEndtime);
		config.qsim().setSimStarttimeInterpretation(StarttimeInterpretation.onlyUseStarttime);
		config.qsim().setSnapshotStyle(SnapshotStyle.queue);
		config.qsim().setStuckTime(900);
		
		config.qsim().setUseLanes(false);
		
		config.qsim().setFlowCapFactor(1.0);
		config.qsim().setStorageCapFactor(1.0);
		config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(true);
		config.qsim().setLinkDynamics(LinkDynamics.FIFO);
		config.qsim().setRemoveStuckVehicles(false);
		config.qsim().setTrafficDynamics(TrafficDynamics.queue);
//		config.qsim().setTimeStepSize(60);
//		config.qsim().setMainModes(Arrays.asList("car","pt"));
		
		
	
		
		
		
		
		config.parallelEventHandling().setNumberOfThreads(1);
		
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
		
		//DRT

		
		//SCENARIO
		Scenario scenario = ScenarioUtils.loadScenario(config);

		
		
		//CONTROLER
		Controler controler = new Controler( scenario ) ;
		
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		config.controler().setOutputDirectory("scenarios/coimbra_ewgt_test/outputs_tst8");
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
		w.write("scenarios/coimbra_ewgt_test/config.xml");
		controler.run();
	}
}
