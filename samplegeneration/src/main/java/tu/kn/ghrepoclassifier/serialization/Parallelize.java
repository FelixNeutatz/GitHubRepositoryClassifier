package tu.kn.ghrepoclassifier.serialization;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.PagedIterable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.File;


import static tu.kn.ghrepoclassifier.serialization.SplitSearch.splitSearch;

/**
 * Created by felix on 28.11.16.
 */
public class Parallelize {
	static volatile AtomicInteger runningThreads = new AtomicInteger(0);
	static int maxThreads = 5;
	
	public static void runInParallel(Iterator<String> propertyFiles,
									 PagedIterable<GHRepository> repos, 
									 String category,
									 File outputDir,
									 int maximumRecords,
									 int numberAccounts) {
		ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue = new ConcurrentLinkedQueue();
		queue.add(repos);

		startThreads(propertyFiles, queue, category, outputDir, maximumRecords, numberAccounts);
	}

	public static void runInParallel(Iterator<String> propertyFiles,
									 GHRepositorySearchBuilder search,
									 String category,
									 File outputDir,
									 int maximumRecords,
									 int numberAccounts) {
		ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue = new ConcurrentLinkedQueue();
		splitSearch(search, queue, 1000, maximumRecords);

		startThreads(propertyFiles, queue, category, outputDir, maximumRecords, numberAccounts);
	}

	public static void startThreads(Iterator<String> propertyFiles,
									ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue, 
									String category,
									File outputDir,
									int maximumRecords,
									int numberAccounts) {
		AtomicInteger count = new AtomicInteger(0);

		for (int i = 0; i < Math.min(numberAccounts, queue.size()); i++) {
			while (runningThreads.get() >= maxThreads) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Thread t = new GenerateThread(i, queue, propertyFiles, category, outputDir, count, maximumRecords);
			t.start();
		}
	}
}
