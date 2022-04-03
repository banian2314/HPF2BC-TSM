package main;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

public class ILS extends IGA {

	public ILS(Initialize params, String instance, int typeTime) {
		super(params, instance, typeTime);
		AlgorithmName = "ILS_HPF23";
		// TODO Auto-generated constructor stub
	}

	public void run(){
		individual pop = new individual(n, m, f);
		individual bestsofar = new individual(n, m, f);
		individual tempindividual = new individual(n, m, f);
		Evaluation feval = new Evaluation();
		System.out.println("cputime" + cpuTime);
		initial(pop, feval);
		VNS(pop, feval);

		Temprature = temp * Temprature / (n * m * 10);

		
		System.out.println("temprature"+ Temprature);
		function.copyIndividual(pop, bestsofar);
		function.copyIndividual(pop, tempindividual);	
		long starttime = System.currentTimeMillis();
		long endtime = System.currentTimeMillis();

		for (int i = 1; endtime - starttime < cpuTime; i++) {
			// System.out.println("copy");
			perturbation_ILS(tempindividual, d, feval);
			VNS(tempindividual, feval);			
			reassingnment(tempindividual, feval);
			permutation(tempindividual, feval);
			
			if (tempindividual.fit < pop.fit) {
				function.copyIndividual(tempindividual, pop);
				if (tempindividual.fit < bestsofar.fit) {
					function.copyIndividual(tempindividual, bestsofar);
				}
			} else if (Math.random() < Math.exp((pop.fit - tempindividual.fit) / Temprature)) {
//				System.out.println("jump");
				function.copyIndividual(tempindividual, pop);
//				System.out.println("temp"+tempindividual.fit);
//				System.out.println("pop"+pop.fit);
			}else {
				function.copyIndividual(pop, tempindividual);
			}	
			System.out.println(bestsofar.fit);
			endtime = System.currentTimeMillis();
		}
		System.out.println("well done");
		System.out.println("time " + (endtime - starttime));
		displayVector(bestsofar);
		recordResualt(bestsofar);
		checkLegal(bestsofar, feval);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		String instance = "ta1";
		params.readConfig(instance);
		ILS ils= new ILS(params, instance, 2);
		ils.run();
	}

}
