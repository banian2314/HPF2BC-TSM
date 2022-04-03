package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.BaseStream;

import javax.swing.PopupFactory;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

public class IGA extends Algorithm {

	public int d = 4;
	public double temp = 0.4;
	public int nintlim = 30;
	public double Temprature;

	public IGA(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		AlgorithmName = "IGA_HPF23";
	}

	public void initial(individual pop, Evaluation feval) {
		Hueristics hueristics = new Hueristics();
		hueristics.HPF3(pop, feval);
		Temprature = hueristics.totalprocessingtime;
	}

	public double LS1(individual pop, ArrayList<Integer> jobseqeunce, double leavetime[][], double tailtime[][],
			Evaluation feval) {
		int count = jobseqeunce.size() - 1;
		int vector[] = new int[count + 1];

		// System.out.println(count);
		for (int i = 0; i < vector.length; i++) {
			vector[i] = jobseqeunce.get(i).intValue();
		}
		// for (int i = 1; i <vector.length; i++) {
		// System.out.print(vector[i]+" ");
		// }
		// System.out.println();
		function.shuffleArray(vector, count); // 随机生成工件

		int jobIndex[] = new int[n + 1]; // 工件的位置索引
		for (int i = 1; i <= count; i++) {
			jobIndex[jobseqeunce.get(i).intValue()] = i;
		}

		// System.out.println();
		// System.out.println("-----shao-------");
		// for (int i = 1; i <vector.length; i++) {
		// System.out.print(vector[i]+" ");
		// }
		// System.out.println();
		// System.out.println();

		int removejobpos, bestpos = 0;
		double fit, bestfit, makespan;
		makespan = leavetime[m][count];

		// System.out.println("makespan" + makespan);

		for (int i = 1; i <= count; i++) {
			removejobpos = jobIndex[vector[i]];
			// System.out.println("removejobpos"+removejobpos);
			// System.out.println("removejob"+vector[i]);
			bestfit = Double.MAX_VALUE;

			// for (int j2 = 1; j2 <=jobseqeunce.size()-1; j2++) {
			// System.out.print(jobseqeunce.get(j2) + " ");
			// }
			// System.out.println();
			// System.out.println();

			for (int j = removejobpos + 1; j <= count; j++) {
				Collections.swap(jobseqeunce, removejobpos, j);
				// fit = feval.evaluateSingleSequence(jobseqeunce, leavetime,
				// tailtime, count);
				fit = feval.evaluateLeavetime(jobseqeunce, leavetime, tailtime, count);
				// fit = feval.swapEvaluateSingleSequence
				// System.out.println("fit "+fit);

				// for (int j2 = 1; j2 <=jobseqeunce.size()-1; j2++) {
				// System.out.print(jobseqeunce.get(j2) + " ");
				// }
				// System.out.println();

				if (bestfit > fit) {
					bestfit = fit;
					bestpos = j;
				}
				Collections.swap(jobseqeunce, removejobpos, j);
			}
			// System.out.println("bestfit"+bestfit);
			// System.out.println("bestpos"+bestpos);
			if (bestfit < makespan) {
				// System.out.println("hahah");
				Collections.swap(jobseqeunce, removejobpos, bestpos);
				fit = feval.evaluateLeavetime(jobseqeunce, leavetime, tailtime, count);
				makespan = leavetime[m][count];
				for (int k = 1; k <= count; k++) {
					jobIndex[jobseqeunce.get(k).intValue()] = k;
				}
			}
			// System.out.println("makespan"+makespan);
		}
		// for (int j2 = 1; j2 <=jobseqeunce.size()-1; j2++) {
		// System.out.print(jobseqeunce.get(j2) + " ");
		// }
		// System.out.println();

		feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count); // 用于恢复矩阵(tail)

