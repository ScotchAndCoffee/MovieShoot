package com.scotchandcoffee.movieshoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ScheduledDay {

	private ArrayList<Scene> availableForRescheduling;
	private CombinationGenerator gen;
	private ScheduledDay lowerLevelHook;
	private boolean isInitial = true;
	private boolean discardLevel = false;
	private Scene[][] modifiedDayList;
	private int[] daysToReschedule;
	private boolean hasLowerLevels = false;
	private ArrayList<Scene> removeContainer;
	private Scene[] optimalDay;
	private Map<String,Set<Scene[]>> computedComponents;
	private boolean wasPrecomputed = false;

	// This is the constructor used to initialize a top level
	public ScheduledDay(Scene[][] dayList, int[] daysToReschedule, Map<String,Set<Scene[]>> computed) {

		this.computedComponents = computed;
		this.daysToReschedule = daysToReschedule;
		modifiedDayList = new Scene[dayList.length][dayList[0].length];
		
		// Create a copy of what we get, we need to keep the original
		for (int i = 0; i < dayList.length; i++) {
			if (dayList[i] != null) {
				for (int j = 0; j < dayList[i].length; j++) {
					modifiedDayList[i][j] = dayList[i][j];

				}
			}
		}
		
		// Pick out the scenes we will be rescheduling
		availableForRescheduling = new ArrayList<Scene>();
		for(int i : daysToReschedule){
			for(Scene j : modifiedDayList[i]){
				availableForRescheduling.add(j);
			}
			// Those days have to be replaces
			modifiedDayList[i] = null;
		}
		
		ArrayList<Integer> sigList = new ArrayList<Integer>();
		for(Scene s : availableForRescheduling){
			sigList.add(s.getSignature());
		}
		Collections.sort(sigList);
		StringBuffer buildSig = new StringBuffer("");
		for(Integer i : sigList){
			buildSig.append(i.toString() + " ");
		}
		
		if (computed.containsKey(buildSig.toString())) {
			Set<Scene[]> precomputed = computed.get(buildSig.toString());
			Iterator<Scene[]> it = precomputed.iterator();
			for (int i : daysToReschedule) {
				modifiedDayList[i] = it.next();
			}
			System.out.println("Used Precomputed component in top level!-------------------- " + buildSig.toString());
			wasPrecomputed = true;
		} else {

			// Suggestion for the first day
			gen = new CombinationGenerator(availableForRescheduling.size(), 5);
			int[] indices = gen.getNext();
			Scene[] newDay = new Scene[5];
			removeContainer = new ArrayList<Scene>();
			for (int i = 0; i < indices.length; i++) {
				newDay[i] = availableForRescheduling.get(indices[i]);
				removeContainer.add(availableForRescheduling.get(indices[i]));
			}

			// Place the day in
			modifiedDayList[daysToReschedule[0]] = newDay;
			// Save it away
			optimalDay = newDay;
			if (daysToReschedule.length > 1) {
				hasLowerLevels = true;
			}
		}

	}

	// Use this constructer to build lower levels
	public ScheduledDay(Scene[][] dayList, int[] daysToReschedule, ArrayList<Scene> lowerAvailableForRescheduling, Map<String,Set<Scene[]>> computed) {
		
		this.computedComponents = computed;
		this.daysToReschedule = daysToReschedule;
		modifiedDayList = new Scene[dayList.length][dayList[0].length];
		availableForRescheduling = lowerAvailableForRescheduling;
		for (int i = 0; i < dayList.length; i++) {
			if (dayList[i] != null) {
				for (int j = 0; j < dayList[i].length; j++) {
					modifiedDayList[i][j] = dayList[i][j];

				}
			} else {
				modifiedDayList[i] = null;
			}
		}

		
		ArrayList<Integer> sigList = new ArrayList<Integer>();
		for (Scene s : availableForRescheduling) {
			sigList.add(s.getSignature());
		}
		Collections.sort(sigList);
		StringBuffer buildSig = new StringBuffer("");
		for (Integer i : sigList) {
			buildSig.append(i.toString() + " ");
		}

		if (computed.containsKey(buildSig.toString())) {
			Set<Scene[]> precomputed = computed.get(buildSig.toString());
			Iterator<Scene[]> it = precomputed.iterator();
			for (int i : daysToReschedule) {
				modifiedDayList[i] = it.next();
			}
			System.out.println("Used Precomputed component in lower level!-------------------- " + buildSig.toString());
			wasPrecomputed = true;
		} else {

			// The first combination again
			gen = new CombinationGenerator(availableForRescheduling.size(), 5);
			int[] indices = gen.getNext();

			Scene[] newDay = new Scene[5];
			removeContainer = new ArrayList<Scene>();
			for (int i = 0; i < indices.length; i++) {
				newDay[i] = availableForRescheduling.get(indices[i]);
				removeContainer.add(availableForRescheduling.get(indices[i]));
			}
			modifiedDayList[daysToReschedule[0]] = newDay;
			optimalDay = newDay;

			if (daysToReschedule.length > 1) {
				hasLowerLevels = true;
			}
		}
	}


	public boolean hasDays() {
		if (isInitial){
			return true;
		}
		else if(hasLower() && lowerLevelHook == null){
			return true;
		}
		else if (hasLower() && lowerLevelHook != null && !lowerLevelHook.discardLevel && lowerLevelHook.hasDays()) {
			return true;
		}
		else if(wasPrecomputed){
			return false;
		}
		else{
			return gen.hasMore();
		}
	}

	
	public ScheduledDay getAttempt() {
		if (isInitial) {
			// We got the first combination already by building the object
			isInitial = false;
			return this;
		} else {
			
			// Only go deeper if there are more days down there and if we are not already past an earlier maximum
			if (!discardLevel && hasLower() && lowerLevelHook == null) {
							
				int[] lowerDaysToReschedule = new int[daysToReschedule.length - 1];
				for (int i = 1; i < daysToReschedule.length; i++) {
					lowerDaysToReschedule[i - 1] = daysToReschedule[i];
				}
				ArrayList<Scene> lowerAvailableForRescheduling2 = new ArrayList<Scene>();
				lowerAvailableForRescheduling2.addAll(availableForRescheduling);
				lowerAvailableForRescheduling2.removeAll(removeContainer);

				// lower = new DaysToCheck(lowerModifiedList, forLower);
				lowerLevelHook = new ScheduledDay(modifiedDayList, lowerDaysToReschedule, lowerAvailableForRescheduling2, computedComponents);
				
				return lowerLevelHook.getAttempt();
			}
			else if(!discardLevel && hasLower() && lowerLevelHook != null && !lowerLevelHook.discardLevel && lowerLevelHook.hasDays()){
				return lowerLevelHook.getAttempt();
			}
			
			// If we good here we are trying a new path, reset
			discardLevel = false;
			lowerLevelHook = null;
			int[] indices = gen.getNext();
			Scene[] newDay = new Scene[5];
			removeContainer = new ArrayList<Scene>();
			
			for(int i = 0; i < indices.length; i++){
				newDay[i] = availableForRescheduling.get(indices[i]);
				removeContainer.add(availableForRescheduling.get(indices[i]));
			}
			
			modifiedDayList[daysToReschedule[0]] = newDay;
			optimalDay = newDay;

			if (daysToReschedule.length > 1) {
				hasLowerLevels = true;
			}
			return this;
		}

	}
	


	public boolean hasLower() {
		return hasLowerLevels;
	}
	
	public void discardLevel(){
		discardLevel = true;
	} 
	
	public Scene[][] getDayList(){
		return modifiedDayList;
	}
	
	public Scene[] getOptimalDay(){
		return optimalDay;
	}
	
	public ScheduledDay getLowerLevelHook(){
		return lowerLevelHook;
	}

}
