package libs;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Initialize {
	static public int job; // 工件数
	static public int machines; // 机器数
	static public double T[][]; // 加工时间
//	public static double dueDate[]; // 到期时间
//	public static weightIndividual ajob[]; // 工件信息
	public static double totalProccesingTime[]; // 每个工件的总加工时间
	public int m;
	public int n;

	public void readConfig(String name) {
		String filename = "data/TA/" + name + ".txt";
		File file = new File(filename);
		T = new double[1000][1000];
		totalProccesingTime = new double[1000];
		int k = 1;
		double tempProccesingTime;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				String[] strarray = s.split(",");
				machines = strarray.length;
				tempProccesingTime = 0;
				for (int i = 0; i < strarray.length; i++) {
					T[k][i + 1] = Double.parseDouble(strarray[i]);
					tempProccesingTime = tempProccesingTime + T[k][i + 1];
				}
				 totalProccesingTime[k] = tempProccesingTime;
				k++;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		job = k - 1;
		this.m = machines;
		this.n = job;
	}
	
	
	public void readConfigCalibration(String name) {
	
		String filename = "data/TAIIWO/" + name + ".txt";
		File file = new File(filename);
		T = new double[1000][1000];
		totalProccesingTime = new double[1000];
		int k = 1;
		double tempProccesingTime;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				String[] strarray = s.split(",");
				machines = strarray.length;
				tempProccesingTime = 0;
				for (int i = 0; i < strarray.length; i++) {
					T[k][i + 1] = Double.parseDouble(strarray[i]);
					tempProccesingTime = tempProccesingTime + T[k][i + 1];
				}
				totalProccesingTime[k] = tempProccesingTime;
				k++;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		job = k - 1;
		this.m = machines;
		this.n = job;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initialize params = new Initialize();
		params.readConfig("ta1");
		
	}

}
