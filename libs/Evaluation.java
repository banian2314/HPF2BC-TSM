package libs;

import java.util.ArrayList;

import main.Algorithm;

public class Evaluation {

	public int n;
	public int m; //
	public int f; // 车间数目
	public double p[][];
	public double dueDate[];

	public Evaluation() {
		n = libs.Initialize.job;
		m = libs.Initialize.machines;
		f = Algorithm.f;
		p = libs.Initialize.T;
	}

	public void blockFeval(individual pop) {
		double makespan = Double.MIN_VALUE;
		// departure time
		for (int k = 0; k < f; k++) {
			for (int j = 1; j <= m; j++) {
				if (j == 1) {
//					System.out.println(pop.vector.get(k).get(1).intValue());
					pop.leaveTime[k][j][1] = p[pop.vector.get(k).get(1).intValue()][j];
				} else {
					pop.leaveTime[k][j][1] = pop.leaveTime[k][j - 1][1] + p[pop.vector.get(k).get(1).intValue()][j];
				}
			}
			for (int i = 2; i < pop.vector.get(k).size(); i++) {
				for (int j = 1; j <= m; j++) {
					if (j == m) {
						pop.leaveTime[k][j][i] = pop.leaveTime[k][j - 1][i] + p[pop.vector.get(k).get(i).intValue()][j];

					} else if (j == 1) {
						pop.leaveTime[k][j][i] = Math.max(
								pop.leaveTime[k][j][i - 1] + p[pop.vector.get(k).get(i).intValue()][j],
								pop.leaveTime[k][j + 1][i - 1]);
					} else {
						pop.leaveTime[k][j][i] = Math.max(
								pop.leaveTime[k][j - 1][i] + p[pop.vector.get(k).get(i).intValue()][j],
								pop.leaveTime[k][j + 1][i - 1]);
					}
				}
			}
			if (pop.leaveTime[k][m][pop.vector.get(k).size() - 1] > makespan) {
				makespan = pop.leaveTime[k][m][pop.vector.get(k).size() - 1];
				pop.makespanFactoryNum = k;
			}

			// tail time
			for (int tk = m; tk >= 1; tk--) {
				if (tk == m) {
					pop.tailTime[k][tk][pop.vector.get(k).size()
							- 1] = p[pop.vector.get(k).get(pop.vector.get(k).size() - 1).intValue()][tk];
				} else {
					pop.tailTime[k][tk][pop.vector.get(k).size()
							- 1] = pop.tailTime[k][tk + 1][pop.vector.get(k).size() - 1]
									+ p[pop.vector.get(k).get(pop.vector.get(k).size() - 1).intValue()][tk];
				}
			}

			for (int l = pop.vector.get(k).size() - 2; l >= 1; l--) {
				for (int tk = m; tk >= 1; tk--) {
					if (tk == 1) {
						pop.tailTime[k][tk][l] = pop.tailTime[k][tk + 1][l]
								+ p[pop.vector.get(k).get(l).intValue()][tk];
					} else if (tk == m) {
						pop.tailTime[k][tk][l] = Math.max(
								pop.tailTime[k][tk][l + 1] + p[pop.vector.get(k).get(l).intValue()][tk],
								pop.tailTime[k][tk - 1][l + 1]);
					} else {
						pop.tailTime[k][tk][l] = Math.max(
								pop.tailTime[k][tk + 1][l] + p[pop.vector.get(k).get(l).intValue()][tk],
								pop.tailTime[k][tk - 1][l + 1]);
					}
				}
			}
		}
		pop.fit = makespan;
	}

	// 单序列评价 (hueristic使用)
	public double evaluateSingleSequence(int vector[], double leavetime[][], double tailtime[][], int length) {
		
		for (int j = 1; j <= m; j++) {
			if (j == 1) {
				leavetime[j][1] = p[vector[1]][j];
			} else {
				leavetime[j][1] = leavetime[j - 1][1] + p[vector[1]][j];
			}
		}

		for (int i = 2; i <= length; i++) {
			for (int j = 1; j <= m; j++) {
				if (j == m) {
					leavetime[j][i] = leavetime[j - 1][i] + p[vector[i]][j];
				} else if (j == 1) {
					leavetime[j][i] = Math.max(leavetime[j][i - 1] + p[vector[i]][j], leavetime[j + 1][i - 1]);
				} else {
					leavetime[j][i] = Math.max(leavetime[j - 1][i] + p[vector[i]][j], leavetime[j + 1][i - 1]);
				}
			}
		}

		// tail time
		for (int tk = m; tk >= 1; tk--) {
			if (tk == m) {
				tailtime[tk][length] = p[vector[length]][tk];
			} else {
				tailtime[tk][length] = tailtime[tk + 1][length] + p[vector[length]][tk];
			}
		}

		for (int l = length - 1; l >= 1; l--) {
			for (int tk = m; tk >= 1; tk--) {
				if (tk == 1) {
					tailtime[tk][l] = tailtime[tk + 1][l] + p[vector[l]][tk];
				} else if (tk == m) {
					tailtime[tk][l] = Math.max(tailtime[tk][l + 1] + p[vector[l]][tk], tailtime[tk - 1][l + 1]);
				} else {
					tailtime[tk][l] = Math.max(tailtime[tk + 1][l] + p[vector[l]][tk], tailtime[tk - 1][l + 1]);
				}
			}
		}

		return leavetime[m][length];
	}

