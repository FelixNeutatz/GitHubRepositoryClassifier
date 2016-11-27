package tu.kn.ghrepoclassifier.samplegeneration;

import au.com.bytecode.opencsv.CSVWriter;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedSearchIterable;
import tu.kn.ghrepoclassifier.featureextraction.FeatureExtraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by felix on 27.11.16.
 */
public class SaveSearch {
	
	/*
	save extracted features in a file
	 */
	public static void extractIterableToFile(PagedSearchIterable<GHRepository> searchResult, 
									   File out, String category, AtomicInteger count){
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(out,true), ',', CSVWriter.NO_QUOTE_CHARACTER);

			String[] entries = {"","", category};

			System.out.println("search:" + searchResult + "size: " + searchResult.getTotalCount());

			//TODO: check why we need this to prevent duplicates??
			HashSet<Integer> idList = new HashSet<Integer>();

			for (GHRepository repo : searchResult) {
				if (!idList.contains(repo.getId())) {
					idList.add(repo.getId());

					entries[1] = FeatureExtraction.extractFeatures(repo);
					entries[0] = repo.getHtmlUrl().toString();

					count.incrementAndGet();
					System.out.println(count.get() + ": " + entries[0] + ", " + entries[1]);
					writer.writeNext(entries);
					writer.flush();
				} else {
					System.err.println("there were duplicates");
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	save extracted features in a collection
	 */
	public static void extractIterable(PagedSearchIterable<GHRepository> searchResult, 
									   List<String> result, String category, AtomicInteger count) throws IOException {
		String[] entries = {"","", category};

		System.out.println("search:" + searchResult + "size: " + searchResult.getTotalCount());

		//safe guard to warn in case of duplicates
		HashSet<Integer> idList = new HashSet<Integer>();

		for (GHRepository repo : searchResult) {
			if (!idList.contains(repo.getId())) {
				idList.add(repo.getId());

				entries[1] = FeatureExtraction.extractFeatures(repo);
				entries[0] = repo.getHtmlUrl().toString();

				count.incrementAndGet();
				System.out.println(count.get() + ": " + entries[0] + ", " + entries[1]);
				result.add(entries[0] + "," + entries[1] + "," + entries[2]);
			}
		}
	}

}
