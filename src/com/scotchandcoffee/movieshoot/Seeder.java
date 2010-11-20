package com.scotchandcoffee.movieshoot;



public class Seeder {

	CombinationGenerator gen;

	public Seeder(int totalDays, int depth) {
		gen = new CombinationGenerator(totalDays, depth);

	}

	public int[] getDaysToReschedule() {
		int[] indecis = gen.getNext();
		StringBuffer out = new StringBuffer("");
		for(int i : indecis){
			out.append(i + " ");
		}
		System.out.println(out);
		return indecis;
	}

	public boolean hasDays() {
		return gen.hasMore();
	}
	
	public void reset(int totalDays, int depth){
		gen = new CombinationGenerator(totalDays, depth);
	}

}
