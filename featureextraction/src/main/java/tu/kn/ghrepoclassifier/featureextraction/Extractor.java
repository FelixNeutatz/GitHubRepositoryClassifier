package tu.kn.ghrepoclassifier.featureextraction;

import au.com.bytecode.opencsv.CSVWriter;
import tu.kn.ghrepoclassifier.serialization.Serializer;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.*;
import java.util.Arrays;

import static tu.kn.ghrepoclassifier.featureextraction.FeatureExtraction2.extractFeatures;

/**
 * Created by felix on 04.12.16.
 */
public class Extractor {
	
	public static String[] getSubDirectories(String dir) {
		File file = new File(dir);
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		return directories;
	}
	
	public static File[] getBinFiles(String categoryDir) {
		File[] binFiles = new File(categoryDir).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".bin");
			}
		});
		return binFiles;
	}
	
	public static void extract(String inputDir, String outputDir) throws IOException {
		String[] categories = getSubDirectories(inputDir);
		System.out.println(Arrays.toString(categories));

		CSVWriter writer = null;
		
		for (String category: categories) {
			File[] binFiles = getBinFiles(inputDir + "/" + category);

			try {
				writer = new CSVWriter(new FileWriter(
					new File(outputDir + "/" + "data" + category + ".csv"),true), 
					',', CSVWriter.NO_QUOTE_CHARACTER);

				String[] entries = {"","", category};
			
				for (File f: binFiles) {
					RepoData repo = Serializer.readFromFile(f);
					
					entries[1] = extractFeatures(repo);
					entries[0] = repo.getHtmlUrl().toString();
	
					System.out.println(entries[0] + ", " + entries[1]);
					writer.writeNext(entries);
				}
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File outputDir = new File("/home/felix/GitHubRepositoryClassifier/GitHubRepositoryClassifier/featureextraction/src/main/resources/data/features");
		
		String inputDir = "/home/felix/GitHubRepositoryClassifier/data";
		
		extract(inputDir, outputDir.getAbsolutePath());
	}
}
