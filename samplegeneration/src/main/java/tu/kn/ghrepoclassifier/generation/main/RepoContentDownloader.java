package tu.kn.ghrepoclassifier.generation.main;

import org.apache.commons.io.FileUtils;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.serialization.Serializer;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

import static tu.kn.ghrepoclassifier.generation.main.ContentDownloader.downloadFromDir;

/**
 * Created by felix on 04.12.16.
 */
public class RepoContentDownloader {

	public static void main(String[] args) throws IOException {
		String inputDir = "/tmp/GitHubClassifier/APIdownlaods";
		String outputDir = Config.get("sample.generation.content.output.path");
		
		File outputFolder = new File(outputDir);
		//create
		outputFolder.mkdirs();
		
		downloadFromDir(inputDir, outputDir);
	}
}
