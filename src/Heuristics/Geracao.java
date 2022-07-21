package Heuristics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import LocalSearch.Solution;
import LocalSearch.Utils.LSUtils;

public class Geracao {

	public static Solution min_min(double[][] etc, int tasks, int machines) {

		double[][] tmp = new double[tasks][machines];

		Solution solution = new Solution(etc);

		for (int i = 0; i < tasks; i++) {
			for (int j = 0; j < machines; j++) {
				tmp[i][j] = etc[i][j];
			}
		}

		boolean[] isRemoved = new boolean[tasks];

		// inicializa com false
		for (int i = 0; i < tasks; i++) {
			isRemoved[i] = false;
		}

		int numTasks = tasks;

		do {

			double minValue = Double.MAX_VALUE;
			int machine = -1;
			int task = -1;

			for (int i = 0; i < tasks; i++) {

				if (isRemoved[i]) {
					continue;
				}

				for (int j = 0; j < machines; j++) {
					if (tmp[i][j] < minValue) {
						minValue = tmp[i][j];
						machine = j;
						task = i;
					}
				}
			}

			solution.load[machine] = minValue;
			solution.mapping[task] = machine;
			isRemoved[task] = true;

			for (int i = 0; i < tasks; i++) {
				if (isRemoved[i]) {
					continue;
				}

				tmp[i][machine] = etc[i][machine] + solution.load[machine];

			}

			numTasks--;
		} while (numTasks > 0);

		solution.makespan();

		return solution;

	}

	public static Solution generatePopInitial(double[][] etc, int tasks, int machines) {

		double[][] tmp = new double[tasks][machines];

		Solution solution = new Solution(etc);

		for (int i = 0; i < tasks; i++) {
			solution.mapping[i] = LSUtils.randInt(0, machines - 1);
		}

		for (int i = 0; i < tasks; i++) {
			for (int j = 0; j < machines; j++) {
				tmp[i][j] = etc[i][j];
			}
		}

		boolean[] isRemoved = new boolean[tasks];

		// inicializa com false
		for (int i = 0; i < tasks; i++) {
			isRemoved[i] = false;
		}

		int numTasks = tasks / 2;

		do {

			int machine = LSUtils.randInt(0, machines - 1);
			int task = LSUtils.randInt(0, tasks - 1);

			solution.load[machine] = tmp[task][machine];
			solution.mapping[task] = machine;
			isRemoved[task] = true;

			for (int i = 0; i < tasks; i++) {
				if (isRemoved[i]) {
					continue;
				}

				tmp[i][machine] = etc[i][machine] + solution.load[machine];

			}

			numTasks--;
		} while (numTasks > 0);

		solution.makespan();

		return solution;

	}

	public static Solution generateIndividualPaulo(String fileName, double[][] etc, int tasks, int machines)
			throws IOException {

		File file = new File(fileName);
		FileReader fileReader = new FileReader(file);

		BufferedReader br = new BufferedReader(fileReader);

		String mappingStr = br.readLine();

		String[] mapping = mappingStr.split(",");

		Solution solution = new Solution(etc);

		for (int i = 0; i < tasks; i++) {
			int machine = Integer.valueOf(mapping[i]);

			solution.mapping[i] = machine - 1;
		}

		solution.load();
		solution.makespan();

		return solution;

	}

	public static Solution generatePrimeiroIndividuo(int tipoGeracao, String fileName, double[][] etc, int tasks, int machines) throws IOException {
		Solution solution;
		
		if(Integer.valueOf(1).equals(tipoGeracao)) {
			return min_min(etc, tasks, machines);
		} else if(Integer.valueOf(2).equals(tipoGeracao)) {
			return generateIndividualPaulo(fileName, etc, tasks, machines);
		}
		
		return null;
		
	}
	
}
