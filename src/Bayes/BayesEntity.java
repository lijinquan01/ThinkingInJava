package Bayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 贝叶斯主体类
 * <p>
 *
 * @author ljq 2018/1/29 0029
 **/
public class BayesEntity
{
	double laplace = 1.00;

	private static String error = "无法判断";

	/**
	 * 将原训练元组按类别划分
	 *
	 * @param datas 训练元组
	 * @return Map<类别，属于该类别的训练元组>
	 */
	Map<String, ArrayList<ArrayList<String>>> datasOfClass(ArrayList<ArrayList<String>> datas)
	{
		Map<String, ArrayList<ArrayList<String>>> map = new HashMap<>();
		ArrayList<String> t = null;
		String c = "";
		for (int i = 0; i < datas.size(); i++)
		{
			t = datas.get(i);
			c = t.get(t.size() - 1);
			if (map.containsKey(c))
			{
				map.get(c).add(t);
			} else
			{
				ArrayList<ArrayList<String>> nt = new ArrayList<>();
				nt.add(t);
				map.put(c, nt);
			}
		}
		return map;
	}

	/**
	 * 在训练数据的基础上预测测试元组的类别
	 *
	 * @param datas 训练元组
	 * @param testT 测试元组
	 * @return 测试元组的类别
	 */
	public String predictClass(ArrayList<ArrayList<String>> datas, ArrayList<String> testT)
	{
		Map<String, ArrayList<ArrayList<String>>> doc = this.datasOfClass(datas);
		Object classes[] = doc.keySet().toArray();
		double maxP = 0.00;
		int maxPIndex = -1;
		for (int i = 0; i < doc.size(); i++)
		{
			String c = classes[i].toString();
			ArrayList<ArrayList<String>> d = doc.get(c);
			//平滑处理
			double a = d.size() + laplace;
			double b = datas.size() + doc.size();
			double pOfC = DecimalCalculate.div(a, b, 5);
			//double pOfC = DecimalCalculate.div(d.size(), datas.size(), 5);
			for (int j = 0; j < testT.size(); j++)
			{
				double pv = this.pOfV(d, testT.get(j), j, doc.size());
				//, doc.size()
				pOfC = DecimalCalculate.mul(pOfC, pv);
			}
			if (pOfC > maxP)
			{
				maxP = pOfC;
				maxPIndex = i;
			}
		}
		if(maxPIndex == -1){
			return error;
		}else{
			return classes[maxPIndex].toString();
		}
	}

	/**
	 * 计算指定属性列上指定值出现的概率
	 *
	 * @param d     属于某一类的训练元组
	 * @param value 列值
	 * @param index 属性列索引
	 * @return 概率
	 */
	private double pOfV(ArrayList<ArrayList<String>> d, String value, int index, int dimension)
	{
		double p = 0.00;
		//count应当是平滑值
		double count = laplace;
		//total为y初始值下的的概率加上n倍平滑值
		double total = d.size() + dimension + laplace;
		//不加平滑
		/*int count = 0;
		int total = d.size();*/
		ArrayList<String> t = null;
		for (int i = 0; i < d.size(); i++)
		{
			if (d.get(i).get(index).equals(value))
			{
				count++;
			}
		}
		p = DecimalCalculate.div(count, total, 5);
		return p;
	}
}
