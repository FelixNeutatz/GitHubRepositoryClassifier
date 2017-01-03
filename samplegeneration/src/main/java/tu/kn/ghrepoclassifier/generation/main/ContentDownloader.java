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

/**
 * Created by felix on 04.12.16.
 */
public class ContentDownloader {

	public static String[] getSubDirectories(String dir) {
		return new File(dir).list((current, name) -> new File(current, name).isDirectory());
	}

	public static File[] getBinFiles(String categoryDir) {
		return new File(categoryDir).listFiles(file -> file.getName().endsWith(".bin"));
	}

	public static void download(String inputDir, String outputDir)
			throws IOException {
		String[] categories = getSubDirectories(inputDir);
		System.out.println(Arrays.toString(categories));

		for (String category: categories) {
			File[] binFiles = getBinFiles(inputDir + "/" + category);

			for (File f: binFiles) {
				RepoData repo = Serializer.readFromFile(f);

				String url = repo.getHtmlUrl() + "/archive/" + repo.getDefault_branch() + ".zip";
				System.out.println(url);
				
				File contentFile = new File(outputDir +"/" + "content" + repo.getId() + ".zip");

				if (!contentFile.exists()) {
					try {
						FileUtils.copyURLToFile(new URL(url), contentFile);
					} catch (Exception e) {
						System.err.println("Error downloading repos for category " + category);
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void downloadFromDir(String inputDir, String outputDirS) throws IOException {
		File outputDir = new File(outputDirS);

		if (!Files.exists(outputDir.toPath()))
			Files.createDirectories(outputDir.toPath());

		download(inputDir, outputDir.getAbsolutePath());
	}

	public static void main(String[] args) throws IOException {
		String inputDir = Config.get("sample.generation.output.path");
		String outputDir = Config.get("sample.generation.content.output.path");
		downloadFromDir(inputDir, outputDir);
	}
}
