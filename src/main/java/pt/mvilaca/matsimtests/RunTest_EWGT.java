package pt.mvilaca.matsimtests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType;
import org.matsim.core.config.groups.QSimConfigGroup.VehicleBehavior;
import org.matsim.core.config.groups.QSimConfigGroup.VehiclesSource;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.mobsim.hermes.HermesConfigGroup;
import org.matsim.core.scenario.ScenarioUtils;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario;

public class RunTest_EWGT {
public static void final String COIMBR_EWGT_CONFIG = "scenarios/coimbra_ewgt_test/config.xml";
		
		Config config = ConfigUtils.createConfig();

		
		
		File f = Paths.get("data", "population", "Filtro2", "SyntheticPopulationCoimbra_EWGT.tsv").toFile();
		CoimbraQuestionario cq = CoimbraQuestionario.readCoimbraTSV(f);
		
	
		//NETWORK
		config.network().setInputFile("scenarios/coimbra_ewgt/networkWithTransports_MV.xml");
		
		

		//PLANS
		config.plans().setInputFile("scenarios/coimbra_ewgt/population.xml");

		//FACILITIES
		config.facilities().setInputFile("scenarios/coimbra_ewgt/facilities.xml");
		config.facilities().setInputCRS("EPSG:20790");
		
		//SCORE
		//new_score = (1-learningRate)*old_score + learningRate * score_from_mobsim.  learning rates close to zero emulate score averaging, but slow down initial convergence
		config.planCalcScore().setLearningRate(1.0);
		//logit model scale parameter. default: 1.  Has name and default value for historical reasons (see Bryan Raney's phd thesis)
		config.planCalcScore().setBrainExpBeta(1.0);
	
		
		cq.defenirActtypeForPlanCalcScore(config.planCalcScore());
				
		//GLOBAL
		config.global().setCoordinateSystem("EPSG:20790");
		config.global().setNumberOfThreads(2);
		//Number of random seeds was selected based on CPU cores of the WS
		config.global().setRandomSeed(28);
	
		
		
		//HERMES: Communication framework
		config.hermes().setFlowCapacityFactor(1);//Try 1.2
		config.hermes().setStorageCapacityFactor(1);//try 1.2
		//time in seconds
		config.hermes().setStuckTime(10);
		
		//QSim
		config.qsim().setEndTime(86400);
		config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(false);
		//to use lanes i have to allow the 
		config.qsim().setUseLanes(false);
		config.qsim().setVehicleBehavior(VehicleBehavior.wait);
		//change after
		config.qsim().setVehiclesSource(VehiclesSource.defaultVehicle);
				
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
		config.controler().setOutputDirectory("scenarios/coimbra_ewgt/outputs_tst4");
		config.controler().setFirstIteration(1);
		config.controler().setLastIteration(10);
		config.controler().setWriteEventsInterval(10);
		config.controler().setWritePlansInterval(10);

		config.controler().setMobsim("qsim");
		config.controler().setRoutingAlgorithmType(RoutingAlgorithmType.Dijkstra);
//		config.controler().setRunId("teste_1");
		
		
		
		//RUN
		ConfigWriter w = new ConfigWriter(config);
		w.write("scenarios/coimbra_ewgt/config.xml");
		controler.run();
	}
}
