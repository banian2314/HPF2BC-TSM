package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import libs.Result;
import libs.parametersIndividual;

public class DataPrcoessCalibration {

	public int runtime = 5;
	parametersIndividual con[];
	public int totalclibration;
	public int totalclibration2;

	parametersIndividual con2[];

	public DataPrcoessCalibration() {
		generateInstance();
	}

	public void generateInstance() {
		int NP[] = { 1, 2, 3, 5, 10 };
		int SN[] = { 3, 4, 5, 10, 15 };
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
						// System.out.println("group" + (number + 1) + " " + con[number].NP + " " +
						// con[number].SN + " "
						// + con[number].d + " " + con[number].tvalue);
						number++;
					}
				}
			}
		}
		totalclibration = con.length;

		/** --------------------------------------------------------- */
		int SN2[] = { 1, 2 };
		int NP2[] = { 1, 2, 3, 4, 5, 10};
		int total2 = NP2.length * SN2.length * d.length * tvalue.length;
		con2 = new parametersIndividual[total2];
		for (int i = 0; i < con2.length; i++) {
			con2[i] = new parametersIndividual();
		}
		number = 0;
		for (int i = 0; i < NP2.length; i++) {
			for (int j = 0; j < SN2.length; j++) {
				for (int j2 = 0; j2 < d.length; j2++) {
					for (int k = 0; k < tvalue.length; k++) {
						con2[number].NP = NP2[i];
						con2[number].SN = SN2[j];
						con2[number].d = d[j2];
						con2[number].tvalue = tvalue[k];
						// System.out.println("group" + (number + 1) + " " + con[number].NP + " " +
						// con[number].SN + " "
						// + con[number].d + " " + con[number].tvalue);
						number++;
					}
				}
			}
		}
		totalclibration2 = con2.length;
	}

	// 读取算法结果
	public void readResults(String conMumber, String instance, Result result[], int filenumber) {
		int i = 1;
		String filename = "";
		if (filenumber == 1) {
			filename = "result/parameters/" + conMumber + "/" + instance + ".txt";
		} else {
			filename = "result/parameters2/" + conMumber + "/" + instance + ".txt";
		}
		File file = new File(filename);
		System.out.println(filename);
		int k = 1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				if (s.length() == 0) {
					k = 1;
					i++;
					continue;
				}
				if (k == 1) {
					result[i].result = Double.parseDouble(s);
					k++;
				} else {
					if (result[i].vector == null) {
						result[i].vector = s;
					} else {
						result[i].vector = result[i].vector + "\n" + s;
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 记录文件结果
	public void fileRecord(parametersIndividual con, double response) {
		String filename = "result/resultfile/resultCal.txt";
		File file = new File(filename);
		try {
			if (!file.exists()) {
				System.out.println("creat file");
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(String.valueOf(con.NP));
			bw.write("\t");
			bw.write(String.valueOf(con.SN));
			bw.write("\t");
			bw.write(String.valueOf(con.d));
			bw.write("\t");
			bw.write(String.valueOf(con.tvalue));
			bw.write("\t");
			bw.write(String.valueOf(response));
			bw.write("\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {

		Result result[][][] = new Result[totalclibration + 1][61][runtime + 1];
		Result result2[][][] = new Result[totalclibration2 + 1][61][runtime + 1];

		for (int i = 1; i <= totalclibration; i++) {
			for (int j = 1; j <= 60; j++) {
				for (int j2 = 1; j2 <= runtime; j2++) {
					result[i][j][j2] = new Result();
				}
			}
		}
		for (int i = 1; i <= totalclibration2; i++) {
			for (int j = 1; j <= 60; j++) {
				for (int j2 = 1; j2 <= runtime; j2++) {
					result2[i][j][j2] = new Result();
				}
			}
		}
		double bestsolution[] = new double[61];
		for (int i = 0; i < 61; i++) {
			bestsolution[i] = Double.MAX_VALUE;
		}

		for (int i = 1; i <= totalclibration; i++) {
			if (con[i - 1].NP == 1) {
				continue;
			}
			for (int j = 1; j <= 60; j++) {
				// for (int k = 1; k <= runtime; k++) {
				// System.out.println(result[i][j][k].result);
				// }
				readResults(String.valueOf(i - 1), String.valueOf(j), result[i][j], 1);
				for (int k = 1; k <= runtime; k++) {
					if (bestsolution[j] > result[i][j][k].result) {
						bestsolution[j] = result[i][j][k].result;
					}
				}
			}
		}

		for (int i = 1; i <= totalclibration2; i++) {
			if (con2[i - 1].NP == 4||con2[i - 1].NP == 1) {
				continue;
			}
			for (int j = 1; j <= 60; j++) {
				// for (int k = 1; k <= runtime; k++) {
				// System.out.println(result[i][j][k].result);
				// }
				readResults(String.valueOf(i - 1), String.valueOf(j), result2[i][j], 2);
				for (int k = 1; k <= runtime; k++) {
					if (bestsolution[j] > result2[i][j][k].result) {
						bestsolution[j] = result2[i][j][k].result;
					}
				}
			}
		}

		for (int i = 1; i < bestsolution.length; i++) {
			System.out.println(bestsolution[i]);
		}

		for (int i = 1; i <= totalclibration; i++) {
			if (con[i - 1].NP == 1) {
				continue;
			}
			for (int j = 1; j <= 60; j++) {
				for (int j2 = 1; j2 <= runtime; j2++) {
					// System.out.print(bestsolution[j]+" ");
					// System.out.print(result[i][j][j2].result+" ");
					result[i][j][j2].result = 100 * (result[i][j][j2].result - bestsolution[j]) / bestsolution[j];
					// System.out.println(result[i][j][j2].result);
					fileRecord(con[i - 1], result[i][j][j2].result);
				}
			}
		}
		System.out.println("------------------------second---------");
		for (int i = 1; i <= totalclibration2; i++) {
			if (con2[i - 1].NP == 4||con2[i - 1].NP == 1) {
				continue;
			}
			for (int j = 1; j <= 60; j++) {
				for (int j2 = 1; j2 <= runtime; j2++) {
					// System.out.print(bestsolution[j]+" ");
					// System.out.print(result[i][j][j2].result+" ");
					result2[i][j][j2].result = 100 * (result2[i][j][j2].result - bestsolution[j]) / bestsolution[j];
					// System.out.println(result[i][j][j2].result);
					fileRecord(con2[i - 1], result2[i][j][j2].result);
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataPrcoessCalibration dataPrcoessCalibration = new DataPrcoessCalibration();
		dataPrcoessCalibration.run();
	}

}
