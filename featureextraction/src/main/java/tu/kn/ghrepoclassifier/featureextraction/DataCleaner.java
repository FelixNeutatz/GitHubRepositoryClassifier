package tu.kn.ghrepoclassifier.featureextraction;

import au.com.bytecode.opencsv.CSVReader;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by felix on 12.12.16.
 */
public class DataCleaner {
	private Set<String> testRepos;
	private boolean isTest;
	
	
	public DataCleaner(boolean isTest) {
		this.isTest = isTest;
		testRepos = new HashSet<String>();

		if (!isTest) {
			populateTestData(Config.get("attachmentA.repos.file"));
			populateTestData(Config.get("attachmentB.repos.file"));
		}
	}
	
	public void populateTestData(String testCsvFile) {
		try {
			CSVReader reader = new CSVReader(new FileReader(testCsvFile), ' ');
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				String repoUrl = nextLine[0];
				testRepos.add(repoUrl);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isValid(RepoData repo) {
		
		if ((repo.getCommits().size() == 0)
			&& !repo.getOurClassification().equals("OTHER")) {
			System.err.println("Repo is probably not valid: " + repo.getFull_name() + "\n" + "cause: number commits = 0");
			return false;
		}
		
		if (repo.getName().contains(".github.io") && !repo.getOurClassification().equals("WEB")) {
			System.err.println("Repo is probably not valid: " + repo.getFull_name() + "\n" + "cause: web error");
			return false;
		}

		
		if ((repo.getName().toLowerCase().contains("documentation"))
		    
			 && !repo.getOurClassification().equals("DOCS")) {
			System.err.println("Repo is probably not valid: " + repo.getFull_name() + "\n" + "cause: docs");
			return false;
		}
		
		//check whether it is contained within the test data
		//if so skip it
		if (!isTest && testRepos.contains(repo.getHtmlUrl().toString())) {
			System.err.println("Repo was found in the test data and will be skipped: " + repo.getFull_name());
			return false;
		}
		
		//text extraction problems
		if (repo.getFull_name().equals("yui/yuidoc")) {
			return false;
		}
		
		return true;
	}
}
