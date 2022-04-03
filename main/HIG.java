package main;

import java.util.ArrayList;
import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

/**
 * Minimizing Makespan in Distributed Blocking Flowshops Using Hybrid Iterated
 * Greedy Algorithms
 * 
 * Lin 2017
 * 
 */
public class HIG extends Algorithm {

	public double Tvalue = 0.03;
	public int Iter = 3000;
	public double lambda = 0.900;
	public double TLTrLow = 0.05;
	public double TLTrhigh = 0.10;
	public int alphamin = 3;
	public int alphmax = 6;
	public double TLTlow;
	public double TLThigi;
	public ArrayList<Integer> TabuList;
	public double Temprature;

	public HIG(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		AlgorithmName = "HIG";
		TLTlow = TLTrLow * n;
		TLThigi = TLTrhigh * n;
		TabuList = new ArrayList<Integer>();

	}

	public void initial(individual pop, Evaluation feval) {
		Hueristics hueristics = new Hueristics();
		hueristics.PW_NEH2(pop, feval);
		Temprature = Tvalue * hueristics.totalprocessingtime;
	}

	public void destruction(individual pop, ArrayList<Integer> selectJob, Evaluation feval) {

		int TLT = (int) Math.round((TLTlow + Math.random() * (TLThigi - TLTlow))); // 四舍五入

		// System.out.println("TLT" + TLT);
		double smalltime = Double.MAX_VALUE;
		int smallfactory = 0;
		// displayVector(pop);
		int job;
		int result[] = new int[2]; // 用于返回工件的位置和工厂
		int recordIndex[] = new int[n + 1];
		int canselectedJob[] = new int[n + 1];
		int randnum;
		int alpha;
		selectJob.clear();
		for (int i = 0; i < TabuList.size(); i++) {
			recordIndex[TabuList.get(i).intValue()] = i;
		}
		// System.out.println("-----------原始种群---------");
		// displayVector(pop);
		// System.out.println("numberMakespan"+ pop.makespanFactoryNum);
		// 找到工厂最小

		// System.out.println("smellfactory"+smallfactory);
		// 先移除工件最大完成时间
		job = selectedFromfactory(pop, recordIndex, selectJob, pop.makespanFactoryNum);
		recordIndex[job] = 1;
		// System.out.println("slectedjob" + job);

		// System.out.println("pop.makespanFactoryNum" +
		// pop.makespanFactoryNum);
		// for (int i = 1; i < pop.vector.get(pop.makespanFactoryNum).size();
		// i++) {
		// System.out.print(pop.vector.get(pop.makespanFactoryNum).get(i).intValue()
		// + " ");
		// }
		// System.out.println();
		// System.out.println();

		for (int i = 0; i < f; i++) {
			// System.out.println(i+"
			// "+pop.leaveTime[i][m][pop.vector.get(i).size() - 1]);
			if (i == pop.makespanFactoryNum) {
				continue;
			}
			if (smalltime > pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				smalltime = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
				smallfactory = i;
			}
		}

		job = selectedFromfactory(pop, recordIndex, selectJob, smallfactory);
		recordIndex[job] = 1;
//		System.out.println("slectedjob" + job);
//		System.out.println("smallfactory " + smallfactory);

		// System.out.println("smallfactory" + smallfactory);
		// for (int i = 1; i < pop.vector.get(smallfactory).size(); i++) {
		// System.out.print(pop.vector.get(smallfactory).get(i).intValue() + "
		// ");
		// }
		// System.out.println();
		// for (int i = 1; i < recordIndex.length; i++) {
		// System.out.print(recordIndex[i] + " ");
		// }
		int count = 0;
		for (int i = 1; i <= n; i++) {
			if (recordIndex[i] == 0) {
				count++;
				canselectedJob[count] = i;
			}
		}
		// System.out.println();
		// System.out.println("--------可以选择的工件-------------");
		// for (int i = 1; i < canselectedJob.length; i++) {
		// System.out.print(canselectedJob[i] + " ");
		// }
		// System.out.println();
		alpha = (int) Math.round((alphamin + Math.random() * (alphmax - alphamin)));
		alpha = alpha - 2;
		// System.out.println(alpha);
		// System.out.println("---------------geti-----------");
		// displayVector(pop);

		for (int j = 1; j <= alpha; j++) {
			randnum = (int) Math.ceil(Math.random() * count);
			while (recordIndex[canselectedJob[randnum]] == 1) {
				randnum = (int) Math.ceil(Math.random() * count);
				// System.out.println("hahha");
			}
			// System.out.println("selectedjob " + canselectedJob[randnum]);

			recordIndex[canselectedJob[randnum]] = 1;
			findJob(pop, result, canselectedJob[randnum]);

			// System.out.println("position " + result[0] + " " + result[1]);

			selectJob.add(pop.vector.get(result[0]).get(result[1]));
			pop.vector.get(result[0]).remove(result[1]);

			count = 0;
			for (int i = 1; i <= n; i++) {
				if (recordIndex[i] == 0) {
					count++;
					canselectedJob[count] = i;
				}
			}
		}

		// displayVector(pop);
		// System.out.println();
		// System.out.println();
//		System.out.println("-----------------selected job----------");
//		for (int i = 0; i < selectJob.size(); i++) {
//			System.out.print(selectJob.get(i) + " ");
//		}
//		System.out.println();
		// 更新禁忌表
		updateTabuList(selectJob, TLT);

		// System.out.println("------禁忌表----------");
		// for (int i = 0; i < TabuList.size(); i++) {
		// System.out.print(TabuList.get(i).intValue() + " ");
		// }
		// System.out.println();
		// System.out.println("----------");
		// displayVector(pop);
		//
		// System.out.println("------------");
		//
		// for (int i = 0; i < f; i++) {
		// System.out.println(pop.vector.get(i).size() + " ");
		// }
		// System.out.println();

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

	}

