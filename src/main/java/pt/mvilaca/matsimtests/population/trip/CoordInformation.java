package pt.mvilaca.matsimtests.population.trip;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.matsim.api.core.v01.Coord;

public class CoordInformation {
	
	Coord coord;
	int zone;
	Map<String, Integer> types;
	Set<Integer> persons;
	
	
	public CoordInformation(Coord coord, int zone) {
		this.coord = coord;
		this.zone = zone;
		this.types = new HashMap<>();
		persons = new HashSet<Integer>();
	}
	
	public Coord getCoord() {
		return coord;
	}
	
	public Set<Integer> getPersons() {
		return persons;
	}
	
	public Map<String, Integer> getTypes() {
		return types;
	}
	
	public int getZone() {
		return zone;
	}

	public void addPerson(int personId) {
		persons.add(personId);
	}
	
	public void addType(String type) {
		Integer i = types.get(type);
		if(i == null) i=0;
		i++;
		types.put(type, i);
	}

	public void merge(CoordInformation coordI) {
		
		persons.addAll(coordI.getPersons());
		for(Entry<String, Integer> e: coordI.getTypes().entrySet()) {
			Integer i = types.get(e.getKey());
			
			if(i == null) i =0;
			i+= e.getValue();
			
			types.put(e.getKey(), i);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(coord, zone);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoordInformation other = (CoordInformation) obj;
		return Objects.equals(coord, other.coord) && zone == other.zone;
	}

	@Override
	public String toString() {
		return "CoordInformation [coord=" + coord + ", zone=" + zone + ", types=" + types + ", persons=" + persons + "]";
	}
	
	
	public void treatInfo() {
		
		Integer homes = types.getOrDefault(TripsPlan.TYPE_COORD_HOME,0);
		Integer restaurant = types.getOrDefault(TripsPlan.TYPE_COORD_RESTAURANT,0);
		
//		Integer escort =  types.remove(TripsPlan.TYPE_COORD_ESCORT);
		
		if(homes > 0 && restaurant >0) types.remove(TripsPlan.TYPE_COORD_RESTAURANT);
		
		
	}
	
}
