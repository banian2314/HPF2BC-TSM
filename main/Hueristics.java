package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;
import libs.weightIndividual;

public class Hueristics {

	public int n; // 工件数量
	public int m; // 机器数量
	public int f; // 车间数目
	public double T[][]; // 运行时间
	public double totaltime[];
	public double totalprocessingtime;

	public Hueristics() {
		n = libs.Initialize.job;
		m = libs.Initialize.machines;
		f = Algorithm.f;
		T = libs.Initialize.T;
		totaltime = new double[n + 1];
		// 计算总加工时间
		totalprocessingtime = 0;
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) {
				totaltime[i] = totaltime[i] + T[i][j];
				totalprocessingtime = totalprocessingtime + T[i][j];
			}
		}
	}

	public void dividJobsFactory(int vector[], individual pop) {
		double load = totalprocessingtime / f;
		double average = totalprocessingtime / n;
		double loadvalue;
		int job = 1;
		for (int i = 1; i <= f - 1; i++) {
			loadvalue = 0;
			while (loadvalue + totaltime[vector[job]] <= load) { // 松弛处理（原文并没有）

				loadvalue = loadvalue + totaltime[vector[job]];
				Integer jobValue = new Integer(vector[job]);
				pop.vector.get(i - 1).add(jobValue);
				job++;
			}
			if (loadvalue + totaltime[vector[job]] - load <= load - loadvalue) {
				loadvalue = loadvalue + totaltime[vector[job]];
				Integer jobValue = new Integer(vector[job]);
				pop.vector.get(i - 1).add(jobValue);
				job++;
			}
		}
		loadvalue = 0;
		// 最后一个工厂
		for (; job <= n;) {
			loadvalue = loadvalue + totaltime[vector[job]];
			Integer jobValue = new Integer(vector[job]);
			pop.vector.get(f - 1).add(jobValue);
			job++;
		}
	}

	// 基于单车间的NEH (first tie)
	public void insert(individual pop, int factorynumber, ArrayList<Integer> Jobsequence, double leavetime[][],
			double tailtime[][], Evaluation feval) {
		int count = Jobsequence.size() - 1; // 当前车间中
		int vector[] = new int[count + 1];
		for (int i = 1; i <= count; i++) {
			vector[i] = Jobsequence.get(i).intValue();
		}
		double time[] = new double[m + 1];

		double fit, bestfit = 0, makespan = 0;
		int bestpos = 0;
		Integer temp;
		feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, 1);
		for (int i = 2; i <= count; i++) {
			bestfit = Double.MAX_VALUE;
			// Integer tempjob = new Integer(vector[i]);
			for (int j = 1; j <= i; j++) {
				fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
				// System.out.println("fit"+fit);
				if (fit < bestfit) {
					bestfit = fit;
					bestpos = j;
				}
			}

			temp = Jobsequence.get(i);
			Jobsequence.remove(temp);
			Jobsequence.add(bestpos, temp);
			makespan = feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i);
		}
		// 更新适应度值
		if (pop.fit < makespan) {
			pop.fit = makespan;
			pop.makespanFactoryNum = factorynumber;
		}
	}

	// 基于单车间的NEH
	public double insert_tie(individual pop, int factorynumber, ArrayList<Integer> Jobsequence, double leavetime[][],
			double tailtime[][], Evaluation feval) {

		int count = Jobsequence.size() - 1; // 当前车间中
		int vector[] = new int[count + 1];
		for (int i = 1; i <= count; i++) {
			vector[i] = Jobsequence.get(i).intValue();
		}
		double time[] = new double[m + 1];

		double fit, bestfit = 0, makespan = 0;
		int bestpos = 0;
		Integer temp;
		feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, 1);
		double a[] = new double[n + 1];
		double b[] = new double[n + 1];

		for (int j = 1; j <= n; j++) {
			for (int i = 1; i <= m; i++) {
				a[j] = a[j] + ((m - 1) * (m - 2) / 2 + m - i) * T[j][i];
				b[j] = b[j] + ((m - 1) * (m - 2) / 2 + i - 1) * T[j][i];
			}
		}
		for (int i = 2; i <= count; i++) {
			bestfit = Double.MAX_VALUE;
			// Integer tempjob = new Integer(vector[i]);

			if (a[vector[i]] <= b[vector[i]]) {
				for (int j = 1; j <= i; j++) {
					fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			} else {
				for (int j = i; j >= 1; j--) {
					fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			}
			temp = Jobsequence.get(i);
			Jobsequence.remove(temp);
			Jobsequence.add(bestpos, temp);
			makespan = feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i);
			// System.out.println();
		}
		return makespan;
	}

	// 基于单车间的NEH
	public double insert_tie_partial(individual pop, int lambda, int factorynumber, ArrayList<Integer> Jobsequence,
			double leavetime[][], double tailtime[][], Evaluation feval) {

		int count = Jobsequence.size() - 1; // 当前车间中
		int vector[] = new int[count + 1];
		for (int i = count - lambda + 1; i <= count; i++) {
			vector[i] = Jobsequence.get(i).intValue();
		}

		// for (int i = 1; i < vector.length; i++) {
		// System.out.print(vector[i]+" ");
		// }

		double time[] = new double[m + 1];

		double fit, bestfit = 0, makespan = 0;
		int bestpos = 0;
		Integer temp;
		feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, count - lambda);
		double a[] = new double[n + 1];
		double b[] = new double[n + 1];

		for (int j = 1; j <= n; j++) {
			for (int i = 1; i <= m; i++) {
				a[j] = a[j] + ((m - 1) * (m - 2) / 2 + m - i) * T[j][i];
				b[j] = b[j] + ((m - 1) * (m - 2) / 2 + i - 1) * T[j][i];
			}
		}
		for (int i = count - lambda + 1; i <= count; i++) {
			bestfit = Double.MAX_VALUE;
			// Integer tempjob = new Integer(vector[i]);

			if (a[vector[i]] <= b[vector[i]]) {
				for (int j = 1; j <= i; j++) {
					fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			} else {
				for (int j = i; j >= 1; j--) {
					fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			}
			temp = Jobsequence.get(i);
			Jobsequence.remove(temp);
			Jobsequence.add(bestpos, temp);
			makespan = feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i);
			// System.out.println();
		}
		return makespan;
	}

	// 整体插入
	public double insertwhole(individual pop, ArrayList<Integer> Jobsequence, int job, int recordPositionandfactory[],
			int factory, double leavetime[][], double tailtime[][], Evaluation feval) {

		double bestfit = Double.MAX_VALUE, makepsan = 0, fit = 0;
		double time[] = new double[m + 1];
		int bestpos = 0;

		for (int j = 1; j <= Jobsequence.size() - 1; j++) {
			fit = feval.quickEvaluation(job, j, Jobsequence.size() - 1, time, leavetime, tailtime);
			// System.out.println("fit" + fit);
			if (fit < bestfit) {
				bestfit = fit;
				bestpos = j;
			}
		}
		// System.out.println("bestfit"+bestfit);
		if (bestfit < pop.fit) {
			bestfit = pop.fit;
		}

		recordPositionandfactory[0] = bestpos; // 记录位置
		recordPositionandfactory[1] = factory; // 记录工厂
		return bestfit;
	}

	public double insert_tie_front(individual pop, int factorynumber, int flag, ArrayList<Integer> Jobsequence,
			double leavetime[][], double tailtime[][], Evaluation feval) {
		int count = Jobsequence.size() - 1; // 当前车间中
		int vector[] = new int[count + 1];
		for (int i = 1; i <= count; i++) {
			vector[i] = Jobsequence.get(i).intValue();
		}
		double time[] = new double[m + 1];

		double fit, bestfit = 0, makespan = 0;
		int bestpos = 0;
		Integer temp;
		feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, 1);
		double a[] = new double[n + 1];
		double b[] = new double[n + 1];
		int removejob, removeposition = 0;
		for (int j = 1; j <= n; j++) {
			for (int i = 1; i <= m; i++) {
				a[j] = a[j] + ((m - 1) * (m - 2) / 2 + m - i) * T[j][i];
				b[j] = b[j] + ((m - 1) * (m - 2) / 2 + i - 1) * T[j][i];
			}
		}
		for (int i = 2; i <= count; i++) {
			bestfit = Double.MAX_VALUE;
			// Integer tempjob = new Integer(vector[i]);

			if (a[vector[i]] <= b[vector[i]]) {
				for (int j = 1; j <= i; j++) {
					fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			} else {
				for (int j = i; j >= 1; j--) {
					fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			}
			temp = Jobsequence.get(i);
			Jobsequence.remove(i);
			Jobsequence.add(bestpos, temp);

			// for (int j = 1; j <=i; j++) {
			// System.out.print(Jobsequence.get(j)+" ");
			// }
			// System.out.println();

			if (flag == 1) {
				makespan = feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i);
				if (bestpos != 1) { // 前一位置
					removeposition = bestpos - 1;
				} else {
					continue;
				}
			} else if (flag == 2) {
				makespan = feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i);
				if (bestpos != i) {
					removeposition = bestpos + 1;
				} else {
					continue;
				}
			} else {
				double random = Math.random();
				// System.out.println("random"+random);
				if (random > 0.5 && bestpos != i) {
					removeposition = bestpos + 1;
				} else if (random < 0.5 && bestpos != 1) {
					removeposition = bestpos - 1;
				} else if (bestpos == i) {
					removeposition = bestpos - 1;
				} else if (bestpos == 1) {
					removeposition = bestpos + 1;
				}
			}

			removejob = Jobsequence.get(removeposition).intValue();
			Jobsequence.remove(removeposition);

			feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i - 1);
			bestfit = Double.MAX_VALUE;
			if (a[removejob] <= b[removejob]) {
				for (int j = 1; j <= i; j++) {
					fit = feval.quickEvaluation(removejob, j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			} else {
				for (int j = i; j >= 1; j--) {
					fit = feval.quickEvaluation(removejob, j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			}
			temp = new Integer(removejob);
			Jobsequence.add(bestpos, temp);
			makespan = feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i);
			// System.out.println("bestpos"+bestpos);
			//
			// for (int j = 1; j <=i; j++) {
			// System.out.print(Jobsequence.get(j)+" ");
			// }
			// System.out.println();
			// System.out.println();
			// System.out.println();
		}
		return makespan;
	}

	public double insert_tie_double(individual pop, int factorynumber, ArrayList<Integer> Jobsequence,
			double leavetime[][], double tailtime[][], Evaluation feval) {
		int count = Jobsequence.size() - 1; // 当前车间中
		int vector[] = new int[count + 1];
		for (int i = 1; i <= count; i++) {
			vector[i] = Jobsequence.get(i).intValue();
		}
		double time[] = new double[m + 1];
		double fit, bestfit = 0, makespan = 0;
		int bestpos = 0;
		Integer temp;
		feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, 1);
		double a[] = new double[n + 1];
		double b[] = new double[n + 1];
		for (int j = 1; j <= n; j++) {
			for (int i = 1; i <= m; i++) {
				a[j] = a[j] + ((m - 1) * (m - 2) / 2 + m - i) * T[j][i];
				b[j] = b[j] + ((m - 1) * (m - 2) / 2 + i - 1) * T[j][i];
			}
		}
		ArrayList<Integer> frontandbackList = new ArrayList<Integer>();
		for (int i = 2; i <= count; i++) {
			bestfit = Double.MAX_VALUE;
			// Integer tempjob = new Integer(vector[i]);

			if (a[vector[i]] <= b[vector[i]]) {
				for (int j = 1; j <= i; j++) {
					fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			} else {
				for (int j = i; j >= 1; j--) {
					fit = feval.quickEvaluation(vector[i], j, i - 1, time, leavetime, tailtime);
					// System.out.println("fit" + fit);
					if (fit < bestfit) {
						bestfit = fit;
						bestpos = j;
					}
				}
			}
			temp = Jobsequence.get(i);
			Jobsequence.remove(i);
			Jobsequence.add(bestpos, temp);

			// for (int j = 1; j <=i; j++) {
			// System.out.print(Jobsequence.get(j)+" ");
			// }
			// System.out.println();

			if (bestpos == 1) {
				frontandbackList.add(Jobsequence.get(bestpos + 1));
				Jobsequence.remove(bestpos + 1);
				// System.out.println("job1 " + frontandbackList.get(0));
			} else if (bestpos == i) {
				frontandbackList.add(Jobsequence.get(bestpos - 1));
				Jobsequence.remove(bestpos - 1);
				// System.out.println("job2 " + frontandbackList.get(0));
			} else {
				frontandbackList.add(Jobsequence.get(bestpos - 1));
				frontandbackList.add(Jobsequence.get(bestpos + 1));
				Jobsequence.remove(bestpos + 1);
				Jobsequence.remove(bestpos - 1);
				// System.out.println("job1 " + frontandbackList.get(0));
				// System.out.println("job2 " + frontandbackList.get(1));
			}

			// Algorithm.displayIntegerArray(frontandbackList);

			feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i - frontandbackList.size());

			for (int g = 0, length = frontandbackList.size(); g < length; g++) {
				bestfit = Double.MAX_VALUE;
				if (a[frontandbackList.get(0)] <= b[frontandbackList.get(0)]) {
					for (int j = 1; j <= i - (length - g) + 1; j++) {
						fit = feval.quickEvaluation(frontandbackList.get(0), j, i - (length - g), time, leavetime,
								tailtime);
						// System.out.println("fit" + fit);
						// double testleavetime[][] = new double[m + 1][n + 1];
						// double testtailtime[][] = new double[m + 1][n + 1];
						//
						// Jobsequence.add(j, frontandbackList.get(0));
						// double testfit =
						// feval.evaluateSingleSequence(Jobsequence,
						// testleavetime, testtailtime,
						// i - (length - g) + 1);
						// System.out.println("testfit" + testfit);
						// Jobsequence.remove(j);

						if (fit < bestfit) {
							bestfit = fit;
							bestpos = j;
						}
					}
				} else {
					for (int j = i - (length - g) + 1; j >= 1; j--) {
						fit = feval.quickEvaluation(frontandbackList.get(0), j, i - (length - g), time, leavetime,
								tailtime);
						// System.out.println("fit" + fit);
						// double testleavetime[][] = new double[m + 1][n + 1];
						// double testtailtime[][] = new double[m + 1][n + 1];
						//
						// Jobsequence.add(j, frontandbackList.get(0));
						// double testfit =
						// feval.evaluateSingleSequence(Jobsequence,
						// testleavetime, testtailtime,
						// i - (length - g) + 1);
						// System.out.println("testfit" + testfit);
						// Jobsequence.remove(j);
						if (fit < bestfit) {
							bestfit = fit;
							bestpos = j;
						}
					}
				}
				Jobsequence.add(bestpos, frontandbackList.get(0));
				frontandbackList.remove(0);
				makespan = feval.evaluateSingleSequence(Jobsequence, leavetime, tailtime, i - (length - g) + 1);
			}
			// System.out.println("bestpos"+bestpos);
			//
			// for (int j = 1; j <= i; j++) {
			// System.out.print(Jobsequence.get(j) + " ");
			// }
			// System.out.println();
			// System.out.println();
		}
		return makespan;
	}

	// 每个车间执行NEH操作 (无二次插入)
	public void NEH_insertion(individual pop, Evaluation feval) {
		double makespan;
		individual temp = new individual(n, m, f);
		function.copyIndividual(pop, temp);

		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			makespan = insert_tie(temp, i, temp.vector.get(i), temp.leaveTime[i], temp.tailTime[i], feval);
			if (makespan < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				function.copyFactory(temp, pop, i);
			}
			if (pop.leaveTime[i][m][pop.vector.get(i).size() - 1] > pop.fit) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
				pop.makespanFactoryNum = i;
			}
		}
	}

	// 每个车间执行NEH操作 1:previous, 2:following, 3:random
	public void NEH_insertion_front(individual pop, int flag, Evaluation feval) {
		double makespan;
		individual temp = new individual(n, m, f);
		function.copyIndividual(pop, temp);

		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			makespan = insert_tie_front(temp, i, flag, temp.vector.get(i), temp.leaveTime[i], temp.tailTime[i], feval);
			if (makespan < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				function.copyFactory(temp, pop, i);
			}
			if (pop.leaveTime[i][m][pop.vector.get(i).size() - 1] > pop.fit) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
				pop.makespanFactoryNum = i;
			}
		}
	}

	public void NEH_insertion_double(individual pop, Evaluation feval) {
		double makespan;
		individual temp = new individual(n, m, f);
		function.copyIndividual(pop, temp);

		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			makespan = insert_tie_double(temp, i, temp.vector.get(i), temp.leaveTime[i], temp.tailTime[i], feval);
			if (makespan < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				function.copyFactory(temp, pop, i);
			}
			if (pop.leaveTime[i][m][pop.vector.get(i).size() - 1] > pop.fit) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
				pop.makespanFactoryNum = i;
			}
		}
	}
	public void Block_critical(individual pop, Evaluation feval) {
		double makespan;
		int a=3;
		int recordFactoryandPositionandJob[] = new int[3];
		individual temp = new individual(n, m, f);
		function.copyIndividual(pop, temp);
		pop.fit = Double.MIN_VALUE;
		boolean flag=true;
		if (flag){
			flag=false;
			boolean[] bool=new boolean[pop.vector.get(pop.makespanFactoryNum).size()-1];
			List<Integer> Rremove=new ArrayList<Integer>();
			for (int i = 1; i <=pop.vector.get(pop.makespanFactoryNum).size()-1; i++) {//
				Random rand=new Random();
				int randInt=0;
				for (int j = 0; j < a; j++) {
					do {
						randInt = rand.nextInt(pop.vector.get(pop.makespanFactoryNum).size()-1);
					}while(bool[randInt]||randInt==0);
					bool[randInt]=true;
					Rremove.add(randInt);
					//temp.vector.get(pop.makespanFactoryNum).remove(randInt);
				}
				Integer[] ri = new Integer[Rremove.size()];
				Rremove.toArray(ri);
				Arrays.sort(ri);
				for (int j = ri.length-1; j >=0; j--) {
					pop.vector.get(pop.makespanFactoryNum).remove(ri[j].intValue());
				}
                System.out.println(pop.vector.get(pop.makespanFactoryNum));
        		double bestmakespan,makespan1;
        		int bestrecord[] = new int[2];
                for (int j = 1; j <= ri.length; j++) {
                	bestmakespan = Double.MAX_VALUE;
                    for (int l = 0; l < f; l++) {
                    	makespan1=insertwhole(pop, pop.vector.get(l), Rremove.get(j), recordFactoryandPositionandJob, l, pop.leaveTime[l], pop.tailTime[l], feval);
        				if (makespan1 < bestmakespan) {
        					bestmakespan = makespan1;
        					bestrecord[0] = recordFactoryandPositionandJob[0];
        					bestrecord[1] = recordFactoryandPositionandJob[1];
        				} else if (makespan1 == bestmakespan) { // tiebreacking
        					// System.out.println("equal");
        				}
                    }
        			Integer temp1 = new Integer(Rremove.get(j));
        			pop.vector.get(bestrecord[1]).add(bestrecord[0], temp1);
        			// Algorithm.displayVector(pop);
        			feval.evaluateSingleSequence(pop.vector.get(bestrecord[1]), pop.leaveTime[bestrecord[1]],
        					pop.tailTime[bestrecord[1]], pop.vector.get(bestrecord[1]).size() - 1);
        			// pop.fit = bestmakespan;
        			updatefitness(pop);
        			// System.out.println("--------------------------");

				}

			}
		}
	}
	public void NEH_insertion_partial(individual pop, int lambda, Evaluation feval) {
		double makespan;
		individual temp = new individual(n, m, f);
		function.copyIndividual(pop, temp);

		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			//
			if (pop.vector.get(i).size() - 1 <= lambda) {
				makespan = insert_tie(temp, i, temp.vector.get(i), temp.leaveTime[i], temp.tailTime[i], feval);
			} else {
				makespan = insert_tie_partial(temp, lambda, i, temp.vector.get(i), temp.leaveTime[i], temp.tailTime[i],
						feval);
			}
			if (makespan < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				function.copyFactory(temp, pop, i);
			}
			if (pop.leaveTime[i][m][pop.vector.get(i).size() - 1] > pop.fit) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
				pop.makespanFactoryNum = i;
			}
		}
	}

	public void NEH2_insertion(individual pop, ArrayList<Integer> selectjobs, Evaluation feval) {
		int bestfactory = 0, bestposition = 0, bestpos = 0;
		double bestmakespan, fit, bestfit, Cmax = 0;
		double time[] = new double[m + 1];
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
		// System.out.println("----------begin----");
		// displayVector(pop);
		// construction
		// System.out.println("-----------construction--------");
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
		// System.out.println("------end------");
		// displayVector(pop);
	}

	public void HPF2(int vector[], Evaluation feval) {
		double part2, partTotal;
		double bestRI = Double.MAX_VALUE;
		int bestJob = 0;
		double lameda = 0.55;
		double mu = 0.7;

		// 选择第一个工件
		for (int i = 1; i <= n; i++) {

			part2 = 0;
			for (int j = 1; j <= m; j++) {
				part2 = part2 + (m - j) * T[i][j] / (m - 1);
			}
			partTotal = 2 * lameda * part2 + (1 - lameda) * totaltime[i];
			if (partTotal < bestRI) {
				bestRI = partTotal;
				bestJob = i;
			}
		}
		// 确定其他工件
		int record[] = new int[n + 1];
		int unselectedJobs[] = new int[n + 1];
		record[bestJob] = 1;
		int k = 0;
		for (int i = 1; i < unselectedJobs.length; i++) {
			if (record[i] != 1) {
				k++;
				unselectedJobs[k] = i;
			}
		}
		vector[1] = bestJob;
		double time[][] = new double[m + 1][n + 1];
		double tailtime[][] = new double[m + 1][n + 1];
		feval.evaluateSingleSequence(vector, time, tailtime, 1);

		int njob = 0;
		double fpi, tefpi;
		int count = n - 1;
		double makespan = 0, bestmakespan = 0;

		for (int i = 2; i <= n; i++) {
			fpi = Double.MAX_VALUE;
			// System.out.println("------------------");

			for (int j = count; j >= 1; j--) {
				tefpi = 0;
				for (int j2 = 1; j2 <= m; j2++) {
					if (j2 == m) {
						time[j2][i] = time[j2 - 1][i] + T[unselectedJobs[j]][j2];
					} else if (j2 == 1) {
						time[j2][i] = Math.max(time[j2][i - 1] + T[unselectedJobs[j]][j2], time[j2 + 1][i - 1]);
					} else {
						time[j2][i] = Math.max(time[j2 - 1][i] + T[unselectedJobs[j]][j2], time[j2 + 1][i - 1]);
					}
					tefpi = tefpi + (time[j2][i] - time[j2][i - 1] - T[unselectedJobs[j]][j2]);
				}
				tefpi = mu * tefpi + (1 - mu) * (time[m][i] - time[m][i - 1]);
				makespan = time[m][i];
				if (tefpi < fpi) {
					njob = unselectedJobs[j];
					fpi = tefpi;
					bestmakespan = makespan;
				}
				// else if (tefpi == fpi) { // 处理节
				//// System.out.println("tie");
				// if (bestmakespan > makespan) {
				// // System.out.println("exchange");
				// njob = unselectedJobs[j];
				// fpi = tefpi;
				// bestmakespan = makespan;
				// }
				// }
			}
			for (int j2 = 1; j2 <= m; j2++) {
				if (j2 == m) {
					time[j2][i] = time[j2 - 1][i] + T[njob][j2];
				} else if (j2 == 1) {
					time[j2][i] = Math.max(time[j2][i - 1] + T[njob][j2], time[j2 + 1][i - 1]);
				} else {
					time[j2][i] = Math.max(time[j2 - 1][i] + T[njob][j2], time[j2 + 1][i - 1]);
				}
			}
			record[njob] = 1;
			count = 0;
			vector[i] = njob;
			for (k = 1; k < record.length; k++) {
				if (record[k] != 1) {
					count++;
					unselectedJobs[count] = k;
				}
			}
		}
	}

	public void PF(int vector[], Evaluation feval) {
		weightIndividual jobs[] = new weightIndividual[n + 1];
		double leavetime[][] = new double[m + 1][n + 1];
		int record[] = new int[n + 1];
		int selected[] = new int[n + 1];
		int unselected[] = new int[n + 1];
		double fpi, tefpi;
		int count = 0;
		for (int i = 1; i <= n; i++) {
			jobs[i] = new weightIndividual();
			jobs[i].num = i;
			jobs[i].weight = totaltime[i];
		}
		Arrays.sort(jobs, 1, n + 1, function.recompareWeightIndividualMintoMax());
		record[jobs[1].num] = 1;
		selected[1] = jobs[1].num;
		vector[1] = jobs[1].num;
		int njob = 0;
		for (int i = 1; i < record.length; i++) {
			if (record[i] != 1) {
				count++;
				unselected[count] = i;
			}
		}

		// 计算第一个工件的离开时间
		for (int i = 1; i <= m; i++) {
			if (i == 1) {
				leavetime[i][1] = T[selected[1]][1];
			} else {
				leavetime[i][1] = leavetime[i - 1][1] + T[selected[1]][i];
			}
		}
		for (int i = 2; i <= n; i++) {
			// int i=2;
			fpi = Double.MAX_VALUE;
			for (int j = 1; j <= count; j++) {
				tefpi = 0;
				for (int j2 = 1; j2 <= m; j2++) {
					if (j2 == m) {
						leavetime[j2][i] = leavetime[j2 - 1][i] + T[unselected[j]][j2];
					} else if (j2 == 1) {
						leavetime[j2][i] = Math.max(leavetime[j2][i - 1] + T[unselected[j]][j2],
								leavetime[j2 + 1][i - 1]);
					} else {
						leavetime[j2][i] = Math.max(leavetime[j2 - 1][i] + T[unselected[j]][j2],
								leavetime[j2 + 1][i - 1]);
					}
					tefpi = tefpi + (leavetime[j2][i] - leavetime[j2][i - 1] - T[unselected[j]][j2]);
				}
				// System.out.println(tefpi);
				if (tefpi < fpi) {
					njob = unselected[j];
					fpi = tefpi;
				}
			}
			// System.out.println(njob);
			for (int j2 = 1; j2 <= m; j2++) {
				if (j2 == m) {
					leavetime[j2][i] = leavetime[j2 - 1][i] + T[njob][j2];
				} else if (j2 == 1) {
					leavetime[j2][i] = Math.max(leavetime[j2][i - 1] + T[njob][j2], leavetime[j2 + 1][i - 1]);
				} else {
					leavetime[j2][i] = Math.max(leavetime[j2 - 1][i] + T[njob][j2], leavetime[j2 + 1][i - 1]);
				}
			}
			record[njob] = 1;
			count = 0;
			selected[i] = njob;
			vector[i] = njob;
			for (int k = 1; k < record.length; k++) {
				if (record[k] != 1) {
					count++;
					unselected[count] = k;
				}
			}
		}
	}

	public void HPF3(individual pop, Evaluation feval) {
		int vector[] = new int[n + 1];
		HPF2(vector, feval);
		dividJobsFactory(vector, pop);
		feval.blockFeval(pop);
		NEH_insertion(pop, feval);
	}

	public void PF3(individual pop, Evaluation feval) {
		int vector[] = new int[n + 1];
		PF(vector, feval);
		dividJobsFactory(vector, pop);
		feval.blockFeval(pop); // important
		NEH_insertion(pop, feval);
	}

	public void RC1(individual pop, double mu, double lameda, int type, Evaluation feval) {
		double part1, part2, partTotal;
		double bestfirstfit, fpi, tefpi, makespan;
		int bestfactory = 0;
		int record[] = new int[n + 1];
		int unselected[] = new int[n + 1];
		weightIndividual jobs[] = new weightIndividual[n + 1];
		// 选择第一个工件
		for (int i = 1; i <= n; i++) {
			part1 = 0;
			part2 = 0;
			for (int j = 1; j <= m; j++) {
				part1 = part1 + T[i][j];
				part2 = part2 + (m - j) * T[i][j] / (m - 1);
			}
			partTotal = 2 * lameda * part2 + (1 - lameda) * part1;
			jobs[i] = new weightIndividual();
			jobs[i].num = i;
			jobs[i].weight = partTotal;
		}

		Arrays.sort(jobs, 1, n + 1, function.recompareWeightIndividualMintoMax());

		// 确定每一个车间的第一个工件
		for (int i = 0; i < f; i++) {
			record[jobs[i + 1].num] = 1;
			Integer firstJob = new Integer(jobs[i + 1].num);
			pop.vector.get(i).add(firstJob);
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], 1);
		}

		// Algorithm.displayVector(pop);
		// feval.displayLeavetime(pop);

		// 剩余工件
		int njob = 0;
		int count = 0;
		int totalcount = 0;
		for (int i = 1; i < record.length; i++) {
			if (record[i] != 1) {
				count++;
				unselected[count] = i;
			}
		}
		totalcount = count;
		// feval.displayLeavetime(pop);

		// System.out.println("total" + totalcount);
		// 确定其他工件

		for (int i = 1; i <= totalcount; i++) {
			// 选择工厂的分配
			bestfirstfit = Double.MAX_VALUE;
			// RC1_1
			if (type == 1) {
				for (int j = 0; j < f; j++) {
					if (bestfirstfit > pop.leaveTime[j][1][pop.vector.get(j).size() - 1]) {
						bestfirstfit = pop.leaveTime[j][1][pop.vector.get(j).size() - 1];
						bestfactory = j;
					}
				}
			} else {
				// System.out.println("hhahah");
				for (int j = 0; j < f; j++) {
					if (bestfirstfit > pop.leaveTime[j][m][pop.vector.get(j).size() - 1]) {
						bestfirstfit = pop.leaveTime[j][m][pop.vector.get(j).size() - 1];
						bestfactory = j;
					}
				}
			}

			// for (int j = 1; j <=count; j++) {
			// System.out.print(unselected[j]+" ");
			// }
			// System.out.println();
			// System.out.println("factpory" + bestfactory);
			// RC1_2
			// System.out.println();
			// System.out.println("bestfactory" + bestfactory);
			fpi = Double.MAX_VALUE;
			for (int j = 1; j <= count; j++) {
				tefpi = 0;
				// System.out.println(unselected[j]);
				for (int j2 = 1, k = pop.vector.get(bestfactory).size(); j2 <= m; j2++) {
					if (j2 == m) {
						pop.leaveTime[bestfactory][j2][pop.vector.get(bestfactory)
								.size()] = pop.leaveTime[bestfactory][j2 - 1][k] + T[unselected[j]][j2];
					} else if (j2 == 1) {
						pop.leaveTime[bestfactory][j2][k] = Math.max(
								pop.leaveTime[bestfactory][j2][k - 1] + T[unselected[j]][j2],
								pop.leaveTime[bestfactory][j2 + 1][i - 1]);
					} else {
						pop.leaveTime[bestfactory][j2][k] = Math.max(
								pop.leaveTime[bestfactory][j2 - 1][k] + T[unselected[j]][j2],
								pop.leaveTime[bestfactory][j2 + 1][k - 1]);
					}
					tefpi = tefpi + (pop.leaveTime[bestfactory][j2][k] - pop.leaveTime[bestfactory][j2][k - 1]
							- T[unselected[j]][j2]);
					// System.out.println(pop.leaveTime[bestfactory][j2][k] + "
					// ");
				}
				// System.out.println("tefpi-----"+tefpi);
				// System.out.println("totaltime[unselected[j]"+totaltime[unselected[j]]);
				tefpi = mu * tefpi + (1 - mu) * (totaltime[unselected[j]]);
				// System.out.println("tefpi"+tefpi);
				if (tefpi < fpi) {
					njob = unselected[j];
					fpi = tefpi;
				}
				// else if (tefpi==fpi) {
				// System.out.println("tie");
				// }
			}

			Integer temp = new Integer(njob);
			pop.vector.get(bestfactory).add(temp);
			// Algorithm.displayVector(pop);
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			// feval.displayLeavetime(pop);
			record[njob] = 1;
			count = 0;
			for (int k = 1; k < record.length; k++) {
				if (record[k] != 1) {
					count++;
					unselected[count] = k;
				}
			}
			// System.out.println("count"+count);
			// System.out.println();
		}

		// feval.blockFeval(pop);
		// feval.displayLeavetime(pop);
	}

	// the first machine available
	public void RC1_1(individual pop, Evaluation feval) {
		RC1(pop, 0.95, 1, 1, feval);

		// improvement
		feval.blockFeval(pop);
		NEH_insertion(pop, feval);
	}

	// the last machine available
	public void RC1_m(individual pop, Evaluation feval) {
		RC1(pop, 1, 0.85, m, feval);
		// improvement
		feval.blockFeval(pop);
		NEH_insertion(pop, feval);
	}

	public void NEH2(individual pop, Evaluation feval) {
		weightIndividual jobs[] = new weightIndividual[n + 1];
		int record[] = new int[n + 1];
		int unselected[] = new int[n + 1];
		int recordFactoryandPositionandJob[] = new int[3];
		// 选择第一个工件
		for (int i = 1; i <= n; i++) {
			jobs[i] = new weightIndividual();
			jobs[i].num = i;
			jobs[i].weight = totaltime[i];
		}

		Arrays.sort(jobs, 1, n + 1, function.recompareWeightIndividualMaxtoMin());

		// 确定每一个车间的第一个工件
		for (int i = 0; i < f; i++) {
			record[jobs[i + 1].num] = 1;
			Integer firstJob = new Integer(jobs[i + 1].num);
			pop.vector.get(i).add(firstJob);
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], 1);
		}

		// 确定部分工序的工件的适应度值
		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}

		int count = 0;
		for (int i = 1; i < record.length; i++) {
			if (record[jobs[i].num] != 1) {
				count++;
				unselected[count] = jobs[i].num;
			}
		}

		double bestmakespan, tempbestmakespan;

		// for (int i = 1; i <= count; i++) {
		// System.out.print(unselected[i] + " ");
		// }
		// System.out.println();
		//
		int bestrecord[] = new int[2];
		for (int i = 1; i <= count; i++) {
			bestmakespan = Double.MAX_VALUE;
			for (int k = 0; k < f; k++) {
				tempbestmakespan = insertwhole(pop, pop.vector.get(k), unselected[i], recordFactoryandPositionandJob, k,
						pop.leaveTime[k], pop.tailTime[k], feval); // 单个车间的makespan
				// System.out.println("tempbestmakespan"+tempbestmakespan);
				if (tempbestmakespan < bestmakespan) {
					bestmakespan = tempbestmakespan;
					bestrecord[0] = recordFactoryandPositionandJob[0];
					bestrecord[1] = recordFactoryandPositionandJob[1];
				} else if (tempbestmakespan == bestmakespan) { // tiebreacking
					// System.out.println("equal");
				}
			}
			Integer temp = new Integer(unselected[i]);
			pop.vector.get(bestrecord[1]).add(bestrecord[0], temp);
			// Algorithm.displayVector(pop);
			feval.evaluateSingleSequence(pop.vector.get(bestrecord[1]), pop.leaveTime[bestrecord[1]],
					pop.tailTime[bestrecord[1]], pop.vector.get(bestrecord[1]).size() - 1);
			pop.fit = bestmakespan;
			// System.out.println("--------------------------");
		}
		updatefitness(pop);
		// feval.displayLeavetime(pop);

		// System.out.println(pop.fit);
		//
		// System.out.println("----------------");
		// for (int i = 0; i <= 1; i++) {
		// System.out.println(bestrecord[i]);
		// }

		// for (int i = 1; i <= count; i++) {
		// System.out.print(unselected[i] + " ");
		// }
		// System.out.println();
	}

	public void NEH2Random(individual pop, Evaluation feval) {
		int vector[] = new int[n + 1];
		int record[] = new int[n + 1];
		int unselected[] = new int[n + 1];
		int recordFactoryandPositionandJob[] = new int[3];

		function.shuffle(n, vector);
		// 确定每一个车间的第一个工件
		for (int i = 0; i < f; i++) {
			record[vector[i + 1]] = 1;
			Integer firstJob = new Integer(vector[i + 1]);
			pop.vector.get(i).add(firstJob);
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], 1);
		}

		// 确定部分工序的工件的适应度值
		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}

		int count = 0;
		for (int i = 1; i < record.length; i++) {
			if (record[vector[i]] != 1) {
				count++;
				unselected[count] = vector[i];
			}
		}

		double bestmakespan, tempbestmakespan;

		// for (int i = 1; i <= count; i++) {
		// System.out.print(unselected[i] + " ");
		// }
		// System.out.println();
		//
		int bestrecord[] = new int[2];
		for (int i = 1; i <= count; i++) {
			bestmakespan = Double.MAX_VALUE;
			for (int k = 0; k < f; k++) {
				tempbestmakespan = insertwhole(pop, pop.vector.get(k), unselected[i], recordFactoryandPositionandJob, k,
						pop.leaveTime[k], pop.tailTime[k], feval); // 单个车间的makespan
				// System.out.println("tempbestmakespan"+tempbestmakespan);
				if (tempbestmakespan < bestmakespan) {
					bestmakespan = tempbestmakespan;
					bestrecord[0] = recordFactoryandPositionandJob[0];
					bestrecord[1] = recordFactoryandPositionandJob[1];
				} else if (tempbestmakespan == bestmakespan) { // tiebreacking
					// System.out.println("equal");
				}
			}
			Integer temp = new Integer(unselected[i]);
			pop.vector.get(bestrecord[1]).add(bestrecord[0], temp);
			// Algorithm.displayVector(pop);
			feval.evaluateSingleSequence(pop.vector.get(bestrecord[1]), pop.leaveTime[bestrecord[1]],
					pop.tailTime[bestrecord[1]], pop.vector.get(bestrecord[1]).size() - 1);
			// pop.fit = bestmakespan;
			updatefitness(pop);
			// System.out.println("--------------------------");
		}

		// feval.displayLeavetime(pop);

		// System.out.println(pop.fit);
		//
		// System.out.println("----------------");
		// for (int i = 0; i <= 1; i++) {
		// System.out.println(bestrecord[i]);
		// }

		// for (int i = 1; i <= count; i++) {
		// System.out.print(unselected[i] + " ");
		// }
		// System.out.println();
	}

	public void updatefitness(individual pop) {
		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
				pop.makespanFactoryNum = i;
			}
		}
	}

	public void SPT3(individual pop, Evaluation feval) {
		weightIndividual ajob[] = new weightIndividual[n + 1];
		for (int i = 1; i < ajob.length; i++) {
			ajob[i] = new weightIndividual();
			ajob[i].num = i;
			ajob[i].weight = totaltime[i];
		}
		Arrays.sort(ajob, 1, n + 1, function.recompareWeightIndividualMintoMax());
		int vector[] = new int[n + 1];
		for (int i = 1; i < vector.length; i++) {
			vector[i] = ajob[i].num;
		}
		dividJobsFactory(vector, pop);
		feval.blockFeval(pop);
		NEH_insertion(pop, feval);

	}

	public void LPT3(individual pop, Evaluation feval) {
		weightIndividual ajob[] = new weightIndividual[n + 1];
		for (int i = 1; i < ajob.length; i++) {
			ajob[i] = new weightIndividual();
			ajob[i].num = i;
			ajob[i].weight = totaltime[i];
		}
		Arrays.sort(ajob, 1, n + 1, function.recompareWeightIndividualMaxtoMin());
		int vector[] = new int[n + 1];
		for (int i = 1; i < vector.length; i++) {
			vector[i] = ajob[i].num;
		}
		dividJobsFactory(vector, pop);
		feval.blockFeval(pop); // important
		NEH_insertion(pop, feval);
	}

	public void EHPF23(individual pop, double lambda, double mu, Evaluation feval) {
		// 排序工件
		int record[] = new int[n + 1];
		int vector[] = new int[n + 1];
		weightIndividual jobs[] = new weightIndividual[n + 1];
		double part1 = 0, part2 = 0;
		int totalcount = 0;
		double averageload = totalprocessingtime / f;

		for (int i = 1; i <= n; i++) {
			// jobs[i] = new weightIndividual();
			// part1 = 0;
			// part2 = 0;
			// // for (int j = 1; j <= m; j++) {
			// // part1 = part1 + (double) (m - j) * T[i][j];
			// // }
			//
			// for (int j = 2; j <= m; j++) {
			// part2 = 0;
			// for (int j2 = 1; j2 <= j - 1; j2++) {
			// part2 = part2 + totaltime[j2];
			// }
			// part1 = (m * part2) / (j - 1);
			// }
			// jobs[i].num = i;
			// jobs[i].weight = part1 * (n - 2) / 4 + totaltime[i];

			part1 = 0;
			part2 = 0;
			for (int j = 1; j <= m; j++) {
				part1 = part1 + T[i][j];
				part2 = part2 + (m - j) * T[i][j] / (m - 1);
			}
			jobs[i] = new weightIndividual();
			jobs[i].num = i;
			jobs[i].weight = 2 * lambda * part2 + (1 - lambda) * part1;
		}

		Arrays.sort(jobs, 1, n + 1, function.recompareWeightIndividualMintoMax());
		// for (int i = 1; i < jobs.length; i++) {
		// System.out.println(jobs[i].num + " " + jobs[i].weight);
		// }

		// 确定每一个车间的第一个工件
		for (int i = 0; i < f; i++) {
			record[jobs[i + 1].num] = 1;
			Integer firstJob = new Integer(jobs[i + 1].num);
			pop.vector.get(i).add(firstJob);
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], 1);
		}
		//
		for (int i = 1; i < record.length; i++) {
			if (record[i] != 1) {
				totalcount++;
				vector[totalcount] = i;
			}
		}
		int count = totalcount, insertpostion, keyJob;
		double tefpi = 0, loadvalue;
		loadvalue = totaltime[pop.vector.get(0).get(1).intValue()];
		for (int i = 0; i < f;) {
			insertpostion = pop.vector.get(i).size();
			keyJob = weightEvaluation(count, mu, vector, insertpostion, pop.leaveTime[i], feval);
			if (loadvalue + totaltime[keyJob] > averageload && i != f - 1) {
				if (loadvalue + totaltime[keyJob] - averageload > averageload - loadvalue && i != f - 1) {
					i++;
					// System.out.println("loadvalue"+loadvalue);
					loadvalue = totaltime[pop.vector.get(i).get(1).intValue()];
					continue;
				}
			}
			loadvalue = loadvalue + totaltime[keyJob];
			Integer temp = new Integer(keyJob);
			pop.vector.get(i).add(temp);
			record[keyJob] = 1;
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			count = 0;
			for (int k = 1; k < record.length; k++) {
				if (record[k] != 1) {
					count++;
					vector[count] = k;
				}
			}
			if (count == 0) {
				break;
			}
		}
	}
	//start-HPF2BC
	public void HPF2BC(individual pop, double lambda, double mu, Evaluation feval) {
		// 排序工件
		int record[] = new int[n + 1];
		int vector[] = new int[n + 1];
		weightIndividual jobs[] = new weightIndividual[n + 1];
		double part1 = 0, part2 = 0;
		int totalcount = 0;
		double averageload = totalprocessingtime / f;

		for (int i = 1; i <= n; i++) {
			part1 = 0;
			part2 = 0;
			for (int j = 1; j <= m; j++) {
				part1 = part1 + T[i][j];
				part2 = part2 + (m - j) * T[i][j] / (m - 1);
			}
			jobs[i] = new weightIndividual();
			jobs[i].num = i;
			jobs[i].weight = 2 * lambda * part2 + (1 - lambda) * part1;
		}
		Arrays.sort(jobs, 1, n + 1, function.recompareWeightIndividualMintoMax());
		// 确定每一个车间的第一个工件
		for (int i = 0; i < f; i++) {
			record[jobs[i + 1].num] = 1;
			Integer firstJob = new Integer(jobs[i + 1].num);
			pop.vector.get(i).add(firstJob);
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], 1);
		}
		for (int i = 1; i < record.length; i++) {
			if (record[i] != 1) {
				totalcount++;
				vector[totalcount] = i;
			}
		}
		int count = totalcount, insertpostion, keyJob;
		double tefpi = 0, loadvalue;
		loadvalue = totaltime[pop.vector.get(0).get(1).intValue()];
		for (int i = 0; i < f;) {
			insertpostion = pop.vector.get(i).size();
			keyJob = weightEvaluation(count, mu, vector, insertpostion, pop.leaveTime[i], feval);
			if (loadvalue + totaltime[keyJob] > averageload && i != f - 1) {
				if (loadvalue + totaltime[keyJob] - averageload > averageload - loadvalue && i != f - 1) {
					i++;
					// System.out.println("loadvalue"+loadvalue);
					loadvalue = totaltime[pop.vector.get(i).get(1).intValue()];
					continue;
				}
			}
			loadvalue = loadvalue + totaltime[keyJob];
			Integer temp = new Integer(keyJob);
			pop.vector.get(i).add(temp);
			record[keyJob] = 1;
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			count = 0;
			for (int k = 1; k < record.length; k++) {
				if (record[k] != 1) {
					count++;
					vector[count] = k;
				}
			}
			if (count == 0) {
				break;
			}
		}
	}
	//end-HPF2BC
	public void PFS_random(individual pop, Evaluation feval) {
		EHPF23(pop, 0.55, 0.70, feval);
		feval.blockFeval(pop);

		NEH_insertion_front(pop, 3, feval);
		// NEH_insertion_partial(pop, 25, feval);
	}

	public void PFS_previous(individual pop, Evaluation feval) {
		EHPF23(pop, 0.55, 0.70, feval);
		feval.blockFeval(pop);

		NEH_insertion_front(pop, 1, feval);
		// NEH_insertion_partial(pop, 25, feval);
	}

	public void PFS_following(individual pop, Evaluation feval) {
		EHPF23(pop, 0.55, 0.70, feval);
		feval.blockFeval(pop);
		NEH_insertion_front(pop, 2, feval);
		// NEH_insertion_partial(pop, 25, feval);
	}

	public void PFS_double(individual pop, Evaluation feval) {
		EHPF23(pop, 0.55, 0.70, feval);
		feval.blockFeval(pop);
		NEH_insertion_double(pop, feval);
	}
	//CDWOA构造启发式，初始化方法
	public void PFS_2BC(individual pop, Evaluation feval) {
		HPF2BC(pop, 0.55, 0.70, feval);
		feval.blockFeval(pop);
		
		//NEH_insertion_double(pop, feval);//FOA启发式
		Block_critical(pop,feval);//关键工厂块插入，CDWOA启发式
	}
	
	public void PFS_origin(individual pop, Evaluation feval) {
		EHPF23(pop, 0.55, 0.70, feval);
		feval.blockFeval(pop);
		NEH_insertion(pop, feval);
	}

	public int weightEvaluation(int count, double mu, int vector[], int position, double leavetime[][],
			Evaluation feval) {
		double tefpi = 0;
		double bestfpi = Double.MAX_VALUE;
		int bestjob = 0;
		for (int j = 1; j <= count; j++) {
			tefpi = 0;
			for (int j2 = 1; j2 <= m; j2++) {
				if (j2 == m) {
					leavetime[j2][position] = leavetime[j2 - 1][position] + T[vector[j]][j2];
				} else if (j2 == 1) {
					leavetime[j2][position] = Math.max(leavetime[j2][position - 1] + T[vector[j]][j2],
							leavetime[j2 + 1][position - 1]);
				} else {
					leavetime[j2][position] = Math.max(leavetime[j2 - 1][position] + T[vector[j]][j2],
							leavetime[j2 + 1][position - 1]);
				}
				tefpi = tefpi + (leavetime[j2][position] - leavetime[j2][position - 1] - T[vector[j]][j2]);
			}
			// System.out.println("jobs "+vector[j]);
			// System.out.println("tefpi "+tefpi);
			// System.out.println("leavetime[m][position]
			// "+leavetime[m][position]);
			// System.out.println("tefpi" + (n-position-2)*tefpi);
			// tefpi = (1-ratio)*tefpi+ratio*leavetime[m][position];
			// tefpi = (n-position-2)*tefpi+leavetime[m][position];

			tefpi = mu * tefpi + (1 - mu) * (leavetime[m][position] - leavetime[m][position - 1]);
			// tefpi = (1-ratio)*tefpi +
			// ratio*(leavetime[m][position]-leavetime[m][position - 1]);

			// System.out.println("tpi "+tefpi);
			if (tefpi < bestfpi) {
				bestfpi = tefpi;
				bestjob = vector[j];
			}
		}
		// System.out.println();
		return bestjob;
	}

	// PW
	public void PW(int vector[], Evaluation feval) {

		double leavetime[][] = new double[m + 1][n + 1];
		double tailtime[][] = new double[m + 1][n + 1];
		int unselected[] = new int[n + 1]; // 带选择工件
		int lastjob[] = new int[n + 1]; // 剩余工件

		int count = n, countlast;
		// System.out.println("-----------确定第一个工件-------------------");
		double bestfa = Double.MAX_VALUE, sigma;
		int bestjob = 0;
		double tafang, weight;
		double pv[] = new double[m + 1];
		double fa;
		int index[] = new int[n + 1];
		int haveCount = 0;

		for (int i = 1; i <= n; i++) {
			unselected[i] = i;
		}
		for (int k = 1; k <= n - 1; k++) { // 位置信息
			bestfa = Double.MAX_VALUE;
			for (int i = 1; i <= count; i++) {
				sigma = 0;
				if (k == 1) {
					for (int j2 = 1; j2 <= m; j2++) {
						if (j2 == 1) {
							leavetime[j2][k] = T[unselected[i]][j2];
						} else {
							leavetime[j2][k] = leavetime[j2 - 1][k] + T[unselected[i]][j2];
						}
						weight = (double) m / (j2 + (k - 1) * (n - j2) / (n - 2));
						// System.out.println("weight"+weight);
						// System.out.println("jian" + (leavetime[j2][k] -
						// T[unselected[i]][j2]));
						sigma = sigma + weight * (leavetime[j2][k] - T[unselected[i]][j2]);
					}
				} else {
					for (int j2 = 1; j2 <= m; j2++) {
						if (j2 == m) {
							leavetime[j2][k] = leavetime[j2 - 1][k] + T[unselected[i]][j2];
						} else if (j2 == 1) {
							leavetime[j2][k] = Math.max(leavetime[j2][k - 1] + T[unselected[i]][j2],
									leavetime[j2 + 1][k - 1]);
						} else {
							leavetime[j2][k] = Math.max(leavetime[j2 - 1][k] + T[unselected[i]][j2],
									leavetime[j2 + 1][k - 1]);
						}
						weight = (double) m / (j2 + (k - 1) * (n - j2) / (n - 2));
						sigma = sigma + weight * (leavetime[j2][k] - leavetime[j2][k - 1] - T[unselected[i]][j2]);
					}
				}
				// System.out.println("sigma " + sigma);
				// 计算剩余工件
				for (int j = 1; j <= m; j++) {
					pv[j] = 0;
					for (int j2 = 1; j2 <= count; j2++) {
						if (unselected[i] != unselected[j2]) {
							pv[j] = pv[j] + T[unselected[j2]][j];
						}
					}
					pv[j] = pv[j] / (n - (k - 1) - 1);
				}
				// System.out.println();
				tafang = 0;
				for (int j2 = 1; j2 <= m; j2++) {
					if (j2 == m) {
						leavetime[j2][k + 1] = leavetime[j2 - 1][(k + 1)] + pv[j2];
					} else if (j2 == 1) {
						leavetime[j2][k + 1] = Math.max(leavetime[j2][k + 1 - 1] + pv[j2],
								leavetime[j2 + 1][k + 1 - 1]);
					} else {
						leavetime[j2][k + 1] = Math.max(leavetime[j2 - 1][k + 1] + pv[j2],
								leavetime[j2 + 1][k + 1 - 1]);
					}
					weight = (double) m / (j2 + (k - 1) * (n - j2) / (n - 2));
					tafang = tafang + weight * (leavetime[j2][k + 1] - leavetime[j2][k + 1 - 1] - pv[j2]);
				}
				fa = (n - (k - 1) - 2) * sigma + tafang;
				// System.out.println("fa " + fa);
				if (bestfa > fa) {
					bestfa = fa;
					bestjob = unselected[i];
				}
				// System.out.println();
			}
			index[bestjob] = 1;
			count = 0;
			for (int j = 1; j <= n; j++) {
				if (index[j] == 0) {
					count++;
					unselected[count] = j;
				}
			}
			haveCount++;
			vector[k] = bestjob;
			feval.evaluateSingleSequence(vector, leavetime, tailtime, haveCount);

		}
		vector[n] = unselected[1];
	}

	public void NEH2_insertion(int vector[], individual pop, Evaluation feval) {
		int record[] = new int[n + 1];
		int unselected[] = new int[n + 1];
		int recordFactoryandPositionandJob[] = new int[3];

		// 确定每一个车间的第一个工件
		for (int i = 0; i < f; i++) {
			record[vector[i + 1]] = 1;
			Integer firstJob = new Integer(vector[i + 1]);
			pop.vector.get(i).add(firstJob);
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], 1);
		}

		// 确定部分工序的工件的适应度值
		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}

		int count = 0;
		for (int i = 1; i < record.length; i++) {
			if (record[vector[i]] != 1) {
				count++;
				unselected[count] = vector[i];
			}
		}

		double bestmakespan, tempbestmakespan;

		int bestrecord[] = new int[2];
		for (int i = 1; i <= count; i++) {
			bestmakespan = Double.MAX_VALUE;
			for (int k = 0; k < f; k++) {
				tempbestmakespan = insertwhole(pop, pop.vector.get(k), unselected[i], recordFactoryandPositionandJob, k,
						pop.leaveTime[k], pop.tailTime[k], feval); // 单个车间的makespan
				// System.out.println("tempbestmakespan"+tempbestmakespan);
				if (tempbestmakespan < bestmakespan) {
					bestmakespan = tempbestmakespan;
					bestrecord[0] = recordFactoryandPositionandJob[0];
					bestrecord[1] = recordFactoryandPositionandJob[1];
				} else if (tempbestmakespan == bestmakespan) { // tiebreacking
					// System.out.println("equal");
				}
			}
			Integer temp = new Integer(unselected[i]);
			pop.vector.get(bestrecord[1]).add(bestrecord[0], temp);
			// Algorithm.displayVector(pop);
			feval.evaluateSingleSequence(pop.vector.get(bestrecord[1]), pop.leaveTime[bestrecord[1]],
					pop.tailTime[bestrecord[1]], pop.vector.get(bestrecord[1]).size() - 1);
			pop.fit = bestmakespan;
			// System.out.println("--------------------------");
		}
	}

	public void PW_NEH2(individual pop, Evaluation feval) {
		int vector[] = new int[n + 1];
		PW(vector, feval);
		NEH2_insertion(vector, pop, feval);
	}

	public void testNEH(individual pop, Evaluation feval) {
		weightIndividual ajob[] = new weightIndividual[n + 1];
		for (int i = 1; i < ajob.length; i++) {
			ajob[i] = new weightIndividual();
			ajob[i].num = i;
			ajob[i].weight = totaltime[i];
		}
		Arrays.sort(ajob, 1, n + 1, function.recompareWeightIndividualMaxtoMin());
		int vector[] = new int[n + 1];
		for (int i = 1; i < vector.length; i++) {
			vector[i] = ajob[i].num;
		}
		dividJobsFactory(vector, pop);
		feval.blockFeval(pop); // important
	}

	// 2018.10.8补充
	// Discrete differential evolution algorithm for distributed blocking
	// flowshop scheduling with makespan criterion

	public void DSPT(individual pop, Evaluation feval) {
		weightIndividual jobs[] = new weightIndividual[n + 1];
		for (int i = 0; i < jobs.length; i++) {
			jobs[i] = new weightIndividual();
			jobs[i].weight = totaltime[i];
			jobs[i].num = i;
		}
		Arrays.sort(jobs, 1, n + 1, function.recompareWeightIndividualMintoMax());
		double time[] = new double[m + 1];
		double temptime;
		double bestfit = Double.MAX_VALUE;
		int bestfactory = 0;

		for (int k = 1; k <= n; k++) {
			bestfit = Double.MAX_VALUE;
			bestfactory = 0;
			for (int i = 0; i < f; i++) {
				temptime = partilEvaluation(pop.vector.get(i), jobs[k].num, time, pop.leaveTime[i], feval);
				//System.out.println(temptime);
				if (bestfit > temptime) {
					bestfactory = i;
					bestfit = temptime;
				}
			}
			Integer temp = new Integer(jobs[k].num);
			pop.vector.get(bestfactory).add(temp);
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
		}
		updatefitness(pop);
		//checkLegal(pop, feval);
		//System.out.println("---------------");
		//Algorithm.displayVector(pop);
	}

	//
	public void DLPT(individual pop, Evaluation feval) {
		weightIndividual jobs[] = new weightIndividual[n + 1];
		for (int i = 0; i < jobs.length; i++) {
			jobs[i] = new weightIndividual();
			jobs[i].weight = totaltime[i];
			jobs[i].num = i;
		}
		Arrays.sort(jobs, 1, n + 1, function.recompareWeightIndividualMaxtoMin());
//		for (int i = 1; i < jobs.length; i++) {
//			System.out.println(jobs[i].weight + " " + jobs[i].num);
//		}
		double time[] = new double[m + 1];
		double temptime;
		double bestfit = Double.MAX_VALUE;
		int bestfactory = 0;

		for (int k = 1; k <= n; k++) {
			bestfit = Double.MAX_VALUE;
			bestfactory = 0;
			for (int i = 0; i < f; i++) {
				temptime = partilEvaluation(pop.vector.get(i), jobs[k].num, time, pop.leaveTime[i], feval);
				//System.out.println(temptime);
				if (bestfit > temptime) {
					bestfactory = i;
					bestfit = temptime;
				}
			}
			Integer temp = new Integer(jobs[k].num);
			pop.vector.get(bestfactory).add(temp);
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
		}
		updatefitness(pop);
//		checkLegal(pop, feval);
//		System.out.println("---------------");
//		Algorithm.displayVector(pop);
	}
	
	public void DLS(individual pop, Evaluation feval){
		weightIndividual jobs[] = new weightIndividual[n + 1];
		for (int i = 0; i < jobs.length; i++) {
			jobs[i] = new weightIndividual();
			jobs[i].weight = totaltime[i];
			jobs[i].num = i;
		}
		Arrays.sort(jobs, 1, n + 1, function.recompareWeightIndividualMaxtoMin());
		double time[] = new double[m + 1];
		double temptime;
		double bestfit = Double.MAX_VALUE;
		int bestfactory = 0;
		int flag = 1, start = 1, end = n,job;
		
//		for (int i = 1; i < jobs.length; i++) {
//			System.out.print(jobs[i].num+" ");
//		}
//		System.out.println();
		for (int k = 1; k <=n; k++) {
			if (flag == 1) {
				job = jobs[start].num;
				start++;
				flag--;
			}else {
				job = jobs[end].num;
				end--;
				flag++;
			}
//			System.out.println(job);
			bestfit = Double.MAX_VALUE;
			bestfactory = 0;
			for (int i = 0; i < f; i++) {
				temptime = partilEvaluation(pop.vector.get(i), job, time, pop.leaveTime[i], feval);
				//System.out.println(temptime);
				if (bestfit > temptime) {
					bestfactory = i;
					bestfit = temptime;
				}
			}
			Integer temp = new Integer(job);
			pop.vector.get(bestfactory).add(temp);
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
		}
		updatefitness(pop);
	}
	
	public void DNEH(individual pop, Evaluation feval){
		NEH2(pop, feval);
	}
	
	// 插入序列尾部评价
	public double partilEvaluation(ArrayList<Integer> jobList, int insertJob, double time[], double leaveTime[][],
			Evaluation feval) {
		double makespan = 0;
		int insertpos = jobList.size();
//		System.out.println(jobList.size());
		if (jobList.size() - 1 == 0) {
			for (int j = 1; j <= m; j++) {
				if (j == 1) {
					// System.out.println(pop.vector.get(k).get(1).intValue());
					time[j] = T[insertJob][j];
				} else {
					time[j] = time[j - 1] + T[insertJob][j];
				}
			}
		} else {
			for (int j = 1; j <= m; j++) {
				if (j == m) {
					time[j] = time[j - 1] + T[insertJob][j];
				} else if (j == 1) {
					time[j] = Math.max(leaveTime[j][insertpos - 1] + T[insertJob][j], leaveTime[j + 1][insertpos - 1]);
				} else {
					time[j] = Math.max(time[j - 1] + T[insertJob][j],
							leaveTime[j + 1][insertpos - 1]);
				}
			}
		}
		
//		for (int i = 1; i <=m; i++) {
//			System.out.println(time[i]+" ");
//		}
//		System.out.println();
		makespan = time[m];
		return makespan;
	}

	// 验证是否为非法解
	public void checkLegal(individual pop, Evaluation feval) {
		int record[] = new int[n + 1];
		for (int i = 0; i < f; i++) {
			for (int j = 1, length = pop.vector.get(i).size() - 1; j <= length; j++) {
				if (record[pop.vector.get(i).get(j).intValue()] == 0) {
					record[pop.vector.get(i).get(j).intValue()] = 1;
				} else {
					System.out.println("error permutation in " + f);
					return;
				}
			}
		}
		int k = 0;
		for (int i = 1; i <= n; i++) {
			if (record[i] == 1) {
				k++;
			}
		}

		if (k != n) {
			System.out.println("error job missing");
		}
		individual temp = new individual(n, m, f);
		function.copyIndividual(pop, temp);

		feval.blockFeval(temp);
		if (temp.fit != pop.fit) {
			System.out.println("error in fitness");
		}
		System.out.println("test" + temp.fit);
		System.out.println("pop" + pop.fit);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize param = new Initialize();
		long startime = System.currentTimeMillis();
		//double results[][] = new double[121][3];
	//	for (int iter = 1; iter <= 10; iter++) {

			for (int i = 1; i <= 120; i++) {
				param.readConfig("ta" + String.valueOf(i));
				Evaluation feval = new Evaluation();
				Hueristics hueristics = new Hueristics();
				individual pop = new individual(hueristics.n, hueristics.m, hueristics.f);

				// hueristics.RC1(pop, 0.95, 1, 1, feval);
				// hueristics.RC1(pop, 1, 0.85,hueristics.m, feval);
				// hueristics.RC1_m(pop, feval);
				// hueristics.LPT3(pop, feval);
				// hueristics.PW_NEH2(pop, feval);
				// hueristics.SPT3(pop, feval);
				// hueristics.NEH2(pop, feval);
				 //hueristics.PF3(pop, feval);
				// hueristics.HPF3(pop, feval);
				// hueristics.PFS_origin(pop, feval);
				// hueristics.PFS_previous(pop, feval);
				// hueristics.PFS_following(pop, feval);
				// hueristics.PFS_random(pop, feval);
				//hueristics.PFS_double(pop, feval);
				//hueristics.HPF2BC(pop, lambda, mu, feval);(pop, feval);
				// hueristics.checkLegal(pop, feval);2
				//hueristics.DSPT(pop, feval);
				//hueristics.DLPT(pop, feval);
				//hueristics.DLS(pop, feval);
				//hueristics.DNEH(pop, feval);
				//保存数据-start
				String filename = "HerResult/" + "EHPF2" + "/"  + String.valueOf(hueristics.f)  + ".txt";
				File file = new File(filename);
				try {
					File file1 = new File("HerResult/" + "EHPF2");
					// 如果文件夹不存在则创建
					if (!file1.exists() && !file1.isDirectory()) {
						System.out.println("//不存在，创建");
						file1.mkdir();
					}
					if (!file.exists()) {
						System.out.println("creat file");
						file.createNewFile();
					}
					FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(String.valueOf(pop.fit));
					//bw.write(String.valueOf(pop.vector.get(0).get(0)));
					bw.write("\n");
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//System.out.println("Done");
				// 保存数据-end
				//System.out.println(pop.fit);
			}
	//	}

		long endtime = System.currentTimeMillis();
		System.out.println("time " + (endtime - startime) / 1200.0);
	}
}
