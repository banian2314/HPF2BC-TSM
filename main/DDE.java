package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;
import libs.weightIndividual;

/*
 * Discrete differential evolution algorithm for distributed blocking flowshop scheduling with makespan criterion
 * 
 * Zhang 2018
 * */

public class DDE extends Algorithm {

	int ps = 100;
	double k = 0.2;
	double cp = 0.3;
	double lambda = 0.02;
	int lstimes = 30;

	public DDE(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		AlgorithmName="DDE";
	}

	public void randmethod(individual pop, Evaluation feval) {
		int vector[] = new int[n + 1];
		int randfactory;
		function.shuffle(n, vector);
		// 确定每一个车间的第一个工件
		for (int i = 0; i < f; i++) {
			Integer firstJob = new Integer(vector[i + 1]);
			pop.vector.get(i).add(firstJob);
		}
		for (int i = f + 1; i <= n; i++) {
			randfactory = (int) Math.ceil(Math.random() * f) - 1;
			Integer firstJob = new Integer(vector[i]);
			pop.vector.get(randfactory).add(firstJob);
		}
		feval.blockFeval(pop);
	}

	public void initial(individual pop[], Evaluation feval) {
		Hueristics hueristics = new Hueristics();
		individual temp[] = new individual[6];
		for (int i = 1; i <= 5; i++) {
			temp[i] = new individual(n, m, f);
		}
		hueristics.DLPT(temp[1], feval);
		hueristics.DSPT(temp[2], feval);
		hueristics.DLS(temp[3], feval);
		hueristics.DNEH(temp[4], feval);
		hueristics.HPF3(temp[5], feval);
		Arrays.sort(temp, 1, 6, function.recompareIndividualMintoMax());

		for (int i = 0; i < pop.length; i++) {
			pop[i] = new individual(n, m, f);
		}
		function.copyIndividual(temp[1], pop[1]);
		for (int i = 2; i < pop.length; i++) {
			hueristics.NEH2Random(pop[i], feval);
		}
	}

	public int selectInidividual(individual pop[]) {
		weightIndividual selectOne[] = new weightIndividual[3];
		for (int i = 0; i < selectOne.length; i++) {
			selectOne[i] = new weightIndividual();
		}
		int rand1, rand2, rand3;

		rand1 = (int) Math.ceil(Math.random() * ps);
		rand2 = (int) Math.ceil(Math.random() * ps);
		rand3 = (int) Math.ceil(Math.random() * ps);
		while (rand1 == rand2 || rand1 == rand3 || rand2 == rand3) {
			rand1 = (int) Math.ceil(Math.random() * ps);
			rand2 = (int) Math.ceil(Math.random() * ps);
			rand3 = (int) Math.ceil(Math.random() * ps);
		}
		selectOne[0].num = rand1;
		selectOne[0].weight = pop[rand1].fit;
		selectOne[1].num = rand2;
		selectOne[1].weight = pop[rand2].fit;
		selectOne[2].num = rand3;
		selectOne[2].weight = pop[rand3].fit;
		Arrays.sort(selectOne, function.recompareWeightIndividualMintoMax());

		// for (int i = 0; i < selectOne.length; i++) {
		// System.out.println(selectOne[i].num + " " + selectOne[i].weight);
		// }

		return selectOne[0].num;
	}

