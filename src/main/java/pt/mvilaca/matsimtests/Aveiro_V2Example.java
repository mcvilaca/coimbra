package pt.mvilaca.matsimtests;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

public class Aveiro_V2Example {

	public static void main(String[] args) {
		
		Config config = ConfigUtils.createConfig();
		config.network().setInputFile("scenarios/aveiro_v2/network.xml");
		config.plans().setInputFile("scenarios/aveiro_v2/population.xml");
		
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		config.controler().setOutputDirectory("scenarios/aveiro_v2/outputs");
		
		config.counts().setInputFile("countFile");
		System.out.println(config.counts().getCountsFileName());
		
		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		// possibly modify scenario here
		
		// ---
		
		Controler controler = new Controler( scenario ) ;
//		controler.getEvents().
		// possibly modify controler here

		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		
		// ---
		
		controler.run();
	}
}
