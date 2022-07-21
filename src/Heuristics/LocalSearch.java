package Heuristics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import LocalSearch.Solution;

public class LocalSearch {
	
	public static void apply(Solution solution) {
		List<Double> histMakespan = new ArrayList();
		List<Solution> histSolution = new ArrayList();

		int k = 1;

		int max = 0;
		int min = 0;

		boolean ctrl = false;

		while (!ctrl) {

			solution.load();
			solution.makespan();

			double makespan = solution.makespan;

			double[] load = solution.load;

			histMakespan.add(makespan);
			histSolution.add(solution);

			max = maxIndexLoad(load);

			min = minIndexLoad(load, k);

			int[] tasksMoreLoad = (int[]) getTasksMoreLoad(solution, max);

			double light = Double.MAX_VALUE;
			int il = 0;

			for (int i : tasksMoreLoad) {
				if (solution.etc[i][min] < light) {
					light = solution.etc[i][min];
					il = i;
				}

			}

			solution.mapping[il] = min;

			solution.load();
			solution.makespan();

			makespan = solution.makespan;

			if (contains(makespan, histMakespan)) {
				solution.mapping[il] = max;

				int kl = il;

				int[] tasksLessLoad = getTasksLessLoad(solution, min);
				light = Double.MAX_VALUE;

				for (int i : tasksLessLoad) {
					if (solution.etc[i][max] < light) {
						light = solution.etc[i][max];
						kl = i;
					}
				}

				solution.mapping[il] = min;
				solution.mapping[kl] = max;

				solution.load();
				solution.makespan();

				makespan = solution.makespan;

				if (contains(makespan, histMakespan)) {
					k = k + 1;
				}

			}

			if (max == min) {
				ctrl = true;
			}
		}

	}

	public static boolean contains(double makespan, List<Double> histMakespan) {
		boolean hasMakespan = false;

		for (double hist : histMakespan) {
			if (hist == makespan) {
				return true;
			}
		}

		return hasMakespan;
	}

	public static int[] getTasksLessLoad(Solution solution, int machine) {
		List<Integer> tasksMoreLoad = new ArrayList<>();

		for (int i = 0; i < solution.mapping.length; i++) {
			if (solution.mapping[i] == machine) {
				tasksMoreLoad.add(i);
			}
		}

		return tasksMoreLoad.stream().mapToInt(i -> i).toArray();

	}

	public static int[] getTasksMoreLoad(Solution solution, int machine) {
		List<Integer> tasksMoreLoad = new ArrayList<>();

		for (int i = 0; i < solution.mapping.length; i++) {
			if (solution.mapping[i] == machine) {
				tasksMoreLoad.add(i);
			}
		}

		return tasksMoreLoad.stream().mapToInt(i -> i).toArray();

	}

	public static int maxIndexLoad(double[] load) {
		int index = 0;

		double max = -1;

		for (int i = 0; i < load.length; i++) {
			if (load[i] > max) {
				index = i;
				max = load[i];
			}
		}

		return index;

	}

	public static double convert2CasasDecimais(double precoDouble) {
		DecimalFormat fmt = new DecimalFormat("0.00");
		String string = fmt.format(precoDouble);
		String[] part = string.split("[,]");
		String string2 = part[0] + "." + part[1];
		double preco = Double.parseDouble(string2);
		return preco;
	}

	public static int toInt(double value) {
		return (int) value;
	}

	public static int minIndexLoad(double[] load, int k) {

		double[] tmp = load.clone();

		Arrays.sort(tmp);

		int[] tmpIndex = new int[16];

		for (int i = 0; i < tmp.length; i++) {
			for (int j = 0; j < load.length; j++) {
				if (tmp[i] == load[j]) {
					tmpIndex[i] = j;
				}
			}
		}

		return tmpIndex[k];

	}


}
