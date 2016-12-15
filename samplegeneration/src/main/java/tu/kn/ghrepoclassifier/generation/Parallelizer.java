package tu.kn.ghrepoclassifier.generation;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.PagedIterable;
import tu.kn.ghrepoclassifier.generation.thread.GenerateFromRepo;
import tu.kn.ghrepoclassifier.generation.thread.GenerateFromSearch;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.File;


import static tu.kn.ghrepoclassifier.generation.SplitSearch.splitSearch;

/**
 * Created by felix on 28.11.16.
 */
public class Parallelizer {
	private volatile AtomicInteger runningThreads;
	private int maxThreads;
	private Iterator<String> propertyFiles;
	private int numberAccounts;
	private File outputDir;
	
	public Parallelizer(int maxThreads, Iterator<String> propertyFiles, int numberAccounts, File outputDir) {
		runningThreads = new AtomicInteger(0);
		this.maxThreads = maxThreads;
		this.propertyFiles = propertyFiles;
		this.numberAccounts = numberAccounts;
		this.outputDir = outputDir;
	}
	
	public void runInParallel(PagedIterable<GHRepository> repos, 
									 String category,
									 int maximumRecords) {
		ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue = new ConcurrentLinkedQueue();
		queue.add(repos);

		startThreads(queue, category, maximumRecords);
	}

	public void runInParallel(GHRepositorySearchBuilder search,
									 String category,
									 int maximumRecords) {
		ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue = new ConcurrentLinkedQueue();
		splitSearch(search, queue, 1000, maximumRecords);

		startThreads(queue, category, maximumRecords);
	}

	public void startThreads(ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue, 
									String category,
									int maximumRecords) {
		AtomicInteger count = new AtomicInteger(0);

		for (int i = 0; i < Math.min(numberAccounts, queue.size()); i++) {
			String propertyFile = waitAndGetPropertyFile();
			
			Thread t = new GenerateFromSearch(this, queue, propertyFile, category, outputDir, count, maximumRecords);
			t.start();
		}
	}
	
	public String waitAndGetPropertyFile() {
		while (runningThreads.get() >= maxThreads) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		String propertyFile = null;
		synchronized (propertyFiles) {
			propertyFile = propertyFiles.next();
		}
		return propertyFile;
	}

	public void startThreads(List<Repo> repoList) {
		AtomicInteger count = new AtomicInteger(0);

		for (Repo r: repoList) {
			String propertyFile = waitAndGetPropertyFile();

			Thread t = new GenerateFromRepo(this, r, propertyFile, outputDir, count);
			t.start();
		}
	}
	
	public void registerThread() {
		runningThreads.incrementAndGet();
	}
	
	public void unregisterThread() {
		runningThreads.decrementAndGet();
	}
}
