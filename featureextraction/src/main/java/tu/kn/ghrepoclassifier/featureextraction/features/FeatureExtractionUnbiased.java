package tu.kn.ghrepoclassifier.featureextraction.features;

import tu.kn.ghrepoclassifier.featureextraction.features.commits.Stats;
import tu.kn.ghrepoclassifier.featureextraction.features.content.ExtractContent;
import tu.kn.ghrepoclassifier.featureextraction.features.languages.ExtractLanguages;
import tu.kn.ghrepoclassifier.featureextraction.features.commits.CommitHistory;
import tu.kn.ghrepoclassifier.serialization.data.ContributorData;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static tu.kn.ghrepoclassifier.featureextraction.features.languages.ExtractLanguages.*;

/**
 * Created by felix on 24.11.16.
 */
public class FeatureExtractionUnbiased {

	public static String extractFeatures(RepoData repo) {
		int numberBranches = repo.getBranches().size(); 							//number of branches
		//int numberForks = repo.getForks(); 											//number of forks
		int numberOpenIssues = repo.getOpenIssueCount();							//number of open issues
		int totalNumberIssues = repo.listIssues().size();							//number of total issues
		int repoSize = repo.getSize();												//size of the repository in bytes
		//int numberStars = repo.getStargazersCount();								//number of stars
		int numberSubscribers = repo.getSubscribersCount();							//number of subscribers
		int numberWatchers = repo.getWatchers();									//number of watchers
		int numberReleases = repo.listReleases().size();							//number of releases
		int indexHTMLfileLength = repo.getIndexHTML().getContent().length();		//index.html length

		int hasDownloads = repo.hasDownloads() ? 1 : 0;								//was the repo downloaded
		int descriptionLength = 0;													//decription length
		if (repo.getDescription() != null) {
			descriptionLength = repo.getDescription().length();
		}

		Map<String, Long> programmingLanguages = repo.listLanguages();				//number of programming languages

		//this introduces sparsity
		String languages = extractProgrammingLanguages(programmingLanguages);		//top 50 language contribution
		String languagesFractions = extractProgrammingLanguagesFraction(programmingLanguages);
		
		int numberProgrammingLanguages = programmingLanguages.size();

		int hasLicense = (repo.getLicense().length() > 0) ? 0 : 1;					//repo has a license

		CommitHistory h = new CommitHistory(repo);
		String commitHistory = h.summarizeCommitHistory();							//summarized commit history

		String fileTreeSummary = "";
		try {
			fileTreeSummary = ExtractContent.extractFeatures(repo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Stats contributorStats = new Stats("contributor", true, true);
		for(ContributorData c: repo.listContributors()) {
			contributorStats.add(c.getContributions());
		}

		long readmeSize = repo.getReadme().getSize();								//size of the readme
		
		
		return
						numberBranches				//number of branches
				//+ "," + numberForks 				//number of branches
				+ "," + numberOpenIssues			//number of open issues
				+ "," + totalNumberIssues			//number of total issues
				+ "," + repoSize					//size of the repository in bytes
				//+ "," + numberStars					//number of stars
				+ "," + numberSubscribers			//number of subscribers
				+ "," + numberWatchers				//number of watchers
				+ "," + numberReleases				//number of releases
				+ "," + indexHTMLfileLength			//index.html length
				+ "," + contributorStats.toString()	//contributor stats
				+ "," + hasDownloads				//was the repo downloaded
				+ "," + descriptionLength				//decription length
				+ "," + numberProgrammingLanguages	//number of programming languages
				+ "," + hasLicense					//repo has a license
				+ "," + readmeSize					//size of the readme
				+ "," + commitHistory				//summarized commit history
				+ "," + fileTreeSummary			
				+		languages					//all language contributions
			    +		languagesFractions			//all language contribution fractions
			;
	}

	public static List<String> getFeatureLabels() {
		List<String> features = new ArrayList();
		features.add("numberBranches");
		//features.add("numberForks");
		features.add("numberOpenIssues");
		features.add("totalNumberIssues");
		features.add("repoSize");
		//features.add("numberStars");
		features.add("numberSubscribers");
		features.add("numberWatchers");
		features.add("numberReleases");
		features.add("indexHTMLfileLength");
		features.addAll(new Stats("contributor", true, true).getFeatureLabels());
		features.add("hasDownloads");
		features.add("descriptionLength");
		features.add("numberProgrammingLanguages");
		features.add("hasLicense");
		features.add("readmeSize");
		features.addAll(CommitHistory.getFeatureLabels());
		features.addAll(ExtractContent.getFeatureLabels());
		features.addAll(ExtractLanguages.getFeatureLabels());
		features.addAll(ExtractLanguages.getFeatureLabelsFraction());

		return features;
	}
}
