package main;

import java.util.ArrayList;
import java.util.Collections;
import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

/**
 * Iterated greedy method for the distributed permutation flowshop scheduling
 * problem
 * 
 * Ruiz et l.2018
 */

public class IG2S extends Algorithm {

	public int d = 5;
	public double Tvalue = 0.2;
	public double rou = 0.95;
	public int d2 = 6;
	public double Temprature;

	public IG2S(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		AlgorithmName = "IG2S";
	}

	public void initial(individual pop, Evaluation feval) {
		Hueristics hueristics = new Hueristics();
		hueristics.HPF3(pop, feval);
		Temprature = hueristics.totalprocessingtime;
	}

	public void destruction(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		selectjobs.clear();
		int count = 0, randnum = 0, randum2 = 0;
		if (pop.vector.get(pop.makespanFactoryNum).size() - 1 < d / 2) {
			count = pop.vector.get(pop.makespanFactoryNum).size() - 1;
		} else {
			count = d / 2;
		}
		// displayVector(pop);
		// System.out.println();
		for (int i = 1; i <= count; i++) {
			randnum = (int) Math.ceil(Math.random() * (pop.vector.get(pop.makespanFactoryNum).size() - 1));
			selectjobs.add(pop.vector.get(pop.makespanFactoryNum).get(randnum));
			pop.vector.get(pop.makespanFactoryNum).remove(randnum);
		}
		int factory[] = new int[f];

		// System.out.println("fmax" + pop.makespanFactoryNum);
		for (int i = 0, k = 1; i < f; i++) { // 其余工厂
			if (i != pop.makespanFactoryNum) {
				factory[k] = i;
				// System.out.println(factory[k] + " ");
				k++;
			}
		}

		// displayVector(pop);
		// System.out.println();
		for (int i = 1, length = d - d / 2; i <= length; i++) {
			randum2 = (int) Math.ceil(Math.random() * (f - 1));
			while (pop.vector.get(factory[randum2]).size() - 1 == 0) { // 选择一个存在工厂的工件
				randum2 = (int) Math.ceil(Math.random() * (f - 1));
			}
			randnum = (int) Math.ceil(Math.random() * (pop.vector.get(factory[randum2]).size() - 1));
			// System.out.println("factory" + randum2);
			// System.out.println("postion" + randnum);
			selectjobs.add(pop.vector.get(factory[randum2]).get(randnum));
			pop.vector.get(factory[randum2]).remove(randnum);
		}
		// displayVector(pop);
		// System.out.println("---------seletjobs-----");
		// displayEachFactory(selectjobs);

		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			if (0 == pop.vector.get(i).size() - 1) { // 某一生产线没有工件加工
				pop.leaveTime[i] = new double[m + 1][n + 1];
				pop.tailTime[i] = new double[m + 1][n + 1];
				continue;
			}
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}
		// feval.displayLeavetime(pop);
	}

	public void construction(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		double bestmakespan, fit;
		int result[] = new int[1];
		int bestfactory = 0, bestposition = 0, removeposition = 0, count1;
		Integer removejob;
		double random, bestfit;
		double time[] = new double[m + 1];

		for (int j = 0; j < f; j++) {
			if (pop.vector.get(j).size() - 1 == 0) { // 工厂中不存在工件(特殊情况,原文中没有说明)
				pop.vector.get(j).add(selectjobs.get(0).intValue());
				fit = feval.evaluateSingleSequence(pop.vector.get(j), pop.leaveTime[j], pop.tailTime[j],
						pop.vector.get(j).size() - 1);
				if (fit > pop.fit) {
					pop.fit = fit;
					pop.makespanFactoryNum = j;
				}
				selectjobs.remove(0);
			}
		}

		// feval.displayLeavetime(pop);

		// System.out.println("pop.fit"+pop.fit);
		// System.out.println("===============================");
		for (int i = 0, length = selectjobs.size(); i < length; i++) {
			bestmakespan = Double.MAX_VALUE;
			for (int j = 0; j < f; j++) {
				fit = insert(pop.fit, selectjobs.get(0).intValue(), pop.leaveTime[j], pop.tailTime[j], feval,
						pop.vector.get(j).size(), result); // the number of
															// positions
				// System.out.println("fit"+fit);
				if (bestmakespan > fit) {
					bestmakespan = fit;
					bestfactory = j;
					bestposition = result[0];
				}
			}

			// System.out.println("bestfactory"+bestfactory);
			// System.out.println("bestpostion"+bestposition);
			pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
			selectjobs.remove(0);
			// feval.evaluateSingleSequence(pop.vector.get(bestfactory),
			// pop.leaveTime[bestfactory], pop.tailTime[bestfactory],
			// pop.vector.get(bestfactory).size()-1);
			// pop.fit = bestmakespan;
			// displayVector(pop);
			// 移除前后位置
			random = Math.random();
			// System.out.println("random"+random);
			count1 = pop.vector.get(bestfactory).size() - 1;
			if (random > 0.5 && bestposition != count1) {
				removeposition = bestposition + 1;
			} else if (random < 0.5 && bestposition != 1) {
				removeposition = bestposition - 1;
			} else if (bestposition == count1) {
				removeposition = bestposition - 1;
			} else if (bestposition == 1) {
				removeposition = bestposition + 1;
			}
			// System.out.println("remvoeposition"+removeposition);
			removejob = pop.vector.get(bestfactory).get(removeposition);
			pop.vector.get(bestfactory).remove(removeposition);
			//
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			bestfit = Double.MAX_VALUE;
			for (int j = 1, length2 = pop.vector.get(bestfactory).size(); j <= length2; j++) {
				fit = feval.quickEvaluation(removejob.intValue(), j, length2 - 1, time, pop.leaveTime[bestfactory],
						pop.tailTime[bestfactory]);
				// System.out.println("fit" + fit);
				if (fit < bestfit) {
					bestfit = fit;
					bestposition = j;
				}
			}
			pop.vector.get(bestfactory).add(bestposition, removejob);
			fit = feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			// System.out.println("fittest"+fit);
			// System.out.println("bestfit"+bestfit);
			// 更新最优解
			updatefitness(pop);
			// System.out.println();
			// System.out.println();
		}
	}

	public double insert(double Cmax, int job, double leavetime[][], double tailtime[][], Evaluation feval, int length,
			int result[]) {
		double fit, bestfit;
		bestfit = Double.MAX_VALUE;
		int bestpos = 0;
		double time[] = new double[m + 1];
		for (int j = 1; j <= length; j++) {
			fit = feval.quickEvaluation(job, j, length - 1, time, leavetime, tailtime);
			// System.out.println("insert fit" + fit);
			if (fit < bestfit) {
				bestfit = fit;
				bestpos = j;
			}
		}
		result[0] = bestpos;
		if (Cmax > bestfit) {
			bestfit = Cmax;
		}
		return bestfit;
	}

	public void LS3(individual pop, Evaluation feval) {
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
		Collections.shuffle(arr);
		while (cnt < pop.vector.get(pop.makespanFactoryNum).size() - 1) {
			// displayEachFactory(pop.vector.get(pop.makespanFactoryNum));
			if (cnt == 0) {
				// 重新生成随机序列
				arr.clear();
				for (int i = 1, length = pop.vector.get(pop.makespanFactoryNum).size() - 1; i <= length; i++) {
					Integer temp = new Integer(pop.vector.get(pop.makespanFactoryNum).get(i).intValue());
					recordIndex[pop.vector.get(pop.makespanFactoryNum).get(i).intValue()] = i; // 索引
					arr.add(temp);
				}
				Collections.shuffle(arr);
			}
			// displayEachFactory(arr);
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
				bestfit = Double.MAX_VALUE;
				for (int j = 1, length = pop.vector.get(i).size(); j <= length; j++) {
					if (i == recordfactory && j == recordIndex[arr.get(cnt)]) { // 原来的插入位置
						continue;
					}
					fit = feval.quickEvaluation(removejob.intValue(), j, length - 1, time, pop.leaveTime[i],
							pop.tailTime[i]);
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

			if (Cmax < recordfitness) {
				// System.out.println("improve");
				pop.vector.get(bestfactory).add(bestposition2, removejob);
				feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
						pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
				cnt = 0;
				updatefitness(pop);
			} else {
				// System.out.println("no improve");
				pop.vector.get(recordfactory).add(recordIndex[arr.get(cnt)], removejob);
				feval.evaluateSingleSequence(pop.vector.get(recordfactory), pop.leaveTime[recordfactory],
						pop.tailTime[recordfactory], pop.vector.get(recordfactory).size() - 1);
				updatefitness(pop);
				cnt++;
			}
//			System.out.println("Cmax" + Cmax);
			// System.out.println("--------------result------------");
			// displayVector(pop);
			// feval.blockFeval(pop);
			// System.out.println(pop.fit);
			// System.out.println("-----------------------------------------------------------------------------");
		}
	}

	public void destruction_construction_locolsearch(ArrayList<Integer> pop, double leavetime[][], double tailtime[][],
			ArrayList<Integer> selectedjob, Evaluation feval) {
		int selectCount = d2;
		int randnum, count = pop.size() - 1;
		double bestfit = 0, fit = 0;
		double time[] = new double[m + 1];
		double random;
		int bestposition = 0, removeposition = 0;
		Integer temp;

		selectedjob.clear();
		if (pop.size() - 1 < d2) {
			selectCount = pop.size() - 1;
		}
		// System.out.println("select" + selectCount);
		for (int i = 1; i <= selectCount; i++) {
			randnum = (int) Math.ceil(Math.random() * count);
			// System.out.println("selectjob" + pop.get(randnum));
			selectedjob.add(pop.get(randnum));
			pop.remove(randnum);
			count--;
		}
		feval.evaluateSingleSequence(pop, leavetime, tailtime, pop.size() - 1);
		Collections.shuffle(selectedjob);
		// for (int i = 0; i < selectCount; i++) {
		// System.out.print(selectedjob.get(i).intValue() + " ");
		// }
		// System.out.println();
		// construction
		for (int i = 0; i < selectCount; i++) {
			if (pop.size() - 1 == 0) {
				pop.add(selectedjob.get(0));
				selectedjob.remove(0);
				feval.evaluateLeavetime(pop, leavetime, tailtime, 1);
				continue;
			}
			bestfit = Double.MAX_VALUE;
			count = pop.size();
			for (int j = 1, length = pop.size(); j <= length; j++) {
				fit = feval.quickEvaluation(selectedjob.get(0).intValue(), j, length - 1, time, leavetime, tailtime);
				if (bestfit > fit) {
					bestfit = fit;
					bestposition = j;
				}
			}
			pop.add(bestposition, selectedjob.get(0));
			// feval.evaluateSingleSequence(pop, leavetime, tailtime, pop.size()
			// - 1);
			selectedjob.remove(0);
			// System.out.println("testfit2 "+leavetime[m][pop.size()-1]);
			random = Math.random();
			if (random > 0.5 && bestposition != count) {
				removeposition = bestposition + 1;
			} else if (random < 0.5 && bestposition != 1) {
				removeposition = bestposition - 1;
			} else if (bestposition == count) {
				removeposition = bestposition - 1;
			} else if (bestposition == 1) {
				removeposition = bestposition + 1;
			}
			temp = pop.get(removeposition);
			pop.remove(removeposition);

			feval.evaluateSingleSequence(pop, leavetime, tailtime, pop.size() - 1);
			bestfit = Double.MAX_VALUE;
			for (int j = 1, length = pop.size(); j <= length; j++) {
				fit = feval.quickEvaluation(temp.intValue(), j, length - 1, time, leavetime, tailtime);
				if (bestfit > fit) {
					bestfit = fit;
					bestposition = j;
				}
			}
			pop.add(bestposition, temp);
			feval.evaluateSingleSequence(pop, leavetime, tailtime, pop.size() - 1);
		}
		// System.out.println("bestfit"+bestfit);
		// System.out.println("testfit"+leavetime[m][pop.size()-1]);
		// displayEachFactory(pop);

		// local search
		LS2(pop, leavetime, tailtime, feval);

	}

	public void LS2(ArrayList<Integer> jobseqeunce, double leavetime[][], double tailtime[][], Evaluation feval) {
		int count = jobseqeunce.size() - 1;
		int vector[] = new int[count + 1];
		for (int i = 1; i < vector.length; i++) {
			vector[i] = jobseqeunce.get(i).intValue();
		}
		function.shuffleArray(vector, count); // 随机生成工件
		int jobIndex[] = new int[n + 1]; // 工件的位置索引
		for (int i = 1; i <= count; i++) {
			jobIndex[jobseqeunce.get(i).intValue()] = i;
		}
		int removejobpos, bestpos = 0;
		double fit, bestfit, makespan;
		makespan = leavetime[m][count];
		Integer temp;
		double time[] = new double[m + 1];

		for (int i = 1; i <= count; i++) {
			removejobpos = jobIndex[vector[i]];
			// System.out.println("removejpos "+removejobpos);
			// System.out.println("removejobs "+vector[i]);
			temp = jobseqeunce.get(removejobpos);
			jobseqeunce.remove(removejobpos);
			feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count - 1);
			bestfit = feval.quickEvaluation(vector[i], count, count, time, leavetime, tailtime);
			bestpos = count;
			for (int j = count - 1; j >= 1; j--) {
				if (j == removejobpos) {
					continue;
				}
				fit = feval.quickEvaluation(vector[i], j, count, time, leavetime, tailtime);
				// System.out.println("fit"+fit);
				if (bestfit > fit) {
					bestfit = fit;
					bestpos = j;
				}
			}
			// System.out.println("bestfit"+bestfit);
			if (bestfit < makespan) {
				temp = new Integer(vector[i]);
				jobseqeunce.add(bestpos, temp);
				feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count);
				makespan = leavetime[m][count];
				// System.out.println("makespan"+makespan);
				for (int k = 1; k <= count; k++) {
					jobIndex[jobseqeunce.get(k).intValue()] = k;
				}
			} else {
				temp = new Integer(vector[i]);
				jobseqeunce.add(removejobpos, temp);
			}
			// System.out.println();
		}
		feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count);
	}

	public void IG(individual pop, int cputime, Evaluation feval) {

		ArrayList<Integer> selectjobs = new ArrayList<Integer>();
		individual tempindividual = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);
		function.copyIndividual(pop, tempindividual);
		function.copyIndividual(pop, bestsofar);
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		for (int i = 0; cputime>end-start; i++) {
			destruction(tempindividual, selectjobs, feval);
			construction(tempindividual, selectjobs, feval);
			LS3(tempindividual, feval);
//			displayVector(tempindividual);
			if (tempindividual.fit < pop.fit) {
				function.copyIndividual(tempindividual, pop);
				if (tempindividual.fit < bestsofar.fit) {
					function.copyIndividual(tempindividual, bestsofar);
				}
			} else if (Math.random() < Math.exp((pop.fit - tempindividual.fit) / Temprature)) {
//				System.out.println("jump");
				function.copyIndividual(tempindividual, pop);
				// System.out.println("temp"+tempindividual.fit);
				// System.out.println("pop"+pop.fit);
			} else {
				function.copyIndividual(pop, tempindividual);
			}
			end = System.currentTimeMillis();
			System.out.println(bestsofar.fit);
		}
		function.copyIndividual(bestsofar, pop);
	}

	public void IG2(individual pop, int cputime, Evaluation feval) {

		int fmax = pop.makespanFactoryNum;
		ArrayList<Integer> selectjob = new ArrayList<Integer>();
		ArrayList<Integer> jobsequnce = new ArrayList<Integer>();
		double leavetime[][] = new double[m + 1][n + 1];
		double tailtime[][] = new double[m + 1][n + 1];
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		
		for (int i = 0; cputime>end-start; i++) {

			copyFactory(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax], jobsequnce, leavetime, tailtime);

			destruction_construction_locolsearch(jobsequnce, leavetime, tailtime,
					selectjob, feval);
			if (pop.leaveTime[fmax][m][pop.vector.get(fmax).size()-1]>leavetime[m][jobsequnce.size()-1]) {
				copyFactory(jobsequnce, leavetime, tailtime, pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax]);
			}
			updatefitness(pop);
			fmax = pop.makespanFactoryNum;
			end = System.currentTimeMillis();
			System.out.println(pop.fit);
		}
		
	}

	public void copyFactory(ArrayList<Integer> pop, double leavetime1[][], double tailtime1[][],
			ArrayList<Integer> jobsequence, double leavetime2[][], double tailtime2[][]) {
		jobsequence.clear();
		Integer temp = new Integer(0);
		jobsequence.add(temp);

		for (int i = 1; i < pop.size(); i++) {
			temp = new Integer(pop.get(i).intValue());
			jobsequence.add(temp);
		}

		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				leavetime2[i][j] = leavetime1[i][j];
				tailtime2[i][j] = tailtime1[i][j];
			}
		}
	}

	public void run() {
		individual pop = new individual(n, m, f);
		Evaluation feval = new Evaluation();
		System.out.println("-------start-----");
		System.out.println(Temprature);
		initial(pop, feval);
		
		Temprature = Tvalue * Temprature / (n * m * 10);
//		System.out.println(Temprature);
		int time = cpuTime * 95 / 100;
		System.out.println("time" + time);

		IG(pop, time, feval);
//      System.out.println("------second stage------");
		time = cpuTime - time;
		IG2(pop, time, feval);

		System.out.println("well done");
		displayVector(pop);
		recordResualt(pop);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta120";
		params.readConfig(instance);
		IG2S ig2s = new IG2S(params, instance, 1);
		ig2s.run();
	}

}
