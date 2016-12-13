package tu.kn.ghrepoclassifier.featureextraction.commits;

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
	
	public Stats(String name) {
		this.name = name;
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
		return getAverage() + "," + getStdDev() + "," + min + "," + max;
	}

	public String getFeatureLabels() {
		String l = "";
		l += name + "Average\n";
		l += name + "StdDev\n";
		l += name + "Min\n";
		l += name + "Max\n";
		return l;
	}
}
