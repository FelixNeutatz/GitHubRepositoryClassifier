package tu.kn.ghrepoclassifier.generation.main;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Iterators;
import org.kohsuke.github.*;
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
	
	public static void main(String[] args) throws IOException {
		List<String> accountFileList = readAllLines(new File(
			Config.get("sample.generation.git-accounts.file")).toPath(),
			StandardCharsets.UTF_8);

		int numberAccounts = accountFileList.size();
		Iterator<String> propertyFiles = Iterators.cycle(accountFileList);

		GitHub github = null;
		synchronized (propertyFiles) {
			github = GitHubBuilder.fromPropertyFile(propertyFiles.next()).build();
		}
		System.out.println(github.getMyself());
 
		GHRateLimit limit = github.getRateLimit();
		System.out.println("remaining requests: " + limit.remaining);
		System.out.println("limit: " + limit.limit);
		System.out.println("reset date: " + limit.reset);

		//Download repos of attachment A
		File outputDirA = new File(Config.get("attachmentA.download.output.path"));

		Parallelizer pA = new Parallelizer(numberAccounts, propertyFiles, numberAccounts, outputDirA);

		List<Repo> repoListA = new ArrayList<>();
		
		String attachmentAFile = Config.get("attachmentA.repos.file");
		CSVReader reader = new CSVReader(new FileReader(attachmentAFile), ' ');
		String [] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Repo r = new Repo(nextLine[0],nextLine[1]);
			repoListA.add(r);
		}
		
		pA.startThreads(repoListA);

		//Download repos of attachment B
		File outputDirB = new File(Config.get("attachmentB.download.output.path"));

		Parallelizer pB = new Parallelizer(pA.getRunningThreads(), numberAccounts, propertyFiles, numberAccounts, outputDirB);

		List<Repo> repoListB = new ArrayList<>();

		String attachmentBFile = Config.get("attachmentB.repos.file");
		reader = new CSVReader(new FileReader(attachmentBFile), ' ');
		while ((nextLine = reader.readNext()) != null) {
			Repo r = new Repo(nextLine[0],nextLine[1]);
			repoListB.add(r);
		}

		pB.startThreads(repoListB);
	}
}
