package DRT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.DateFormat;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.drt.optimizer.insertion.DrtInsertionSearchParams;
import org.matsim.contrib.drt.optimizer.insertion.ExtensiveInsertionSearchParams;
import org.matsim.contrib.drt.optimizer.rebalancing.RebalancingParams;
import org.matsim.contrib.drt.optimizer.rebalancing.mincostflow.MinCostFlowRebalancingStrategyParams;
import org.matsim.contrib.drt.run.DrtConfigGroup;
import org.matsim.contrib.drt.run.DrtConfigGroup.OperationalScheme;
import org.matsim.contrib.drt.run.DrtConfigs;
import org.matsim.contrib.drt.run.DrtModeModule;
import org.matsim.contrib.dvrp.fleet.DvrpVehicleLookup.VehicleAndMode;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.ChangeModeConfigGroup;
import org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup.EndtimeInterpretation;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.QSimConfigGroup.StarttimeInterpretation;
import org.matsim.core.config.groups.QSimConfigGroup.VehicleBehavior;
import org.matsim.core.config.groups.QSimConfigGroup.VehiclesSource;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.mobsim.hermes.HermesConfigGroup;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleUtils;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario.MotivoViagem;


// ESTA CLASS NÃO ESTA A FUNCIONAR COMO GOSTARIA - SUBSTITUIDA POR TestDrtCreator

public class DrtCreator {
	
//	private static final DrtInsertionSearchParams DrtInsertionSearchParams = null;
//	private static final double MAX_CAPACITY = 4;

