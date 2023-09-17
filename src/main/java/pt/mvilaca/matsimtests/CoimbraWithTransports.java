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
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;
import org.matsim.vis.otfvis.OTFVisConfigGroup.ColoringScheme;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario;

public class CoimbraWithTransports {
public static void main(String[] args) throws IOException, ParseException {
		
	
		OTFVisConfigGroup configOFV = new  OTFVisConfigGroup();
		configOFV.setDrawLinkIds(false);
		configOFV.setDrawNonMovingItems(false);
		configOFV.setDrawOverlays(true);
		configOFV.setDrawTransitFacilityIds(false);
		configOFV.setDrawTransitFacilities(false);
//		configOFV.setDrawTime(false);
		
//		ColoringScheme cs = new ColoringScheme.;
		configOFV.setColoringScheme(ColoringScheme.bvg2 );
		
		Config config = ConfigUtils.createConfig(configOFV);
//		config.global().setNumberOfThreads(6);
		
		File f = Paths.get("data", "population", "test_coimbra.tsv").toFile();
		CoimbraQuestionario cq = CoimbraQuestionario.readCoimbraTSV(f);
		
//		config.network().setInputFile("scenarios/coimbra/network.xml");
//		config.network().setInputFile("scenarios/coimbra/networkWithTransports.xml");
//		config.network().setInputFile("scenarios/coimbra/networkWithTransports-debug1.xml"); - last used
		config.network().setInputFile("scenarios/coimbra/networkWithTransports_MV.xml");
		
		
		
		
		config.plans().setInputFile("scenarios/coimbra/population.xml");
		config.facilities().setInputFile("scenarios/coimbra/facilities.xml");
//		config.transit().setTransitScheduleFile("scenarios/coimbra/schedule.xml");
		
//		config.transit().setTransitScheduleFile("scenarios/coimbra/scheduleWithTransports.xml");
//		config.transit().setTransitScheduleFile("scenarios/coimbra/scheduleWithTransports-debug1.xml"); - Last used
		config.transit().setTransitScheduleFile("scenarios/coimbra/scheduleWithTransports_MV.xml");
		
		
		config.transit().setUseTransit(true);
		config.transit().setVehiclesFile("scenarios/coimbra/vehicle.xml");
		
//		config.vehicles().setVehiclesFile("scenarios/coimbra/vehicle.xml");
		
		
		config.planCalcScore().setLearningRate(1.0);
		config.planCalcScore().setBrainExpBeta(1.0);
		cq.defenirActtypeForPlanCalcScore(config.planCalcScore());
		
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		config.controler().setOutputDirectory("scenarios/coimbra/outputsWithTransports_MV");
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

		
//		for(Link link: scenario.getNetwork().getLinks().values()) {
//			
//			Set<String> allowedModes = new HashSet<>(link.getAllowedModes());
//			allowedModes.add("Bus");
//			link.setAllowedModes(allowedModes);
//		}
		
		
		// possibly modify scenario here
		
		// ---
		
		Controler controler = new Controler( scenario ) ;
		// possibly modify controler here

		//Para abrir o simulador
		OTFVisLiveModule vismodule = new OTFVisLiveModule();
		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		
		// ---
		
		ConfigWriter w = new ConfigWriter(config);
		w.write("scenarios/coimbra/config.xml");
		controler.run();
	}
}


