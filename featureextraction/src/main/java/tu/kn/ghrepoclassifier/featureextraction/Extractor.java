package tu.kn.ghrepoclassifier.featureextraction;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.featureextraction.features.FeatureExtractionUnbiased;
import tu.kn.ghrepoclassifier.serialization.Serializer;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static tu.kn.ghrepoclassifier.featureextraction.features.FeatureExtractionUnbiased.extractFeatures;

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
		
		DataCleaner cleaner = new DataCleaner();

		CSVWriter writer = null;
		
		for (String category: categories) {
			File[] binFiles = getBinFiles(inputDir + "/" + category);

			try {
				writer = new CSVWriter(new FileWriter(
					new File(outputDir + "/" + "data" + category + ".csv"),false), 
					',', CSVWriter.NO_QUOTE_CHARACTER);

				String[] entries = {"","", category};
			
				for (File f: binFiles) {
					RepoData repo = Serializer.readFromFile(f);
					
					if (cleaner.isValid(repo)) {
						entries[1] = extractFeatures(repo);
						entries[0] = repo.getHtmlUrl().toString();

						System.out.println(entries[0] + ", " + entries[1]);
						writer.writeNext(entries);
					}
				}
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createSchema(File outputDir, List<String> labelList) {
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(
				new File(outputDir + "/" + "schema" + ".csv"),false),
				',', CSVWriter.NO_QUOTE_CHARACTER);

			for(String labels: labelList) {
				writer.writeNext(new String[]{labels});
			}
			
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		String inputDir = Config.get("sample.generation.output.path");
		File outputDir = new File(Config.get("feature.extraction.output.path"));

		FileUtils.cleanDirectory(outputDir);
		
		extract(inputDir, outputDir.getAbsolutePath());
		
		List<String> labels = FeatureExtractionUnbiased.getFeatureLabels();
		createSchema(outputDir, labels);
	}
}
