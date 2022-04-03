package main;

import java.util.ArrayList;
import java.util.Random;
import libs.Evaluation;

/**
 * 
 * Iterated reference greedy algorithm for solving distributed no-idle permutation flowshop scheduling problems
 * 
 * Ying and Lin
 * 
 * 2017
 * 
 * */

import libs.Initialize;
import libs.function;
import libs.individual;

public class IRG extends Algorithm {

	public Hueristics hueristics;
	public int d;
	public int dmin = 3;
	public int epsilon = 800;
	public int gama = 700;
	public double aplha = 0.65;
	public double Temprature = 10;

	public IRG(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		AlgorithmName = "IRG";
		hueristics = new Hueristics();
		d = dmin;
	}

	public void initial(individual pop, Evaluation feval) {
		hueristics.HPF3(pop, feval);
	}

	// Perturbation mechanism
	public void destruction(individual pop, ArrayList<Integer> selectjobs, ArrayList<Integer> jobList,
			Evaluation feval) {
		Random random = new Random();
		selectjobs.clear();
		jobList.clear();
		int selectpos = random.nextInt(pop.vector.get(pop.makespanFactoryNum).size() - 1) + 1;
		int result[] = new int[2];
		Integer randnum;
		selectjobs.add(pop.vector.get(pop.makespanFactoryNum).get(selectpos));
		pop.vector.get(pop.makespanFactoryNum).remove(selectpos);
		feval.evaluateSingleSequence(pop.vector.get(pop.makespanFactoryNum), pop.leaveTime[pop.makespanFactoryNum],
				pop.tailTime[pop.makespanFactoryNum], pop.vector.get(pop.makespanFactoryNum).size() - 1);
		updatefitness(pop);
		int bestpos = 0, bestposition = 0, bestfactory = 0;
		double time[] = new double[m + 1];
		double fit = 0, bestfit = 0, bestfit2;
		bestfit2 = Double.MAX_VALUE;
		for (int i = 0; i < f; i++) {
			bestfit = Double.MAX_VALUE;
			for (int j = 1, length = pop.vector.get(i).size(); j <= length; j++) {
				fit = feval.quickEvaluation(selectjobs.get(0), j, length - 1, time, pop.leaveTime[i], pop.tailTime[i]);
				if (bestfit > fit) {
					bestfit = fit;
					bestpos = j;
				}
			}
			if (bestfit < pop.fit) {
				bestfit = pop.fit;
			}
			if (bestfit2 > bestfit) {
				bestfit2 = bestfit;
				bestfactory = i;
				bestposition = bestpos;
			}
		}
		pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
		feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory], pop.tailTime[bestfactory],
				pop.vector.get(bestfactory).size() - 1);
		updatefitness(pop);
		selectjobs.remove(0);
		// System.out.println("bestfit" + pop.fit);
		// remove jobs (从每一个最大完成时间的工厂选择一个工件)
		int count = 0;
		for (int i = 0; i < f; i++) {
			if (pop.leaveTime[i][m][pop.vector.get(i).size() - 1] == pop.fit) {
				count++;
				selectpos = random.nextInt(pop.vector.get(i).size() - 1) + 1;
				selectjobs.add(pop.vector.get(i).get(selectpos));
				pop.vector.get(i).remove(selectpos);
			}
		}

		for (int i = 0; i < f; i++) {
			for (int j = 1; j < pop.vector.get(i).size(); j++) {
				jobList.add(pop.vector.get(i).get(j));
			}
		}
		for (int j = 1; j <= d - count; j++) {
			randnum = jobList.get(random.nextInt(jobList.size()));
			findJob(pop, result, randnum.intValue());
			selectjobs.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);
			jobList.remove(randnum);
		}
		// System.out.println("--------------result-----------");
		// displayVector(pop);
		// System.out.println();
		// displayIntegerArray(selectjobs);
		// System.out.println();
		// displayIntegerArray(jobList);
		// 找到最佳插入位置
	}

	public void construction(individual pop, ArrayList<Integer> selectjobs, individual bestsofar,
			ArrayList<Integer> joblist, ArrayList<Integer> bestsofarjoblist, Evaluation feval) {
		Random random = new Random();
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
		updatejoblist(pop, joblist);
		// System.out.println("--------------------pop--------");
		// displayVector(pop);
		// System.out.println("------------------joblist------");
		// displayIntegerArray(joblist);
		int bestpos = 0, bestposition = 0, bestfactory = 0;
		double time[] = new double[m + 1];
		double fit = 0, bestfit = 0, bestfit2;
		int frontjob = 0, lastjob = 0;
		int result1[] = new int[2];
		int pos;
		double lastfitness;
		boolean flag;
		int factory1 = 0, factory2 = 0, pos1, pos2;
		int recordjob;
		for (int g = 0, length = selectjobs.size(); g < length; g++) {
			bestfit2 = Double.MAX_VALUE;
			for (int i = 0; i < f; i++) {
				bestfit = Double.MAX_VALUE;
				for (int j = 1, length2 = pop.vector.get(i).size(); j <= length2; j++) {
					fit = feval.quickEvaluation(selectjobs.get(0), j, length2 - 1, time, pop.leaveTime[i],
							pop.tailTime[i]);
					if (bestfit > fit) {
						bestfit = fit;
						bestpos = j;
					}
				}
				if (bestfit < pop.fit) {
					bestfit = pop.fit;
				}
				if (bestfit2 > bestfit) {
					bestfit2 = bestfit;
					bestfactory = i;
					bestposition = bestpos;
				}
			}
			pop.vector.get(bestfactory).add(bestposition, selectjobs.get(0));
			recordjob = selectjobs.get(0);
			selectjobs.remove(0);
			feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory],
					pop.tailTime[bestfactory], pop.vector.get(bestfactory).size() - 1);
			updatefitness(pop);

			// 交换和插入
			// 找到工件在besofar 并记录前去和后续工件
			for (int i = 0, length3 = bestsofarjoblist.size(); i < length3; i++) {
				if (bestsofarjoblist.get(i).intValue() == pop.vector.get(bestfactory).get(bestposition).intValue()) {
					if (i == length3 - 1) {
						lastjob = 0; // 没有后续工件
						frontjob = bestsofarjoblist.get(random.nextInt(i));
					} else if (i == 0) {
						frontjob = 0; // 没有后续工件
						lastjob = bestsofarjoblist.get(random.nextInt(length3 - 1 - (i + 1) + 1) + (i + 1));
					} else {
						frontjob = bestsofarjoblist.get(random.nextInt(i));
						lastjob = bestsofarjoblist.get(random.nextInt(length3 - 1 - (i + 1) + 1) + (i + 1));
					}
					break;
				}
			}
			lastfitness = pop.fit;
			flag = false;
			factory1 = 0;
			factory2 = 0;
			// System.out.println("----------begin--------");
			// displayVector(pop);
			// System.out.println("frontjob " + frontjob);
			// System.out.println("lastjob " + lastjob);
			// System.out.println("bestfactoy " + bestfactory);
			// System.out.println("bestposition " + bestposition);
			// 交换操作
			if (!(bestfactory == 0 && bestposition == 1)) { // 存在前驱
				// System.out.println("findJob2(pop, result1, frontjob)" +
				// findJob2(pop, result1, frontjob));
				if (frontjob != 0 && findJob2(pop, result1, frontjob)) { // 参考序列存在前驱
					if (bestposition - 1 != 0) {
						// System.out.println("branch 1");
						flag = swapjob(pop, pop.vector.get(bestfactory), pop.vector.get(result1[0]), bestposition - 1,
								result1[1], pop.leaveTime[bestfactory], pop.tailTime[bestfactory],
								pop.leaveTime[result1[0]], pop.tailTime[result1[0]], feval);
						factory1 = bestfactory;
						pos1 = bestposition - 1;
						factory2 = result1[0];
						pos2 = result1[1];
					} else {
						// System.out.println("branch 2");
						pos = pop.vector.get(bestfactory - 1).size() - 1;
						// System.out.println();
						// System.out.println("result [0] " + result1[0]);
						// System.out.println("result [1] " + result1[1]);
						flag = swapjob(pop, pop.vector.get(bestfactory - 1), pop.vector.get(result1[0]), pos,
								result1[1], pop.leaveTime[bestfactory - 1], pop.tailTime[bestfactory - 1],
								pop.leaveTime[result1[0]], pop.tailTime[result1[0]], feval);
						factory1 = bestfactory - 1;
						pos1 = pos;
						factory2 = result1[0];
						pos2 = result1[1];
					}
					// System.out.println("-----medium------");
					// displayVector(pop);
					if (flag) {
						if (pop.fit > lastfitness) {
							// System.out.println("noimprove");
							// System.out.println();
							flag = swapjob(pop, pop.vector.get(factory1), pop.vector.get(factory2), pos1, pos2,
									pop.leaveTime[factory1], pop.tailTime[factory1], pop.leaveTime[factory2],
									pop.tailTime[factory2], feval);
							// 取出前一工件进行插入操作
							// displayVector(pop);
							// System.out.println("excutive insert 1");
							if (bestposition - 1 != 0) {
								insert(pop, bestfactory, bestposition - 1, feval);
							} else {
								insert(pop, bestfactory - 1, pop.vector.get(bestfactory - 1).size() - 1, feval);
							}
						}
					} else {
						// 执行insert 插入
						if (bestposition - 1 != 0) {
							insert(pop, bestfactory, bestposition - 1, feval);
						} else {
							insert(pop, bestfactory - 1, pop.vector.get(bestfactory - 1).size() - 1, feval);
						}
					}
				} else { //
					// System.out.println("excutive insert 2");
					if (bestposition - 1 != 0) {
						insert(pop, bestfactory, bestposition - 1, feval);
					} else {
						insert(pop, bestfactory - 1, pop.vector.get(bestfactory - 1).size() - 1, feval);
					}
				}
			}
			// System.out.println();
			// System.out.println();
			// System.out.println();
			// System.out.println("lastjobs" + lastjob);
			flag = false;
			lastfitness = pop.fit;
			flag = false;
			factory1 = 0;
			factory2 = 0;
			pos1 = 0;
			pos2 = 0;
			// displayVector(pop);
			findJob(pop, result1, recordjob);
			bestfactory = result1[0];
			bestposition = result1[1];
			// System.out.println("besfacroy " + bestfactory);
			// System.out.println("bestposition " + bestposition);
			// System.out.println("considerejob " + recordjob);
			if (!(bestfactory == f - 1 && bestposition == pop.vector.get(bestfactory).size() - 1)) { // 存在后继
				// System.out.println("findJob2(pop, result1, lastjob) " +
				// findJob2(pop, result1, lastjob));
				if (lastjob != 0 && findJob2(pop, result1, lastjob)) {
					if (bestposition != pop.vector.get(bestfactory).size() - 1) { // 在一个车间内存在后驱元素
						// System.out.println("branch 3");
						flag = swapjob(pop, pop.vector.get(bestfactory), pop.vector.get(result1[0]), bestposition + 1,
								result1[1], pop.leaveTime[bestfactory], pop.tailTime[bestfactory],
								pop.leaveTime[result1[0]], pop.tailTime[result1[0]], feval);
						factory1 = bestfactory;
						pos1 = bestposition + 1;
						factory2 = result1[0];
						pos2 = result1[1];
					} else {
						// System.out.println("branch 4");
						// System.out.println("result [0] " + result1[0]);
						// System.out.println("result [1] " + result1[1]);
						flag = swapjob(pop, pop.vector.get(bestfactory + 1), pop.vector.get(result1[0]), 1, result1[1],
								pop.leaveTime[bestfactory + 1], pop.tailTime[bestfactory + 1],
								pop.leaveTime[result1[0]], pop.tailTime[result1[0]], feval);
						factory1 = bestfactory + 1;
						pos1 = 1;
						factory2 = result1[0];
						pos2 = result1[1];
					}
					// System.out.println("----------medium-----");
					// displayVector(pop);
					// System.out.println();
					// System.out.println();
					if (flag) {
						if (pop.fit > lastfitness) {
							flag = swapjob(pop, pop.vector.get(factory1), pop.vector.get(factory2), pos1, pos2,
									pop.leaveTime[factory1], pop.tailTime[factory1], pop.leaveTime[factory2],
									pop.tailTime[factory2], feval);
							// 取出前一工件进行插入操作
							// displayVector(pop);
							// System.out.println("excutive insert 1");
							if (bestposition != pop.vector.get(bestfactory).size() - 1) {
								insert(pop, bestfactory, bestposition + 1, feval);
							} else {
								insert(pop, bestfactory + 1, 1, feval);
							}
						}
					} else {
						if (bestposition != pop.vector.get(bestfactory).size() - 1) {
							insert(pop, bestfactory, bestposition + 1, feval);
						} else {
							insert(pop, bestfactory + 1, 1, feval);
						}
					}
				} else {
					// 执行后续工件的插入
					if (bestposition != pop.vector.get(bestfactory).size() - 1) {
						insert(pop, bestfactory, bestposition + 1, feval);
					} else {
						insert(pop, bestfactory + 1, 1, feval);
					}
				}
			}

		}

		// System.out.println("---------result----");
		// displayVector(pop);
