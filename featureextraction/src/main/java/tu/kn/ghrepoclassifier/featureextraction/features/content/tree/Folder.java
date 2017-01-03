package tu.kn.ghrepoclassifier.featureextraction.features.content.tree;

import tu.kn.ghrepoclassifier.featureextraction.features.commits.Stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 02.01.17.
 */
public class Folder extends Node{
	List<Node> children;

	public Folder(String name, int level) {
		super(name, level);
		children = new ArrayList<>();
	}

	public void add(Node node) {
		children.add(node);
	}
	
	public long getSize() {
		long size = 0;
		for (Node child: children) {
			size += child.getSize();
		}
		return size;
	}
	
	public int getNumberFiles() {
		int num = 0;
		for (Node child: children) {
			if (child instanceof RepoFile) {
				num++;
			}
		}
		return num;
	}

	public int getNumberSubFolders() {
		return children.size() - getNumberFiles();
	}
	
	public void build(String dir,
					  Stats fileSizeStats,
					  Stats fileLevelStats,
					  Stats filesPerDirStats,
					  Stats subfolderPerDirStats, 
					  String namesString) {
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();

		int fileNumPerDir = 0;
		int folderNumPerDir = 0;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				fileNumPerDir++;
				long fileSize = listOfFiles[i].length();
				fileSizeStats.add(fileSize);
				fileLevelStats.add(level);
				RepoFile f = new RepoFile(listOfFiles[i].getName(), fileSize, level);
				add(f);
				namesString += f.getNameWithoutExtension() + " ";
			} else if (listOfFiles[i].isDirectory()) {
				folderNumPerDir++;
				Folder f = new Folder(listOfFiles[i].getName(), level + 1);
				f.populate(dir, fileSizeStats, fileLevelStats, filesPerDirStats, subfolderPerDirStats, namesString);
				add(f);
			}
		}
		filesPerDirStats.add(fileNumPerDir);
		subfolderPerDirStats.add(folderNumPerDir);
	}

	public void populate(String path, 
						 Stats fileSizeStats, 
						 Stats fileLevelStats, 
						 Stats filesPerDirStats,
						 Stats subfolderPerDirStats,
						 String namesString) {
		String dir = path + "/" + name;
		build(dir, fileSizeStats, fileLevelStats, filesPerDirStats, subfolderPerDirStats, namesString);
	}
}
