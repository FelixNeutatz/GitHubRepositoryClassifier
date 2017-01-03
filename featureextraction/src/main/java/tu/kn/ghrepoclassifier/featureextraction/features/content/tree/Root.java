package tu.kn.ghrepoclassifier.featureextraction.features.content.tree;

import tu.kn.ghrepoclassifier.featureextraction.features.commits.Stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 02.01.17.
 */
public class Root extends Folder {
	
	private Stats fileSizeStats;
	private Stats filesPerDirStats;
	private Stats subfolderPerDirStats;
	private Stats fileLevelStats;
	private String namesString;

	public Root(String path) {
		super(path,0);
		fileSizeStats = new Stats("fileSize", true, true);
		fileLevelStats = new Stats("fileLevelStats", false, false);
		filesPerDirStats = new Stats("filesPerDir", false, true);
		subfolderPerDirStats = new Stats("subfolderPerDir", false, false);
		namesString = "";
	}
	
	public void populate() {
		build(name, fileSizeStats, fileLevelStats, filesPerDirStats, subfolderPerDirStats, namesString);
	}

	@Override
	public String getName() {
		return "";
	}

	public String getDirAndFileNames() {
		return namesString;
	}

	public String summarizeFileTree() {
		String l = "";

		l += fileSizeStats.toString();
		l += "," + fileLevelStats.toString();
		l += "," + filesPerDirStats.toString();
		l += "," + subfolderPerDirStats.toString();

		return l;
	}

	public static List<String> getFeatureLabels() {
		List<String> features = new ArrayList();
		features.addAll(new Stats("fileSizeStats").getFeatureLabels());
		features.addAll(new Stats("fileLevelStats").getFeatureLabels());
		features.addAll(new Stats("filesPerDirStats").getFeatureLabels());
		features.addAll(new Stats("subfolderPerDirStats").getFeatureLabels());

		return features;
	}
}
