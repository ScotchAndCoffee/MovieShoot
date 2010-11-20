package com.scotchandcoffee.movieshoot;

import java.util.HashSet;
import java.util.Set;


public class Scene implements Comparable<Scene> {
	private int signature;
	private int rawCost = 0;
	private Set<Actor> involvedActors = new HashSet<Actor>();;

	public Scene(int signature, Actor... involvedActors) {
		this.signature = signature;
		
		for(Actor a : involvedActors){
			this.involvedActors.add(a);
		}
		
		for(Actor a : involvedActors){
			rawCost += a.getDailyCost();
		}
	}

	public int getSignature() {
		return signature;
	}

	public int getRawCost() {
		return rawCost;
	}

	public Set<Actor> getInvolvedActors() {
		return involvedActors;
	}

	public String printActors() {
		StringBuffer names = new StringBuffer("");
		for(Actor a : involvedActors){
			names.append(a.getName() + ", ");
		}
		return names.substring(0, names.length()-2);
	}

	@Override
	public int compareTo(Scene s) {
		return signature - s.getSignature();
		
	}

}
