package main;

import libs.Initialize;

//运行所有算法
public class RunAlgorithm {
	public int algorithm =10;
	/**
	 * 1. IGA 2. ILS 3. HIG (Minimizing Makespan in Distributed Blocking
	 * Flowshops Using Hybrid Iterated Greedy Algorithms) 4. IG2S (Iterated
	 * greedy method for the distributed permutation flowshop scheduling
	 * problem) 5. SS (A scatter search algorithm for the distributed
	 * permutation flow shop scheduling problem) 6. BSIG (A bounded-search
	 * iterated greedy algorithm for the distributed permutation flow shop
	 * scheduling problem) 7. MIG (Minimising makespan in distributed
	 * permutation flowshops using a modified iterated greedy algorithm) 8. ICG
	 * (Minimizing makespan for solving the distributed no-wait flowshop
	 * scheduling problem) 9. IRG (Iterated reference greedy algorithm for
	 * solving distributed no-idle permutation flowshop scheduling problems) 10.HPF2BC-TSM
	 */
	int typeTime = 1; // 时间类型 100*m*n
	int runtime = 5; // 运行次数
	String instance = "";
	public void run() {
		for (int i = 1; i <= 24; i++) {
			instance = "ta" + String.valueOf(i);
			Initialize params = new Initialize();
			params.readConfig(instance);
			switch (algorithm) {
			case 1:
				for (int j = 1; j <= runtime; j++) {
					IGA iga = new IGA(params, instance, typeTime);
					iga.run();
				}
				break;
			case 2:
				for (int j = 1; j <= runtime; j++) {
					ILS ils = new ILS(params, instance, typeTime);
					ils.run();
				}
				break;
			case 3:
				for (int j = 1; j <= runtime; j++) {
					HIG hig = new HIG(params, instance, typeTime);
					hig.run();
				}
				break;
			case 4:
				for (int j = 1; j <= runtime; j++) {
					IG2S ig2s = new IG2S(params, instance, typeTime);
					ig2s.run();
				}
				break;
			case 5:
				for (int j = 1; j <= runtime; j++) {
					SS ss = new SS(params, instance, typeTime);
					ss.run();
				}
				break;
			case 6:
				for (int j = 1; j <= runtime; j++) {
					BSIG bsig = new BSIG(params, instance, typeTime);
					bsig.run();
				}
				break;
			case 7:
				for (int j = 1; j <= runtime; j++) {
					MIG mig = new MIG(params, instance, typeTime);
					mig.run();
				}
				break;
			case 8:
				for (int j = 1; j <= runtime; j++) {
					ICG icg = new ICG(params, instance, typeTime);
					icg.run();
				}
				break;
			case 9:
				for (int j = 1; j <= runtime; j++) {
					IRG irg = new IRG(params, instance, typeTime);
					irg.run();
				}
				break;
			case 10:
				for (int j = 1; j <= runtime; j++) {
					HPF2BC-TSM tsm = new HPF2BC-TSM(params, instance, typeTime);
					foa.run();
				}
				break;
			case 11:
				for (int j = 1; j <= runtime; j++) {
					DDE dde = new DDE(params, instance, typeTime);
					dde.run();
				}
				break;
			default:
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RunAlgorithm runAlgorithm = new RunAlgorithm();
		runAlgorithm.run();
	}

}
