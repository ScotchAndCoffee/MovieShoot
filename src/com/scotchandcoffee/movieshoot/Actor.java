package com.scotchandcoffee.movieshoot;


/**
 * @author timo
 * 
 *        Simple representation for an Actor
 */

public class Actor {
	
	private int actorId;
	private String name;
	private int dailyCost;
	
	public Actor(int actorId, String name, int dailyCost) {
		this.actorId = actorId;
		this.name = name;
		this.dailyCost = dailyCost;
	}
	public String getName() {
		return name;
	}
	public int getActorId() {
		return actorId;
	}
	public int getDailyCost() {
		return dailyCost;
	}
	
	
}
