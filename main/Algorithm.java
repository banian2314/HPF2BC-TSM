package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import libs.Evaluation;
import libs.Initialize;
import libs.function;
import libs.individual;

public class Algorithm {
	public int n; // ��������
	public int m; // ��������
	public static int f = 3; // ������Ŀ

	public String AlgorithmName;
	public String instance;
	public int cpuTime; // �������ʱ��
	public int typeTime;
	public Initialize params;
	public double totalflowtime[]; // ÿ�����������ӳ�ʱ��
	public double T[][];

	public Algorithm(Initialize params, String instance, int typeTime) {
		// TODO Auto-generated constructor stub
		this.params = params;
		this.instance = instance;
		this.typeTime = typeTime;
		n = Initialize.job;
		m = Initialize.machines;
		T = params.T;
		switch (this.typeTime) {
		case 0:
			cpuTime = 5 * n * m * f;
			break;
		case 1:
			cpuTime = 10*n * m * f;//2021.4.21 CPUʱ���޸�
			break;
		case 2:
			cpuTime = 10 * n * m * f;
			break;
		case 3:
			cpuTime = 60 * n * m * f;
			break;
		case 4:
			cpuTime = 80 * n * m * f;
			break;
		case 5:
			cpuTime = 100 * n * m * f;
			break;
		case 6:
			cpuTime = 15 * n * n * m / 100;
			break;
		case 7:
			cpuTime = 30 * n * n * m / 100;
			break;
		default:
			cpuTime = 5 * n * m * f;
			break;
		}
		double sum = 0;
		totalflowtime = new double[n + 1];
		for (int i = 1; i <= n; i++) {
			sum = 0;
			for (int j = 1; j <= m; j++) {
				sum = sum + T[i][j];
			}
			totalflowtime[i] = sum;
		}
	}

	public static void displayVector(individual pop) {
		for (int i = 0; i < pop.vector.size(); i++) {
			for (int j = 1; j < pop.vector.get(i).size(); j++) {
				System.out.print(pop.vector.get(i).get(j) + " ");
			}
			System.out.println();
		}
	}

	public static void displayEachFactory(ArrayList<Integer> pop) {
		for (int j = 1; j < pop.size(); j++) {
			System.out.print(pop.get(j).intValue() + " ");
		}
		System.out.println();
	}

	public static void displayIntegerArray(ArrayList<Integer> pop) {
		for (int j = 0; j < pop.size(); j++) {
			System.out.print(pop.get(j).intValue() + " ");
		}
		System.out.println();
	}

	// ������Ӧ��ֵ
	public void updatefitness(individual pop) {
		pop.fit = Double.MIN_VALUE;
		for (int i = 0; i < f; i++) {
			if (pop.fit < pop.leaveTime[i][m][pop.vector.get(i).size() - 1]) {
				pop.fit = pop.leaveTime[i][m][pop.vector.get(i).size() - 1];
				pop.makespanFactoryNum = i;
			}
		}
	}

	// �ҵ�������λ��
	public void findJob(individual pop, int result[], int job) {
		for (int i = 0; i < f; i++) {
			for (int j = 1; j < pop.vector.get(i).size(); j++) {
				if (pop.vector.get(i).get(j).intValue() == job) {
					result[0] = i;
					result[1] = j;
					return;
				}
			}
		}
	}

	// �ҵ�������λ��
	public boolean findJob2(individual pop, int result[], int job) {
		for (int i = 0; i < f; i++) {
			for (int j = 1; j < pop.vector.get(i).size(); j++) {
				if (pop.vector.get(i).get(j).intValue() == job) {
					result[0] = i;
					result[1] = j;
					return true;
				}
			}
		}
		return false;
	}

	// ��֤�Ƿ�Ϊ�Ƿ���
	public void checkLegal(individual pop, Evaluation feval) {
		int record[] = new int[n + 1];
		for (int i = 0; i < f; i++) {
			for (int j = 1, length = pop.vector.get(i).size() - 1; j <= length; j++) {
				if (record[pop.vector.get(i).get(j).intValue()] == 0) {
					record[pop.vector.get(i).get(j).intValue()] = 1;
				} else {
					System.out.println("");
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
	}

	// �������Ƿ���ͬ
	public boolean similarIndiviudal(individual pop1, individual pop2) {
		boolean flag = true;
		for (int i = 0; i < f; i++) {
			if (pop1.vector.get(i).size() != pop2.vector.get(i).size()) {
				return false;
			}
			for (int j = 1, length = pop1.vector.get(i).size(); j < length; j++) {
				if (pop1.vector.get(i).get(j).intValue() != pop2.vector.get(i).get(j).intValue()) {
					return false;
				}
			}
		}
		return flag;
	}

	public void recordResualt(individual pop) {

		if (AlgorithmName == null) {
			System.out.println("The algorithm name is not stated");
			return;
		}
		String cpuTimeName = "";
		switch (this.typeTime) {
		case 1:
			cpuTimeName = "20nmf";
			break;
		case 2:
			cpuTimeName = "40nmf";
			break;
		case 3:
			cpuTimeName = "60nmf";
			break;
		case 4:
			cpuTimeName = "80nmf";
			break;
		case 5:
			cpuTimeName = "100nmf";
			break;
		case 6:
			cpuTimeName = "15nnm_100"; // Ribas 1
			break;
		case 7:
			cpuTimeName = "30nnm_100"; // Ribas 2
			break;
		default:
			cpuTimeName = "5nmf_2";
			break;
		}

		String filename = "result/" + AlgorithmName + "/" + cpuTimeName + "/" + String.valueOf(f) + "/" + instance
				+ ".txt";
		File file = new File(filename);
		try {
			File file1 = new File("result/" + AlgorithmName);
			// ����ļ��в������򴴽�
			if (!file1.exists() && !file1.isDirectory()) {
				System.out.println("//�����ڣ�����");
				file1.mkdir();
			}
			File file2 = new File("result/" + AlgorithmName + "/" + cpuTimeName);
			if (!file2.exists() && !file2.isDirectory()) {
				System.out.println("//������,����");
				file2.mkdir();
			}
			File file3 = new File("result/" + AlgorithmName + "/" + cpuTimeName + "/" + String.valueOf(f));
			if (!file3.exists() && !file3.isDirectory()) {
				System.out.println("//������,����");
				file3.mkdir();
			}
			if (!file.exists()) {
				System.out.println("creat file");
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(String.valueOf(pop.fit));
			//bw.write("\n");
			//for (int i = 0, length1 = pop.vector.size(); i < length1; i++) {
				//for (int j = 1, length2 = pop.vector.get(i).size(); j < length2; j++) {
					bw.write(String.valueOf(pop.vector.get(0).get(0)));
					//bw.write(" ");
				//}
				bw.write("\n");
			//}
			//bw.write("\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
