package libs;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Initialize {
	static public int job; // ������
	static public int machines; // ������
	static public double T[][]; // �ӹ�ʱ��
//	public static double dueDate[]; // ����ʱ��
//	public static weightIndividual ajob[]; // ������Ϣ
	public static double totalProccesingTime[]; // ÿ���������ܼӹ�ʱ��
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
			BufferedReader br = new BufferedReader(new FileReader(file));// ����һ��BufferedReader������ȡ�ļ�
			String s = null;
			while ((s = br.readLine()) != null) {// ʹ��readLine������һ�ζ�һ��
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
			BufferedReader br = new BufferedReader(new FileReader(file));// ����һ��BufferedReader������ȡ�ļ�
			String s = null;
			while ((s = br.readLine()) != null) {// ʹ��readLine������һ�ζ�һ��
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
