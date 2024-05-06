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
import org.matsim.contrib.drt.run.DrtControlerCreator;
import org.matsim.contrib.drt.run.DrtModeModule;
import org.matsim.contrib.drt.run.MultiModeDrtConfigGroup;
import org.matsim.contrib.dvrp.fleet.DvrpVehicleLookup.VehicleAndMode;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.otfvis.OTFVis;
import org.matsim.contrib.otfvis.RunOTFVis;
import org.matsim.core.config.Config;
	import org.matsim.core.config.ConfigUtils;
	import org.matsim.core.config.ConfigWriter;
	import org.matsim.core.config.groups.ChangeModeConfigGroup;
	import org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType;
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
import org.matsim.vis.otfvis.OTFVisConfigGroup;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario;

/*Simulation of the drt scenario Input and alteration done directly in the config.xml file Note: adjust the input files:network;population;vehicles and the output directories*/

	public class TestDrtCreator {
		private static final String COIMBRA_DOOR2DOOR_CONFIG ="scenarios/DRTStructure/RegionalDRT/config.xml";
		
//		private static final double MAX_CAPACITY = 4;
		
		public static void run(Config config, boolean otfvis) {
			//Creates a MATSim Controler and preloads all DRT related packages
			Controler controler = DrtControlerCreator.createControler(config, otfvis);
			
			// Add the vehicle capacity constraint
	        PlanCalcScoreConfigGroup.ModeParams drtModeParams = new PlanCalcScoreConfigGroup.ModeParams(TransportMode.drt);
	        drtModeParams.setConstant(0.0);
	        drtModeParams.setMarginalUtilityOfTraveling(-1.0);
	        drtModeParams.setMonetaryDistanceRate(-1.0);
	        drtModeParams.setMarginalUtilityOfDistance(-1.0);
	        drtModeParams.setMarginalUtilityOfTraveling(-1.0);
//	        drtModeParams.setConstant(-MAX_CAPACITY);
	        config.planCalcScore().addModeParams(drtModeParams);
	    
	        
	        
     		config.controler().setFirstIteration(0);
     		config.controler().setLastIteration(0);
     		
     		
     		

			//starts the simulation
			controler.run();
		}

		public static void main(String[] args) {
			Config config = ConfigUtils.loadConfig(COIMBRA_DOOR2DOOR_CONFIG, new MultiModeDrtConfigGroup(),
					new DvrpConfigGroup(), new OTFVisConfigGroup());
			run(config, false);
		}
	}
		