	// 单序列评价 (hueristic使用)
	public double evaluateSingleSequence(ArrayList<Integer> jobsequence, double leavetime[][], double tailtime[][], int length) {
		
		if (length == 0) {
			leavetime = new double[m+1][n+1];
			tailtime  = new double[m+1][n+1];
			return 0;
		}
		
		for (int j = 1; j <= m; j++) {
			if (j == 1) {
				leavetime[j][1] = p[jobsequence.get(1).intValue()][j];
			} else {
				leavetime[j][1] = leavetime[j - 1][1] + p[jobsequence.get(1).intValue()][j];
			}
		}

		for (int i = 2; i <= length; i++) {
			for (int j = 1; j <= m; j++) {
				if (j == m) {
					leavetime[j][i] = leavetime[j - 1][i] + p[jobsequence.get(i).intValue()][j];
				} else if (j == 1) {
					leavetime[j][i] = Math.max(leavetime[j][i - 1] + p[jobsequence.get(i).intValue()][j], leavetime[j + 1][i - 1]);
				} else {
					leavetime[j][i] = Math.max(leavetime[j - 1][i] + p[jobsequence.get(i).intValue()][j], leavetime[j + 1][i - 1]);
				}
			}
		}
		// tail time
		for (int tk = m; tk >= 1; tk--) {
			if (tk == m) {
				tailtime[tk][length] = p[jobsequence.get(length).intValue()][tk];
			} else {
				tailtime[tk][length] = tailtime[tk + 1][length] + p[jobsequence.get(length).intValue()][tk];
			}
		}

		for (int l = length - 1; l >= 1; l--) {
			for (int tk = m; tk >= 1; tk--) {
				if (tk == 1) {
					tailtime[tk][l] = tailtime[tk + 1][l] + p[jobsequence.get(l).intValue()][tk];
				} else if (tk == m) {
					tailtime[tk][l] = Math.max(tailtime[tk][l + 1] + p[jobsequence.get(l).intValue()][tk], tailtime[tk - 1][l + 1]);
				} else {
					tailtime[tk][l] = Math.max(tailtime[tk + 1][l] + p[jobsequence.get(l).intValue()][tk], tailtime[tk - 1][l + 1]);
				}
			}
		}
		return leavetime[m][length];	
	}

	
	public double evaluateLeavetime(ArrayList<Integer> jobsequence, double leavetime[][], double tailtime[][], int length){
		
		if (length == 0) {
			leavetime = new double[m+1][n+1];
			return 0;
		}
		for (int j = 1; j <= m; j++) {
			if (j == 1) {
				leavetime[j][1] = p[jobsequence.get(1).intValue()][j];
			} else {
				leavetime[j][1] = leavetime[j - 1][1] + p[jobsequence.get(1).intValue()][j];
			}
		}

		for (int i = 2; i <= length; i++) {
			for (int j = 1; j <= m; j++) {
				if (j == m) {
					leavetime[j][i] = leavetime[j - 1][i] + p[jobsequence.get(i).intValue()][j];
				} else if (j == 1) {
					leavetime[j][i] = Math.max(leavetime[j][i - 1] + p[jobsequence.get(i).intValue()][j], leavetime[j + 1][i - 1]);
				} else {
					leavetime[j][i] = Math.max(leavetime[j - 1][i] + p[jobsequence.get(i).intValue()][j], leavetime[j + 1][i - 1]);
				}
			}
		}
		return leavetime[m][length];
	}
	// 快速评价
	public double quickEvaluation(int job, int insertpos, int length, double time[], double leavetime[][],
			double tailtime[][]) {
		// 构建插入时间
		if (insertpos == 1) { // 第一个位置
			for (int j = 1; j <= m; j++) {
				if (j == 1) {
					time[j] = p[job][j];
				} else {
					time[j] = time[j - 1] + p[job][j];
				}
			}
		} else {
			for (int j = 1; j <= m; j++) {
				if (j == m) {
					time[j] = time[j - 1] + p[job][j];
				} else if (j == 1) {
					time[j] = Math.max(leavetime[j][insertpos - 1] + p[job][j], leavetime[j + 1][insertpos - 1]);
				} else {
					time[j] = Math.max(time[j - 1] + p[job][j], leavetime[j + 1][insertpos - 1]);
				}
			}
		}
        
//		for (int j = 1; j <=m; j++) {
//			idletime = idletime + time[j]-leavetime[m][insertpos-1]-p[job][j];
//		}
//		
		
		// 计算完成时间
		double makespan = Double.MIN_VALUE;
		if (insertpos > length) {
			makespan = time[m];
		} else {
			for (int j = 1; j <= m; j++) {
				if (makespan < time[j] + tailtime[j][insertpos]) {
					makespan = time[j] + tailtime[j][insertpos];
				}
			}
		}
//		for (int j = 1; j <= m; j++) {
//			System.out.println(time[j]);
//		}
//		System.out.println();
//		System.out.println("makespan" + makespan);
//		
//		System.out.println();
//		System.out.println();
		return makespan;
	}

