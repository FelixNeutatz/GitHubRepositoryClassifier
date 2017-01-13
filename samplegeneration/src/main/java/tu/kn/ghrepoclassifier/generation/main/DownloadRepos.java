package tu.kn.ghrepoclassifier.generation.main;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Iterators;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.generation.Parallelizer;
import tu.kn.ghrepoclassifier.generation.Repo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.nio.file.Files.readAllLines;


/**
 * Created by felix on 24.11.16.
 */
public class DownloadRepos {
	
	public static void downloadReposFromCsv(String file, Parallelizer p, int columnID) throws IOException {
		List<Repo> repoList = new ArrayList<>();

		CSVReader reader = new CSVReader(new FileReader(file), ' ');
		String [] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Repo r = new Repo(nextLine[columnID], "null");
			repoList.add(r);
		}

		p.startThreads(repoList);
	}
	
	public static void main(String[] args) throws IOException {

		Options options = new Options();
		Option input = OptionBuilder.withArgName( "input" )
			.hasArg(true)
			.withDescription(  "input csv file" )
			.create( "input");
		options.addOption(input);

		Option columnIdArg = OptionBuilder.withArgName( "index" )
			.hasArg(true)
			.withDescription(  "index of the column which contains the repository urls (0 .. n-1)\\ndefault:first(0) " +
				"column" )
			.create( "columnURLindex");
		options.addOption(columnIdArg);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		String inputCsv = "";
		int columnID = 0;
		
		try {
			cmd = parser.parse(options, args);
			
			if (cmd.hasOption("input")) {
				inputCsv = cmd.getOptionValue("input");
				System.out.println("here: " + inputCsv);
			}
			if (cmd.hasOption("column_index")) {
				columnID = Integer.parseInt(cmd.getOptionValue("column_index", "0"));
				System.out.println("here: " + columnID);
			}
			
		} catch (Exception e) {
			e.printStackTrace();

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "DownloadRepos", options );
			return;
		}
		
		System.out.println (Arrays.toString(args));

		Iterator<String> propertyFiles = null;
		int numberAccounts = 0;
		try {
			List<String> accountFileList = readAllLines(new File(
					Config.get("sample.generation.git-accounts.file")).toPath(),
				StandardCharsets.UTF_8);

			numberAccounts = accountFileList.size();
			propertyFiles = Iterators.cycle(accountFileList);
		} catch (Exception e) {
			numberAccounts = 1;
		}

		//Download repos
		File outputDirA = new File("/tmp/GitHubClassifier/APIdownlaods");
		//clean
		try {
			FileUtils.deleteDirectory(outputDirA);
		} catch (Exception e) {}
		//create
		outputDirA.mkdirs();

		Parallelizer pA = new Parallelizer(numberAccounts, propertyFiles, numberAccounts, outputDirA);
		downloadReposFromCsv(inputCsv, pA, columnID);
	}
}