	// 返回值为工件
	public int selectedFromfactory(individual pop, int recordIndex[], ArrayList<Integer> selectJob, int factory) {

		int count = 0;
		int canselecetposition[] = new int[n + 1];
		int maxjob, randomnum;
		for (int i = 1, length = pop.vector.get(factory).size(); i < length; i++) {
			if (!isInTabulist(pop.vector.get(factory).get(i).intValue())) {
				// if (recordIndex[pop.vector.get(factory).get(i).intValue()]
				// !=1) {
				count++;
				canselecetposition[count] = i; // 存储为工件位置
			}
		}
		// for (int i = 1; i <= count; i++) {
		// System.out.print(canselecetposition[i] + " ");
		// }
		// System.out.println();
		randomnum = (int) Math.ceil(Math.random() * count); //
		if (randomnum == 0) {
			// System.out.println("exp");
			randomnum = (int) Math.ceil(Math.random() * (pop.vector.get(factory).size() - 1));
			maxjob = pop.vector.get(factory).get(randomnum).intValue();
			selectJob.add(pop.vector.get(factory).get(randomnum));
			pop.vector.get(factory).remove(randomnum);
		} else {
			maxjob = pop.vector.get(factory).get(canselecetposition[randomnum]).intValue();
			// System.out.println("maxjob" + maxjob);
			// 在移除工件
			selectJob.add(pop.vector.get(factory).get(canselecetposition[randomnum]));
			pop.vector.get(factory).remove(canselecetposition[randomnum]);
		}
		return maxjob;
	}

	// 更新禁忌表
	public void updateTabuList(ArrayList<Integer> selectJobsequence, int TLT) {
		if (TabuList.size() + selectJobsequence.size() <= TLT) {
			for (int i = 0; i < selectJobsequence.size(); i++) {
				Integer temp = new Integer(selectJobsequence.get(i).intValue());
				TabuList.add(temp);
			}
		} else {
			// System.out.println("hahh");
			for (int i = 0; i < selectJobsequence.size(); i++) {
				Integer temp = new Integer(selectJobsequence.get(i).intValue());
				TabuList.add(temp);
			}
			// System.out.println("size" + TabuList.size());
			while (TabuList.size() != TLT) {
				TabuList.remove(0);
			}
		}
	}

	public boolean isInTabulist(int job) {

		for (int i = 0, length = TabuList.size(); i < length; i++) {
			if (TabuList.get(i).intValue() == job) {
				return true;
			}
		}
		return false;
	}

