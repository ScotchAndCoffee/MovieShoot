package com.scotchandcoffee.movieshoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.text.Position;

public class Schedule {

	HashSet<Scene> scenes = new HashSet<Scene>();
	int numberOfShootingDays;
	private List<Day> possibleDays;
	private SceneScheduler scheduler;
	private List<Day> currentSchedule;

	public Schedule(int numberOfShootingDays, Set<Scene> scenes, List<Day> possibleDays, SceneScheduler scheduler) {
		this(numberOfShootingDays, scenes, possibleDays, scheduler, new ArrayList<Day>());
	}

	private Schedule(int numberOfShootingDays, Set<Scene> scenes, List<Day> possibleDays, SceneScheduler scheduler, List<Day> currentSchedule) {

		this.scenes.addAll(scenes);
		this.numberOfShootingDays = numberOfShootingDays;
		this.possibleDays = possibleDays;
		this.scheduler = scheduler;
		this.currentSchedule = currentSchedule;
	}

	public void computeOptimum() {
		int simpleSum = 0;
		for (Scene s : scenes) {
			simpleSum += s.getRawCost();
		}
		scheduler.setMinimalCost(simpleSum);
		computeOptimum(simpleSum, 0);
	}

	private void computeOptimum(int currentOptimum, int alreadySpent) {
		HashSet<Day> discarded = new HashSet<Day>();
		Day lastChoice = null;
		
		for (Day d : possibleDays) {
			discarded.add(d);
		
			if(lastChoice == null){
				lastChoice = d;
			}
			
			if (alreadySpent + d.getCost() < currentOptimum) {
				if (alreadySpent + d.getCost() + getLowerBound(d) < currentOptimum) {

					ArrayList<Day> dayAdded = new ArrayList<Day>(currentSchedule);
					dayAdded.add(d);
					int newAlreadySpent = alreadySpent + d.getCost();
					HashSet<Scene> lessScenes = new HashSet<Scene>(scenes);
					lessScenes.removeAll(d.getIncludedScenes());

					if (lessScenes.isEmpty()) {
						scheduler.setMinimalSchedule(dayAdded);
						scheduler.setMinimalCost(newAlreadySpent);
						scheduler.determineSubMinima();
						System.out.println("Found new optimum:" + newAlreadySpent);
						scheduler.printSchedule();
					} else {

						ArrayList<Scene> sortedScenes = new ArrayList<Scene>(lessScenes);
						Collections.sort(sortedScenes);

						StringBuffer sig = new StringBuffer();
						for (Scene s : sortedScenes) {
							sig.append(s.getSignature() + " ");
						}

						String fingerPrint = sig.toString();

						if (scheduler.getSubOptima().containsKey(fingerPrint)) {
							HashSet<Day> subOptimaDays = new HashSet<Day>(scheduler.getSubOptima().get(fingerPrint));
							int costViaSubOptima = 0;
							for (Day day : subOptimaDays) {
								costViaSubOptima += day.getCost();
							}
							if (costViaSubOptima + newAlreadySpent < scheduler.getMinimalCost()) {
								scheduler.setMinimalSchedule(dayAdded);
								scheduler.getMinimalSchedule().addAll(subOptimaDays);
								scheduler.setMinimalCost(costViaSubOptima + newAlreadySpent);
								System.out.println("Used lookup!");
								int tmp = costViaSubOptima + newAlreadySpent;
								System.out.println("Found new optimum:" + tmp);
								scheduler.printSchedule();
							}
							else{
								System.out.println("Discarded by lookup");
							}

						} else {

							ArrayList<Day> remainingDays = getRemainingDays(possibleDays.subList(possibleDays.indexOf(d), possibleDays.size() - 1),d,lastChoice);
							if (lessScenes.size() == 20) {
								System.out.println("New search spece: Scenes " + lessScenes.size() + " with days " + remainingDays.size());
							}
							Schedule nextDay = new Schedule(numberOfShootingDays - 1, lessScenes, remainingDays, scheduler, dayAdded);
							nextDay.computeOptimum(scheduler.getMinimalCost(), newAlreadySpent);
						}
					}
				}
			}
			lastChoice = d;
		}
	}

	private int getLowerBound(Day d) {
		Set<Actor> actors = new HashSet<Actor>();
		int lowerBound = 0;
		for (Scene s : scenes) {
			if (!d.getIncludedScenes().contains(s)) {
				actors.addAll(s.getInvolvedActors());
			}
		}
		for (Actor a : actors) {
			lowerBound += a.getDailyCost();
		}
		return lowerBound;

	}

	private ArrayList<Day> getRemainingDays(List<Day> total, Day pick) {
		ArrayList<Day> remainingDays = new ArrayList<Day>();
		remainingDays.addAll(total);
		
		
		
		Iterator<Day> iter = remainingDays.iterator();

		while (iter.hasNext()) {
			Day d = iter.next();
			if (d.matches(pick)) {
				iter.remove();
			}
		}

		return remainingDays;
	}
	
	private ArrayList<Day> getRemainingDays(List<Day> total, Day pick, Day lastPick) {
		ArrayList<Day> remainingDays = new ArrayList<Day>(total);

		remainingDays.removeAll(scheduler.getRejectedBasedOnDay().get(pick));
		
		return remainingDays;
	}
	
//	private ArrayList<Day> getRemainingDays(List<Day> total, Day pick, Day lastPick, HashSet<Day> discarded) {
//		ArrayList<Day> remainingDays = new ArrayList<Day>(total);
//		remainingDays.addAll(total);
//		Set<Scene> dayDistanceAdded = getDayDistance(pick,lastPick);
//		Set<Scene> dayDistanceRemoved = getDayDistance(lastPick, pick);
//		
//		for(Scene s : dayDistanceRemoved){
//			remainingDays.addAll(scheduler.getRejectedBasedOnDay().get(s.getSignature()));
//		}
//		
//		for(Scene s : dayDistanceAdded){
//			remainingDays.removeAll(scheduler.getRejectedBasedOnDay().get(s.getSignature()));
//		}
//		
//		remainingDays.removeAll(discarded);
//
//
//		return remainingDays;
//	}

	private Set<Scene> getDayDistance(Day d1, Day d2) {
		HashSet<Scene> distance = new HashSet<Scene>(d1.getIncludedScenes());
		
		for(Scene s : d1.getIncludedScenes()){
			if(!d2.getIncludedScenes().contains(s)){
				distance.add(s);
			}
		}

		
		return distance;
	}

}
