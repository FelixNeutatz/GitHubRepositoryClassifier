package tu.kn.ghrepoclassifier.featureextraction.features.text;

import org.apache.commons.io.FileUtils;
import tu.kn.ghrepoclassifier.featureextraction.features.content.ExtractContent;
import tu.kn.ghrepoclassifier.featureextraction.features.content.tree.Root;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 06.12.16.
 */
public class ExtractFileExtensions {

	public static String extractFeatures(RepoData repo) throws Exception {
		String l = "";

		Root tree = null;
		try {
			String outputFolder = ExtractContent.unZip(repo);

			String[] directories = new File(outputFolder).list(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});

			tree = new Root(outputFolder + "/" + directories[0]);
			tree.populate();

			FileUtils.deleteDirectory(new File(outputFolder));
		} catch (Exception e){
			tree = new Root("");
		}
		l += tree.getDirAndFileNames();

		l = l.replace("\'", "");
		l = l.replace("\n", " ");
		l = l.replace("\r", " ");
		l = l.replace("\0", " ");
		
		l = l.toLowerCase();
		
		return '\'' + l + '\'';
	}

	public static List<String> getFeatureLabels() {
		List<String> features = new ArrayList();
		features.add("text");
		return features;
	}
}