	// 快速评价
		public void quickEvaluation2(int job, double result[], int insertpos, int length, double time[], double leavetime[][],
				double tailtime[][]) {
	       double idletime = 0;
			// 构建插入时间
			if (insertpos == 1) { // 第一个位置
				for (int j = 1; j <= m; j++) {
					if (j == 1) {
						time[j] = p[job][j];
					} else {
						time[j] = time[j - 1] + p[job][j];
					}
				}
			} else {
				for (int j = 1; j <= m; j++) {
					if (j == m) {
						time[j] = time[j - 1] + p[job][j];
					} else if (j == 1) {
						time[j] = Math.max(leavetime[j][insertpos - 1] + p[job][j], leavetime[j + 1][insertpos - 1]);
					} else {
						time[j] = Math.max(time[j - 1] + p[job][j], leavetime[j + 1][insertpos - 1]);
					}
				}
			}
	        
			for (int j = 1; j <=m; j++) {
				idletime = idletime + time[j]-leavetime[j][insertpos-1]-p[job][j];
			}
			
			
			// 计算完成时间
			double makespan = Double.MIN_VALUE;
			if (insertpos > length) {
				makespan = time[m];
			} else {
				for (int j = 1; j <= m; j++) {
					if (makespan < time[j] + tailtime[j][insertpos]) {
						makespan = time[j] + tailtime[j][insertpos];
					}
				}
			}
//			for (int j = 1; j <= m; j++) {
//				System.out.println(time[j]);
//			}
//			System.out.println();
//			System.out.println("makespan" + makespan);
//			
//			System.out.println();
//			System.out.println();
			result[0] = makespan;
			result[1] = idletime;
		}
	
	
	// 输出离开时间
	public void displayLeavetime(individual pop) {
		for (int i = 0; i < f; i++) {
			for (int k = 1; k < pop.leaveTime[i].length; k++) {
				for (int j = 1; j < pop.vector.get(i).size(); j++) {
					System.out.print(pop.leaveTime[i][k][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	// 输出后置时间
	public void displayTailtime(individual pop) {
		for (int i = 0; i < f; i++) {
			for (int k = 1; k < pop.tailTime[i].length; k++) {
				for (int j = 1; j < pop.vector.get(i).size(); j++) {
					System.out.print(pop.tailTime[i][k][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	public void run() {
		individual pop = new individual(n, m, f);
		for (int i = 1; i <= n / 2; i++) {
			Integer job = new Integer(i);
			pop.vector.get(0).add(job);
		}
		for (int i = n / 2 + 1; i <= n; i++) {
			Integer job = new Integer(i);
			pop.vector.get(1).add(job);
		}

		//Algorithm.displayVector(pop);

//		System.out.println("");

		blockFeval(pop);
		displayLeavetime(pop);
		displayTailtime(pop);

//		System.out.println();
//		System.out.println(pop.fit);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize param = new Initialize();
		param.readConfig("ta1");
		Evaluation feval = new Evaluation();
		feval.run();
	}

}
