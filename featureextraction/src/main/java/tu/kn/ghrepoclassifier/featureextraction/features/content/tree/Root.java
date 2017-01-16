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
	private StringBuilder namesString;
	private StringBuilder fileExtensionsString;

	public Root(String path) {
		super(path,0);
		fileSizeStats = new Stats("fileSize", true, true);
		fileLevelStats = new Stats("fileLevel", false, false);
		filesPerDirStats = new Stats("filesPerDir", false, true);
		subfolderPerDirStats = new Stats("subfolderPerDir", false, false);
		namesString = new StringBuilder();
		fileExtensionsString = new StringBuilder();
	}
	
	public void populate() {
		build(name,
				fileSizeStats,
				fileLevelStats,
				filesPerDirStats,
				subfolderPerDirStats,
				namesString,
				fileExtensionsString,
				true);
	}

	@Override
	public String getName() {
		return "";
	}

	public String getDirAndFileNames() {
		return namesString.toString();
	}

	public String getFileExtensions() {
		return fileExtensionsString.toString();
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
		features.addAll(new Stats("fileSize", true, true).getFeatureLabels());
		features.addAll(new Stats("fileLevel", false, false).getFeatureLabels());
		features.addAll(new Stats("filesPerDir", false, true).getFeatureLabels());
		features.addAll(new Stats("subfolderPerDir", false, false).getFeatureLabels());

		return features;
	}
}
