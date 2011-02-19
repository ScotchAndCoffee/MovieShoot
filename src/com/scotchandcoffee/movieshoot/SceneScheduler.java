package com.scotchandcoffee.movieshoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SceneScheduler {

	private int numberOfShootingDays;
	private Set<Scene> scenes;
	private int minimalCost = -1;
	private List<Day> minimalSchedule;
	private Map<String,List<Day>> subOptima = new HashMap<String, List<Day>>();
	private HashMap<Day, HashSet<Day>> rejectedBasedOnDay = new HashMap<Day, HashSet<Day>>();
	
	public List<Day> getMinimalSchedule() {
		return minimalSchedule;
	}

	public void setMinimalSchedule(List<Day> minimalSchedule) {
		this.minimalSchedule = minimalSchedule;
	}

	public void setMinimalCost(int minimalCost) {
		this.minimalCost = minimalCost;
	}

	public SceneScheduler(Set<Scene> scenes, int numberOfShootingDays) {
		if(numberOfShootingDays < 1 || scenes.isEmpty()){
			throw new SchedulingException("Please provide number of shooting days and number of scenes larger 0");
		}
		if(numberOfShootingDays * 5 < scenes.size()){
			throw new SchedulingException("Not enough shooting days to schedule your scenes");
		}
		
		this.numberOfShootingDays = numberOfShootingDays;
		
		// Scenes without cost to simplify the computation
		while(scenes.size() !=  numberOfShootingDays * 5)
			scenes.add(new Scene(scenes.size(), new Actor(-1, "empty", 0)));
		
		this.scenes = scenes;
	}

	public int getMinimalCost() {
		return minimalCost;
	}

	public int getNumberOfShootingDays() {
		return numberOfShootingDays;
	}

	public void setNumberOfShootingDays(int numberOfShootingDays) {
		this.numberOfShootingDays = numberOfShootingDays;
	}

	public Set<Scene> getScenes() {
		return scenes;
	}

	public void setScenes(Set<Scene> scenes) {
		this.scenes = scenes;
	}

	public void computeOptimalSchedule(){
		
		CombinationGenerator gen = new CombinationGenerator(this.scenes.size(), 5);
		ArrayList<Day> possibleDays = new ArrayList<Day>();
		
		while(gen.hasMore()){
			int[] indices = gen.getNext();
			Scene[] forDay = new Scene[indices.length];
			for(int i = 0; i<indices.length;i++){
				forDay[i] = this.scenes.toArray(new Scene[0])[indices[i]];
			}
			possibleDays.add(new Day(forDay));
		}
		
		
		rejectedBasedOnDay = new HashMap<Day, HashSet<Day>>();
		int counter = 0;
		for(Day d : possibleDays){
			HashSet<Day> gonerSet = new HashSet<Day>();
			for(Day goner : possibleDays){
				if(goner.matches(d)){
					gonerSet.add(goner);
				}
			}
			rejectedBasedOnDay.put(d, gonerSet);
			System.out.println(counter);
			counter++;
		}
		
		Schedule optimalSchedule = new Schedule(numberOfShootingDays, scenes, possibleDays, this);
		optimalSchedule.computeOptimum();
		
		System.out.println(minimalCost);
		printSchedule();
	}

	public HashMap<Day, HashSet<Day>> getRejectedBasedOnDay() {
		return rejectedBasedOnDay;
	}

	public static void main(String[] args) {

		// Instantiate actors, ugly hard coded data for simplicity
		Map<Integer,Actor> actors = new HashMap<Integer,Actor>();

		actors.put(0, new Actor(0, "Spring", 3303));
		actors.put(1, new Actor(1, "Murphy", 4085));
		actors.put(2, new Actor(2, "McDougal", 5788));
		actors.put(3, new Actor(3, "Mercer", 7423));
		actors.put(4, new Actor(4, "Brown", 7562));
		actors.put(5, new Actor(5, "Anderson", 8770));
		actors.put(6, new Actor(6, "Hacket", 9381));
		actors.put(7, new Actor(7, "Thompson", 9593));
		actors.put(8, new Actor(8, "Casta", 25043));
		actors.put(9, new Actor(9, "Patt", 26481));
		actors.put(10, new Actor(10, "Scolaro", 30310));
		
		// Some input diagnostics
		for(Actor a: actors.values()){
			System.out.format("%02d is payed %5d, name is " + a.getName() + "%n",  a.getActorId(), a.getDailyCost());
		}
		
		// Instantiate actors, ugly hard coded data for simplicity
		Set<Scene> scenes = new HashSet<Scene>();
		
		scenes.add(new Scene(0, actors.get(6))); // Hacket
		scenes.add(new Scene(1, actors.get(1), actors.get(4), actors.get(6), actors.get(9))); // Murphy, Brown, Hacket, Patt
		scenes.add(new Scene(2, actors.get(1), actors.get(2), actors.get(3), actors.get(4), actors.get(10))); // McDougal, Mercer,Brown, Scolaro
		scenes.add(new Scene(3, actors.get(3), actors.get(8))); // Mercer, Casta
		scenes.add(new Scene(4, actors.get(0), actors.get(2), actors.get(3), actors.get(5), actors.get(9))); //Spring, McDougal, Mercer, Anderson, Patt
		scenes.add(new Scene(5, actors.get(0), actors.get(2), actors.get(5), actors.get(7), actors.get(10))); // Spring, McDougal, Anderson, Thompson, Scolaro
		scenes.add(new Scene(6, actors.get(8), actors.get(9))); // Casta, Patt
		scenes.add(new Scene(7, actors.get(1), actors.get(3))); // Murphy, Mercer 
		scenes.add(new Scene(8, actors.get(2), actors.get(3), actors.get(7), actors.get(8), actors.get(10))); // McDougal, Mercer, Thompson, Casta, Scolaro
		scenes.add(new Scene(9, actors.get(2), actors.get(8), actors.get(9), actors.get(10))); // McDougal, Casta, Patt, Scolaro
		scenes.add(new Scene(10, actors.get(9))); // Patt
		scenes.add(new Scene(11, actors.get(1), actors.get(2), actors.get(4), actors.get(6), actors.get(7))); // Murphy, McDougal, Brown, Hacket, Thompson
		scenes.add(new Scene(12, actors.get(1), actors.get(6), actors.get(8), actors.get(9))); // Murphy, Hacket, Casta, Patt
		scenes.add(new Scene(13, actors.get(5), actors.get(10))); // Anderson, Scolaro
		scenes.add(new Scene(14, actors.get(1), actors.get(2), actors.get(7), actors.get(9))); // Murphy, McDougal, Thompson, Patt
		scenes.add(new Scene(15, actors.get(2), actors.get(3), actors.get(8), actors.get(10))); // McDougal ,Mercer, Casta, Scolaro
		scenes.add(new Scene(16, actors.get(4), actors.get(9), actors.get(10))); // Brown, Patt, Scolaro
		scenes.add(new Scene(17, actors.get(2), actors.get(6), actors.get(7), actors.get(10))); // McDougal, Hacket, Thompson, Scolaro
		scenes.add(new Scene(18, actors.get(8))); // Casta

		
		
		//int[] test = {0, 15, 17, 18, 19, 2, 1, 11, 14, 16, 4, 5, 8, 9, 13, 3, 6, 7, 12, 10, 20, 21, 22, 23, 24}; 
//		int[] test = { 0, 19, 1, 11, 14, 15, 17, 8, 9, 12, 2, 16, 4, 5, 13, 18, 3, 6, 7, 10, 20, 21, 22, 23, 24 };
		
		
//		int costCounter = 0;
//		Set<Actor> set;
//		for (int i = 0; i < 25; i += 5) {
//			set = new HashSet<Actor>();
//			int temp = 0;
//			for(int j = 0; j < 5; j++){
//				for(Actor a: scenes[test[i+j]].getInvolvedActors()){
//					set.add(a);
//				}
//			}
//			for (Actor a : set) {
//				temp += a.getDailyCost();
//				System.out.print(a.getName() + " (" + a.getDailyCost() + ") ");
//			}
//			System.out.println();
//			System.out.println("Subday cost: " + temp);
//			costCounter += temp;
//		}
//		System.out.println("Test: " + costCounter);
		
		// Some input diagnostics
		for(Scene s : scenes){
			System.out.format("Scene %02d has a total cost of %5d and includes actors: " + s.printActors() + "%n", s.getSignature(), s.getRawCost());
		}
		
		
		
		// Start the computation
		SceneScheduler scheduler = new SceneScheduler(scenes, 5);
		scheduler.computeOptimalSchedule();
		
		System.out.println("Computed minimal cost of: $" + scheduler.getMinimalCost());
		System.out.println("The matching optimal schedule:\n" + scheduler);
		

	}

	public void printSchedule() {
		System.out.println("Minimal schedule: \n");
		for(Day d : minimalSchedule){
			for(Scene s : d.getIncludedScenes()){
				System.out.print(s.getSignature() + " ");
			}
			System.out.println();
		}
		
	}

	public void determineSubMinima() {
		for(int i = 4; i > 1; i--){
			CombinationGenerator gen = new CombinationGenerator(5, i);
			while(gen.hasMore()){
				int[] indices = gen.getNext();
				ArrayList<Scene> scenes = new ArrayList<Scene>();
				ArrayList<Day> sub = new ArrayList<Day>();
				for(int j : indices){
					scenes.addAll(minimalSchedule.get(j).getIncludedScenes());
					sub.add(minimalSchedule.get(j));
				}
				Collections.sort(scenes);
				StringBuffer sig = new StringBuffer();
				for(Scene s : scenes){
					sig.append(s.getSignature() + " ");
				}
				subOptima.put(sig.toString(), sub);
				
			}
		}
		
	}

	public Map<String, List<Day>> getSubOptima() {
		return subOptima;
	}
	

}
