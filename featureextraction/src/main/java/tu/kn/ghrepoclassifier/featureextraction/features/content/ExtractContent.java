package tu.kn.ghrepoclassifier.featureextraction.features.content;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.featureextraction.features.content.tree.Root;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by felix on 06.12.16.
 */
public class ExtractContent {

	public static String unZip(RepoData repo) throws Exception {
		String zipFolder = Config.get("sample.generation.content.output.path");
		String zipFile = zipFolder + "/" + "content" + repo.getId() + ".zip";
		String outputFolder = "/tmp/GithubClassifier/content" + repo.getId();
		
		File f = new File(zipFile);
		if (f.exists()) {
			ZipFile zip = new ZipFile(zipFile);
			zip.extractAll(outputFolder);
			
		} else {
			throw new Exception("Repo was not downloaded:" + repo.getFull_name());
		}
		
		return outputFolder;
	}
	
	public static String extractFeatures(RepoData repo) throws Exception{
		String l = "";

		Root tree = null;
		try {
			String outputFolder = unZip(repo);
			tree = new Root(outputFolder);
			tree.populate();

			FileUtils.deleteDirectory(new File(outputFolder));
		} catch (Exception e) {
			tree = new Root("");
		}
		l += tree.summarizeFileTree();
		
		
		return l;
	}

	public static List<String> getFeatureLabels() {
		List<String> features = new ArrayList();
		features.addAll(Root.getFeatureLabels());
		return features;
	}
}
