package LocalSearch;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import ETC.ETCGenerator;
import Heuristics.Crossover;
import Heuristics.Geracao;
import Heuristics.LocalSearch;
import Heuristics.Mutation;
import LocalSearch.Utils.LSUtils;
import Parameters.Parameters;

public class Main {

	public static Parameters parameters = new Parameters("/parameters/parameters_ag.txt");

	public static void main(String[] args) throws InterruptedException, IOException {

		Logger logger = Logger.getLogger("MyLog");
		FileHandler fh;

		UUID uuid = UUID.randomUUID();

		try {
			fh = new FileHandler(System.getProperty("user.dir") + "/logs/MyLogFile" + uuid.toString() + ".log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.setUseParentHandlers(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int tasks = parameters.get_NUM_TASKS();
		int machines = parameters.get_NUM_MACHINES();

		ETCGenerator etcGenerator = new ETCGenerator(machines, tasks, parameters.getFileName());

		Solution primeiroIndividuo = Geracao.generatePrimeiroIndividuo(parameters.getTipoGeracao(), parameters.getFilePaulo(), etcGenerator.getETCDouble(), tasks, machines);
		
		int popSize = parameters.getPopulacaoSize();

		Solution[] popA = new Solution[500];
		Solution[] popB = new Solution[500];

		popA[0] = primeiroIndividuo;
		popB[0] = primeiroIndividuo;

		for (int a = 1; a < popSize; a++) {
			popA[a] = Geracao.generatePopInitial(etcGenerator.getETCDouble(), tasks, machines);
		}

		int maxGenerations = parameters.getNumIteration();

		int best = 0;
		double tmp, tmp1, popMakespan = 0;
		long e = 0;
		double c = 2.0;

		Crossover crossover = new Crossover();
		Mutation mutation = new Mutation();

		while (e < maxGenerations) {
			e++;

			if (e % 4 == 0) {
				c = 0.99 * c;
			}

			Solution backup = (Solution) popA[0].clone();

			System.out
					.println("Best makespan " + String.format("%.2f", popA[0].makespan) + " in Generation " + e + "\n");

			logger.info("Best makespan " + String.format("%.2f", popA[0].makespan) + " in Generation " + e + "\n");

			// Crossover
			for (int i = 0; i < 2; i++) {

				best = 0;

				tmp = (i == 0) ? popA[0].makespan : popB[0].makespan;

				popMakespan = tmp;

				for (int b = 1; b < popSize; b++) {
					tmp1 = (i == 0) ? popA[b].makespan : popB[b].makespan;
					popMakespan += tmp1;
					if (tmp1 >= tmp) {
						tmp = tmp1;
						best = b;
					}
				}

				if (i == 0) {
					if (toInt(popA[best].makespan) > toInt(popA[0].makespan)) {
						popB[0] = copySol(popA[best]);
					}

				} else {
					if (toInt(popB[best].makespan) > toInt(popA[0].makespan)) {
						popA[0] = copySol(popB[best]);
					}
				}

				int a = 1;

				while (a < popSize) {

					double fit = 0.0;

					int x, y = 0;

					do {
						x = LSUtils.randInt(0, popSize - 1);

						y = LSUtils.randInt(0, popSize - 1);

						fit = (i == 0) ? popA[x].makespan : popB[y].makespan;
					} while ((x / (double) y) < 0.50);

					if (i == 0) {
						popB[a] = crossover.crossover_uniform_unique(tasks, popA[x], popA[y], c);

						if (popB[a] == null) {
							continue;
						}

					} else if (i == 1) {
						popA[a] = crossover.crossover_uniform_unique(tasks, popB[x], popB[y], c);

						if (popA[a] == null) {
							continue;
						}
					}

					a++;
				}
			}

			// Mutation
			for (int i = 0; i < 2; i++) {

				best = 0;

				tmp = (i == 0) ? popA[0].makespan : popB[0].makespan;

				popMakespan = tmp;

				for (int b = 1; b < popSize; b++) {
					tmp1 = (i == 0) ? popA[b].makespan : popB[b].makespan;
					popMakespan += tmp1;
					if (tmp1 >= tmp) {
						tmp = tmp1;
						best = b;
					}
				}

				if (i == 0) {
					if (toInt(popA[best].makespan) > toInt(popA[0].makespan)) {
						popB[0] = copySol(popA[best]);
					}

				} else {
					if (toInt(popB[best].makespan) > toInt(popA[0].makespan)) {
						popA[0] = copySol(popB[best]);
					}
				}

				int a = 1;

				while (a < popSize) {

					if (i == 0) {
						popB[a] = mutation.mutationOne(popA[a], tasks, machines);
						// popB[a] = mutation.mutationTwo(popA[a], tasks, machines);

						if (popB[a] == null) {
							continue;
						}

					} else if (i == 1) {
						popA[a] = mutation.mutationOne(popA[a], tasks, machines);
						// popA[a] = mutation.mutationTwo(popA[a], tasks, machines);

						if (popA[a] == null) {
							continue;
						}
					}

					a++;
				}
			}

			for (int i = 0; i < popSize / 5; i++) {

				// Buscar 20% da populacao
				// for (int i = 0; i < popSize/3; i++) {
				LocalSearch.apply(popA[i]);
			}

			BigDecimal backupMakespan = BigDecimal.valueOf(backup.makespan);
			BigDecimal bestMakespan = BigDecimal.valueOf(popA[0].makespan);

			if (backupMakespan.compareTo(bestMakespan) > 0) {
				popA[0] = backup;
			}

			// Thread.sleep(200);

		}
	}

	public static Solution copySol(Solution s) {
		Solution solution = new Solution(s.etc);

		int[] mapping = new int[512];

		for (int i = 0; i < mapping.length; i++) {
			mapping[i] = Integer.valueOf(s.mapping[i]);
		}

		solution.mapping = mapping;

		solution.load();
		solution.makespan();

		return solution;

	}

	/*
	public static void localSearch(Solution solution) {
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
	*/
	

	public static int toInt(double value) {
		return (int) value;
	}
}
