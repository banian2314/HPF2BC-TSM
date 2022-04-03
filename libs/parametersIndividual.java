package libs;

public class parametersIndividual {
	//IWO
	public int NP; // 总群数
	public int SN; // 
	public int d; // 
	public double tvalue; // 方差最小值
	
	public void produceInstance(String group[]) {
		for (int i = 1; i <= 25; i++) {
			group[i] = "ta" + String.valueOf(i);
		}
	}
}
