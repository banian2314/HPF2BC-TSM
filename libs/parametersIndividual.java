package libs;

public class parametersIndividual {
	//IWO
	public int NP; // ��Ⱥ��
	public int SN; // 
	public int d; // 
	public double tvalue; // ������Сֵ
	
	public void produceInstance(String group[]) {
		for (int i = 1; i <= 25; i++) {
			group[i] = "ta" + String.valueOf(i);
		}
	}
}
