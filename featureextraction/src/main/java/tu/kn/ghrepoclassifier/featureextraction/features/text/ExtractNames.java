package tu.kn.ghrepoclassifier.featureextraction.features.text;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.markdown4j.Markdown4jProcessor;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.featureextraction.features.content.ExtractContent;
import tu.kn.ghrepoclassifier.featureextraction.features.content.tree.Root;
import tu.kn.ghrepoclassifier.serialization.data.CommitData;
import tu.kn.ghrepoclassifier.serialization.data.ContentData;
import tu.kn.ghrepoclassifier.serialization.data.IssueData;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 06.12.16.
 */
public class ExtractNames {

	public static String extractFeatures(RepoData repo) throws Exception {
		String l = "";

		String outputFolder = ExtractContent.unZip(repo);

		Root tree = new Root(outputFolder);
		tree.populate();

		l += tree.getDirAndFileNames();
		
		FileUtils.deleteDirectory(new File(outputFolder));

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
