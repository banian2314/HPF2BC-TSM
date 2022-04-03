package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

/**
 * a scatter search algorithm for the distributed permutation flow shop
 * scheduling problem
 * 
 * Naderi and Ruiz
 * 
 * 
 * 2014
 */

public class SS extends Algorithm {

	public int b = 10;
	public int l = 10;
	public int a = 40;
	public int NP = 25;
	public double probability = 0.1; // 原文中为p
	public Hueristics hueristics;

	public SS(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		AlgorithmName = "SS";
		hueristics = new Hueristics();
	}

	public void initial(individual pop[], individual H[], int S[][], Evaluation feval) {

		pop[0] = new individual(n, m, f);
		hueristics.HPF3(pop[0], feval);
		for (int i = 1; i < NP; i++) {
			pop[i] = new individual(n, m, f);
			hueristics.NEH2Random(pop[i], feval);
		}
		Arrays.sort(pop, function.recompareIndividualMintoMax());
		for (int i = 0; i < H.length; i++) {
			H[i] = new individual(n, m, f);
			function.copyIndividual(pop[i], H[i]);
		}
		// generateSMatrix(S);
	}

	public void generateSMatrix(int S[][]) {
		Random random = new Random();
		for (int i = 0; i < S.length; i++) {
			S[i] = new int[n + 1];
			for (int j = 1; j <= n; j++) {
				S[i][j] = random.nextInt(f);
			}
		}
	}

	public void solutionCombination(individual pop, int s[], Evaluation feval) {
		int result[] = new int[2];
		int vector[] = new int[n + 1];
		function.shuffle(n, vector);
		Integer removejob;
		double fit = 0, bestfit = 0;
		int bestpos = 0;
		double time[] = new double[m + 1];

		// displayVector(pop);
		// System.out.println();
		// System.out.println("-------factory assignment--------");
		// for (int i = 1; i <=n; i++) {
		// System.out.print(s[i]+" ");
		// }
		// System.out.println();
		// System.out.println("---------vector-----------------");
		// System.out.println();
		// for (int i = 1; i <=n; i++) {
		// System.out.print(vector[i]+" ");
		// }
		// System.out.println();
		// System.out.println();

		for (int i = 1; i <= n; i++) {
			findJob(pop, result, vector[i]);
			if (Math.random() < probability) {
				findJob(pop, result, vector[i]);
				// System.out.println("job" + vector[i]);
				if (s[vector[i]] != result[0]) {
					removejob = pop.vector.get(result[0]).get(result[1]);
					pop.vector.get(result[0]).remove(result[1]);
					feval.evaluateSingleSequence(pop.vector.get(result[0]), pop.leaveTime[result[0]],
							pop.tailTime[result[0]], pop.vector.get(result[0]).size() - 1);
					bestfit = Double.MAX_VALUE;
//					 System.out.println("s[vector[i]] "+s[vector[i]]);
					for (int j = 1, length = pop.vector.get(s[vector[i]]).size(); j <= length; j++) {
						fit = feval.quickEvaluation(removejob, j, length - 1, time, pop.leaveTime[s[vector[i]]],
								pop.tailTime[s[vector[i]]]);
						if (bestfit > fit) {
							bestfit = fit;
							bestpos = j;
						}
					}
					pop.vector.get(s[vector[i]]).add(bestpos, removejob);
					feval.evaluateSingleSequence(pop.vector.get(s[vector[i]]), pop.leaveTime[s[vector[i]]],
							pop.tailTime[s[vector[i]]], pop.vector.get(s[vector[i]]).size() - 1);
				}
			}
		}
		updatefitness(pop);
		// checkLegal(pop, feval);
		// displayVector(pop);
	}