	// 变异
	public void mutation(individual pop[], individual mutantpopulation, Evaluation feval) {
		int selectNum = selectInidividual(pop);
		// System.out.println(selectNum);
		function.copyIndividual(pop[selectNum], mutantpopulation);
		int vector[] = new int[n + 1];
		int vector1[] = new int[n + 1];
		int vector2[] = new int[n + 1];
		int delta[] = new int[n + 1];
		int result1[] = new int[2];
		int result2[] = new int[2];
		int p = 1;
		Integer temp1, temp2;
		for (int i = 0; i < f; i++) {
			for (int j = 1; j < mutantpopulation.vector.get(i).size(); j++) {
				vector[p] = mutantpopulation.vector.get(i).get(j).intValue();
				p++;
			}
		}

		function.shuffle(n, vector1);
		function.shuffle(n, vector2);
		for (int i = 1; i <= n; i++) {
			if (vector1[i] != vector2[i]) {
				delta[i] = vector1[i];
			} else {
				delta[i] = 0;
			}
		}
		// System.out.println("--------------");
		// displayVector(pop[selectNum]);
		// for (int i = 1; i <= n; i++) {
		// System.out.print(vector[i] + " ");
		// }
		for (int j = 1; j < delta.length; j++) {
			if (Math.random() > k) {
				delta[j] = 0;
			}
		}
		// System.out.println();
		// for (int j = 1; j < delta.length; j++) {
		// System.out.print(delta[j] + " ");
		// }
		// System.out.println();
		// System.out.println();
		// System.out.println();
		for (int j = 1; j <= n; j++) {
			if (delta[j] != 0) {
				// displayVector(pop[selectNum]);
				if (vector[j] == delta[j]) {
					continue;
				}
				if (Math.random() < 0.5) { // 作交换
					findJob(mutantpopulation, result1, vector[j]);
					findJob(mutantpopulation, result2, delta[j]);
					// System.out.println("factory1 " + result1[0]);
					// System.out.println("position1 " + result1[1]);
					// System.out.println("factory2 " + result2[0]);
					// System.out.println("position2 " + result2[1]);
					if (result1[0] == result2[0]) {
						// System.out.println("swap");
						Collections.swap(mutantpopulation.vector.get(result1[0]), result1[1], result2[1]);
					} else {
						// System.out.println("swap2");
						temp1 = mutantpopulation.vector.get(result1[0]).get(result1[1]);
						temp2 = mutantpopulation.vector.get(result2[0]).get(result2[1]);
						mutantpopulation.vector.get(result1[0]).remove(result1[1]);
						mutantpopulation.vector.get(result2[0]).remove(result2[1]);
						mutantpopulation.vector.get(result1[0]).add(result1[1], temp2);
						mutantpopulation.vector.get(result2[0]).add(result2[1], temp1);
					}
				} else {
					findJob(mutantpopulation, result1, vector[j]);
					if (mutantpopulation.vector.get(result1[0]).size() >= 3) { // 防止计算当前工厂中仅存在一个工件
						// System.out.println("insert");
						// System.out.println("job1 "+ vector[j]);
						// System.out.println("job2 "+delta[j]);
						// displayVector(mutantpopulation);
						// System.out.println();
						temp1 = mutantpopulation.vector.get(result1[0]).get(result1[1]);
						mutantpopulation.vector.get(result1[0]).remove(result1[1]);
						// displayVector(mutantpopulation);
						findJob(mutantpopulation, result2, delta[j]);
						// System.out.println("factory1 " + result1[0]);
						// System.out.println("position1 " + result1[1]);
						// System.out.println("factory2 " + result2[0]);
						// System.out.println("position2 " + result2[1]);
						mutantpopulation.vector.get(result2[0]).add(result2[1] + 1, temp1);
					}
				}
				// System.out.println();
				// displayVector(pop[selectNum]);
				// System.out.println();
			}
		}
		// displayVector(pop[selectNum]);
		feval.blockFeval(mutantpopulation);
		// checkLegal(mutantpopulation, feval);
	}

