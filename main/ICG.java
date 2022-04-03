package main;

import java.util.ArrayList;
import java.util.Random;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

/**
 * Minimizing makespan for solving the distributed no-wait flowshop scheduling
 * problem
 * 
 * Shih-Wei Lin
 * 
 * 2016
 */

public class ICG extends Algorithm {

	public Hueristics hueristics;
	public int sigma = 2;
	public int destructType[];
	public double T0 = 2.5;
	public int Iiter = 2500;
	public double lambda = 0.9;
	public int alphamin = 2;
	public int Imaxnonimproving;

	public ICG(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		Imaxnonimproving = 3 * Iiter / 2;
		destructType = new int[2];
		AlgorithmName = "ICG";
	}

	public void initial(individual pop, Evaluation feval) {
		hueristics = new Hueristics();
		hueristics.HPF3(pop, feval);
		destructType[0] = sigma;
		destructType[1] = sigma;
	}

	public void destructionRandom(individual pop, ArrayList<Integer> selectjobs, int alpha, Evaluation feval) {
		int result[] = new int[2];
		Integer randnum;
		Random random = new Random();
		ArrayList<Integer> jobList = new ArrayList<Integer>();
		for (int i = 0; i < f; i++) {
			for (int j = 1; j < pop.vector.get(i).size(); j++) {
				jobList.add(pop.vector.get(i).get(j));
			}
		}

		for (int j = 1; j <= alpha; j++) {
			randnum = jobList.get(random.nextInt(jobList.size()));
			findJob(pop, result, randnum.intValue());
			selectjobs.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);
			jobList.remove(randnum);
		}
	}

	public void destructionblock(individual pop, ArrayList<Integer> selectjobs, int alpha, Evaluation feval) {
		// displayVector(pop);
		ArrayList<Integer> jobList = new ArrayList<Integer>();
		for (int i = 0; i < f; i++) {
			for (int j = 1; j < pop.vector.get(i).size(); j++) {
				jobList.add(pop.vector.get(i).get(j));
			}
		}
		// System.out.println();
		// System.out.println();
		// displayIntegerArray(jobList);
		Random random = new Random();
		// System.out.println("time "+((n - 1) - alpha));
		int randum = random.nextInt((n - 1) - alpha);
		randum = 12;
		// System.out.println("randum " + randum);
		int result[] = new int[2];
		int end = randum + alpha;
		if (end > jobList.size()) {
			end = jobList.size();
		}
		for (int i = randum; i < end; i++) {
			findJob(pop, result, jobList.get(i).intValue());
			selectjobs.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);
		}
	}

	public int destruction(individual pop, ArrayList<Integer> selectjobs, int alpha, Evaluation feval) {
		Random random = new Random();
		int randum = random.nextInt(pop.vector.get(pop.makespanFactoryNum).size() - 1) + 1;
		selectjobs.add(pop.vector.get(pop.makespanFactoryNum).get(randum));
		pop.vector.get(pop.makespanFactoryNum).remove(randum);
		int recordIndex[] = new int[n + 1];
		recordIndex[selectjobs.get(0)] = 1;

//		System.out.println("probability " + (double) destructType[0] / (destructType[0] + destructType[1]));

		if (Math.random() < (double) destructType[0] / (destructType[0] + destructType[1])) {
//			System.out.println("radom");
			destructionRandom(pop, selectjobs, alpha, feval);
			return 0;
		} else {
//			System.out.println("block");
			destructionblock(pop, selectjobs, alpha, feval);
			return 1;
		}
		// System.out.println("--------selsecjobs------");
		// displayIntegerArray(selectjobs);

	}

	public void construction(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		hueristics.NEH2_insertion(pop, selectjobs, feval);
	}

	public void run() {
		individual pop = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);
		individual tempindividual = new individual(n, m, f);
		Evaluation feval = new Evaluation();
		ArrayList<Integer> selectjobs = new ArrayList<Integer>();
		initial(pop, feval);
		function.copyIndividual(pop, bestsofar);
		function.copyIndividual(pop, tempindividual);

		System.out.println("------start algrorithm-------");

		double Temprature = T0;
		int alpha = alphamin, Inonimprove = 0;
		boolean improvement, flag1;
		int type = 0;

		long startime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		for (int i = 0; cpuTime > endtime - startime; i++) {
			improvement = false;
			flag1 = false;
			type = destruction(tempindividual, selectjobs, alpha, feval);
			construction(tempindividual, selectjobs, feval);
			if (tempindividual.fit < pop.fit) {
				flag1 = true;
				// System.out.println("improve");
				function.copyIndividual(tempindividual, pop);
				if (tempindividual.fit < bestsofar.fit) {
					improvement = true;
					function.copyIndividual(tempindividual, bestsofar);
				}
			} else if (Math.random() < Math.exp((pop.fit - tempindividual.fit) / Temprature)) {
				// System.out.println("jump");
				function.copyIndividual(tempindividual, pop);
				flag1 = true;
				// System.out.println("temp"+tempindividual.fit);
				// System.out.println("pop"+pop.fit);
			} else {
				function.copyIndividual(pop, tempindividual);
			}
			// Self-tuning mechanisms
			if (improvement) {
				alpha = alphamin;
				Inonimprove = 0;
			} else if (Inonimprove >= Imaxnonimproving) {
				alpha = alpha + 1;
				Inonimprove = 0; // 原文中没有说
				if (alpha >= 10) { // 原文中没有说 (有问题)
					alpha = alphamin;
				}
			} else {
				Inonimprove++;
			}
			if (flag1) {
				destructType[type]++;
			} else if (destructType[type] > sigma) {
				// System.out.println("nonimrpove");
				destructType[type]--;
			}

			if (i % Iiter == 0) {
				Temprature = Temprature * lambda;
			}
			endtime = System.currentTimeMillis();
			System.out.println("time"+(endtime-startime));
			//System.out.println(bestsofar.fit);
		}
		System.out.println("well done");
		System.out.println("time " + (endtime - startime));
		recordResualt(bestsofar);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta120";
		params.readConfig(instance);
		ICG icg = new ICG(params, instance, 1);
		icg.run();
	}

}