	public static void main(String[] args) throws IOException, ParseException {
		// Create the MATSim config
		Config config = ConfigUtils.createConfig();

		// Add DRT module
		DrtConfigGroup drtConfigGroup = ConfigUtils.addOrGetModule(config, DrtConfigGroup.class);
		Scenario scenario = ScenarioUtils.createScenario(config);
//		config.addModule(drtConfigGroup);
		
//		// Add the DrtFareModule to the multiModeDrt module
//	    config.getModule("multiModeDrt").addParam("fareModule", DrtModeModule.class.getName());
//
//	    // Set the parameters for the DrtFareModule
//	    config.getModule("multiModeDrt").addParam("drt", "true");

		// Set DRT Configurations
		drtConfigGroup.setVehiclesFile("scenarios/coimbra_ewgt_drt/vehicle.xml");
		
		drtConfigGroup.setOperationalScheme(OperationalScheme.door2door);
//		drtConfigGroup.setTransitStopFile("scenarios/coimbra_ewgt_drt/drtstops.xml");
//		drtConfigGroup.setDrtServiceAreaShapeFile("scenarios/coimbra_ewgt_drt/shape.xml");
		drtConfigGroup.setChangeStartLinkToLastLinkInSchedule(false);
		

		// Set DRT behavior and constraints
		drtConfigGroup.setMaxTravelTimeAlpha(1.5);
		drtConfigGroup.setMaxTravelTimeBeta(400);
		drtConfigGroup.setMaxWaitTime(1200);
		drtConfigGroup.setRejectRequestIfMaxWaitOrTravelTimeViolated(false);

		drtConfigGroup.setStopDuration(60);
		drtConfigGroup.setPlotDetailedCustomerStats(true);
		drtConfigGroup.setNumberOfThreads(1);
		
		drtConfigGroup.setMaxTravelTimeAlpha(1.0);
		drtConfigGroup.setMaxTravelTimeBeta(1.0);
		
		//new
		drtConfigGroup.getDrtFareParams();	
		drtConfigGroup.setPlotDetailedCustomerStats(true);
		drtConfigGroup.getZonalSystemParams();
		drtConfigGroup.getRebalancingParams();
		drtConfigGroup.getDrtFareParams();
		
		//DvrpConfigGroup.
		
		// NETWORK (sem transportes)
		config.network().setInputFile("scenarios/coimbra_ewgt_drt/network.xml");

		// PLANS
		config.plans().setInputFile("scenarios/coimbra_ewgt_drt/population_debug.xml");

		// FACILITIES
		config.facilities().setInputFile("scenarios/coimbra_ewgt_drt/facilities.xml");
//		config.facilities().setInputCRS("ATLANTIS");//"EPSG:20790"

		
//		config.plansCalcRoute().setNetworkModes("drt");


	
		
		DrtInsertionSearchParams insertionSearchParams = new ExtensiveInsertionSearchParams();
		// Set parameters of the insertion search params if needed
		
		drtConfigGroup.addDrtInsertionSearchParams(insertionSearchParams);
	
		// Set QSim configurations
        config.qsim().setStartTime(0);
        config.qsim().setEndTime(86400);
        config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(true);
        config.qsim().setUseLanes(false);
        config.qsim().setVehicleBehavior(VehicleBehavior.wait);
        config.qsim().setVehiclesSource(VehiclesSource.fromVehiclesData); // Set vehicles source to DRT
		config.qsim().setSimEndtimeInterpretation(EndtimeInterpretation.onlyUseEndtime);
		config.qsim().setSimStarttimeInterpretation(StarttimeInterpretation.onlyUseStarttime);
        config.qsim().setSnapshotStyle(SnapshotStyle.queue);
		
		
//		// Create the vehicle agent
//		int MAX_CAPACITY = 4;
//        VehicleData vehicle = new VehicleData(MAX_CAPACITY, null, null);
       
        
        
        
		// Add the vehicle capacity constraint
        PlanCalcScoreConfigGroup.ModeParams drtModeParams = new PlanCalcScoreConfigGroup.ModeParams(TransportMode.drt);
        drtModeParams.setConstant(0.0);
        drtModeParams.setMarginalUtilityOfTraveling(-1.0);
        drtModeParams.setMonetaryDistanceRate(-1.0);
        drtModeParams.setMarginalUtilityOfDistance(-1.0);
        drtModeParams.setMarginalUtilityOfTraveling(-1.0);
//        drtModeParams.setConstant(-MAX_CAPACITY);
        config.planCalcScore().addModeParams(drtModeParams);
        
		
		
		
		//PlanCalcScore
		config.planCalcScore().setLearningRate(1.0);
		//logit model scale parameter. default: 1.  Has name and default value for historical reasons (see Bryan Raney's phd thesis)
		config.planCalcScore().setBrainExpBeta(1.0);
		
//		File f = Paths.get("data", "population", "Filtro2", "SyntheticPopulationCoimbra_EWGT.tsv").toFile();
//		CoimbraQuestionario cq = CoimbraQuestionario.readCoimbraTSV(f);
//		
//		cq.defenirActtypeForPlanCalcScore(config.planCalcScore());
//		PlanCalcScoreConfigGroup.ActivityParams
		
		//new
		PlanCalcScoreConfigGroup planCalcScoreConfigGroup = new PlanCalcScoreConfigGroup();
		planCalcScoreConfigGroup.getActivityParams();
		
		// GLOBAL
		config.global().setCoordinateSystem("EPSG:20790");
		config.global().setNumberOfThreads(2);
		//Number of random seeds was selected based on CPU cores of the WS
		config.global().setRandomSeed(28);
		
		//HERMES: Communication framework
				config.hermes().setFlowCapacityFactor(1);//Try 1.2
				config.hermes().setStorageCapacityFactor(1);//try 1.2
				//time in seconds
				config.hermes().setStuckTime(10);
		
		//strategyName of strategy.  Possible default names: SelectRandom BestScore KeepLastSelected ChangeExpBeta SelectExpBeta SelectPathSizeLogit      (selectors), ReRouteTimeAllocationMutatorTimeAllocationMutator_ReRouteChangeSingleTripModeChangeTripModeSubtourModeChoice (innovative strategies).
			
        config.strategy().setMaxAgentPlanMemorySize(5);
        StrategySettings y = new StrategySettings();
        y.setStrategyName("BestScore");
        y.setWeight(1);
        
      
        config.changeMode().setIgnoreCarAvailability(false);
     
        

            
  
        config.strategy().addStrategySettings(y);
        config.global().setNumberOfThreads(2);
       

     // Create the controler
     		Controler controler = new Controler(scenario);
     		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
     		config.controler().setOutputDirectory("scenarios/coimbra_ewgt_drt/outputs_DrtCreator");
     		config.controler().setFirstIteration(1);
     		config.controler().setLastIteration(2);
     		config.controler().setWriteEventsInterval(1);
     		config.controler().setWritePlansInterval(1);

     		config.controler().setMobsim("qsim");
     		config.controler().setRoutingAlgorithmType(RoutingAlgorithmType.Dijkstra);
     		
     		config.controler().setLinkToLinkRoutingEnabled(false);

        // Run the simulation
        ConfigWriter w = new ConfigWriter(config);
		w.write("scenarios/coimbra_ewgt_drt/config_DrtCreator.xml");
        controler.run();
    }
}