	public void crossover(individual pop[], individual mutantpopulation[], individual newpop[], Evaluation feval) {
		int vector[] = new int[n + 1];
		int Xvector[] = new int[n + 1];
		int Vvector[] = new int[n + 1];
		individual popX = new individual(n, m, f);
		individual popV = new individual(n, m, f);
		int result[][] = new int[n + 1][2];
		for (int i = 1; i <= ps; i++) {
			for (int j = 1; j < vector.length; j++) {
				if (Math.random() > cp) {
					vector[j] = j;
				}
				// System.out.print(vector[j] + " ");
			}
			// System.out.println();
			function.copyIndividual(pop[i], popX);
			function.copyIndividual(mutantpopulation[i], popV);
			int p = 1;
			for (int j = 0; j < f; j++) {
				for (int k = 1; k < popX.vector.get(j).size(); k++) {
					Xvector[p] = popX.vector.get(j).get(k).intValue();
					p++;
				}
			}
			p = 1;
			for (int j = 0; j < f; j++) {
				for (int k = 1; k < popV.vector.get(j).size(); k++) {
					Vvector[p] = popV.vector.get(j).get(k).intValue();
					p++;
				}
			}
			// displayVector(popX);
			// System.out.println();
			// System.out.println();
			// displayVector(popV);
			// for (int j = 1; j < Vvector.length; j++) {
			// System.out.print(Xvector[j]+" ");
			// }
			// System.out.println();
			// for (int j = 1; j < Vvector.length; j++) {
			// System.out.print(Vvector[j]+" ");
			// }
			// System.out.println();
			for (int j = 1; j < vector.length; j++) {
				if (vector[j] != 0) {
					findJob(popV, result[j], Xvector[vector[j]]);
					Integer temp = new Integer(0);
					popV.vector.get(result[j][0]).add(result[j][1] + 1, temp);
					popV.vector.get(result[j][0]).remove(result[j][1]);
				}
			}

			for (int j = 1; j <= n; j++) {
				if (vector[j] != 0) {
					Integer temp = new Integer(Xvector[vector[j]]);
					// System.out.println("-----------weizhi------");
					// System.out.println(result[j][0]);
					// System.out.println(result[j][1]);
					// System.out.println("----------------------");
					// System.out.println(temp);
					findJob(popV, result[j], 0);
					popV.vector.get(result[j][0]).add(result[j][1] + 1, temp);
					popV.vector.get(result[j][0]).remove(result[j][1]);
				}
			}

			for (int j = 1; j < vector.length; j++) {
				if (vector[j] != 0) {
					findJob(popX, result[j], Vvector[vector[j]]);
					Integer temp = new Integer(0);
					popX.vector.get(result[j][0]).add(result[j][1] + 1, temp);
					popX.vector.get(result[j][0]).remove(result[j][1]);
				}
			}

			for (int j = 1; j <= n; j++) {
				if (vector[j] != 0) {
					Integer temp = new Integer(Vvector[vector[j]]);
					// System.out.println("-----------weizhi------");
					// System.out.println(result[j][0]);
					// System.out.println(result[j][1]);
					// System.out.println("----------------------");
					// System.out.println(temp);
					findJob(popX, result[j], 0);
					popX.vector.get(result[j][0]).add(result[j][1] + 1, temp);
					popX.vector.get(result[j][0]).remove(result[j][1]);
				}
			}
			// System.out.println();
			// System.out.println();
			// displayVector(popV);
			// System.out.println();
			// System.out.println();
			// displayVector(popX);
			feval.blockFeval(popV);
			feval.blockFeval(popX);
			// checkLegal(popV, feval);
			// checkLegal(popX, feval);
			if (popV.fit < popX.fit) {
				function.copyIndividual(popV, newpop[i]);
			} else {
				function.copyIndividual(popX, newpop[i]);
			}
		}
	}

	public void selection(individual pop[], individual newpop[], Evaluation feval) {
		double RE;
		for (int i = 1; i <= ps; i++) {
			RE = (newpop[i].fit - pop[i].fit) / pop[i].fit;
			// System.out.println("RE"+RE);
			if (RE < 0 || Math.random() < Math.max(lambda - RE, 0)) {
				function.copyIndividual(newpop[i], pop[i]);
			}
		}
	}

	public void ICFI(individual pop, Evaluation feval) {
		int randjobpos = (int) Math.ceil(Math.random() * (pop.vector.get(pop.makespanFactoryNum).size() - 1));
		Integer temp = pop.vector.get(pop.makespanFactoryNum).get(randjobpos);
		pop.vector.get(pop.makespanFactoryNum).remove(randjobpos);
		ArrayList<Integer> alist = pop.vector.get(pop.makespanFactoryNum);
		int count = pop.vector.get(pop.makespanFactoryNum).size() - 1;
		double bestfit, fit;
		int bestpos;
		double leavetime[][] = new double[m + 1][count + 2];
		double tailtime[][] = new double[m + 1][count + 2];
		// double testleavetime[][] = new double[m + 1][count + 2];
		// double testtailtime[][] = new double[m + 1][count + 2];
		double time[] = new double[m + 1];

		feval.evaluateSingleSequence(alist, leavetime, tailtime, count);
		bestfit = feval.quickEvaluation(temp, count + 1, count + 1, time, leavetime, tailtime);
		bestpos = count + 1;
		// displayVector(pop);
		// System.out.println();
		// System.out.println();
		// alist.add(temp);
		// System.out.println("count"+count);
		for (int j = count; j >= 1; j--) {
			// Collections.swap(alist, j + 1, j);
			//
			// for (int j2 = 1; j2 <= alist.size() - 1; j2++) {
			// System.out.print(alist.get(j2) + " ");
			// }
			// System.out.println();
			if (j == randjobpos) {
				continue;
			}
			fit = feval.quickEvaluation(temp, j, count + 1, time, leavetime, tailtime);
			// double testfit = feval.evaluateSingleSequence(alist,
			// testleavetime, testtailtime, count + 1);
			// System.out.println("fit " + fit);
			// System.out.println("test " + testfit);
			if (bestfit > fit) {
				bestfit = fit;
				bestpos = j;
			}
		}
		if (bestfit < pop.fit) {
			// System.out.println("improve");
			alist.add(bestpos, temp);
			feval.evaluateSingleSequence(alist, pop.leaveTime[pop.makespanFactoryNum],
					pop.tailTime[pop.makespanFactoryNum], count + 1);
			updatefitness(pop);
		} else {
			alist.add(randjobpos, temp);
		}
		// System.out.println("ICFI");
		// checkLegal(pop, feval);
	}

