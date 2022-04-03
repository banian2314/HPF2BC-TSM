package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

public class WOA extends Algorithm {

	public double Temprature;
	public int NP = 10; 
	public int S = 1;
	public int d = 4; 
	public Hueristics hueristics;
	public int dmain[];
	public double tvalue = 0.2;

	public WOA(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		hueristics = new Hueristics();
		Temprature = tvalue * hueristics.totalprocessingtime / (n * m * 10);
		AlgorithmName = "WOA";

		dmain = new int[3];
		dmain[0] = 3;
		dmain[1] = 4;
		dmain[2] = 5;
	}

	
	public void initial(individual pop[], individual offspring[], Evaluation feval) {
		pop[0] = new individual(n, m, f);
//		hueristics.HPF3(pop[0], feval);
		hueristics.PFS_2BC(pop[0], feval);
		for (int i = 1; i < NP; i++) {
			pop[i] = new individual(n, m, f);
			function.copyIndividual(pop[0], pop[i]);
		}
		for (int i = 0; i < NP * S; i++) {
			offspring[i] = new individual(n, m, f);
		}
	}

	public void destruction(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		int randomnum;
	
		selectjobs.clear();
		randomnum = (int) Math.ceil(Math.random() * (pop.vector.get(pop.makespanFactoryNum).size() - 1));
		selectjobs.add(pop.vector.get(pop.makespanFactoryNum).get(randomnum));
		int record[] = new int[n + 1];
		record[selectjobs.get(0)] = 1;
		pop.vector.get(pop.makespanFactoryNum).remove(randomnum);
		int result[] = new int[2];
		// System.out.println("---------selectjobs-------");
		// displayIntegerArray(selectjobs);
		Random random = new Random();

		for (int i = 1; i <= d - 1; i++) {
			randomnum = (int) Math.ceil(n * Math.random());  
			while (record[randomnum] == 1) {
				randomnum = (int) Math.ceil(n * Math.random());
			}
			record[randomnum] = 1;
			findJob(pop, result, randomnum);
			selectjobs.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);
		}

