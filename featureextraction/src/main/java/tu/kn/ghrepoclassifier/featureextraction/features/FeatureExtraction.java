package tu.kn.ghrepoclassifier.featureextraction.features;


import tu.kn.ghrepoclassifier.featureextraction.features.languages.ExtractLanguages;
import tu.kn.ghrepoclassifier.serialization.data.ContributorData;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static tu.kn.ghrepoclassifier.featureextraction.features.languages.ExtractLanguages.extractProgrammingLanguages;

/**
 * Created by felix on 24.11.16.
 */
public class FeatureExtraction {

	public static String extractFeatures(RepoData repo) {
		int numberBranches = repo.getBranches().size(); 							//number of branches
		int numberForks = repo.getForks(); 											//number of forks
		int numberOpenIssues = repo.getOpenIssueCount();							//number of open issues
		int repoSize = repo.getSize();												//size of the repository in bytes
		int numberStars = repo.getStargazersCount();								//number of stars
		int numberSubscribers = repo.getSubscribersCount();							//number of subscribers
		int numberWatchers = repo.getWatchers();									//number of watchers
		int numberReleases = repo.listReleases().size();							//number of releases
		
		int hasDownloads = repo.hasDownloads() ? 1 : 0;								//was the repo downloaded
		int descriptionLength = 0;													//decription length
		if (repo.getDescription() != null) {
			descriptionLength = repo.getDescription().length();
		}
		
		Map<String, Long> programmingLanguages = repo.listLanguages();				//number of programming languages

		//this introduces sparsity
		String languages = extractProgrammingLanguages(programmingLanguages);
		
		int numberProgrammingLanguages = programmingLanguages.size();
		
		int hasLicense = (repo.getLicense().length() > 0) ? 0 : 1;					//repo has a license
		
		int numberContributors = 0;													//number of contributors
		int numberCommits = 0;														//number of commits

		for(ContributorData c: repo.listContributors()) {
			numberCommits += c.getContributions();
			numberContributors++;
		}

		long readmeSize = repo.getReadme().getSize();								//size of the readme
		
		return 
					numberBranches				//number of branches
			+ "," + numberForks 				//number of branches
			+ "," + numberOpenIssues			//number of open issues
			+ "," + repoSize					//size of the repository in bytes
			+ "," + numberStars					//number of stars
			+ "," + numberSubscribers			//number of subscribers
			+ "," + numberWatchers				//number of watchers
			+ "," + numberReleases				//number of releases
			+ "," + numberCommits				//number of commits
			+ "," + numberContributors			//number of contributors 
			+ "," + hasDownloads				//was the repo downloaded
			+ "," + descriptionLength			//has a decription
			+ "," + numberProgrammingLanguages	//number of programming languages
			+ "," + hasLicense					//repo has a license
			+ "," + readmeSize					//size of the readme
			+ languages							//top 50 language contribution
			;
	}
	
	public static List<String> getFeatureLabels() {
		List<String> features = new ArrayList();
		features.add("numberBranches");
		features.add("numberForks");
		features.add("numberOpenIssues");
		features.add("repoSize");
		features.add("numberStars");
		features.add("numberSubscribers");
		features.add("numberWatchers");
		features.add("numberReleases");
		features.add("numberCommits");
		features.add("numberContributors");
		features.add("hasDownloads");
		features.add("descriptionLength");
		features.add("numberProgrammingLanguages");
		features.add("hasLicense");
		features.add("readmeSize");
		features.addAll(ExtractLanguages.getFeatureLabels());
		
		return features;
	}
}
