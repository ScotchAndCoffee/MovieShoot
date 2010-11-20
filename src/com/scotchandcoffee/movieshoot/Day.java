package com.scotchandcoffee.movieshoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Day {
	private ArrayList<Scene> includedScenes = new ArrayList<Scene>();
	private Set<Actor> involvedActors = new HashSet<Actor>();
	private int cost = 0;
	private String signature;
	private int[] intSignature = new int[5];
	
	public Day(Scene... includedScenes){
		for(Scene s : includedScenes){
			this.includedScenes.add(s);
			involvedActors.addAll(s.getInvolvedActors());
		}
		Collections.sort(this.includedScenes);
		
		StringBuffer b = new StringBuffer();
//		for(Scene s : this.includedScenes){
//			b.append(s.getSignature() + " ");
//		}
		
		for(int i = 0; i < includedScenes.length; i++){
			int sig = this.includedScenes.get(i).getSignature();
			b.append(sig + " ");
			intSignature[i] = sig;
		}
		
		this.signature = b.toString();
		
		for(Actor a : involvedActors){
			cost += a.getDailyCost();
		}
		
		
	}
	
	public String getSignature(){
		return signature;
	}
	
	public int[] getIntSignature(){
		return intSignature;
	}

	public boolean matches(Day pick) {
		int[] pickSig = pick.getIntSignature();
		
		for (int i = 0; i < intSignature.length; i++) {
			for (int j = 0; j < pickSig.length; j++)

				if (intSignature[i] == pickSig[j]) {
					return true;
				}
		}
		return false;
	}
	
	public int getCost(){
		return cost;
	}
	
	public ArrayList<Scene> getIncludedScenes(){
		return includedScenes;
	}
	
}
