package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;
import libs.weightIndividual;

/**
 * Minimising makespan in distributed permutation flowshops using a modified iterated greedy algorithm
 * 
 * Lin and Ying
 * 
 * 2013 
 * */

public class MIG extends Algorithm {

	public int alphamin = 2;
	public int aphamax = 7;
	public double T0 = 5;
	public double lambda = 0.9;
	public int Iiter = 1000;
	public int Norimproving = 8;
	public double Temprature;

	public MIG(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		AlgorithmName = "MIG";
	}

	public void initial(individual pop, Evaluation feval) {
		Hueristics hueristics = new Hueristics();
		hueristics.HPF3(pop, feval);
	}

	public void destruction(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		Random rand = new Random();
		int alpha = rand.nextInt(aphamax - alphamin + 1) + alphamin;
		int selectjob = 0;
		int remainjob = 0;
		int randnum;
		int result[] = new int[2];
//		System.out.println("alpha" + alpha);
		weightIndividual factory[] = new weightIndividual[f];

		int recordIndex[] = new int[n + 1];

		for (int i = 0; i < f; i++) {
			factory[i] = new weightIndividual();
			factory[i].weight = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			factory[i].num = i;
		}
		Arrays.sort(factory, function.recompareWeightIndividualMaxtoMin());
//		for (int i = 0; i < factory.length; i++) {
//			System.out.println(factory[i].weight + " " + factory[i].num);
//		}
		if (alpha < f) {
			selectjob = alpha;
		} else {
			selectjob = f;
			remainjob = alpha - f;
		}
//		System.out.println("selectjob " + selectjob);
//		System.out.println("remainjob " + remainjob);
		for (int i = 0; i < selectjob; i++) {
			randnum = (int) Math.ceil(Math.random() * (pop.vector.get(factory[i].num).size() - 1));
			selectjobs.add(pop.vector.get(factory[i].num).get(randnum));
			recordIndex[pop.vector.get(factory[i].num).get(randnum)] = 1;
			pop.vector.get(factory[i].num).remove(randnum);
		}
		for (int i = 1; i <= remainjob; i++) {
			randnum = (int) Math.ceil(n * Math.random()); // 采样的是job
			while (recordIndex[randnum] == 1) {
				randnum = (int) Math.ceil(n * Math.random());
			}
			recordIndex[randnum] = 1;
			findJob(pop, result, randnum);
			selectjobs.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);
		}

		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			if (0 == pop.vector.get(i).size() - 1) { // 某一生产线没有工件加工
				pop.vector.get(i).add(selectjobs.get(0));
				selectjobs.remove(0);
			}
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}
//		displayIntegerArray(selectjobs);
	}

	public void construction(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		double bestmakespan = 0, Cmax, bestfit, fit;
		double time[] = new double[m + 1];
		int bestfactory = 0, bestposition = 0, bestpos = 0;
		for (int i = 0, length = selectjobs.size(); i < length; i++) {
			bestmakespan = Double.MAX_VALUE;
			Cmax = pop.fit;
			// System.out.println("Cmax"+Cmax);
			for (int g = 0; g < f; g++) {
				// fit = insert(pop.fit, selectjobs.get(0).intValue(),
				// pop.leaveTime[j], pop.tailTime[j], feval,
				// pop.vector.get(j).size(), result); // the number of
				bestfit = Double.MAX_VALUE;
				for (int j = 1, length2 = pop.vector.get(g).size(); j <= length2; j++) {
					fit = feval.quickEvaluation(selectjobs.get(0), j, length2 - 1, time, pop.leaveTime[g],
							pop.tailTime[g]);
					// System.out.println("insert fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
				if (Cmax > bestfit) {
					bestfit = Cmax;
				}
				// System.out.println("bestfit "+bestfit);
				// positions
				// System.out.println("fit"+fit);
				if (bestmakespan > bestfit) {
					bestmakespan = bestfit;
					bestfactory = g;
					bestposition = bestpos;
				}
			}
			// System.out.println("bestfactory "+bestfactory);
			// System.out.println("bestpostion "+bestposition);
			pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
			selectjobs.remove(0);
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			updatefitness(pop);
			// displayVector(pop);
			// System.out.println();
			// System.out.println();
		}
	}

	public void run() {
		Evaluation feval = new Evaluation();
		individual pop = new individual(n, m, f);
		individual tempindividual = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);

		initial(pop, feval);
		displayVector(pop);
//		System.out.println(pop.fit);
		ArrayList<Integer> selectjobs = new ArrayList<Integer>();

		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		Temprature = 5;
		function.copyIndividual(pop, bestsofar);
		function.copyIndividual(pop, tempindividual);
		for (int i = 1; endtime-starttime<cpuTime; i++) {
			destruction(tempindividual, selectjobs, feval);
			construction(tempindividual, selectjobs, feval);

			if (tempindividual.fit < bestsofar.fit) {
				function.copyIndividual(tempindividual, bestsofar);
				function.copyIndividual(tempindividual, pop);
			} else if (tempindividual.fit <= pop.fit) {
//				System.out.println("equal");
				function.copyIndividual(tempindividual, pop);
			} else if (tempindividual.fit > pop.fit) {
				// double acceptance = acception();
				// System.out.println("acceptance " + acceptance);
				// System.out.println("propaties " + (Math.exp((pop.fit -
				// tempindividual.fit) / Temprature)));
				if (Math.random() < (Math.exp((pop.fit - tempindividual.fit) / Temprature))) {
					System.out.println("jump");
					function.copyIndividual(tempindividual, pop);
				} else {
					function.copyIndividual(pop, tempindividual);
				}
			}
			
			if (i % Iiter == 0) {
//				System.out.println("improve");
				Temprature = Temprature*lambda;
			}
//			System.out.println("Temprature "+Temprature);
			System.out.println(bestsofar.fit);
			endtime = System.currentTimeMillis();
		}
		System.out.println("well done");
		System.out.println("time"+(endtime-starttime));
		displayVector(bestsofar);
		recordResualt(bestsofar);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta1";
		params.readConfig(instance);
		MIG mig = new MIG(params, instance, 7);
		mig.run();
	}

}
