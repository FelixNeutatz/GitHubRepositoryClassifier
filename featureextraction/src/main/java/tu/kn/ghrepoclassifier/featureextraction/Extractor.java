package tu.kn.ghrepoclassifier.featureextraction;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.featureextraction.features.FeatureExtractionUnbiased;
import tu.kn.ghrepoclassifier.featureextraction.features.FeatureExtractor;
import tu.kn.ghrepoclassifier.featureextraction.features.LabelExtractor;
import tu.kn.ghrepoclassifier.featureextraction.features.text.ExtractFileExtensions;
import tu.kn.ghrepoclassifier.featureextraction.features.text.ExtractText;
import tu.kn.ghrepoclassifier.featureextraction.features.text.ExtractNames;
import tu.kn.ghrepoclassifier.serialization.Serializer;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * Created by felix on 04.12.16.
 */
public class Extractor {

	public static String[] getSubDirectories(String dir) {
		return new File(dir).list((current, name) -> new File(current, name).isDirectory());
	}

	public static File[] getBinFiles(String categoryDir) {
		return new File(categoryDir).listFiles(file -> file.getName().endsWith(".bin"));
	}

	public static void extract(FeatureExtractor featureExt, String inputDir, String outputDir, boolean isTest)
			throws IOException {
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
						// entries[1] = ExtractText.extractFeatures(repo);
						// entries[1] = FeatureExtractionUnbiased.extractFeatures(repo);
						try {
							entries[1] = featureExt.extractFeatures(repo);
							entries[0] = repo.getHtmlUrl().toString();

							//System.out.println(entries[0] + ", " + entries[1]);
							writer.writeNext(entries);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				writer.flush();
				writer.close();
			} catch (Exception e) {
				System.err.println("Error extracting features for category " + category);
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

	public static void extractFromDir(FeatureExtractor featureExt, LabelExtractor labelExt, String inputDir,
									  String outputDirS, boolean isTest) throws IOException {
		File outputDir = new File(outputDirS);

		if (Files.exists(outputDir.toPath()))
			FileUtils.cleanDirectory(outputDir);
		else
			Files.createDirectories(outputDir.toPath());


		extract(featureExt, inputDir, outputDir.getAbsolutePath(), isTest);

		createSchema(outputDir, labelExt.getFeatureLabels());
	}

	public static void extractMetaFeatures() throws IOException {
		String inputDir = Config.get("sample.generation.output.path");
		String outputDir = Config.get("feature.extraction.output.path");

		String inputDirAttachmentA = Config.get("attachmentA.download.output.path");
		String outputDirAttachmentA = Config.get("attachmentA.feature.extraction.output.path");

		String inputDirAttachmentB = Config.get("attachmentB.download.output.path");
		String outputDirAttachmentB = Config.get("attachmentB.feature.extraction.output.path");

		FeatureExtractor featureExt = FeatureExtractionUnbiased::extractFeatures;
		LabelExtractor labelExt = FeatureExtractionUnbiased::getFeatureLabels;

		extractFromDir(featureExt, labelExt, inputDir, outputDir, false);
		extractFromDir(featureExt, labelExt, inputDirAttachmentA, outputDirAttachmentA, true);
		extractFromDir(featureExt, labelExt, inputDirAttachmentB, outputDirAttachmentB, true);
	}

	public static void extractTextFeatures() throws IOException {
		String inputDir = Config.get("sample.generation.output.path");
		String outputDir = Config.get("feature_text.extraction.output.path");

		String inputDirAttachmentA = Config.get("attachmentA.download.output.path");
		String outputDirAttachmentA = Config.get("attachmentA.feature_text.extraction.output.path");

		String inputDirAttachmentB = Config.get("attachmentB.download.output.path");
		String outputDirAttachmentB = Config.get("attachmentB.feature_text.extraction.output.path");

		FeatureExtractor featureExt = ExtractText::extractFeatures;
		LabelExtractor labelExt = ExtractText::getFeatureLabels;

		extractFromDir(featureExt, labelExt, inputDir, outputDir, false);
		extractFromDir(featureExt, labelExt, inputDirAttachmentA, outputDirAttachmentA, true);
		extractFromDir(featureExt, labelExt, inputDirAttachmentB, outputDirAttachmentB, true);
	}

	public static void extractNamesFeatures() throws IOException {
		String inputDir = Config.get("sample.generation.output.path");
		String outputDir = Config.get("feature_text_names.extraction.output.path");

		String inputDirAttachmentA = Config.get("attachmentA.download.output.path");
		String outputDirAttachmentA = Config.get("attachmentA.feature_text_names.extraction.output.path");

		String inputDirAttachmentB = Config.get("attachmentB.download.output.path");
		String outputDirAttachmentB = Config.get("attachmentB.feature_text_names.extraction.output.path");

		FeatureExtractor featureExt = ExtractNames::extractFeatures;
		LabelExtractor labelExt = ExtractNames::getFeatureLabels;

		extractFromDir(featureExt, labelExt, inputDir, outputDir, false);
		extractFromDir(featureExt, labelExt, inputDirAttachmentA, outputDirAttachmentA, true);
		extractFromDir(featureExt, labelExt, inputDirAttachmentB, outputDirAttachmentB, true);
	}

	public static void extractFileExtensionsFeatures() throws IOException {
		String inputDir = Config.get("sample.generation.output.path");
		String outputDir = Config.get("feature_file_extensions.extraction.output.path");

		String inputDirAttachmentA = Config.get("attachmentA.download.output.path");
		String outputDirAttachmentA = Config.get("attachmentA.feature_file_extensions.extraction.output.path");

		String inputDirAttachmentB = Config.get("attachmentB.download.output.path");
		String outputDirAttachmentB = Config.get("attachmentB.feature_file_extensions.extraction.output.path");

		FeatureExtractor featureExt = ExtractFileExtensions::extractFeatures;
		LabelExtractor labelExt = ExtractFileExtensions::getFeatureLabels;

		extractFromDir(featureExt, labelExt, inputDir, outputDir, false);
		extractFromDir(featureExt, labelExt, inputDirAttachmentA, outputDirAttachmentA, true);
		extractFromDir(featureExt, labelExt, inputDirAttachmentB, outputDirAttachmentB, true);
	}

	public static void main(String[] args) throws IOException {
		extractMetaFeatures();
		extractTextFeatures();
		extractNamesFeatures();
		extractFileExtensionsFeatures();
	}
}
