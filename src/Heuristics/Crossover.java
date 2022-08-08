package Heuristics;

import LocalSearch.Solution;
import LocalSearch.Utils.LSUtils;

public class Crossover {
	
	public int[] crossover_mask;
	
	//Tipo 1
	public Solution crossover_uniform_unique(int tasks, Solution l1, Solution l2, double c) {
		
		Solution l = new Solution(l1.etc);
		
		double average = (l1.makespan + l2.makespan) / 2.0;
		
		generate_crossover_mask(tasks);
		
		for (int a = 0; a < tasks; a++){
		    if(crossover_mask[a] == 1){
		        l.mapping[a] = l1.mapping[a];
		    } else {
		        l.mapping[a] = l2.mapping[a];
		    }
		}
		
		l.load();
		l.makespan();
		
		double rand = LSUtils.randDouble(0.0, 1.0);
		
		if(l.makespan > average || rand < Math.exp((l.makespan - average)/c)) {
			return l;
		}
		
		return null;
		
	}
	
	public void generate_crossover_mask(int tasks) {
	
		crossover_mask = new int[tasks];
		
		 for (int i = 0; i < tasks; ++i) {
		   crossover_mask[i] = LSUtils.randInt(0, 1);
		 }
	}

	
	// Tipo 2
	public Solution crossover_one_point(int tasks, Solution l1, Solution l2, double c) {
		Solution l = new Solution(l1.etc);
		
		int start = LSUtils.randInt(0, tasks);
		
		double average = (l1.makespan + l2.makespan) / 2.0;
		

		for (int a = 0; a < start; a++){
		   l.mapping[a] = l1.mapping[a];
		}
		
		for (int a = start; a < tasks; a++){
		   l.mapping[a] = l2.mapping[a];
		}
		
		l.load();
		l.makespan();
		
		double rand = LSUtils.randDouble(0.0, 1.0);
		
		if(l.makespan > average || rand < Math.exp((l.makespan - average)/c)) {
			return l;
		}
		
		return null;
		
	}
	
	// Tipo 3
	public Solution crossover_two_point(int tasks, Solution l1, Solution l2, double c) {
		Solution l = new Solution(l1.etc);
		
		int start = LSUtils.randInt(0, tasks/2);
		
		int end = LSUtils.randInt((tasks/2)+1, tasks);
		
		double average = (l1.makespan + l2.makespan) / 2.0;
		
		for (int a = 0; a < start; a++){
		   l.mapping[a] = l1.mapping[a];
		}
		
		for (int a = start; a < end; a++){
		   l.mapping[a] = l2.mapping[a];
		}
		
		for (int a = end; a < tasks; a++){
		   l.mapping[a] = l1.mapping[a];
		}
		
		l.load();
		l.makespan();
		
		double rand = LSUtils.randDouble(0.0, 1.0);
		
		if(l.makespan > average || rand < Math.exp((l.makespan - average)/c)) {
			return l;
		}
		
		return null;
		
	}
	
	public Solution apply(int tipo, int tasks, Solution l1, Solution l2, double c) {
		if(Integer.valueOf(1).equals(tipo)) {
			return crossover_uniform_unique(tasks, l1, l2, c);
		} else if(Integer.valueOf(2).equals(tipo)) {
			return crossover_one_point(tasks, l1, l2, c);
		} else if(Integer.valueOf(3).equals(tipo)) {
			return crossover_two_point(tasks, l1, l2, c);
		}
		
		return null;
	}
	

}
