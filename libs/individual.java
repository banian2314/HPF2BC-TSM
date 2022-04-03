package libs;

import java.util.ArrayList;

public class individual {
	public double fit; // 最大完成时间
	public ArrayList<ArrayList<Integer>> vector; // 调度解
	public double leaveTime[][][]; // 离开时间
	public double tailTime[][][];
	public int makespanFactoryNum;  //最大完成时间的车间
	public double KE;  //势能 （CRO属性）
	public int Numhit;   //CRO属性
    //public int num[];        //每个个体上的工件数
    
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
