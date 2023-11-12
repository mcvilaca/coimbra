package pt.mvilaca.matsimtests.population;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.ActivityOption;

import com.google.common.annotations.VisibleForTesting;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario.MotivoViagem;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario.QuestionarioIndividual;

public class CoimbraQuestionarioTest {

	@Test
	public void readCoimbra_transpMetric() throws IOException, ParseException {
		CoimbraQuestionario3.readCoimbraTSV(new File("data/population/coimbra_transpMetric.tsv"));
	}
	
	@Test
	public void readCoimbra() throws IOException, ParseException {
		CoimbraQuestionario.readCoimbraTSV(new File("data/population/coimbra.tsv"));
	}
	
	
//	@Test
//	public void readCoimbra() throws IOException, ParseException {
//		CoimbraQuestionario.readCoimbraTSV(new File("data/population/coimbra.tsv"));
//	}
}
