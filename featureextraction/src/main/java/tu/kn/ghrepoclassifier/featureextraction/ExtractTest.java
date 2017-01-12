package tu.kn.ghrepoclassifier.featureextraction;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.featureextraction.features.FeatureExtractionUnbiased;
import tu.kn.ghrepoclassifier.featureextraction.features.FeatureExtractor;
import tu.kn.ghrepoclassifier.featureextraction.features.LabelExtractor;
import tu.kn.ghrepoclassifier.featureextraction.features.text.ExtractNames;
import tu.kn.ghrepoclassifier.featureextraction.features.text.ExtractText;
import tu.kn.ghrepoclassifier.serialization.Serializer;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static tu.kn.ghrepoclassifier.featureextraction.Extractor.*;

/**
 * Created by felix on 04.12.16.
 */
public class ExtractTest {
	
	public static void cleanDir(String dir) {
		File dirF = new File(dir);
		//clean
		try {
			FileUtils.deleteDirectory(dirF);
		} catch (Exception e) {}
		//create
		dirF.mkdirs();
	}

	public static void extractMetaFeatures(String inputDir) throws IOException {
		String outputDir = "/tmp/GitHubClassifier/metadataFeatures";
		cleanDir(outputDir);

		FeatureExtractor featureExt = FeatureExtractionUnbiased::extractFeatures;
		LabelExtractor labelExt = FeatureExtractionUnbiased::getFeatureLabels;

		extractFromDir(featureExt, labelExt, inputDir, outputDir, true);
	}

	public static void extractTextFeatures(String inputDir) throws IOException {
		String outputDir = "/tmp/GitHubClassifier/textFeatures";
		cleanDir(outputDir);

		FeatureExtractor featureExt = ExtractText::extractFeatures;
		LabelExtractor labelExt = ExtractText::getFeatureLabels;

		extractFromDir(featureExt, labelExt, inputDir, outputDir, true);
	}

	public static void extractNamesFeatures(String inputDir) throws IOException {
		String outputDir = "/tmp/GitHubClassifier/nameFeatures";
		cleanDir(outputDir);

		FeatureExtractor featureExt = ExtractNames::extractFeatures;
		LabelExtractor labelExt = ExtractNames::getFeatureLabels;

		extractFromDir(featureExt, labelExt, inputDir, outputDir, true);
	}

	public static void main(String[] args) throws IOException {
		String inputDir = "/tmp/GitHubClassifier/APIdownlaods";
		extractMetaFeatures(inputDir);
		extractTextFeatures(inputDir);
		extractNamesFeatures(inputDir);
	}
}
