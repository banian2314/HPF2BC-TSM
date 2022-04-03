package main;

import java.util.ArrayList;
import java.util.Random;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

public class FOAcalibration extends FOA {

	public individual returnOne;
	public int dlocal;
	
	public FOAcalibration(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		// TODO Auto-generated constructor stub
		returnOne = new individual(n, m, f);
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
		Random random = new Random();
		if (dlocal == 0) {
			d = dmain[random.nextInt(dmain.length)];
		}
//		System.out.println(" d" + d);
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
	}

	// smell search
	public void smellSearch(individual pop[], individual offspring[], Evaluation feval) {
		ArrayList<Integer> selectjobs = new ArrayList<Integer>();
		for (int i = 0; i < NP; i++) {
			for (int j = i * S; j < i * S + S; j++) {
				function.copyIndividual(pop[i], offspring[j]);
				destruction(offspring[j], selectjobs, feval);
				construction_tieFirst(offspring[j], selectjobs, feval);
			}
		}
	}

	// vision search
	public void visionSearch(individual pop[], individual offspring[], individual bestsofar, Evaluation feval) {
		double bestmakespan;
		int bestposition = 0;
		for (int i = 0; i < NP; i++) {
			bestmakespan = Double.MAX_VALUE;
			for (int j = i * S; j < i * S + S; j++) {
				if (offspring[j].fit < bestmakespan) {
					bestposition = j;
				}
			}
			localSearch(offspring[bestposition], feval);
			if (offspring[bestposition].fit <= pop[i].fit) {
				function.copyIndividual(offspring[bestposition], pop[i]);
			} else if (Math.random() < (Math.exp((pop[i].fit - offspring[bestposition].fit) / Temprature))) { //

				function.copyIndividual(offspring[bestposition], pop[i]);
			}
		}
	}

	public void setParameters(int NP, int SN, int d, double tvalue) {
		this.NP = NP;
		this.S = SN;
		this.d = d;
		this.tvalue = tvalue;
		this.dlocal = d; //为了d = 0的情况
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
		
		System.out.println("d "+d);
		System.out.println("NP" +NP);
		System.out.println("SN"+S);
		System.out.println("tvalue"+tvalue);
		for (int i = 1; end - start < cpuTime; i++) {
			smellSearch(pop, offspring, feval);
			visionSearch(pop, offspring, bestsofar, feval);
			updateBestSofar(bestsofar, pop, feval);
			System.out.println("bestsofar " + bestsofar.fit);
			end = System.currentTimeMillis();
		}
		returnOne = bestsofar;
		System.out.println("well done");
		System.out.println("time " + (end - start));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