	public void ECFI(individual pop, Evaluation feval) {

		// displayVector(pop);
		int randjobpos = (int) Math.ceil(Math.random() * (pop.vector.get(pop.makespanFactoryNum).size() - 1));
		Integer temp = pop.vector.get(pop.makespanFactoryNum).get(randjobpos);
		pop.vector.get(pop.makespanFactoryNum).remove(randjobpos);

		int randfactory = (int) Math.ceil(Math.random() * f) - 1;
		while (randfactory == pop.makespanFactoryNum) {
			randfactory = (int) Math.ceil(Math.random() * f) - 1;
		}
		// System.out.println();
		// displayVector(pop);
		// System.out.println("randfactory"+randfactory);
		int count = pop.vector.get(randfactory).size() - 1;
		// double testleavetime[][] = new double[m + 1][n];
		// double testtailtime[][] = new double[m + 1][n];
		double time[] = new double[m + 1];
		double bestfit = Double.MAX_VALUE, fit;
		int bestpos = 0;
		// ArrayList<Integer> alist;
		// alist = pop.vector.get(randfactory);
		for (int j = count + 1; j >= 1; j--) {
			// alist.add(j,temp);
			fit = feval.quickEvaluation(temp, j, count + 1, time, pop.leaveTime[randfactory],
					pop.tailTime[randfactory]);
			// double testfit = feval.evaluateSingleSequence(alist,
			// testleavetime, testtailtime, count + 1);
			// System.out.println("fit " + fit);
			// System.out.println("test " + testfit);
			if (bestfit > fit) {
				bestfit = fit;
				bestpos = j;
			}
			// alist.remove(j);
		}
		if (bestfit < pop.fit) {
			// System.out.println("improve");
			pop.vector.get(randfactory).add(bestpos, temp);
			feval.evaluateSingleSequence(pop.vector.get(randfactory), pop.leaveTime[randfactory],
					pop.tailTime[randfactory], count + 1);
			feval.evaluateSingleSequence(pop.vector.get(pop.makespanFactoryNum), pop.leaveTime[pop.makespanFactoryNum],
					pop.tailTime[pop.makespanFactoryNum], pop.vector.get(pop.makespanFactoryNum).size() - 1);
			updatefitness(pop);
		} else {
			// System.out.println("nonimprove");
			pop.vector.get(pop.makespanFactoryNum).add(randjobpos, temp);
		}
		// System.out.println("ECFI");
		// checkLegal(pop, feval);
	}

