package com.scotchandcoffee.movieshoot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SceneScheduler2 {
	
	private int numberOfShootingDays;
	private List<Scene> scenes;
	private int minimalCost;

	public SceneScheduler2(List<Scene> scenes, int numberOfShootingDays) {
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
		
		minimalCost = computeUpperBoundCost(new HashSet<Scene>());
		System.out.println("First guess: " + minimalCost);
		
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
		List<Scene> scenes = new ArrayList<Scene>();
		
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

		// Some input diagnostics
		for(Scene s : scenes){
			System.out.format("Scene %02d has a total cost of %5d and includes actors: " + s.printActors() + "%n", s.getSignature(), s.getRawCost());
		}
		
		// Start the computation
		SceneScheduler2 scheduler = new SceneScheduler2(scenes, 5);
		scheduler.computeOptimalSchedule();
		
		System.out.println("Computed minimal cost of: $" + scheduler.getMinimalCost());
		System.out.println("The matching optimal schedule:\n" + scheduler);
		

	}

	private String getMinimalCost() {
		// TODO Auto-generated method stub
		return null;
	}

	private void computeOptimalSchedule() {
		CombinationGenerator comb = new CombinationGenerator(scenes.size(), 5);
		List<Day> possibleDays = new ArrayList<Day>(); 
		
		
		while(comb.hasMore()){
			int[] indeces = comb.getNext();
			Scene[] includedScenes = new Scene[5];
			for(int i = 0; i < 5; i++){
				includedScenes[i] = scenes.get(indeces[i]);
			}
			possibleDays.add(new Day(includedScenes));
		}
		
		System.out.println(possibleDays.size());
		
	}
	
	public int computeUpperBoundCost(Set<Scene> coveredScenes){
		Set<Scene> remainingScenes = new HashSet<Scene>(scenes);
		remainingScenes.removeAll(coveredScenes);
		int cost = 0;
		for(Day d : getSimpleDays(remainingScenes)){
			cost += d.getCost();
		}
		return cost;
		
	}
	
	private Set<Day> getSimpleDays(Set<Scene> remainingScenes){
		HashSet<Day> days = new HashSet<Day>();
		Scene[] scenes = new Scene[5];
		int c = 0;
		Iterator<Scene> iter = remainingScenes.iterator();
		while(iter.hasNext()){
			scenes[c] = iter.next();
			c++;
			if(c == 5){
				days.add(new Day(scenes));
				c = 0;
				scenes = new Scene[5];
			}
		}
		
		return days;
	}
	
	
}