		return leavetime[m][count];
		// System.out.println(leavetime[m][count]);

	}

	public double LS2(individual pop, ArrayList<Integer> jobseqeunce, double leavetime[][], double tailtime[][],
			Evaluation feval) {
		int count = jobseqeunce.size() - 1;
		int vector[] = new int[count + 1];

		// System.out.println(count);
		for (int i = 0; i < vector.length; i++) {
			vector[i] = jobseqeunce.get(i).intValue();
		}

		function.shuffleArray(vector, count); // 随机生成工件

		// System.out.println("-------reference--------");
		// for (int i = 1; i < vector.length; i++) {
		// System.out.print(vector[i] + " ");
		// }
		// System.out.println();
		// System.out.println("----------------");

		int jobIndex[] = new int[n + 1]; // 工件的位置索引
		for (int i = 1; i <= count; i++) {
			jobIndex[jobseqeunce.get(i).intValue()] = i;
		}

		int removejobpos, bestpos = 0;
		double fit, bestfit, makespan;
		makespan = leavetime[m][count];
		Integer temp;

		makespan = leavetime[m][count];
		double time[] = new double[m + 1];

		for (int i = 1; i <= count; i++) {
			// System.out.println("vector[i]"+vector[i]);
			removejobpos = jobIndex[vector[i]];
			// System.out.println("removejobpos"+removejobpos);
			// System.out.println("removejob"+vector[i]);

			temp = jobseqeunce.get(removejobpos);
			jobseqeunce.remove(removejobpos);

			feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count - 1);

			bestfit = feval.quickEvaluation(vector[i], count, count, time, leavetime, tailtime);
			// System.out.println("fit"+bestfit);
			bestpos = count;

			for (int j = count - 1; j >= 1; j--) {
				// Collections.swap(jobseqeunce, j + 1, j);

				// for (int j2 = 1; j2 <= jobseqeunce.size() - 1; j2++) {
				// System.out.print(jobseqeunce.get(j2) + " ");
				// }
				// System.out.println();
				if (j == removejobpos) {
					continue;
				}

				fit = feval.quickEvaluation(vector[i], j, count, time, leavetime, tailtime);

				// double testfit = feval.evaluateSingleSequence(jobseqeunce,
				// testleavetime,testtailtime, count);

				// System.out.println("fit "+fit);
				// System.out.println("test "+testfit);

				if (bestfit > fit) {
					bestfit = fit;
					bestpos = j;
				}
			}

			// System.out.println("bestfit " + bestfit);
			// System.out.println("bestpos " + bestpos);

			if (bestfit < makespan) {
				// System.out.println("improve");
				temp = new Integer(vector[i]);
				// jobseqeunce.remove(1);
				jobseqeunce.add(bestpos, temp);

				feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count);

				// for (int j2 = 1; j2 <= jobseqeunce.size() - 1; j2++) {
				// System.out.print(jobseqeunce.get(j2) + " ");
				// }
				// System.out.println();

				makespan = leavetime[m][count];
				for (int k = 1; k <= count; k++) {
					jobIndex[jobseqeunce.get(k).intValue()] = k;
				}
				// System.out.println("makespan "+makespan);
			} else {
				temp = new Integer(vector[i]);
				// jobseqeunce.remove(1);
				jobseqeunce.add(removejobpos, temp);
			}

			// for (int j2 = 1; j2 <= jobseqeunce.size() - 1; j2++) {
			// System.out.print(jobseqeunce.get(j2) + " ");
			// }
			// System.out.println();
			// System.out.println();
			// System.out.println();
		}

		feval.evaluateSingleSequence(jobseqeunce, leavetime, tailtime, count);

		// for (int j = 1; j < leavetime.length; j++) {
		// for (int j2 = 1; j2 <= count; j2++) {
		// System.out.print(leavetime[j][j2] + " ");
		// }
		// System.out.println();
		// }
		return leavetime[m][count];
	}

	public void VNS(individual pop, Evaluation feval) {
		int flag = 0, nm = 0;
		boolean improve;
		double makespan, makespan2;

		for (int i = f - 1; i < f; i++) {
			if (Math.random() < 0.5) {
				flag = 1;
			} else {
				flag = 0;
			}
			improve = true;
			makespan = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			nm = 0;
			while (improve) {
				nm++;
				if (flag == 1) {
					// System.out.println("LS1");
					makespan2 = LS1(pop, pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], feval);
				} else {
					// System.out.println("LS2");
					makespan2 = LS2(pop, pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i], feval);
				}
				if (makespan > makespan2) {
					// System.out.println("branch1");
					makespan = makespan2;
					improve = true;
					nm = 0;
				} else if (nm == 1) {
					// System.out.println("branch2");
					improve = true;
				} else {
					// System.out.println("branch3");
					improve = false;
				}
				flag = 1 - flag;
			}
		}
		updatefitness(pop);
	}

	public void perturbation_ILS(individual pop, int d, Evaluation feval) {
		int randnum;
		int recordSelected[] = new int[n + 1];
		int result[] = new int[2]; // 用于返回工件的位置和工厂
		int randomfactory;
		double bestfit, fit = 0;
		int bestpos = 0;
		Integer removejob;
		double time[] = new double[m + 1];
		// double testleavetime[][] = new double[m+1][n+1];
		// double testtailtime[][] = new double[m+1][n+1];
		for (int i = 1; i <= d; i++) {
			randnum = (int) Math.ceil(Math.random() * n);
			while (recordSelected[randnum] == 1) {
				randnum = (int) Math.ceil(Math.random() * n);
			}
			recordSelected[randnum] = 1;
			findJob(pop, result, randnum);
			randomfactory = (int) Math.ceil(Math.random() * f) - 1; // 随机选择一个工厂
			// remove job
			if (pop.vector.get(result[0]).size() - 1 == 1) { // 原文中没有这个问题
				i--;
				continue;
			}
			removejob = pop.vector.get(result[0]).get(result[1]);
			pop.vector.get(result[0]).remove(result[1]);
			feval.evaluateLeavetime(pop.vector.get(result[0]), pop.leaveTime[result[0]], pop.tailTime[result[0]],
					pop.vector.get(result[0]).size() - 1);
			bestfit = Double.MAX_VALUE;
			for (int j = 1, length = pop.vector.get(randomfactory).size(); j <= length; j++) {
				fit = feval.quickEvaluation(removejob, j, length - 1, time, pop.leaveTime[randomfactory],
						pop.tailTime[randomfactory]);
				// pop.vector.get(randomfactory).add(j, removejob);
				// double testfit =
				// feval.evaluateLeavetime(pop.vector.get(randomfactory),
				// testleavetime,
				// testtailtime, pop.vector.get(randomfactory).size() - 1);
				// System.out.println("fit "+fit);
				// System.out.println("testfit "+testfit);
				if (bestfit > fit) {
					bestfit = fit;
					bestpos = j;
				}
				// pop.vector.get(randomfactory).remove(j);
			}
			pop.vector.get(randomfactory).add(bestpos, removejob);
			feval.evaluateSingleSequence(pop.vector.get(randomfactory), pop.leaveTime[randomfactory],
					pop.tailTime[randomfactory], pop.vector.get(randomfactory).size() - 1);
		}
		updatefitness(pop);
	}

	public void perturbation_IGA(individual pop, int d, Evaluation feval) {
		int randnum;
		int recordSelected[] = new int[n + 1];
		int result[] = new int[2]; // 用于返回工件的位置和工厂
		double makespan, bestfit, fit;
		int bestpos = 0;
		int bestfactory = 0, bestinsertpos = 0;
		ArrayList<Integer> selectejobs = new ArrayList<Integer>();
		// remove job
		for (int j = 1; j <= d; j++) {
			randnum = (int) Math.ceil(Math.random() * n);
			while (recordSelected[randnum] == 1) {
				randnum = (int) Math.ceil(Math.random() * n);
				// System.out.println("hahha");
			}
			recordSelected[randnum] = 1;
			findJob(pop, result, randnum);
			selectejobs.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);
		}

		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			feval.evaluateSingleSequence(pop.vector.get(i), pop.leaveTime[i], pop.tailTime[i],
					pop.vector.get(i).size() - 1);
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
			}
		}
		double time[] = new double[m + 1];
		// double testleavetime[][] = new double[m+1][n+1];
		// double testtailtime[][] = new double[m+1][n+1];
		for (int i = 1; i <= d; i++) {
			makespan = Double.MAX_VALUE;
			// Integer insertjob = new Integer(selected[i]);
			for (int j = 0; j < f; j++) {
				bestfit = Double.MAX_VALUE;
				for (int j2 = 1, length = pop.vector.get(j).size(); j2 <= length; j2++) {
					// pop.vector.get(j).add(j2, insertjob);
					fit = feval.quickEvaluation(selectejobs.get(0), j2, length - 1, time, pop.leaveTime[j],
							pop.tailTime[j]);
					// double testfit =
					// feval.evaluateLeavetime(pop.vector.get(j), testleavetime,
					// testtailtime,
					// pop.vector.get(j).size() - 1);
					// pop.vector.get(j).remove(j2);
					// System.out.println("fit "+fit);
					// System.out.println("testfit "+testfit);
					// feval.displayLeavetime(pop);
					if (bestfit > fit) {
						bestfit = fit;
						bestpos = j2;
					}
				}
				// System.out.println("bestfit"+bestfit);
				if (bestfit < makespan) {
					makespan = bestfit;
					bestfactory = j;
					bestinsertpos = bestpos;
				}
				// System.out.println("============");
			}
			pop.vector.get(bestfactory).add(bestinsertpos, selectejobs.get(0));
			selectejobs.remove(0);
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			// displayVector(pop);
		}
		updatefitness(pop);
	}

	public void reassingnment(individual pop, Evaluation feval) {
		int flag = 1;
		int h1 = 0, h2 = 0;
		int q = 0;
		int vector[] = new int[n + 1];
		int jobvector[] = new int[n + 1];
		int factoryVector[] = new int[f];
		int index[] = new int[n + 1];
		int index2[] = new int[n + 1];
		int fmax, bestpos1 = 0, bestpos2 = 0;
		double Cmax1, Cmax2, fit;
		double time[] = new double[m + 1];
		do {
			flag = 0;
			h1 = 1;
			fmax = pop.makespanFactoryNum;
			for (int i = 1; i < pop.vector.get(fmax).size(); i++) {
				vector[i] = pop.vector.get(fmax).get(i).intValue();
			}
			// 构建位置索引
			for (int i = 1; i < pop.vector.get(fmax).size(); i++) {
				index[pop.vector.get(fmax).get(i).intValue()] = i;
			}

			function.shuffleArray(vector, pop.vector.get(fmax).size() - 1);
			// System.out.println("------------选择工件----------");
			// for (int i = 1; i <= pop.vector.get(fmax).size() - 1; i++) {
			// System.out.print(vector[i] + " ");
			// }
			// System.out.println();
			// System.out.println();
			// for (int i = 1; i <=n; i++) {
			// System.out.print(index[i]+" ");
			// }
			// System.out.println();

			while (h1 < nintlim && h1 <= pop.vector.get(fmax).size() - 1 && flag == 0) {
				// 选择工厂
				for (int j = 0, k = 0; j < f; j++) {
					if (j != pop.makespanFactoryNum) {
						k++;
						factoryVector[k] = j;
					}
				}
				function.shuffleArray(factoryVector, f - 1);

				// System.out.println("-------工厂顺序-------");
				//
				// for (int i = 1; i <=f-1; i++) {
				// System.out.print(factoryVector[i]+" ");
				// }
				// System.out.println();
				// System.out.println();

				q = 1;

				while (q <= f - 1 && flag == 0) {
					h2 = 1;
					// 选择交换的工件

					for (int i = 1; i <= pop.vector.get(factoryVector[q]).size() - 1; i++) {
						jobvector[i] = pop.vector.get(factoryVector[q]).get(i).intValue();
					}

					function.shuffleArray(jobvector, pop.vector.get(factoryVector[q]).size() - 1);

					// System.out.println("factory"+factoryVector[q]);
					for (int i = 1; i <= pop.vector.get(factoryVector[q]).size() - 1; i++) {
						index2[pop.vector.get(factoryVector[q]).get(i).intValue()] = i;
						// System.out.print(jobvector[i]+" ");
					}

					// System.out.println(" ");

					while (h2 < nintlim && h2 <= pop.vector.get(factoryVector[q]).size() - 1 && flag == 0) {

						// System.out.println("cjob"+vector[h1]);
						// System.out.println("index"+index[vector[h1]]);

						pop.vector.get(fmax).remove(index[vector[h1]]);

						feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
								pop.vector.get(fmax).size() - 1);

						// System.out.println("job"+jobvector[h2]);
						// System.out.println("index2 "+index2[jobvector[h2]]);

						pop.vector.get(factoryVector[q]).remove(index2[jobvector[h2]]);
						// System.out.println("-------test-------");
						// displayVector(pop);
						// System.out.println("--------------------");
						feval.evaluateSingleSequence(pop.vector.get(factoryVector[q]), pop.leaveTime[factoryVector[q]],
								pop.tailTime[factoryVector[q]], pop.vector.get(factoryVector[q]).size() - 1);

						Cmax1 = Double.MAX_VALUE;

						for (int i = 1, length = pop.vector.get(fmax).size(); i <= length; i++) {
							// Integer temp = new Integer(jobvector[h2]);
							// pop.vector.get(fmax).add(i, temp);

							// double testfit =
							// feval.evaluateLeavetime(pop.vector.get(fmax),
							// testleavetime, testtailtime,
							// pop.vector.get(fmax).size() - 1);
							fit = feval.quickEvaluation(jobvector[h2], i, length - 1, time, pop.leaveTime[fmax],
									pop.tailTime[fmax]);

							// System.out.println("fit" + fit);
							// System.out.println("testfit" + testfit);

							if (Cmax1 > fit) {
								Cmax1 = fit;
								bestpos1 = i;
							}
							// pop.vector.get(fmax).remove(i);
						}
						Cmax2 = Double.MAX_VALUE;
						// System.out.println();
						// System.out.println();
						for (int i = 1, length = pop.vector.get(factoryVector[q]).size(); i <= length; i++) {
							// Integer temp = new Integer(vector[h1]);
							// System.out.println("q " +q);
							// System.out.println("factory[q]
							// "+factoryVector[q]);
							// pop.vector.get(factoryVector[q]).add(i, temp);

							// double testfit =
							// feval.evaluateLeavetime(pop.vector.get(factoryVector[q]),
							// testleavetime, testtailtime,
							// pop.vector.get(factoryVector[q]).size() - 1);

							fit = feval.quickEvaluation(vector[h1], i, length - 1, time,
									pop.leaveTime[factoryVector[q]], pop.tailTime[factoryVector[q]]);

							// System.out.println("testfit1 "+testfit);
							// System.out.println("fit "+ fit);
							// System.out.println("fit"+fit);
							if (Cmax2 > fit) {
								Cmax2 = fit;
								bestpos2 = i;
							}
							// pop.vector.get(factoryVector[q]).remove(i);
						}

						// System.out.println("cmax1 "+Cmax1);
						// System.out.println("cmax2 "+Cmax2);
						// System.out.println("pop.fit"+pop.fit);

						if (Cmax1 < pop.fit && Cmax2 < pop.fit) {
							// System.out.println("successs");
							flag = 1;

							Integer temp1 = new Integer(vector[h1]);
							Integer temp2 = new Integer(jobvector[h2]);
							pop.vector.get(fmax).add(bestpos1, temp2);
							pop.vector.get(factoryVector[q]).add(bestpos2, temp1);
							feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
									pop.vector.get(fmax).size() - 1);
							feval.evaluateSingleSequence(pop.vector.get(factoryVector[q]),
									pop.leaveTime[factoryVector[q]], pop.tailTime[factoryVector[q]],
									pop.vector.get(factoryVector[q]).size() - 1);
							// displayVector(pop);

						} else {
							// return i1 and i2 to their pevious line and
							// position
							// System.out.println("fail");
							Integer temp1 = new Integer(vector[h1]);
							Integer temp2 = new Integer(jobvector[h2]);
							pop.vector.get(fmax).add(index[vector[h1]], temp1);
							pop.vector.get(factoryVector[q]).add(index2[jobvector[h2]], temp2);
							feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
									pop.vector.get(fmax).size() - 1);
							feval.evaluateSingleSequence(pop.vector.get(factoryVector[q]),
									pop.leaveTime[factoryVector[q]], pop.tailTime[factoryVector[q]],
									pop.vector.get(factoryVector[q]).size() - 1);
						}
						// System.out.println();
						// System.out.println();
						// System.out.println("flag"+flag);
						h2++;
					}
					q++;
				}
				h1++;
			}
			// feval.blockFeval(pop);
			updatefitness(pop);

			// System.out.println(pop.fit);
			if (flag == 0) {
				break;
			}
		} while (true);
	}

	public void permutation(individual pop, Evaluation feval) {
		int flag, h1, fmax, q;
		int vector[] = new int[n + 1];
		int factoryVector[] = new int[f];
		int index[] = new int[n + 1];
		int bestpos1 = 0;
		double Cmax1, fit;
		Integer temp;
		double time[] = new double[m + 1];
		double testleavtime[][] = new double[m + 1][n + 1];
		double testtailtime[][] = new double[m + 1][n + 1];
		do {
			flag = 0;
			h1 = 1; // 与原文有区别（数据结构原因）
			fmax = pop.makespanFactoryNum;
			for (int i = 1; i < pop.vector.get(fmax).size(); i++) {
				vector[i] = pop.vector.get(fmax).get(i).intValue();
			}
			// 构建位置索引
			for (int i = 1; i < pop.vector.get(fmax).size(); i++) {
				index[pop.vector.get(fmax).get(i).intValue()] = i;
			}
			function.shuffleArray(vector, pop.vector.get(fmax).size() - 1);
			// System.out.println("------------选择工件----------");
			// for (int i = 1; i <= pop.vector.get(fmax).size() - 1; i++) {
			// System.out.print(vector[i] + " ");
			// }
			// System.out.println();
			// System.out.println();
			while (h1 < nintlim && h1 <= pop.vector.get(fmax).size() - 1 && flag == 0) {
				// 选择工厂
				for (int j = 0, k = 0; j < f; j++) {
					if (j != pop.makespanFactoryNum) {
						k++;
						factoryVector[k] = j;
					}
				}
				function.shuffleArray(factoryVector, f - 1);
				// System.out.println("-------工厂顺序-------");
				// for (int i = 1; i <= f - 1; i++) {
				// System.out.print(factoryVector[i] + " ");
				// }
				// System.out.println();
				// System.out.println();
				q = 1;
				while (q <= f - 1 && flag == 0) {
					temp = pop.vector.get(fmax).get(index[vector[h1]]);
					// pop.vector.get(fmax).remove(index[vector[h1]]);
					Cmax1 = Double.MAX_VALUE;
					for (int i = 1, length = pop.vector.get(factoryVector[q]).size(); i <= length; i++) {
						// pop.vector.get(factoryVector[q]).add(i, temp);
						// for (int j = 1; j <
						// pop.vector.get(factoryVector[q]).size(); j++) {
						// System.out.print(pop.vector.get(factoryVector[q]).get(j).intValue()
						// + " ");
						// }
						// System.out.println();
						fit = feval.quickEvaluation(temp, i, length - 1, time, pop.leaveTime[factoryVector[q]],
								pop.tailTime[factoryVector[q]]);
						// double testfit =
						// feval.evaluateLeavetime(pop.vector.get(factoryVector[q]),
						// testleavtime,
						// testtailtime, pop.vector.get(factoryVector[q]).size()
						// - 1);
						//
						// pop.vector.get(factoryVector[q]).remove(i);
						// System.out.println("testfit " + testfit);
						// System.out.println("fit " + fit);
						if (Cmax1 > fit) {
							Cmax1 = fit;
							bestpos1 = i;
						}

					}
					// System.out.println("cmax1 " + Cmax1);
					if (Cmax1 < pop.fit) {
						flag = 1;
						// System.out.println("success");
						pop.vector.get(fmax).remove(index[vector[h1]]);
						pop.vector.get(factoryVector[q]).add(bestpos1, temp);
						feval.evaluateSingleSequence(pop.vector.get(factoryVector[q]), pop.leaveTime[factoryVector[q]],
								pop.tailTime[factoryVector[q]], pop.vector.get(factoryVector[q]).size() - 1);
						feval.evaluateSingleSequence(pop.vector.get(fmax), pop.leaveTime[fmax], pop.tailTime[fmax],
								pop.vector.get(fmax).size() - 1);
					}
					// else {
					// // System.out.println("fail");
					// pop.vector.get(fmax).add(index[vector[h1]], temp);
					// }
					q++;
				}
				h1++;
			}
			updatefitness(pop);
			// checkLegal(pop, feval);
			// System.out.println();
			// System.out.println();
			// System.out.println(pop.fit);
			if (flag == 0) {
				break;
			}
			//
		} while (true);
		// System.out.println(pop.fit);
		// checkLegal(pop, feval);
		// System.out.println("------------fact fitnesss-------");
		// feval.blockFeval(pop);
		// System.out.println(pop.fit);

	}

	public void run() {
		individual pop = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);
		individual tempindividual = new individual(n, m, f);
		Evaluation feval = new Evaluation();
		System.out.println("cputime" + cpuTime);
		initial(pop, feval);
		/** 测试代码 */
		// long starttime = System.currentTimeMillis();
		//
		// // perturbation_IGA(pop, d, feval);
		// // perturbation_ILS(pop, d, feval);
		// System.out.println(pop.fit);
		// permutation(pop, feval);
		// // reassingnment(pop, feval);
		// checkLegal(pop, feval);
		// // permutation(pop, feval);
		// System.out.println(pop.fit);
		// long endtime = System.currentTimeMillis();
		// System.out.println("time" + (endtime - starttime));

		/** --------------------主函数--------- */

		VNS(pop, feval);

		Temprature = temp * Temprature / (n * m * 10);
		System.out.println("temprature" + Temprature);
		function.copyIndividual(pop, bestsofar);
		function.copyIndividual(pop, tempindividual);

		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		for (int i = 1; endtime-starttime<cpuTime; i++) {
			perturbation_IGA(tempindividual, d, feval);
//			System.out.println("perturbation_IGA");
//			checkLegal(tempindividual, feval);

			VNS(tempindividual, feval);
//			System.out.println("VNS");
//			checkLegal(tempindividual, feval);

			
			reassingnment(tempindividual, feval);
//			System.out.println("reassingment");
//			checkLegal(tempindividual, feval);

		
			permutation(tempindividual, feval);
//			System.out.println("permutation");
//			checkLegal(tempindividual, feval);

			if (tempindividual.fit < pop.fit) {
				function.copyIndividual(tempindividual, pop);
				if (tempindividual.fit < bestsofar.fit) {
					function.copyIndividual(tempindividual, bestsofar);
				}
			} else if (Math.random() < Math.exp((pop.fit - tempindividual.fit) / Temprature)) {
				// System.out.println("jump");
				function.copyIndividual(tempindividual, pop);
				// System.out.println("temp"+tempindividual.fit);
				// System.out.println("pop"+pop.fit);
			} else {
				function.copyIndividual(pop, tempindividual);
			}
			//System.out.println(bestsofar.fit);
			endtime = System.currentTimeMillis();
		}
		System.out.println("well done");
		System.out.println("time " + (endtime - starttime));
		displayVector(bestsofar);
		checkLegal(bestsofar, feval);
		recordResualt(bestsofar);
	//%%
