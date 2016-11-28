package tu.kn.ghrepoclassifier.samplegeneration;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.*;
import org.kohsuke.github.extras.OkHttpConnector;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static tu.kn.ghrepoclassifier.samplegeneration.SaveSearch.extractIterableToFile;

/**
 * Created by felix on 27.11.16.
 */
public class GenerateThread extends Thread {
	
	private ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue;
	private int id;
	private String propertyFile;
	private String category;
	private AtomicInteger count;
	private int  maximumRecords;
	private File outputDir;
	
	public GenerateThread(int id,  
						  ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue, 
						  String propertyFile,
						  String category,
						  File outputDir,
						  AtomicInteger count,
						  int maximumRecords) {
		this.id = id;
		this.queue = queue;
		this.propertyFile = propertyFile;
		this.category = category;
		this.count = count;
		this.maximumRecords = maximumRecords;
		this.outputDir = outputDir;
	}
	
	public void run() {
		try {
			Cache cache = new Cache(new File("/tmp/ghrepoclassifier" + id), 1000 * 1024 * 1024); // 1000MB cache
			
			GitHub github = GitHubBuilder.fromPropertyFile(propertyFile)
				.withConnector(new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache))))
				.build();
			github.refreshCache();
			
			PagedIterable<GHRepository> search = queue.poll();
			
			while(search != null) {

				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				File file = new File(outputDir.getPath() + "/" + "data" + category + id + ".csv");

				extractIterableToFile(search, file, category, count, maximumRecords);
				
				search = queue.poll();
			}

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
