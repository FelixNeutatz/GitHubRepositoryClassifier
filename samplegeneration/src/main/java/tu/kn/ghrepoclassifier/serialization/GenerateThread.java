package tu.kn.ghrepoclassifier.serialization;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.*;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static tu.kn.ghrepoclassifier.serialization.SaveSearch.extractIterableToFile;

/**
 * Created by felix on 27.11.16.
 */
public class GenerateThread extends Thread {
	
	private ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue;
	private int id;
	private Iterator<String> propertyFiles;
	private String category;
	private AtomicInteger count;
	private int  maximumRecords;
	private File outputDir;
	
	public GenerateThread(int id,  
						  ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue, 
						  Iterator<String> propertyFiles,
						  String category,
						  File outputDir,
						  AtomicInteger count,
						  int maximumRecords) {
		this.id = id;
		this.queue = queue;
		this.propertyFiles = propertyFiles;
		this.category = category;
		this.count = count;
		this.maximumRecords = maximumRecords;
		this.outputDir = outputDir;
	}
	
	public void run() {
		Parallelize.runningThreads.incrementAndGet();
		try {
			/*
			Cache cache = new Cache(new File("/tmp/ghrepoclassifier" + id), 1000 * 1024 * 1024); // 1000MB cache
			
			GitHub github = GitHubBuilder.fromPropertyFile(propertyFile)
				.withConnector(new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache))))
				.build();
			github.refreshCache();*/

			String propertyFile = null;
			synchronized (propertyFiles) {
				propertyFile = propertyFiles.next();
			}
			System.out.println("thread uses: " + propertyFile);
			
			GitHub github = GitHubBuilder.fromPropertyFile(propertyFile).build();
			
			PagedIterable<GHRepository> search = queue.poll();

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			File file = new File(outputDir.getPath() + "/"  + category);
			file.mkdir();
			
			while(search != null) {
				if (extractIterableToFile(search, file, category, count, maximumRecords, github)){
					break;
				}
				
				search = queue.poll();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Parallelize.runningThreads.decrementAndGet();
	}

}