	public void ECFS(individual pop, Evaluation feval) {

		int randjobpos = (int) Math.ceil(Math.random() * (pop.vector.get(pop.makespanFactoryNum).size() - 1));
		Integer temp1 = pop.vector.get(pop.makespanFactoryNum).get(randjobpos);
		pop.vector.get(pop.makespanFactoryNum).remove(randjobpos);

		int randfactory = (int) Math.ceil(Math.random() * f) - 1;
		while (randfactory == pop.makespanFactoryNum) {
			randfactory = (int) Math.ceil(Math.random() * f) - 1;
		}
		int randjobpos2 = (int) Math.ceil(Math.random() * (pop.vector.get(randfactory).size() - 1));
		Integer temp2 = pop.vector.get(randfactory).get(randjobpos2);
		pop.vector.get(randfactory).remove(randjobpos2);

		pop.vector.get(pop.makespanFactoryNum).add(randjobpos, temp2);
		pop.vector.get(randfactory).add(randjobpos2, temp1);
		feval.evaluateSingleSequence(pop.vector.get(pop.makespanFactoryNum), pop.leaveTime[pop.makespanFactoryNum],
				pop.tailTime[pop.makespanFactoryNum], pop.vector.get(pop.makespanFactoryNum).size() - 1);
		feval.evaluateSingleSequence(pop.vector.get(randfactory), pop.leaveTime[randfactory], pop.tailTime[randfactory],
				pop.vector.get(randfactory).size() - 1);

		if (pop.fit > pop.leaveTime[pop.makespanFactoryNum][m][pop.vector.get(pop.makespanFactoryNum).size() - 1]
				&& pop.fit > pop.leaveTime[randfactory][m][pop.vector.get(randfactory).size() - 1]) {
			// System.out.println("improve");
			updatefitness(pop);
		} else {
			// System.out.println("noimprove");
			pop.vector.get(pop.makespanFactoryNum).remove(randjobpos);
			pop.vector.get(randfactory).remove(randjobpos2);
			pop.vector.get(pop.makespanFactoryNum).add(randjobpos, temp1);
			pop.vector.get(randfactory).add(randjobpos2, temp2);
			feval.evaluateSingleSequence(pop.vector.get(pop.makespanFactoryNum), pop.leaveTime[pop.makespanFactoryNum],
					pop.tailTime[pop.makespanFactoryNum], pop.vector.get(pop.makespanFactoryNum).size() - 1);
			feval.evaluateSingleSequence(pop.vector.get(randfactory), pop.leaveTime[randfactory],
					pop.tailTime[randfactory], pop.vector.get(randfactory).size() - 1);
		}
		// System.out.println("ECFS");
		// checkLegal(pop, feval);

	}

	public void PDLS(individual pop, Evaluation feval) {
		double recordfit = pop.fit;
		for (int i = 1; i <= lstimes; i++) {
			ICFI(pop, feval);
			// System.out.println(pop.fit);
			ECFI(pop, feval);
			// System.out.println(pop.fit);
			ECFS(pop, feval);
			// System.out.println(pop.fit);
		}
	}

	public boolean recordbest(individual pop[], individual bestsofar) {
		int recordpos = -1;
		for (int i = 1; i < pop.length; i++) {
			if (pop[i].fit < bestsofar.fit) {
				recordpos = i;
				bestsofar.fit = pop[i].fit;
			}
		}
		if (recordpos != -1) {
			System.out.println("new best");
			function.copyIndividual(pop[recordpos], bestsofar);
			return true;
		}
		return false;
	}

	public void ElitistRetainStrategy(individual pop[], individual bestsofar) {

		double currentBest = Double.MAX_VALUE;
		int pos = 0;
		for (int i = 1; i <= ps; i++) {
			if (currentBest > pop[i].fit) {
				currentBest = pop[i].fit;
				pos = i;
			}
		}
		if (bestsofar.fit > currentBest) {
			function.copyIndividual(pop[pos], bestsofar);
		} else if (bestsofar.fit < currentBest) {
//			System.out.println("worst");
			int randpos = (int) Math.ceil(Math.random() * ps);
			function.copyIndividual(bestsofar, pop[randpos]);
		}
	}

	public void run() {
		Evaluation feval = new Evaluation();
		individual pop[] = new individual[ps + 1];
		individual mutatant[] = new individual[ps + 1];
		individual newpop[] = new individual[ps + 1];
		individual bestsofar = new individual(n, m, f);
		bestsofar.fit = Double.MAX_VALUE;
		for (int i = 0; i < newpop.length; i++) {
			mutatant[i] = new individual(n, m, f);
			newpop[i] = new individual(n, m, f);
		}
		initial(pop, feval);

		recordbest(pop, bestsofar);
		System.out.println("---------beigin----------");
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		for (int iter = 0; end - start < cpuTime; iter++) {
			PDLS(bestsofar, feval);
			ElitistRetainStrategy(pop, bestsofar);
			
			for (int i = 1; i <= ps; i++) {
				mutation(pop, mutatant[i], feval);
			}
			crossover(pop, mutatant, newpop, feval);
			selection(pop, newpop, feval);

			for (int i = 1; i <= ps; i++) {
				PDLS(pop[i], feval);
			}
			ElitistRetainStrategy(pop, bestsofar);
			System.out.println(bestsofar.fit);
			end = System.currentTimeMillis();
		}
		recordResualt(bestsofar);
		System.out.println(end-start);
		System.out.println("well done");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta120";
		params.readConfig(instance);
		DDE dde = new DDE(params, instance, 1);
		dde.run();
	}

}
