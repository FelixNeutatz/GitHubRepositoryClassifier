package tu.kn.ghrepoclassifier.samplegeneration;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedSearchIterable;
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
	
	private ConcurrentLinkedQueue<PagedSearchIterable<GHRepository>> queue;
	private int id;
	private String propertyFile;
	private String category;
	private AtomicInteger count;
	
	public GenerateThread(int id,  
						  ConcurrentLinkedQueue<PagedSearchIterable<GHRepository>> queue, 
						  String propertyFile,
						  String category,
						  AtomicInteger count) {
		this.id = id;
		this.queue = queue;
		this.propertyFile = propertyFile;
		this.category = category;
		this.count = count;
	}
	
	public void run() {
		try {
			Cache cache = new Cache(new File("/tmp/ghrepoclassifier" + id), 1000 * 1024 * 1024); // 1000MB cache
			
			GitHub github = GitHubBuilder.fromPropertyFile(propertyFile)
				.withConnector(new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache))))
				.build();
			github.refreshCache();
			
			PagedSearchIterable<GHRepository> search = queue.poll();
			
			while(search != null) {

				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				File file = new File(classLoader.getResource("data/").getFile() + "data" + category + id + ".csv");

				extractIterableToFile(search, file, category, count);
				
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