//	
//		String filename = "result/" + AlgorithmName + "/" + "20nmf" + "/" + String.valueOf(f) + "/" + instance
//				+ ".txt";
//		File file = new File(filename);
//		try {
//			File file1 = new File("result/" + AlgorithmName);
//			// 如果文件夹不存在则创建
//			if (!file1.exists() && !file1.isDirectory()) {
//				System.out.println("//不存在，创建");
//				file1.mkdir();
//			}
//			File file2 = new File("result/" + AlgorithmName + "/" + "20nmf");
//			if (!file2.exists() && !file2.isDirectory()) {
//				System.out.println("//不存在,创建");
//				file2.mkdir();
//			}
//			File file3 = new File("result/" + AlgorithmName + "/" +"20nmf"+ "/" + String.valueOf(f));
//			if (!file3.exists() && !file3.isDirectory()) {
//				System.out.println("//不存在,创建");
//				file3.mkdir();
//			}
//			if (!file.exists()) {
//				System.out.println("creat file");
//				file.createNewFile();
//			}
//
//			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//			BufferedWriter bw = new BufferedWriter(fw);
//			bw.write(String.valueOf(pop.fit));
//			//bw.write("\n");
//			//for (int i = 0, length1 = pop.vector.size(); i < length1; i++) {
//				//for (int j = 1, length2 = pop.vector.get(i).size(); j < length2; j++) {
//					bw.write(String.valueOf(pop.vector.get(0).get(0)));
//					//bw.write(" ");
//				//}
//				bw.write("\n");
//			//}
//			//bw.write("\n");
//			bw.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println("Done");
//	
	
	//%%
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta1";
		params.readConfig(instance);
		IGA iga = new IGA(params, instance, 2);
		iga.run();
	}

}