	// 整体插入
	public double insertwhole(individual pop, ArrayList<Integer> Jobsequence, int job, int recordPositionandfactory[],
			int factory, double leavetime[][], double tailtime[][], Evaluation feval) {

		double bestfit = Double.MAX_VALUE, fit = 0;
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

	public void construction(individual pop, ArrayList<Integer> selectJob, Evaluation feval) {
		double bestmakespan, tempbestmakespan;
		int recordFactoryandPositionandJob[] = new int[3];
		int bestrecord[] = new int[2];
		boolean flag;
		// System.out.println("----------pop--------");
		// displayVector(pop);
		// System.out.println("-----selectJob-------");
		// displayEachFactory(selectJob);

		for (int i = 0, length = selectJob.size(); i < length; i++) {
			bestmakespan = Double.MAX_VALUE;
			flag = false;
			for (int k = 0; k < f; k++) {
				if (pop.vector.get(k).size() - 1 == 0) { //
					bestrecord[0] = 1;
					bestrecord[1] = k;
					flag = true;
					break;
				}
				tempbestmakespan = insertwhole(pop, pop.vector.get(k), selectJob.get(i).intValue(),
						recordFactoryandPositionandJob, k, pop.leaveTime[k], pop.tailTime[k], feval); // 单个车间的makespan
				if (tempbestmakespan < bestmakespan) {
					bestmakespan = tempbestmakespan;
					bestrecord[0] = recordFactoryandPositionandJob[0];
					bestrecord[1] = recordFactoryandPositionandJob[1];
				}
			}

			pop.vector.get(bestrecord[1]).add(bestrecord[0], selectJob.get(i));
			// Algorithm.displayVector(pop);
			feval.evaluateSingleSequence(pop.vector.get(bestrecord[1]), pop.leaveTime[bestrecord[1]],
					pop.tailTime[bestrecord[1]], pop.vector.get(bestrecord[1]).size() - 1);
			updatefitness(pop);
			// if (flag == true) {
			// bestmakespan = Double.MIN_VALUE;
			// for (int j = 0; j < f; j++) {
			// if (bestmakespan < pop.leaveTime[j][m][pop.vector.get(j).size() -
			// 1]) {
			// bestmakespan = pop.leaveTime[j][m][pop.vector.get(j).size() - 1];
			// }
			// }
			// }
			// pop.fit = bestmakespan;
		}
		// updatefitness(pop);
	}

	public void run() {
		Evaluation feval = new Evaluation();
		individual pop = new individual(n, m, f);
		individual tempindividual = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);
		initial(pop, feval);
		ArrayList<Integer> selectJob = new ArrayList<Integer>();
		// TabuList.clear();
		System.out.println("Temprature" + Temprature);
		function.copyIndividual(pop, bestsofar);
		function.copyIndividual(pop, tempindividual);
		long startime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		// Temprature = Temprature / (n * m * 10);

		for (int i = 0; cpuTime > endtime - startime; i++) {
			destruction(tempindividual, selectJob, feval);
			construction(tempindividual, selectJob, feval);
			// System.out.println("probability"+Math.exp((pop.fit -
			// tempindividual.fit) /
			// Temprature));
			if (tempindividual.fit <= pop.fit) {
				function.copyIndividual(tempindividual, pop);
				if (tempindividual.fit <= bestsofar.fit) {
					function.copyIndividual(tempindividual, bestsofar);
				}
			} else if (Math.random() < Math.exp((pop.fit - tempindividual.fit) / Temprature)) {
				function.copyIndividual(tempindividual, pop);
				// System.out.println("temp"+tempindividual.fit);
				// System.out.println("pop"+pop.fit);
			} else {
				function.copyIndividual(pop, tempindividual);
			}
			//System.out.println(bestsofar.fit);
			Temprature = lambda * Temprature;
			// System.out.println("temp"+Temprature);
			endtime = System.currentTimeMillis();
		}
		System.out.println("well done");
		System.out.println("time " + (endtime - startime));
		displayVector(bestsofar);
		recordResualt(bestsofar);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta7";
		params.readConfig(instance);
		HIG hig = new HIG(params, instance, 2);
		hig.run();
	}

}
