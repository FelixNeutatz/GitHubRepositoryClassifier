package tu.kn.ghrepoclassifier.featureextraction;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.featureextraction.features.text.ExtractText;
import tu.kn.ghrepoclassifier.serialization.Serializer;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static tu.kn.ghrepoclassifier.featureextraction.features.text.ExtractText.extractFeatures;

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
	
	public static void extract(String inputDir, String outputDir, boolean isTest) throws IOException {
		String[] categories = getSubDirectories(inputDir);
		System.out.println(Arrays.toString(categories));
		
		DataCleaner cleaner = new DataCleaner(isTest);

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
						System.out.println(repo.getFull_name());
						entries[1] = extractFeatures(repo);
						entries[0] = repo.getHtmlUrl().toString();

						//System.out.println(entries[0] + ", " + entries[1]);
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
	
	public static void extractFromDir(String inputDir, String outputDirS, boolean isTest) throws IOException {
		File outputDir = new File(outputDirS);

		FileUtils.cleanDirectory(outputDir);

		extract(inputDir, outputDir.getAbsolutePath(), isTest);

		List<String> labels = ExtractText.getFeatureLabels();
		createSchema(outputDir, labels);
	}

	public static void main(String[] args) throws IOException {

		String inputDir = Config.get("sample.generation.output.path");
		String outputDir = Config.get("feature.extraction.output.path");

		extractFromDir(inputDir, outputDir, false);

		String inputDirAttachmentA = Config.get("attachmentA.download.output.path");
		String outputDirAttachmentA = Config.get("attachmentA.feature.extraction.output.path");

		extractFromDir(inputDirAttachmentA, outputDirAttachmentA, true);
		
	}
}
