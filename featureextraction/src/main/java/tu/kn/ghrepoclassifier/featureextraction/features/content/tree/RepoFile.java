package tu.kn.ghrepoclassifier.featureextraction.features.content.tree;

/**
 * Created by felix on 02.01.17.
 */
public class RepoFile extends Node{
	private long fileSize;
	
	public RepoFile(String name, long fileSize, int level) {
		super(name, level);
		this.fileSize = fileSize;
	}

	public long getSize() {
		return fileSize;
	}
	
	public String getNameWithoutExtension() {
		return name.split("\\.")[0];
	}

	public String getFileExtension() {
		String [] splits = name.split("\\.");
		if (splits.length > 1) {
			return splits[splits.length - 1];
		} 
		return "";
	}
}
