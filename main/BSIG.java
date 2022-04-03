package main;

import java.util.ArrayList;
import java.util.Collections;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

/**
 * 
 * A bounded-search iterated greedy algorithm for the distributed permutation
 * flow shop scheduling problem
 * 
 * Victor Fernandez-Viagas
 * 
 * 2015
 * 
 */
public class BSIG extends Algorithm {

	public int d = 5;
	public int L = 20;
	public double tvalue = 0.4;
	public double pmin[]; // 加工时间的最小值
	public double Temprature;

	public BSIG(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		AlgorithmName = "BSIG";
	}

	public void initial(individual pop, Evaluation feval) {
		Hueristics hueristics = new Hueristics();
		hueristics.HPF3(pop, feval);
		double minprocessingtime;
		// 初始化每个工件的最小值
		pmin = new double[n + 1];
		for (int i = 1; i <= n; i++) {
			minprocessingtime = Double.MAX_VALUE;
			for (int j = 1; j <= m; j++) {
				if (minprocessingtime > T[i][j]) {
					minprocessingtime = T[i][j];
				}
			}
			pmin[i] = minprocessingtime;
			// System.out.println(pmin[i]);
		}
		Temprature = tvalue * hueristics.totalprocessingtime / (n * m * 10);
	}

	public void LS2(ArrayList<Integer> jobseqeunce, double leavetime[][], double tailtime[][], Evaluation feval) {
		int count = jobseqeunce.size() - 1;
		int bestpos = 0;
		double fit, bestfit, makespan;
		makespan = leavetime[m][count];
		Integer temp;
		double time[] = new double[m + 1];
//		System.out.println("makespan" + makespan);
		boolean improvement = true;

//		double testleavetime[][] = new double[m + 1][n + 1];
//		double testtailtime[][] = new double[m + 1][n + 1];

		while (improvement) {
			improvement = false;
			for (int i = 1; i <= count; i++) {
				temp = jobseqeunce.get(i);
//				System.out.println("selectjob" + temp);
				jobseqeunce.remove(i);
				feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count - 1);
				bestfit = Double.MAX_VALUE;
				bestpos = 0;
				for (int j = 1; j <= count; j++) {
					fit = feval.quickEvaluation(temp, j, count - 1, time, leavetime, tailtime);
//					System.out.println("fit" + fit);					
//					jobseqeunce.add(j, temp);
//					double testfit = feval.evaluateSingleSequence(jobseqeunce, testleavetime, testtailtime,
//							jobseqeunce.size() - 1);
//					jobseqeunce.remove(j);
//					System.out.println("testfit"+testfit);
					if (bestfit > fit) {
						bestfit = fit;
						bestpos = j;
					}
				}
//				System.out.println("bestfit" + bestfit);
				if (bestfit < makespan) {
//					System.out.println("improve");
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

	public void RLS1(individual pop, Evaluation feval) {
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
//		 Collections.shuffle(arr);
//		displayVector(pop);
//		System.out.println();
//		System.out.println(pop.vector.get(pop.makespanFactoryNum).size() - 1);
		while (cnt <pop.vector.get(pop.makespanFactoryNum).size() - 1) {
			if (cnt == 0) {
				// 重新生成随机序列
				arr.clear();
				for (int i = 1, length = pop.vector.get(pop.makespanFactoryNum).size() - 1; i <= length; i++) {
					Integer temp = new Integer(pop.vector.get(pop.makespanFactoryNum).get(i).intValue());
					recordIndex[pop.vector.get(pop.makespanFactoryNum).get(i).intValue()] = i; // 索引
					arr.add(temp);
//					System.out.println("improve");
				}
				// Collections.shuffle(arr);
//				displayEachFactory(arr);
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
//					 System.out.println("fit" + fit);
					if (bestfit > fit) {
						bestfit = fit;
						bestposition = j;
					}
				}
				if (bestfit < pop.fit) {
					bestfit = pop.fit;
				}
//				 System.out.println("bestfit" + bestfit);
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
//				 System.out.println("improve");
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
			} else {
//				System.out.println("no improve");

				
				pop.vector.get(recordfactory).add(recordIndex[arr.get(cnt)], removejob);
				feval.evaluateSingleSequence(pop.vector.get(recordfactory), pop.leaveTime[recordfactory],
						pop.tailTime[recordfactory], pop.vector.get(recordfactory).size() - 1);
				updatefitness(pop);
				cnt++;
			}
//			 System.out.println();
//			 System.out.println("Cmax" + Cmax);
//			 System.out.println("--------------result------------");
//			 individual test = new individual(n,m, f);
//			 function.copyIndividual(pop,test);
//			 displayVector(test);
//			 feval.blockFeval(test);
//			 System.out.println(test.fit);
//			 System.out.println("-----------------------------------------------------------------------------");
		}
//		System.out.println("cnt "+cnt);
		// System.out.println("--------------result------------");
		// displayVector(pop);
		// feval.blockFeval(pop);
		// System.out.println(pop.fit);
	}

	public void RLS2(individual pop, Evaluation feval) {
		int cnt = 1, fmax;
		double Cmax_aux, Cmax1 = 0, Cmax2 = 0, fit = 0;
		int bestfactory1 = 0, bestpostion1 = 0, bestfactory2 = 0, bestpostion2 = 0, bestremovejobpostion = 0;
		int BestPosfmax = 0, BesPosf = 0, bestfactory = 0, bestjob = 0, removepostion = 0;
		boolean flag;
		Integer removejob, removejob2 = new Integer(0);
		double time[] = new double[m + 1], recordfit;
		while (cnt <= pop.vector.get(pop.makespanFactoryNum).size() - 1) {
			Cmax_aux = pop.fit;
			recordfit = pop.fit;
			flag = false;
			fmax = pop.makespanFactoryNum;
			removejob = pop.vector.get(fmax).get(cnt);
			pop.vector.get(fmax).remove(cnt);
			feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
					pop.vector.get(fmax).size() - 1);
			for (int i = 0; i < f; i++) {
				if (i == pop.makespanFactoryNum) {
					continue;
				}
				bestfactory1 = i;
				for (int g = 1, length = pop.vector.get(i).size() - 1; g <= length; g++) {
					removejob2 = pop.vector.get(i).get(g);
					// System.out.println("removejob2 " + removejob2);
					// System.out.println("factory " + i);
					// System.out.println("position " + g);

					pop.vector.get(i).remove(g);
					// displayEachFactory(pop.vector.get(i));
					// System.out.println();
					feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
							pop.vector.get(i).size() - 1);
					Cmax1 = Double.MAX_VALUE;
					Cmax2 = Double.MAX_VALUE;
					// for (int j = 1; j <= m; j++) {
					// for (int j2 = 1; j2 <= pop.vector.get(i).size() - 1;
					// j2++) {
					// System.out.print(pop.leaveTime[i][j][j2] + " ");
					// }
					// System.out.println();
					// }
					//
					// System.out.println();
					//
					// for (int j = 1; j <= m; j++) {
					// for (int j2 = 1; j2 <= pop.vector.get(i).size() - 1;
					// j2++) {
					// System.out.print(pop.tailTime[i][j][j2] + " ");
					// }
					// System.out.println();
					// }
					// 插入工厂及插入位置
					for (int j = 1, length2 = pop.vector.get(i).size(); j <= length2; j++) {
						fit = feval.quickEvaluation(removejob.intValue(), j, length2 - 1, time, pop.leaveTime[i],
								pop.tailTime[i]);
						// System.out.println("fit" + fit);
						if (Cmax1 > fit) {
							Cmax1 = fit;
							bestpostion1 = j;
						}
					}
					for (int j = 1, length2 = pop.vector.get(fmax).size(); j <= length2; j++) {
						fit = feval.quickEvaluation(removejob2.intValue(), j, length2 - 1, time, pop.leaveTime[fmax],
								pop.tailTime[fmax]);
						if (Cmax2 > fit) {
							Cmax2 = fit;
							bestpostion2 = j;
						}
					}
					// System.out.println("Cmax1 " + Cmax1);
					// System.out.println("Cmax2 " + Cmax2);
					if (Cmax1 < Cmax_aux && Cmax2 < Cmax_aux) {
						flag = true;
						BestPosfmax = bestpostion2; // fmax需要插入的位置
						BesPosf = bestpostion1; // f需要插入的wezhi
						bestfactory = bestfactory1;
						// bestjob = removejob2.intValue();
						removepostion = g;
						Cmax_aux = Math.max(Cmax1, Cmax2);
					}
					pop.vector.get(i).add(g, removejob2);
					feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
							pop.vector.get(i).size() - 1);
					// displayVector(pop);
					// System.out.println("---------------------------");
				}
			}
			if (flag) {
				pop.vector.get(fmax).add(BestPosfmax, pop.vector.get(bestfactory).get(removepostion));
				pop.vector.get(bestfactory).remove(removepostion);
				pop.vector.get(bestfactory).add(BesPosf, removejob);
				feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
						pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
				feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
						pop.vector.get(fmax).size() - 1);
				updatefitness(pop);
				// feval.displayLeavetime(pop);
				// displayVector(pop);
				// System.out.println(pop.fit);
			} else {
				// System.out.println("no improve");
				pop.vector.get(fmax).add(cnt, removejob);
				feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
						pop.vector.get(fmax).size() - 1);
				updatefitness(pop);
				// displayVector(pop);
			}
			if (pop.fit < recordfit) {
				cnt = 1;
			} else {
				cnt++;
			}
		}
	}

	public void destruction_construction(individual pop, Evaluation feval) {
		int randomnum, bestfactory = 0, bestposition = 0, bestpos = 0;
		int recordJob[] = new int[n + 1];
		int result[] = new int[2];
		ArrayList<Integer> selectjobs = new ArrayList<Integer>();
		double bestmakespan, fit, bestfit, Cmax=0;
		double time[] = new double[m + 1];
//		System.out.println("----------\r\n");
		for (int i = 1; i <= d; i++) {
			randomnum = (int) Math.ceil(n * Math.random()); // 采样的是job
			while (recordJob[randomnum] == 1) {
				randomnum = (int) Math.ceil(n * Math.random());
			}
			recordJob[randomnum] = 1;
			findJob(pop, result, randomnum);
			selectjobs.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);
		}
