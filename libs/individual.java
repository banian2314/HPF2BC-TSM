package libs;

import java.util.ArrayList;

public class individual {
	public double fit; // ������ʱ��
	public ArrayList<ArrayList<Integer>> vector; // ���Ƚ�
	public double leaveTime[][][]; // �뿪ʱ��
	public double tailTime[][][];
	public int makespanFactoryNum;  //������ʱ��ĳ���
	public double KE;  //���� ��CRO���ԣ�
	public int Numhit;   //CRO����
    //public int num[];        //ÿ�������ϵĹ�����
    
	public individual(int n, int m, int f) {
		// TODO Auto-generated constructor stub
		vector = new ArrayList<ArrayList<Integer>>();
		for (int i = 1; i <= f; i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			Integer job = new Integer(0);
			temp.add(job);
			vector.add(temp);
		}
		leaveTime = new double[f][m + 1][n + 1];
		tailTime = new double[f][m + 1][n + 1];
//		num = new int[f+1];
	}
	
}
