package com.scotchandcoffee.movieshoot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Schedule2 {
	
	SceneScheduler2 theScheduler;
	private List<Day> includedDays;
	private List<Day> possibleDays;
	private Set<Scene> coveredScenes;
	private int realCost;
	private int upperBoundCost;
	private boolean complete;
	
	public Schedule2(SceneScheduler2 theScheduler, List<Day> includedDays, List<Day> possibleDays, Set<Scene> coveredScenes, int realCost, int upperBoundCost, boolean complete) {
		super();
		this.theScheduler = theScheduler;
		this.includedDays = includedDays;
		this.possibleDays = possibleDays;
		this.coveredScenes = coveredScenes;
		this.realCost = realCost;
		this.upperBoundCost = upperBoundCost;
		this.complete = complete;
	}

	public void addDay(Day day){
		includedDays.add(day);
		
		for(Scene s : day.getIncludedScenes()){
			coveredScenes.add(s);
		}
		
		realCost = realCost + day.getCost();
		upperBoundCost = theScheduler.computeUpperBoundCost(coveredScenes);
		
		for(Day d : possibleDays){
			if(d.matches(day)){
				possibleDays.remove(day);
			}
		}
		
	}
	


}
