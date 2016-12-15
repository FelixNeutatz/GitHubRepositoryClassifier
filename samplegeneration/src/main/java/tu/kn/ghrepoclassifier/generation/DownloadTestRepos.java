package tu.kn.ghrepoclassifier.generation;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Iterators;
import org.kohsuke.github.*;
import tu.kn.ghrepoclassifier.Config;

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
public class DownloadTestRepos {
	
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
		
		File outputDir = new File(Config.get("attachmentA.download.output.path"));

		List<Repo> repoList = new ArrayList<>();
		
		String attachmentAFile = Config.get("attachmentA.repos.file");
		CSVReader reader = new CSVReader(new FileReader(attachmentAFile), ' ');
		String [] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Repo r = new Repo(nextLine[0],nextLine[1]);
			repoList.add(r);
		}
		
		Parallelizer p = new Parallelizer(numberAccounts, propertyFiles, numberAccounts, outputDir);
	}
}
