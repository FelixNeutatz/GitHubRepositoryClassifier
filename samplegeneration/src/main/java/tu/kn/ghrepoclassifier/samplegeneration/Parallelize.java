package tu.kn.ghrepoclassifier.samplegeneration;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedSearchIterable;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.File;


import static tu.kn.ghrepoclassifier.samplegeneration.SplitSearch.splitSearch;

/**
 * Created by felix on 28.11.16.
 */
public class Parallelize {
	public static void runInParallel(String [] propertyFiles, 
									 GHRepositorySearchBuilder search, 
									 String category,
									 File outputDir,
									 int maximumRecords) {
		ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue = new ConcurrentLinkedQueue();
		splitSearch(search, queue, 1000, maximumRecords);

		startThreads(propertyFiles, queue, category, outputDir, maximumRecords);
	}

	public static void runThread(String propertyFile, 
								 PagedIterable<GHRepository> repos, 
								 String category, 
								 File outputDir,
								 int maximumRecords) {
		ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue = new ConcurrentLinkedQueue();
		queue.add(repos);

		startThreads(new String [] {propertyFile}, queue, category, outputDir, maximumRecords);
	}

	public static void startThreads(String [] propertyFiles,
									ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue, 
									String category,
									File outputDir,
									int maximumRecords) {
		AtomicInteger count = new AtomicInteger(0);

		ArrayList <Thread> threads = new ArrayList<>();
		for (int i = 0; i < propertyFiles.length; i++) {
			Thread t = new GenerateThread(i, queue, propertyFiles[i], category, outputDir, count, maximumRecords);
			t.start();
			threads.add(t);
		}
		//wait for the threads to finish
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
