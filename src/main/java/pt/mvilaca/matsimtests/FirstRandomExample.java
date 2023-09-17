package pt.mvilaca.matsimtests;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

public class FirstRandomExample {
	//"C:\Users\maria\eclipse-workspace\matsim-example-project\scenarios\equil\config.xml"
	public static void main (String[] args) {
//		Config config = new Config();
//		config.network();
		
		Config equilConfig = ConfigUtils.loadConfig("C:\\Users\\maria\\eclipse-workspace\\matsim-example-project\\scenarios\\equil\\config.xml");
	
		equilConfig.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		equilConfig.controler().setOutputDirectory("scenarios/FirstRandomExample/outputs");
//		TreeMap<String, ConfigGroup> var = equilConfig.getModules();
//		for(String id : var.keySet()) {
//			System.out.println(id);
//			ConfigGroup value = var.get(id);
//			System.out.println(value.getComments());
//			System.out.println(value.getParameterSets());
//		}
		
//		NetworkConfigGroup network = equilConfig.network();
//		equilConfig.vehicles();
//		equilConfig.plans();
		
		Scenario scenario = ScenarioUtils.loadScenario(equilConfig) ;

		// possibly modify scenario here
		
		// ---
		
		Controler controler = new Controler( scenario ) ;
		// possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		
		// ---
		
		controler.run();
	}

}
