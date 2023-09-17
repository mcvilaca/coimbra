package pt.mvilaca.matsimtests;

import java.util.EnumSet;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ScoringParameterSet;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.config.groups.ControlerConfigGroup.EventsFileFormat;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.StrategyManager;
import org.matsim.core.scenario.ScenarioUtils;

public class AveiroV1Example {

	public static void main(String[] args) {
		
		Config config = ConfigUtils.createConfig();
//		config.global().setNumberOfThreads(6);
		
		config.network().setInputFile("scenarios/aveiro_v1/network.xml");
		config.plans().setInputFile("scenarios/aveiro_v1/population.xml");
		config.facilities().setInputFile("scenarios/aveiro_v1/facilities.xml");
		config.planCalcScore().setLearningRate(1.0);
		config.planCalcScore().setBrainExpBeta(1.0);
		
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		config.controler().setOutputDirectory("scenarios/aveiro_v1/outputs");
		config.controler().setFirstIteration(1);
		config.controler().setLastIteration(100);
		config.controler().setWriteEventsInterval(10);
		
		
		
		ActivityParams homeScore = new ActivityParams("home");
		homeScore.setTypicalDuration(8*60*60);
		ActivityParams workScore = new ActivityParams("work");
		workScore.setTypicalDuration(12*60*60);
		
		
		
		config.strategy().setMaxAgentPlanMemorySize(5);
		StrategySettings x = new StrategySettings();
		x.setStrategyName("BestScore");
		x.setWeight(1);
		
		config.strategy().addStrategySettings(x );
		
		config.planCalcScore().addActivityParams(homeScore);
		config.planCalcScore().addActivityParams(workScore);

		
//		PlanCalcScoreConfigGroup
//		GroupS
		
//		ScoringParameterSet scoring = 
				
				homeScore = config.planCalcScore().getActivityParams("home");
				System.out.println(homeScore.getActivityType());
				System.out.println(homeScore.setScoringThisActivityAtAll(true));
				
				workScore = config.planCalcScore().getActivityParams("work");
		
				workScore.setScoringThisActivityAtAll(true);
				
//		scoring.getOrCreateActivityParams("home");
//		scoring.getOrCreateActivityParams("work");
		
		
//		ScoringParameterSet scoreWork = config.planCalcScore().getOrCreateScoringParameters("work");

		
//		config.planCalcScore().addParameterSet(ConfigGroup.)
		
//		config.controler().setDumpDataAtEnd(true);
//		config.controler().setEventsFileFormats(EnumSet.of(EventsFileFormat.xml));
		
//		config.counts().setInputFile("countFile");
		
		System.out.println(config.getModules().remove("JDEQSim"));
		System.out.println(config.getModules().keySet());
		
		System.out.println(config.counts().getCountsFileName());
		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		
		// possibly modify scenario here
		
		// ---
		
		Controler controler = new Controler( scenario ) ;
		// possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		
		// ---
		
		ConfigWriter w = new ConfigWriter(config);
		w.write("scenarios/aveiro_v1/config.xml");
		controler.run();
	}
}