//		checkLegal(pop, feval);
		// System.out.println(pop.fit);

	}

	public void updatejoblist(individual pop, ArrayList<Integer> joblist) {
		joblist.clear();
		for (int i = 0; i < f; i++) {
			for (int j = 1; j < pop.vector.get(i).size(); j++) {
				joblist.add(pop.vector.get(i).get(j));
			}
		}
	}

	// 交换工件
	public boolean swapjob(individual pop, ArrayList<Integer> jobSequence1, ArrayList<Integer> jobSequence2,
			int position1, int position2, double leaveTime[][], double tailTime[][], double leaveTime1[][],
			double tailTime2[][], Evaluation feval) {
		if (jobSequence1.get(position1) == jobSequence2.get(position2)) {
			return false;
		}
		Integer temp = new Integer(jobSequence1.get(position1).intValue());
		Integer temp2 = new Integer(jobSequence2.get(position2).intValue());
		jobSequence1.remove(position1);
		jobSequence1.add(position1, temp2);
		jobSequence2.remove(position2);
		jobSequence2.add(position2, temp);
		feval.evaluateSingleSequence(jobSequence1, leaveTime, tailTime, jobSequence1.size() - 1);
		feval.evaluateSingleSequence(jobSequence2, leaveTime1, tailTime2, jobSequence2.size() - 1);
		updatefitness(pop);
		return true;
	}

	public void insert(individual pop, int factory, int position, Evaluation feval) {
		if (pop.vector.get(factory).size() - 1 == 1) { // 说明只有一个工件，这里不做处理
			return;
		}
		// System.out.println("---------insert front------");
		// displayVector(pop);
		// System.out.println("---------insert back--------");
		Integer temp = pop.vector.get(factory).get(position);
		// System.out.println("temp " + temp);
		pop.vector.get(factory).remove(position);
		feval.evaluateSingleSequence(pop.vector.get(factory), pop.leaveTime[factory], pop.tailTime[factory],
				pop.vector.get(factory).size() - 1);
		updatefitness(pop);
		double bestfit2, fit, bestfit;
		int bestpos = -1, bestfactory = -1, bestposition = -1;
		double time[] = new double[m + 1];
		bestfit2 = Double.MAX_VALUE;
		for (int i = 0; i < f; i++) {
			bestfit = Double.MAX_VALUE;
			for (int j = 1, length2 = pop.vector.get(i).size(); j <= length2; j++) {
				fit = feval.quickEvaluation(temp, j, length2 - 1, time, pop.leaveTime[i], pop.tailTime[i]);
				if (bestfit > fit) {
					bestfit = fit;
					bestpos = j;
				}
			}
			if (bestfit < pop.fit) {
				bestfit = pop.fit;
			}
			if (bestfit2 > bestfit) {
				bestfit2 = bestfit;
				bestfactory = i;
				bestposition = bestpos;
			}
		}
		pop.vector.get(bestfactory).add(bestposition, temp);
		feval.evaluateSingleSequence(pop.vector.get(bestfactory), pop.leaveTime[bestfactory], pop.tailTime[bestfactory],
				pop.vector.get(bestfactory).size() - 1);
		updatefitness(pop);
		// displayVector(pop);
	}

	public void run() {
		Evaluation feval = new Evaluation();
		individual pop = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);
		individual tempindividual = new individual(n, m, f);
		ArrayList<Integer> selectjobs = new ArrayList<Integer>();
		ArrayList<Integer> joblist = new ArrayList<Integer>();
		ArrayList<Integer> bestsofarjoblist = new ArrayList<Integer>();

		initial(pop, feval);
		function.copyIndividual(pop, bestsofar);
		function.copyIndividual(pop, tempindividual);
		updatejoblist(bestsofar, bestsofarjoblist);
		int nonImprovementCount = 0;
		boolean improvement;
		Temprature = Temprature * aplha;
		
		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();
		
		for (int i = 1; endtime-starttime<cpuTime; i++) {
			improvement = false;
			// Perturbation mechanism
			destruction(tempindividual, selectjobs, joblist, feval);
			construction(tempindividual, selectjobs, bestsofar, joblist, bestsofarjoblist, feval);

			if (tempindividual.fit < pop.fit) {
				// System.out.println("improve");
				function.copyIndividual(tempindividual, pop);
				if (tempindividual.fit < bestsofar.fit) {
					improvement = true;
					function.copyIndividual(tempindividual, bestsofar);
				}
			} else if (Math.random() < Math.exp((pop.fit - tempindividual.fit) / Temprature)) {
				//System.out.println("jump");
				function.copyIndividual(tempindividual, pop);
				// System.out.println("temp"+tempindividual.fit);
				// System.out.println("pop"+pop.fit);
			} else {
				function.copyIndividual(pop, tempindividual);
			}
			if (improvement == false) {
				nonImprovementCount++;
				if (nonImprovementCount >= epsilon) {
					d++;
					nonImprovementCount = 0;
					if (d > 10) {
						d = dmin;
					}
				}
			} else {
				nonImprovementCount = 0;
				d = dmin;
			}
			if (i % gama == 0) {
				Temprature = Temprature * aplha;
			}
			endtime = System.currentTimeMillis();
			System.out.println(bestsofar.fit);
//			System.out.println("d  "+d);
		}
		System.out.println("well done");
		System.out.println("time "+(endtime-starttime));
		displayVector(bestsofar);
		checkLegal(bestsofar, feval);
		recordResualt(bestsofar);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta1";
		params.readConfig(instance);
		IRG irg = new IRG(params, instance, 7);
		irg.run();
	}

}