		// System.out.println("----------pop--------");
		// displayVector(pop);
		// System.out.println("---------selectjobs-------");
		// displayIntegerArray(selectjobs);
	}
	
	public void destruction1(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		int randomnum;
		int d=8;
		selectjobs.clear();
		randomnum = (int) Math.ceil(Math.random() * (pop.vector.get(pop.makespanFactoryNum).size() - 1));
		selectjobs.add(pop.vector.get(pop.makespanFactoryNum).get(randomnum));
		int record[] = new int[n + 1];
		record[selectjobs.get(0)] = 1;
		pop.vector.get(pop.makespanFactoryNum).remove(randomnum);
		int result[] = new int[2];
		// System.out.println("---------selectjobs-------");
		// displayIntegerArray(selectjobs);
		Random random = new Random();

		for (int i = 1; i <= d - 1; i++) {
			randomnum = (int) Math.ceil(n * Math.random()); // 采样的是job
			while (record[randomnum] == 1) {
				randomnum = (int) Math.ceil(n * Math.random());
			}
			record[randomnum] = 1;
			findJob(pop, result, randomnum);
			selectjobs.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);
		}

		// System.out.println("----------pop--------");
		// displayVector(pop);
		// System.out.println("---------selectjobs-------");
		// displayIntegerArray(selectjobs);
	}

	public void destruction2(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		int randomnum;
		selectjobs.clear();

		randomnum = (int) Math.ceil(Math.random() * (pop.vector.get(pop.makespanFactoryNum).size() - 1));
		selectjobs.add(pop.vector.get(pop.makespanFactoryNum).get(randomnum));
		int record[] = new int[n + 1];
		record[selectjobs.get(0)] = 1;
		pop.vector.get(pop.makespanFactoryNum).remove(randomnum);
		int result[] = new int[2];
		// System.out.println("---------selectjobs-------");
		// displayIntegerArray(selectjobs);
		int randum2, randnum;
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
		for (int i = 1, length = d - 1; i <= length; i++) {
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
	}

	public void construction_tieFirst(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		double bestmakespan = 0, fit = 0;
		int bestfactory = 0, bestposition = 0;
		int result[] = new int[1];
		double secondIndex[] = new double[1];
		pop.fit = Double.MIN_VALUE;
		ArrayList<Integer> frontandbackList = new ArrayList<Integer>();

		for (int i = 0; i < f; i++) { // 更新每个工厂时间矩阵
			if (0 == pop.vector.get(i).size() - 1) { // 某一生产线没有工件加工, 工件直接加入
				pop.vector.get(i).add(selectjobs.get(0));
				selectjobs.remove(0);
			}
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}

		for (int i = 0, length = selectjobs.size(); i < length; i++) {
			bestmakespan = Double.MAX_VALUE;
			for (int j = 0; j < f; j++) {
				fit = insert(pop.fit, selectjobs.get(0).intValue(), secondIndex, pop.leaveTime[j], pop.tailTime[j],
						feval, pop.vector.get(j).size(), result);
				if (bestmakespan > fit) {
					bestmakespan = fit;
					bestfactory = j;
					bestposition = result[0];
				}
			}
			pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
			selectjobs.remove(0);
			frontandbackList.clear();
			if (bestposition == 1) {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition + 1));
				pop.vector.get(bestfactory).remove(bestposition + 1);
				// System.out.println("job1 " + frontandbackList.get(0));
			} else if (bestposition == pop.vector.get(bestfactory).size() - 1) {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition - 1));
				pop.vector.get(bestfactory).remove(bestposition - 1);
				// System.out.println("job2 " + frontandbackList.get(0));
			} else {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition - 1));
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition + 1));
				pop.vector.get(bestfactory).remove(bestposition + 1);
				pop.vector.get(bestfactory).remove(bestposition - 1);
				// System.out.println("job1 " + frontandbackList.get(0));
				// System.out.println("job2 " + frontandbackList.get(1));
			}

			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			updatefitness(pop);
			for (int g = 0, length2 = frontandbackList.size(); g < length2; g++) {
				bestmakespan = Double.MAX_VALUE;
				for (int j = 0; j < f; j++) {
					fit = insert(pop.fit, frontandbackList.get(0).intValue(), secondIndex, pop.leaveTime[j],
							pop.tailTime[j], feval, pop.vector.get(j).size(), result); // the
																						// number
																						// of
																						// positions
					// System.out.println("fit "+fit);
					if (bestmakespan > fit) {
						bestmakespan = fit;
						bestfactory = j;
						bestposition = result[0];
					}
				}
				// System.out.println("bestmakespan "+bestmakespan);
				// System.out.println("bestfactory" + bestfactory);
				// System.out.println("bestpostion" + bestposition);
				pop.vector.get(bestfactory).add(bestposition, frontandbackList.get(0));
				frontandbackList.remove(0);
				feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
						pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
				updatefitness(pop);
			}
			// displayVector(pop);
		}
		// System.out.println("-------construction results--------");
		// displayVector(pop);
		// checkLegal(pop, feval);
		// System.out.println(pop.fit);
	}

	public void construction_tieLast(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		double bestmakespan = 0, fit = 0;
		int bestfactory = 0, bestposition = 0;
		int result[] = new int[1];
		double secondIndex[] = new double[1];
		pop.fit = Double.MIN_VALUE;
		ArrayList<Integer> frontandbackList = new ArrayList<Integer>();
		for (int i = 0; i < f; i++) { // 更新每个工厂时间矩阵
			if (0 == pop.vector.get(i).size() - 1) { // 某一生产线没有工件加工, 工件直接加入
				pop.vector.get(i).add(selectjobs.get(0));
				selectjobs.remove(0);
			}
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}
		for (int i = 0, length = selectjobs.size(); i < length; i++) {
			bestmakespan = Double.MAX_VALUE;
			for (int j = f - 1; j >= 1; j--) {
				fit = insert(pop.fit, selectjobs.get(0).intValue(), secondIndex, pop.leaveTime[j], pop.tailTime[j],
						feval, pop.vector.get(j).size(), result);
				if (bestmakespan > fit) {
					bestmakespan = fit;
					bestfactory = j;
					bestposition = result[0];
				}
			}
			pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
			selectjobs.remove(0);
			frontandbackList.clear();
			if (bestposition == 1) {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition + 1));
				pop.vector.get(bestfactory).remove(bestposition + 1);
				// System.out.println("job1 " + frontandbackList.get(0));
			} else if (bestposition == pop.vector.get(bestfactory).size() - 1) {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition - 1));
				pop.vector.get(bestfactory).remove(bestposition - 1);
				// System.out.println("job2 " + frontandbackList.get(0));
			} else {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition - 1));
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition + 1));
				pop.vector.get(bestfactory).remove(bestposition + 1);
				pop.vector.get(bestfactory).remove(bestposition - 1);
				// System.out.println("job1 " + frontandbackList.get(0));
				// System.out.println("job2 " + frontandbackList.get(1));
			}

			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			updatefitness(pop);
			for (int g = 0, length2 = frontandbackList.size(); g < length2; g++) {
				bestmakespan = Double.MAX_VALUE;
				for (int j = f - 1; j >= 1; j--) {
					fit = insert(pop.fit, frontandbackList.get(0).intValue(), secondIndex, pop.leaveTime[j],
							pop.tailTime[j], feval, pop.vector.get(j).size(), result);
					// System.out.println("fit "+fit);
					if (bestmakespan > fit) {
						bestmakespan = fit;
						bestfactory = j;
						bestposition = result[0];
					}
				}
				// System.out.println("bestmakespan "+bestmakespan);
				// System.out.println("bestfactory" + bestfactory);
				// System.out.println("bestpostion" + bestposition);
				pop.vector.get(bestfactory).add(bestposition, frontandbackList.get(0));
				frontandbackList.remove(0);
				feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
						pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
				updatefitness(pop);
			}
			// displayVector(pop);
		}
		// System.out.println("-------construction results--------");
		// displayVector(pop);
		// checkLegal(pop, feval);
		// System.out.println(pop.fit);
	}

	public void construction_minMakespantie(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		double bestmakespan = 0, fit = 0;
		int bestfactory = 0, bestposition = 0;
		int result[] = new int[1];
		double secondIndex[] = new double[1];
		double secondIndexBestValue = 0;
		pop.fit = Double.MIN_VALUE;
		ArrayList<Integer> frontandbackList = new ArrayList<Integer>();
		Collections.shuffle(selectjobs);
		// displayIntegerArray(selectjobs);
		for (int i = 0; i < f; i++) { // 更新每个工厂时间矩阵
			if (0 == pop.vector.get(i).size() - 1) { // 某一生产线没有工件加工, 工件直接加入
				pop.vector.get(i).add(selectjobs.get(0));
				selectjobs.remove(0);
			}
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}
		for (int i = 0, length = selectjobs.size(); i < length; i++) {
			// System.out.println("insert job " + selectjobs.get(0));
			// feval.displayLeavetime(pop);
			// displayVector(pop);
			bestmakespan = Double.MAX_VALUE;
			for (int j = 0; j < f; j++) {
				fit = insert(pop.fit, selectjobs.get(0).intValue(), secondIndex, pop.leaveTime[j], pop.tailTime[j],
						feval, pop.vector.get(j).size(), result);
				// System.out.println("fit" + fit);
				// System.out.println("second " + secondIndex[0]);
				if (bestmakespan > fit) {
					bestmakespan = fit;
					bestfactory = j;
					bestposition = result[0];
					secondIndexBestValue = secondIndex[0];
				} else if (bestmakespan == fit) {
					if (secondIndex[0] < secondIndexBestValue) {
						bestmakespan = fit;
						bestfactory = j;
						bestposition = result[0];
						secondIndexBestValue = secondIndex[0];
					}
				}
			}
			// System.out.println("bestmakespan " + bestmakespan);
			// System.out.println("bestfactory " + bestfactory);
			// System.out.println("bestposition " + bestposition);
			pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
			selectjobs.remove(0);
			// displayVector(pop);
			frontandbackList.clear();
			if (bestposition == 1) {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition + 1));
				pop.vector.get(bestfactory).remove(bestposition + 1);
				// System.out.println("job1 " + frontandbackList.get(0));
			} else if (bestposition == pop.vector.get(bestfactory).size() - 1) {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition - 1));
				pop.vector.get(bestfactory).remove(bestposition - 1);
				// System.out.println("job2 " + frontandbackList.get(0));
			} else {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition - 1));
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition + 1));
				pop.vector.get(bestfactory).remove(bestposition + 1);
				pop.vector.get(bestfactory).remove(bestposition - 1);
				// System.out.println("job1 " + frontandbackList.get(0));
				// System.out.println("job2 " + frontandbackList.get(1));
			}
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			updatefitness(pop);
			for (int g = 0, length2 = frontandbackList.size(); g < length2; g++) {
				bestmakespan = Double.MAX_VALUE;
				for (int j = 0; j < f; j++) {
					fit = insert(pop.fit, frontandbackList.get(0).intValue(), secondIndex, pop.leaveTime[j],
							pop.tailTime[j], feval, pop.vector.get(j).size(), result);
					// System.out.println("fit "+fit);
					if (bestmakespan > fit) {
						bestmakespan = fit;
						bestfactory = j;
						bestposition = result[0];
						secondIndexBestValue = secondIndex[0];
					} else if (bestmakespan == fit) {
						if (secondIndex[0] < secondIndexBestValue) {
							// System.out.println("improve");
							bestmakespan = fit;
							bestfactory = j;
							bestposition = result[0];
							secondIndexBestValue = secondIndex[0];
						}
					}
				}
				// System.out.println("bestmakespan "+bestmakespan);
				// System.out.println("bestfactory" + bestfactory);
				// System.out.println("bestpostion" + bestposition);
				pop.vector.get(bestfactory).add(bestposition, frontandbackList.get(0));
				frontandbackList.remove(0);
				feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
						pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
				updatefitness(pop);
				// System.out.println();
				// System.out.println();
				// System.out.println();
			}
			// displayVector(pop);
		}
		// System.out.println("-------construction results--------");
		// feval.displayLeavetime(pop);
		// displayVector(pop);
		// checkLegal(pop, feval);
		// System.out.println(pop.fit);
	}

	public void construction_tieRandom(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		double bestmakespan = 0, fit = 0;
		int bestfactory = 0, bestposition = 0;
		int result[] = new int[1];
		double secondIndex[] = new double[1];
		pop.fit = Double.MIN_VALUE;
		ArrayList<Integer> frontandbackList = new ArrayList<Integer>();
		int reocordTie[][] = new int[n + 1][2];
		int count;
		int randnum;

		for (int i = 0; i < f; i++) { // 更新每个工厂时间矩阵
			if (0 == pop.vector.get(i).size() - 1) { // 某一生产线没有工件加工, 工件直接加入
				pop.vector.get(i).add(selectjobs.get(0));
				selectjobs.remove(0);
			}
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}

		for (int i = 0, length = selectjobs.size(); i < length; i++) {
			bestmakespan = Double.MAX_VALUE;
			count = 0;
			for (int j = 0; j < f; j++) {
				fit = insert(pop.fit, selectjobs.get(0).intValue(), secondIndex, pop.leaveTime[j], pop.tailTime[j],
						feval, pop.vector.get(j).size(), result);
				// System.out.println("fit "+fit);
				if (bestmakespan > fit) {
					bestmakespan = fit;
					bestfactory = j;
					bestposition = result[0];
					count = 1;
					reocordTie[count][0] = bestfactory;
					reocordTie[count][1] = bestposition;
				} else if (bestmakespan == fit) {
					count++;
					reocordTie[count][0] = j;
					reocordTie[count][1] = result[0];
				}
			}

			randnum = (int) Math.ceil(Math.random() * count);
			bestfactory = reocordTie[randnum][0];
			bestposition = reocordTie[randnum][1];

			pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
			selectjobs.remove(0);
			frontandbackList.clear();
			if (bestposition == 1) {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition + 1));
				pop.vector.get(bestfactory).remove(bestposition + 1);
				// System.out.println("job1 " + frontandbackList.get(0));
			} else if (bestposition == pop.vector.get(bestfactory).size() - 1) {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition - 1));
				pop.vector.get(bestfactory).remove(bestposition - 1);
				// System.out.println("job2 " + frontandbackList.get(0));
			} else {
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition - 1));
				frontandbackList.add(pop.vector.get(bestfactory).get(bestposition + 1));
				pop.vector.get(bestfactory).remove(bestposition + 1);
				pop.vector.get(bestfactory).remove(bestposition - 1);
				// System.out.println("job1 " + frontandbackList.get(0));
				// System.out.println("job2 " + frontandbackList.get(1));
			}

			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			updatefitness(pop);
			for (int g = 0, length2 = frontandbackList.size(); g < length2; g++) {
				bestmakespan = Double.MAX_VALUE;
				count = 0;
				for (int j = 0; j < f; j++) {
					fit = insert(pop.fit, frontandbackList.get(0).intValue(), secondIndex, pop.leaveTime[j],
							pop.tailTime[j], feval, pop.vector.get(j).size(), result);
					// System.out.println("fit "+fit);
					if (bestmakespan > fit) {
						bestmakespan = fit;
						bestfactory = j;
						bestposition = result[0];
						count = 1;
						reocordTie[count][0] = bestfactory;
						reocordTie[count][1] = bestposition;
					} else if (bestmakespan == fit) {
						count++;
						reocordTie[count][0] = j;
						reocordTie[count][1] = result[0];
					}
				}
				randnum = (int) Math.ceil(Math.random() * count);
				bestfactory = reocordTie[randnum][0];
				bestposition = reocordTie[randnum][1];

				// System.out.println("bestmakespan "+bestmakespan);
				// System.out.println("bestfactory" + bestfactory);
				// System.out.println("bestpostion" + bestposition);
				pop.vector.get(bestfactory).add(bestposition, frontandbackList.get(0));
				frontandbackList.remove(0);
				feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
						pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
				updatefitness(pop);
			}
			// // displayVector(pop);
			// System.out.println();
			// System.out.println();
		}
		// System.out.println("-------construction results--------");
		// displayVector(pop);
		// checkLegal(pop, feval);
		// System.out.println(pop.fit);
	}

	public double insert(double Cmax, int job, double secondIndex[], double leavetime[][], double tailtime[][],
			Evaluation feval, int length, int result[]) {
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
		// System.out.println("bestpostionfit "+bestfit);
		secondIndex[0] = bestfit;
		result[0] = bestpos;
		if (Cmax > bestfit) {
			bestfit = Cmax;
		}
		return bestfit;
	}

	// 从最大里面取出一个工件插入到另一个工厂的最优位置(用于跳出局部最优)
	public void randomInsert(individual pop, Evaluation feval) {
		// if (pop.vector.get(pop.makespanFactoryNum).size() - 1 == 1) {
		// //特殊情况，当该车间中只有一个工件时不做处理
		// return;
		// }
		Random random = new Random();
		int fmax = pop.makespanFactoryNum;
		int randomposition = random.nextInt(pop.vector.get(fmax).size() - 1) + 1;
		int insertfactory = 0, bestposition = 0;
		double fit = 0, bestfit = 0;
		double time[] = new double[m + 1];
		// System.out.println("randomposition " + randomposition);
		Integer temp = pop.vector.get(fmax).get(randomposition);
		pop.vector.get(fmax).remove(randomposition);
		feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
				pop.vector.get(fmax).size() - 1);
		insertfactory = random.nextInt(f);
		while (insertfactory == fmax) {
			insertfactory = random.nextInt(f);
		}
		// System.out.println("insertfactory " + insertfactory);
		bestfit = Double.MAX_VALUE;
		for (int i = 1, length = pop.vector.get(insertfactory).size(); i <= length; i++) {
			fit = feval.quickEvaluation(temp, i, length - 1, time, pop.leaveTime[insertfactory],
					pop.tailTime[insertfactory]);
			// System.out.println("fit" + fit);
			if (bestfit > fit) {
				bestfit = fit;
				bestposition = i;
			}
		}
		// System.out.println("bestfit "+bestfit);
		// System.out.println("bestposition" +bestposition);
		pop.vector.get(insertfactory).add(bestposition, temp);
		feval.evaluateSingleSequence(pop.vector.get(insertfactory), pop.leaveTime[insertfactory],
				pop.tailTime[insertfactory], pop.vector.get(insertfactory).size() - 1);

		updatefitness(pop);
		// displayVector(pop);
	}

	// 取出一个工件与另一个工厂的工件进行交换 (用于跳出局部最优)
	public void randomSwap(individual pop, Evaluation feval) {
		Random random = new Random();
		int fmax = pop.makespanFactoryNum;
		int selectfactory = random.nextInt(f);
		while (selectfactory == fmax) {
			selectfactory = random.nextInt(f);
		}
		int randomposition = 0, bestposition = 0;
		double bestfit = 0, fit = 0;
		double time[] = new double[m + 1];
		// System.out.println("fmax "+fmax);
		// System.out.println("selectfactory "+selectfactory);
		// 随机取出关键车间中的一个工件
		randomposition = random.nextInt(pop.vector.get(fmax).size() - 1) + 1;
		Integer temp1 = pop.vector.get(fmax).get(randomposition);
		// System.out.println("random "+randomposition);
		pop.vector.get(fmax).remove(randomposition);
		feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
				pop.vector.get(fmax).size() - 1);
		// 取出另一个车间的一个随机工件
		randomposition = random.nextInt(pop.vector.get(selectfactory).size() - 1) + 1;
		Integer temp2 = pop.vector.get(selectfactory).get(randomposition);
		pop.vector.get(selectfactory).remove(randomposition);
		feval.evaluateSingleSequence(pop.vector.get(selectfactory), pop.leaveTime[selectfactory],
				pop.tailTime[selectfactory], pop.vector.get(selectfactory).size() - 1);
		// System.out.println("job2 "+temp2);
		// 插入第一个工件
		bestfit = Double.MAX_VALUE;
		for (int i = 1, length = pop.vector.get(selectfactory).size(); i <= length; i++) {
			fit = feval.quickEvaluation(temp1, i, length - 1, time, pop.leaveTime[selectfactory],
					pop.tailTime[selectfactory]);
			// System.out.println("fit" + fit);
			if (bestfit > fit) {
				bestfit = fit;
				bestposition = i;
			}
		}
		// System.out.println("bestfit "+bestfit);
		// System.out.println("bestposition" +bestposition);
		pop.vector.get(selectfactory).add(bestposition, temp1);
		feval.evaluateSingleSequence(pop.vector.get(selectfactory), pop.leaveTime[selectfactory],
				pop.tailTime[selectfactory], pop.vector.get(selectfactory).size() - 1);
		// 插入第二个工件
		bestfit = Double.MAX_VALUE;
		for (int i = 1, length = pop.vector.get(fmax).size(); i <= length; i++) {
			fit = feval.quickEvaluation(temp2, i, length - 1, time, pop.leaveTime[fmax], pop.tailTime[fmax]);
			// System.out.println("fit" + fit);
			if (bestfit > fit) {
				bestfit = fit;
				bestposition = i;
			}
		}
		// System.out.println("bestfit "+bestfit);
		// System.out.println("bestposition" +bestposition);
		pop.vector.get(fmax).add(bestposition, temp2);
		feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
				pop.vector.get(fmax).size() - 1);
		updatefitness(pop);
		// displayVector(pop);
		// checkLegal(pop, feval);
		// System.out.println(pop.fit);
	}

	public void smellSearch(individual pop[], individual offspring[], Evaluation feval) {
		ArrayList<Integer> selectjobs = new ArrayList<Integer>();
		long end1 = System.currentTimeMillis();
		
		for (int i = 0; i < NP; i++) {
			for (int j = i * S; j < i * S + S; j++) {
				function.copyIndividual(pop[i], offspring[j]);
				if (end1 < cpuTime/3) {
				 destruction(offspring[j], selectjobs, feval);
				}else {
				 destruction(offspring[j], selectjobs, feval);
				}
				 //destruction2(offspring[j], selectjobs, feval);
				construction_tieFirst(offspring[j], selectjobs, feval);
				// construction_tieLast(pop, selectjobs, feval);
				// construction_minMakespantie(offspring[j], selectjobs, feval);
				// construction_tieRandom(offspring[j], selectjobs, feval);
				// construction_tieRandom(offspring[j], selectjobs, feval);
			}
		}
	}

	public void smellSeach2(individual pop[], individual offspring[], Evaluation feval) {
		ArrayList<Integer> selectjobs = new ArrayList<Integer>();
		for (int i = 0; i < NP; i++) {
			for (int j = i * S; j < i * S + S; j++) {
				function.copyIndividual(pop[i], offspring[j]);
				if (Math.random() < 0.5) {
					randomInsert(offspring[j], feval);
				} else {
					randomSwap(offspring[j], feval);
				}
			}
		}
	}

	public void jumpLocal(individual pop, Evaluation feval) {
		// randomInsert(pop, feval);

		ArrayList<Integer> selectjobs = new ArrayList<>();
		destruction(pop, selectjobs, feval);
		construction_tieFirst(pop, selectjobs, feval);

		// randomSwap(pop, feval);
		// randomSwap(pop, feval);
	}

	public void visionSearch(individual pop[], individual offspring[], individual bestsofar, Evaluation feval) {
		double bestmakespan;
		int bestposition = 0;
		for (int i = 0; i < NP; i++) {
			bestmakespan = Double.MAX_VALUE;
			for (int j = i * S; j < i * S + S; j++) {
				if (offspring[j].fit < bestmakespan) {   //有问题（发现2019.8.23,缺少赋值，bestmakespan = offspring[j].fit）
					bestposition = j;
				}
			}
			localSearch(offspring[bestposition], feval);
			// deepLocalsearch(offspring[bestposition], feval);
			if (offspring[bestposition].fit <= pop[i].fit) {
//				if (offspring[bestposition].fit < pop[i].fit) {
//					System.out.println("improve");
//				} else {
//					System.out.println("equal ");
//				}
				function.copyIndividual(offspring[bestposition], pop[i]);
			} else if (Math.random() < (Math.exp((pop[i].fit - offspring[bestposition].fit) / Temprature))) { //
//				System.out.println("jump");
				function.copyIndividual(offspring[bestposition], pop[i]);
				// pop[i].improvementCount = 0;
			}
		}
	}

	public void localSearch(individual pop, Evaluation feval) {
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
				if (bestfit < pop.fit) { //
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
			// System.out.println("Cmax" + Cmax);
			// System.out.println("--------------result------------");
			// displayVector(pop);
			// feval.blockFeval(pop);
			// System.out.println(pop.fit);
			// System.out.println("-----------------------------------------------------------------------------");
		}
	}

	public void deepLocalsearch(individual pop, Evaluation feval) {
		double recorfit = pop.fit;
		boolean flag = true;
		while (flag) {
			localSearch(pop, feval);
			if (recorfit > pop.fit) {
				System.out.println("hhahha");
				recorfit = pop.fit;
				flag = true;
			} else {
				flag = false;
			}
		}

	}
	
	public void updateBestSofar(individual bestsofar, individual pop[], Evaluation feval) {
		double bestmakespan = Double.MAX_VALUE;
		int bestposition = 0;
		for (int i = 0; i < pop.length; i++) {
			if (pop[i].fit < bestmakespan) {
				bestmakespan = pop[i].fit;
				bestposition = i;
			}
		}
		boolean flag = false;
		if (pop[bestposition].fit < bestsofar.fit) {
			function.copyIndividual(pop[bestposition], bestsofar);
			flag = true;
		}
		// Swarm collaboration (有一定效果，不明显，加快了收敛能力)
		if (flag) {
			for (int i = 0; i < pop.length; i++) {
				if (pop[i].fit != bestmakespan
				// && Math.random() < 1 - Math.exp(100 * (bestmakespan -
				// pop[i].fit) / bestmakespan / tvalue)
				) {
					function.copyIndividual(bestsofar, pop[i]);
				}
			}
		}
	}

	public void updateBestsofar2(individual bestsofar, individual pop[], Evaluation feval) {
		double bestmakespan = Double.MAX_VALUE;
		double worstmakespan = Double.MIN_VALUE;
		int bestposition = 0;
		int worstposition = 0;
		for (int i = 0; i < pop.length; i++) {
			if (pop[i].fit < bestmakespan) {
				bestmakespan = pop[i].fit;
				bestposition = i;
			}
			if (pop[i].fit > worstmakespan) {
				worstmakespan = pop[i].fit;
				worstposition = i;
			}
		}
		boolean flag = false;
		if (pop[bestposition].fit < bestsofar.fit) {
			function.copyIndividual(pop[bestposition], bestsofar);
			flag = true;
		}
		// 代替种群中最差的一个
		if (flag) {
			function.copyIndividual(bestsofar, pop[worstposition]);
		}
	}

	public void run() {
		Evaluation feval = new Evaluation();
		individual pop[] = new individual[NP];
		individual bestsofar = new individual(n, m, f);
		individual offspring[] = new individual[S * NP];
		initial(pop, offspring, feval);
		bestsofar.fit = Double.MAX_VALUE;
		updateBestSofar(bestsofar, pop, feval);
		System.out.println("--------start FOA----------------");
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();		
		
		for (int i = 1; end - start < cpuTime; i++) {
			if (Math.random()<0.5) {
				smellSearch(pop, offspring, feval);
			}else{
				visionSearch(pop, offspring, bestsofar, feval);
			}
			
			// smellSeach2(pop, offspring, feva0l);
			
			updateBestSofar(bestsofar, pop, feval);
			// updateBestsofar2(bestsofar, pop, feval);
//			for (int j = 0; j < NP; j++) {
//				System.out.println(pop[j].fit);
//			}
//			System.out.println(bestsofar.fit);
			end = System.currentTimeMillis();
//			System.out.println("time " + (end - start));
		}
		recordResualt(bestsofar);
		System.out.println("time " + (end - start));

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta1";
		params.readConfig(instance);
		WOA foa = new WOA(params, instance, 1);
		foa.run();
	}

}
