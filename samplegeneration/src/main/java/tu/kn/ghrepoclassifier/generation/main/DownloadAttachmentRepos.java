package tu.kn.ghrepoclassifier.generation.main;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Iterators;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.generation.Parallelizer;
import tu.kn.ghrepoclassifier.generation.Repo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.nio.file.Files.readAllLines;


/**
 * Created by felix on 24.11.16.
 */
public class DownloadAttachmentRepos {
	
	public static void downloadReposFromCsv(String file, Parallelizer p) throws IOException {
		List<Repo> repoList = new ArrayList<>();

		CSVReader reader = new CSVReader(new FileReader(file), ' ');
		String [] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Repo r = new Repo(nextLine[0],nextLine[1]);
			repoList.add(r);
		}

		p.startThreads(repoList);
	}
	
	public static void main(String[] args) throws IOException {

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

		//Download repos of attachment A
		String attachmentAFile = Config.get("attachmentA.repos.file");
		File outputDirA = new File(Config.get("attachmentA.download.output.path"));

		Parallelizer pA = new Parallelizer(numberAccounts, propertyFiles, numberAccounts, outputDirA);
		downloadReposFromCsv(attachmentAFile, pA);

		//Download repos of attachment B
		String attachmentBFile = Config.get("attachmentB.repos.file");
		File outputDirB = new File(Config.get("attachmentB.download.output.path"));

		Parallelizer pB = new Parallelizer(pA.getRunningThreads(), numberAccounts, propertyFiles, numberAccounts, outputDirB);
		downloadReposFromCsv(attachmentBFile, pB);
	}
}
