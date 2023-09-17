package pt.mvilaca.matsimtests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario;

public class Coimbra {
public static void main(String[] args) throws IOException, ParseException {
		
		Config config = ConfigUtils.createConfig();
//		config.global().setNumberOfThreads(6);
		
		File f = Paths.get("data", "population", "test_coimbra.tsv").toFile();
		CoimbraQuestionario cq = CoimbraQuestionario.readCoimbraTSV(f);
		
		config.network().setInputFile("scenarios/coimbra/network.xml");
		config.plans().setInputFile("scenarios/coimbra/population.xml");
		config.facilities().setInputFile("scenarios/coimbra/facilities.xml");
		config.planCalcScore().setLearningRate(1.0);
		config.planCalcScore().setBrainExpBeta(1.0);
		cq.defenirActtypeForPlanCalcScore(config.planCalcScore());
		
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		config.controler().setOutputDirectory("scenarios/coimbra/outputs");
		config.controler().setFirstIteration(1);
		config.controler().setLastIteration(1);
		config.controler().setWriteEventsInterval(10);
		
		
		
		
		
		
		
		config.strategy().setMaxAgentPlanMemorySize(5);
		StrategySettings x = new StrategySettings();
		x.setStrategyName("BestScore");
		x.setWeight(1);
		
		config.strategy().addStrategySettings(x );
		
		

		

//		homeScore.setScoringThisActivityAtAll(true);
//		workScore.setScoringThisActivityAtAll(true);
				


		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		
		// possibly modify scenario here
		
		// ---
		
		Controler controler = new Controler( scenario ) ;
		// possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		
		// ---
		
		ConfigWriter w = new ConfigWriter(config);
		w.write("scenarios/coimbra/config.xml");
		controler.run();
	}
}


