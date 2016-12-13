package tu.kn.ghrepoclassifier.featureextraction;

import tu.kn.ghrepoclassifier.serialization.data.RepoData;

/**
 * Created by felix on 12.12.16.
 */
public class DataCleaner {
	public static boolean isValid(RepoData repo) {
		
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
		
		
		return true;
	}
}
