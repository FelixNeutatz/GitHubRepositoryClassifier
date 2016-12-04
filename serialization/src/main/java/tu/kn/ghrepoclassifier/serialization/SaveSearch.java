package tu.kn.ghrepoclassifier.serialization;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by felix on 27.11.16.
 */
public class SaveSearch {
	
	/*
	save extracted features in a file
	 */
	public static boolean extractIterableToFile(PagedIterable<GHRepository> searchResult, 
											 File out, 
											 String category, 
											 AtomicInteger count,
											 int maximumRecords){
		boolean isFinished = false;
		try {
			String[] entries = {"","", category};

			//TODO: check why we need this to prevent duplicates??
			HashSet<Integer> idList = new HashSet<Integer>();

			for (GHRepository repo : searchResult) {
				if (!idList.contains(repo.getId())) {
					idList.add(repo.getId());

					if (count.incrementAndGet() <= maximumRecords) {
						if (!Serializer.exists(repo, out)) {
							RepoData data = new RepoData(repo, category);
							Serializer.writeToDir(out, data);
							System.out.println(count.get() + ": " + data.getName() + " id: " + data.getId());
						}
					} else {
						isFinished = true;
						break;
					}
				} else {
					System.err.println("there were duplicates");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isFinished;
	}

}
