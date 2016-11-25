package tu.kn.ghrepoclassifier.featureextraction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.kohsuke.github.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by felix on 24.11.16.
 */
public class FeatureExtraction {

	public static String extractFeatures(String url, GitHub github) throws IOException {
		String [] splits = url.split("/");
		System.out.println(Arrays.toString(splits));
		String user = splits[splits.length - 2];
		String reponame = splits[splits.length - 1];
		
		GHRepository repo = github.getRepository(user + "/" + reponame);
		System.out.println(repo);
		
		int numberBranches = repo.getBranches().size(); 					//number of branches
		int numberForks = repo.getForks(); 									//number of forks
		int numberOpenIssues = repo.getOpenIssueCount();					//number of open issues
		int totalNumberIssues = repo.getIssues(GHIssueState.ALL).size();	//number of total issues
		int repoSize = repo.getSize();										//size of the repository in bytes
		int numberStars = repo.getStargazersCount();						//number of stars
		int numberSubscribers = repo.getSubscribersCount();					//number of subscribers
		int numberWatchers = repo.getWatchers();							//number of watchers
		int numberReleases = repo.listReleases().asList().size();			//number of releases
		int numberCommits = repo.listCommits().asList().size();				//number of commits
		int numberContributors = repo.listContributors().asList().size();	//number of contributors
		int hasDownloads = repo.hasDownloads() ? 1 : 0;						//was the repo downloaded
		int numberProgrammingLanguages = repo.listLanguages().size();		//number of programming languages
		int hasLicense = repo.getLicense() == null ? 0 : 1;					//repo has a license
		
		long readmeSize;													//size of the readme
		try {
			GHContent readme = repo.getReadme();
			readmeSize = readme.getSize();
		} catch (IOException e){
			readmeSize = 0;
		}
		
		int numberTeams;													//number of owning organizations
		try {
			numberTeams = repo.getTeams().size();
		} catch (IOException e){
			numberTeams = 0;
		}
		
		/*
		//forbidden
		int numberCollaborators = repo.getCollaborators().size();			//number of collaborators
		*/
		
		
		return numberBranches					//number of branches
			+ "," + numberForks 				//number of branches
			+ "," + numberOpenIssues			//number of open issues
			+ "," + totalNumberIssues			//number of total issues
			+ "," + repoSize					//size of the repository in bytes
			+ "," + numberStars					//number of stars
			+ "," + numberSubscribers			//number of subscribers
			+ "," + numberWatchers				//number of watchers
			+ "," + numberReleases				//number of releases
			+ "," + numberCommits				//number of commits
			+ "," + numberContributors			//number of contributors 
			+ "," + hasDownloads				//was the repo downloaded
			+ "," + numberProgrammingLanguages	//number of programming languages
			+ "," + hasLicense					//repo has a license
			+ "," + readmeSize					//size of the readme
			+ "," + numberTeams 				//number of owning organizations
			;
		


	}

	public static void main(String[] args) throws IOException {

		GitHub github = GitHub.connectUsingOAuth("780afd229d60f12f1ce6aad7adc8712690d20268");
		
		System.out.println(github.getMyself());

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File file = new File(classLoader.getResource("data/Beispieleingabe1.csv").getFile());

		Reader in = new FileReader(file);
		Iterable<CSVRecord> records = CSVFormat.newFormat(' ').parse(in);
		for (CSVRecord record : records) {
			String url = record.get(0);

			System.out.println(url);

			String features = extractFeatures(url, github);

			System.out.println(features);
			break;
		}
	}
}
