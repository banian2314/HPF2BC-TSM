package libs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class function {

	// weightIndividual (从小到大排序)
	public static Comparator<weightIndividual> recompareWeightIndividualMintoMax() {
		Comparator<weightIndividual> compare = new Comparator<weightIndividual>() { // 初始化分类器
			@Override
			public int compare(weightIndividual o1, weightIndividual o2) {
				if (o1.weight < o2.weight) {
					return -1;
				} else if (o1.weight > o2.weight) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		return compare;
	}

	// weightIndividual (从小到大排序)
	public static Comparator<weightIndividual> recompareWeightIndividualMaxtoMin() {
		Comparator<weightIndividual> compare = new Comparator<weightIndividual>() { // 初始化分类器
			@Override
			public int compare(weightIndividual o1, weightIndividual o2) {
				if (o1.weight > o2.weight) {
					return -1;
				} else if (o1.weight < o2.weight) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		return compare;
	}

	// individual (从小到大排序)
	public static Comparator<individual> recompareIndividualMaxtoMin() {
		Comparator<individual> compare = new Comparator<individual>() { // 初始化分类器
			@Override
			public int compare(individual o1, individual o2) {
				if (o1.fit > o2.fit) {
					return -1;
				} else if (o1.fit < o2.fit) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		return compare;
	}

	// individual (从小到大排序)
	public static Comparator<individual> recompareIndividualMintoMax() {
		Comparator<individual> compare = new Comparator<individual>() { // 初始化分类器
			@Override
			public int compare(individual o1, individual o2) {
				if (o1.fit < o2.fit) {
					return -1;
				} else if (o1.fit > o2.fit) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		return compare;
	}

	// 随机打乱当前排列
	public static void shuffle(int jobs, int arr[]) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i <= jobs; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		Iterator<Integer> ite = list.iterator();
		int i = 1;
		while (ite.hasNext()) {
			arr[i] = ite.next();
			i++;
		}
	}

	public static void shuffleArray(int arr[], int num) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i <= num; i++) {
			list.add(arr[i]);
		}
		Collections.shuffle(list);
		Iterator<Integer> ite = list.iterator();
		int i = 1;
		while (ite.hasNext()) {
			arr[i] = ite.next();
			i++;
		}
	}

	public static void copyIndividual(individual pop1, individual pop2) {
		for (int i = 0; i < pop1.vector.size(); i++) {
			pop2.vector.get(i).clear();
			Integer job = new Integer(0);
			pop2.vector.get(i).add(job);

			for (int j = 1; j < pop1.vector.get(i).size(); j++) {
				Integer temp = new Integer(pop1.vector.get(i).get(j).intValue());
				pop2.vector.get(i).add(j, temp);
			}
			for (int j = 1; j < pop1.leaveTime[i].length; j++) {
				for (int j2 = 1; j2 < pop1.leaveTime[i][j].length; j2++) {
					pop2.leaveTime[i][j][j2] = pop1.leaveTime[i][j][j2];
				}
			}
			for (int j = 1; j < pop1.leaveTime[i].length; j++) {
				for (int j2 = 1; j2 < pop1.leaveTime[i][j].length; j2++) {
					pop2.tailTime[i][j][j2] = pop1.tailTime[i][j][j2];
				}
			}
		}
		pop2.fit = pop1.fit;
		pop2.makespanFactoryNum = pop1.makespanFactoryNum;
	}

	// 复制某一个车间
	public static void copyFactory(individual pop1, individual pop2, int i) {
		pop2.vector.get(i).clear();
		Integer job = new Integer(0);
		pop2.vector.get(i).add(job);

		for (int j = 1; j < pop1.vector.get(i).size(); j++) {
			Integer temp = new Integer(pop1.vector.get(i).get(j).intValue());
			pop2.vector.get(i).add(j, temp);
		}
		for (int j = 1; j < pop1.leaveTime[i].length; j++) {
			for (int j2 = 1; j2 < pop1.leaveTime[i][j].length; j2++) {
				pop2.leaveTime[i][j][j2] = pop1.leaveTime[i][j][j2];
			}
		}
		for (int j = 1; j < pop1.leaveTime[i].length; j++) {
			for (int j2 = 1; j2 < pop1.leaveTime[i][j].length; j2++) {
				pop2.tailTime[i][j][j2] = pop1.tailTime[i][j][j2];
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
