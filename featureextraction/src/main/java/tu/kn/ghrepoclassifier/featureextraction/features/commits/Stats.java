package tu.kn.ghrepoclassifier.featureextraction.features.commits;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 03.12.16.
 */
public class Stats {
	private double sum = 0;
	private long max;
	private long min;
	private double squaredSum = 0;
	private long n = 0;
	private String name;
	
	private boolean showSum;
	private boolean showN;
	
	
	public Stats(String name) {
		this.name = name;
		this.showSum = false;
		this.showN = false;
	}

	public Stats(String name, boolean showSum, boolean showN) {
		this.name = name;
		this.showSum = showSum;
		this.showN = showN;
	}
	
	public void add(long value) {
		sum += value;
		squaredSum += Math.pow(value,2);
		
		if (n == 0) {
			max = value;
			min = value;
		} else {
			if (value > max) {
				max = value;
			}
			if (value < min) {
				min = value;
			}
		}
		n++;
	}
	
	public double getAverage() {
		if (n == 0) {
			return 0;
		} else {
			return sum / n;
		}
	}
	
	public double getStdDev() {
		if (n == 0) {
			return 0;
		} else {
			return Math.sqrt((squaredSum / n) - Math.pow(getAverage(), 2));
		}
	}
	
	@Override
	public String toString(){
		String l = "";
		
		if (showSum) {
			l += sum + ",";
		}
		if (showN) {
			l += n + ",";
		}
		
		return l + getAverage() + "," + getStdDev() + "," + min + "," + max;
	}

	public List<String> getFeatureLabels() {
		List<String> features = new ArrayList();
		if (showSum) {
			features.add(name + "Sum");
		}
		if (showN) {
			features.add(name + "N");
		}
		features.add(name + "Average");
		features.add(name + "StdDev");
		features.add(name + "Min");
		features.add(name + "Max");
		return features;
	}
}