	public void LS_1(ArrayList<Integer> jobseqeunce, double leavetime[][], double tailtime[][], Evaluation feval) {
		int count = jobseqeunce.size() - 1;
		int bestpos = 0;
		double fit, bestfit, makespan;
		makespan = leavetime[m][count];
		Integer temp;
		double time[] = new double[m + 1];
		// System.out.println("makespan" + makespan);
		boolean improvement = true;

		// double testleavetime[][] = new double[m + 1][n + 1];
		// double testtailtime[][] = new double[m + 1][n + 1];

		while (improvement) {
			improvement = false;
			for (int i = 1; i <= count; i++) {
				temp = jobseqeunce.get(i);
				// System.out.println("selectjob" + temp);
				jobseqeunce.remove(i);
				feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count - 1);
				bestfit = Double.MAX_VALUE;
				bestpos = 0;
				for (int j = 1; j <= count; j++) {
					fit = feval.quickEvaluation(temp, j, count - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					// jobseqeunce.add(j, temp);
					// double testfit =
					// feval.evaluateSingleSequence(jobseqeunce, testleavetime,
					// testtailtime,
					// jobseqeunce.size() - 1);
					// jobseqeunce.remove(j);
					// System.out.println("testfit"+testfit);
					if (bestfit > fit) {
						bestfit = fit;
						bestpos = j;
					}
				}
				// System.out.println("bestfit" + bestfit);
				if (bestfit < makespan) {
					// System.out.println("improve");
					jobseqeunce.add(bestpos, temp);
					feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count);
					makespan = leavetime[m][count];
					improvement = true;
					break;
				} else {
					jobseqeunce.add(i, temp);
				}
			}
		}
		feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count);
	}

	public boolean LS_2(individual pop, int factory[], Evaluation feval) {
		boolean improve = false;
		int cnt = 0;
		int recordIndex[] = new int[n + 1];
		Integer removejob;
		double bestfit = 0, fit = 0, Cmax;
		double time[] = new double[m + 1];
		int bestposition = 0, bestfactory = 0, bestposition2 = 0;
		double recordfitness;
		int recordfactory;
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for (int i = 1, length = pop.vector.get(pop.makespanFactoryNum).size() - 1; i <= length; i++) {
			Integer temp = new Integer(pop.vector.get(pop.makespanFactoryNum).get(i).intValue());
			recordIndex[pop.vector.get(pop.makespanFactoryNum).get(i).intValue()] = i;
			arr.add(temp);
		}
		// Collections.shuffle(arr);
		// displayVector(pop);
		// System.out.println();
		// System.out.println(pop.vector.get(pop.makespanFactoryNum).size() -
		// 1);
		while (cnt < pop.vector.get(pop.makespanFactoryNum).size() - 1) {
			if (cnt == 0) {
				// 重新生成随机序列
				arr.clear();
				for (int i = 1, length = pop.vector.get(pop.makespanFactoryNum).size() - 1; i <= length; i++) {
					Integer temp = new Integer(pop.vector.get(pop.makespanFactoryNum).get(i).intValue());
					recordIndex[pop.vector.get(pop.makespanFactoryNum).get(i).intValue()] = i; // 索引
					arr.add(temp);
					// System.out.println("improve");
				}
				// Collections.shuffle(arr);
				// displayEachFactory(arr);
			}
			recordfitness = pop.fit;
			removejob = pop.vector.get(pop.makespanFactoryNum).get(recordIndex[arr.get(cnt)]);
			pop.vector.get(pop.makespanFactoryNum).remove(recordIndex[arr.get(cnt)]);
			feval.evaluateSingleSequence(pop.vector.get(pop.makespanFactoryNum), pop.leaveTime[pop.makespanFactoryNum],
					pop.tailTime[pop.makespanFactoryNum], pop.vector.get(pop.makespanFactoryNum).size() - 1);

			recordfactory = pop.makespanFactoryNum;
			updatefitness(pop); // importantly
			// System.out.println("remove" + removejob);
			// displayVector(pop);
			// System.out.println("pop" + pop.fit);
			Cmax = Double.MAX_VALUE;
			// System.out.println();
			for (int i = 0; i < f; i++) {
				// if (pop.leaveTime[i][m][pop.vector.get(i).size() - 1] +
				// pmin[removejob] >= pop.fit) {
				// System.out.println("no possible");
				//// continue;
				// }
				bestfit = Double.MAX_VALUE;
				for (int j = 1, length = pop.vector.get(i).size(); j <= length; j++) {
					if (i == recordfactory && j == recordIndex[removejob.intValue()]) { // 原来的插入位置
						continue;
					}
					fit = feval.quickEvaluation(removejob.intValue(), j, length - 1, time, pop.leaveTime[i],
							pop.tailTime[i]);
					// pop.vector.get(i).add(j, removejob);
					//
					// double testleavetime[][] = new double[m+1][n+1];
					// double testtailtime[][] = new double[m+1][n+1];
					//
					// double testfit =
					// feval.evaluateSingleSequence(pop.vector.get(i),
					// testleavetime, testtailtime, pop.vector.get(i).size()-1);
					//
					// displayEachFactory(pop.vector.get(i));
					//// System.out.println("testfit"+testfit);
					// pop.vector.get(i).remove(j);
					//
					// System.out.println("fit" + fit);
					if (bestfit > fit) {
						bestfit = fit;
						bestposition = j;
					}
				}
				if (bestfit < pop.fit) {
					bestfit = pop.fit;
				}
				// System.out.println("bestfit" + bestfit);
				if (Cmax > bestfit) {
					bestfactory = i;
					bestposition2 = bestposition;
					Cmax = bestfit;
				}
				// System.out.println();
			}
			// System.out.println("cmax" + Cmax);
			// System.out.println("bestfactoy" + bestfactory);
			// System.out.println("bestposition2" + bestposition2);
			// System.out.println("removejob" + removejob);
			// System.out.println("fmax" + pop.makespanFactoryNum);
			if (Cmax < recordfitness) {
				// System.out.println("improve");
				pop.vector.get(bestfactory).add(bestposition2, removejob);
				feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
						pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
				cnt = 0;
				// for (int i = 1, length =
				// pop.vector.get(pop.makespanFactoryNum).size() - 1; i <=
				// length; i++) { //更新索引
				// recordIndex[pop.vector.get(pop.makespanFactoryNum).get(i).intValue()]
				// = i;
				// }
				updatefitness(pop);
				factory[0] = pop.makespanFactoryNum;
				factory[1] = bestfactory;
				return true;
			} else {
				// System.out.println("no improve");
				pop.vector.get(recordfactory).add(recordIndex[arr.get(cnt)], removejob);
				feval.evaluateSingleSequence(pop.vector.get(recordfactory), pop.leaveTime[recordfactory],
						pop.tailTime[recordfactory], pop.vector.get(recordfactory).size() - 1);
				updatefitness(pop);
				cnt++;
			}
			// System.out.println();
			// System.out.println("Cmax" + Cmax);
			// System.out.println("--------------result------------");
			// individual test = new individual(n,m, f);
			// function.copyIndividual(pop,test);
			// displayVector(test);
			// feval.blockFeval(test);
			// System.out.println(test.fit);
			// System.out.println("-----------------------------------------------------------------------------");
		}
		// System.out.println("cnt "+cnt);
		// System.out.println("--------------result------------");
		// displayVector(pop);
		// feval.blockFeval(pop);
		// System.out.println(pop.fit);
		return improve;
	}

	public void VND(individual pop, Evaluation feval) {
		boolean flag = true;
		int factory[] = new int[2];
		for (int g = 0; g < f; g++) {
			LS_1(pop.vector.get(g), pop.leaveTime[g], pop.tailTime[g], feval);
		}
		updatefitness(pop);
		while (flag) {
			factory[0] = 0;
			factory[1] = 0;
			flag = LS_2(pop, factory, feval);
			if (factory[0] != 0 && factory[1] != 0) {
				LS_1(pop.vector.get(factory[0]), pop.leaveTime[factory[0]], pop.tailTime[factory[0]], feval);
				LS_1(pop.vector.get(factory[1]), pop.leaveTime[factory[1]], pop.tailTime[factory[1]], feval);
				updatefitness(pop);
			}
		}
	}

	public boolean isInreferenceSet(individual H[], individual temp) {
		for (int i = 0; i < H.length; i++) {
			if (similarIndiviudal(H[i], temp)) {
				return true;
			}
		}
		return false;
	}

	public void updateReferenceSet(individual H[], individual temp) {
		if (temp.fit < H[b - 1].fit && !isInreferenceSet(H, temp)) {
			function.copyIndividual(temp, H[b - 1]);
			Arrays.sort(H, function.recompareIndividualMintoMax());
		}
	}

	public void restart(individual H[], Evaluation feval) {
		for (int i = b / 2; i <= b - 1; i++) {
			H[i] = new individual(n, m, f);
			hueristics.NEH2Random(H[i], feval);
		}
		Arrays.sort(H, function.recompareIndividualMintoMax());
	}

	public void run() {
		individual pop[] = new individual[NP];
		individual H[] = new individual[b];
		individual temp = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);
		int S[][] = new int[l][n + 1];

		Evaluation feval = new Evaluation();
		initial(pop, H, S, feval);

		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();
		int cnt = 0;
		function.copyIndividual(H[0], bestsofar);
		for (int f = 0; endtime - starttime < cpuTime; f++) {
			generateSMatrix(S);
			for (int i = 0; i < b; i++) {
				for (int j = 0; j < l; j++) {
					function.copyIndividual(H[i], temp);
					solutionCombination(temp, S[j], feval);
					VND(temp, feval);
//					System.out.println("excutive");
					updateReferenceSet(H, temp);
				}
			}
			if (H[0].fit < bestsofar.fit) {
				function.copyIndividual(H[0], bestsofar);
				cnt = 0;
			} else {
				cnt++;
			}
			if (cnt > a) {
//				System.out.println("restart");
				restart(H, feval);
			}
			System.out.println(bestsofar.fit);
			endtime = System.currentTimeMillis();
		}
		System.out.println("well done");
		System.out.println("time"+(endtime-starttime));
		checkLegal(bestsofar, feval);
        recordResualt(bestsofar);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta1";
		params.readConfig(instance);
		SS ss = new SS(params, instance, 7);
		ss.run();
	}

}
