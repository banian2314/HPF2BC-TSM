package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import libs.Initialize;
import libs.individual;
import libs.parametersIndividual;

public class Calibration {

	int instanceNumber = 20; // 用例个数
	int runtime = 5;
	int start = 0;
	int end = 0;
	int typeTime = 1;
	parametersIndividual con[];

	public Calibration() {
		// TODO Auto-generated constructor stub
		Algorithm.f = 1;
	}

	public void clibrationParameter() {
		int NP[] = { 1, 2, 3, 5, 10};
		int SN[] = { 3, 4, 5, 10, 15};
		int d[] = { 0, 3, 4, 5 };
		double tvalue[] = { 0.10, 0.20, 0.30, 0.40 };

		int total = NP.length * SN.length * d.length * tvalue.length;

		con = new parametersIndividual[total];
		for (int i = 0; i < con.length; i++) {
			con[i] = new parametersIndividual();
		}
		int number = 0;
		for (int i = 0; i < NP.length; i++) {
			for (int j = 0; j < SN.length; j++) {
				for (int j2 = 0; j2 < d.length; j2++) {
					for (int k = 0; k < tvalue.length; k++) {
						con[number].NP = NP[i];
						con[number].SN = SN[j];
						con[number].d = d[j2];
						con[number].tvalue = tvalue[k];
						System.out.println("group"+ (number+1)+" "+con[number].NP+" "+con[number].SN+" "+con[number].d+" "+con[number].tvalue);
						number++;
					}
				}
			}
		}

		Initialize params = new Initialize();
//		String instance;
//		for (int i = start; i <= end; i++) {
//			for (int factory = 1; factory <= 3; factory++) {
//				Algorithm.f = factory * 2;
//				for (int j = 1; j <= instanceNumber; j++) {
//					instance = "ta" + String.valueOf(j);
//					params.readConfigCalibration("ta" + String.valueOf(j));
//					FOAcalibration foAcalibration = new FOAcalibration(params, instance, typeTime);
//					foAcalibration.setParameters(con[i].NP, con[i].SN, con[i].d, con[i].tvalue);
//					for (int k = 1; k <= runtime; k++) {
//						foAcalibration.run();
//						recordClibration(foAcalibration.returnOne, i, Algorithm.f, j);
//					}
//				}
//			}
//		}
	}

	public void recordClibration(individual pop, int connumber, int f, int instanceNumber) {
		String algorithmName = "parameters";
		String instance = String.valueOf(instanceNumber + (f / 2 - 1) * 20);
		String filename = "result/" + algorithmName + "/" + connumber + "/" + instance + ".txt";

		File file = new File(filename);
		try {
			File file1 = new File("result/" + algorithmName);
			// 如果文件夹不存在则创建
			if (!file1.exists() && !file1.isDirectory()) {
				System.out.println("//不存在，创建");
				file1.mkdir();
			}
			File file2 = new File("result/" + algorithmName + "/" + connumber);
			if (!file2.exists() && !file2.isDirectory()) {
				System.out.println("//不存在,创建");
				file2.mkdir();
			}
			if (!file.exists()) {
				System.out.println("creat file");
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(String.valueOf(pop.fit));
			bw.write("\n");

			for (int i = 0; i < pop.vector.size(); i++) {
				for (int j = 1; j < pop.vector.get(i).size(); j++) {
					bw.write(String.valueOf(pop.vector.get(i).get(j)));
					bw.write(" ");
				}
				bw.write("\n");
			}
			bw.write("\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Calibration calibration = new Calibration();
		calibration.clibrationParameter();
	}

}
