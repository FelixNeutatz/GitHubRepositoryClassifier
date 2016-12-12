package tu.kn.ghrepoclassifier.featureextraction;

import tu.kn.ghrepoclassifier.serialization.data.RepoData;

/**
 * Created by felix on 12.12.16.
 */
public class DataCleaner {
	public static boolean isValid(RepoData repo) {
		
		if ((repo.getSize() == 0)
			&& !repo.getOurClassification().equals("OTHER")) {
			System.err.println("Repo is probably not valid: " + repo.getFull_name() + "\n" + "cause: size = 0");
			return false;
		}
		
		if (repo.getName().contains(".github.io") && !repo.getOurClassification().equals("WEB")) {
			System.err.println("Repo is probably not valid: " + repo.getFull_name() + "\n" + "cause: web error");
			return false;
		}

		/*
		if ((repo.getName().contains("documentation")
		     || (repo.getDescription() != null && repo.getDescription().contains("documentation"))
		    ) 
			 && !repo.getOurClassification().equals("DOCS")) {
			System.err.println("Repo is probably not valid: " + repo.getFull_name() + "\n" + "cause: docs");
			return false;
		}
		*/
		
		return true;
	}
}