//		System.out.println("----------------selectjobs-------------");
//		displayIntegerArray(selectjobs);
//		System.out.println();
//		displayVector(pop);
		
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
		
		// construction
//		System.out.println("-----------construction--------");
		for (int i = 0, length = selectjobs.size(); i < length; i++) {
			bestmakespan = Double.MAX_VALUE;
			Cmax = pop.fit;
//			System.out.println("Cmax"+Cmax);
			for (int g = 0; g < f; g++) {
//				fit = insert(pop.fit, selectjobs.get(0).intValue(), pop.leaveTime[j], pop.tailTime[j], feval,
//						pop.vector.get(j).size(), result); // the number of
				bestfit =Double.MAX_VALUE;
				for (int j = 1, length2 = pop.vector.get(g).size(); j <= length2; j++) {
					fit = feval.quickEvaluation(selectjobs.get(0), j, length2 - 1, time, pop.leaveTime[g], pop.tailTime[g]);
					// System.out.println("insert fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
				if (Cmax > bestfit) {
					bestfit = Cmax;
				}		
//				System.out.println("bestfit "+bestfit);
				// positions
				// System.out.println("fit"+fit);
				if (bestmakespan > bestfit) {
					bestmakespan = bestfit;
					bestfactory = g;
					bestposition = bestpos;
				}
			}
//			System.out.println("bestfactory "+bestfactory);
//			System.out.println("bestpostion "+bestposition);
			pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
			selectjobs.remove(0);
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			updatefitness(pop);
//			displayVector(pop);
//			System.out.println();
//			System.out.println();	
		}
//		System.out.println(pop.fit);
//		System.out.println("------------test---------");
//		feval.blockFeval(pop);
//		System.out.println("pop.fit"+pop.fit);
	}

	public void run() {
		individual pop = new individual(n, m, f);
		individual tempindividual = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);
		Evaluation feval = new Evaluation();
		initial(pop, feval);
		
		long starttime = System.currentTimeMillis();
		
		for (int i = 0; i < f; i++) {
			LS2(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], feval);
		}
		updatefitness(pop);
		RLS1(pop, feval);
		if (n/f<=L) {
			RLS2(pop, feval);
		}
		System.out.println(Temprature);
		long endtime = System.currentTimeMillis();
		
		function.copyIndividual(pop, tempindividual);
		function.copyIndividual(pop, bestsofar);
		
		for (int i = 0;endtime-starttime<cpuTime; i++) {
			destruction_construction(tempindividual, feval);
			for (int g = 0; g < f; g++) {
				LS2(tempindividual.vector.get(g), tempindividual.leaveTime[g], tempindividual.tailTime[g], feval);
			}
			updatefitness(tempindividual);
			RLS1(tempindividual, feval);
			if (n/f<=L) {
//				System.out.println("excutive");
				RLS2(tempindividual, feval);
			}
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
			endtime = System.currentTimeMillis();
		//	System.out.println(bestsofar.fit);
		}
		System.out.println("well done");
		System.out.println("time " + (endtime - starttime));
		displayVector(bestsofar);
		checkLegal(bestsofar, feval);
		recordResualt(bestsofar);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta42";
		params.readConfig(instance);
		BSIG bsig = new BSIG(params, instance, 7);
		bsig.run();
	}

}
