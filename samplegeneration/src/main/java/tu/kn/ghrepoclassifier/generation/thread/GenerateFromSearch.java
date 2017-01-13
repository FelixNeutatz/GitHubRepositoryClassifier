package tu.kn.ghrepoclassifier.generation.thread;

import org.kohsuke.github.*;
import tu.kn.ghrepoclassifier.generation.Parallelizer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static tu.kn.ghrepoclassifier.serialization.SaveSearch.extractIterableToFile;

/**
 * Created by felix on 27.11.16.
 */
public class GenerateFromSearch extends AbstractThread {
	
	private ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue;
	private String category;
	
	public GenerateFromSearch(Parallelizer p,
							  ConcurrentLinkedQueue<PagedIterable<GHRepository>> queue,
							  String propertyFile,
							  String category,
							  File outputDir,
							  AtomicInteger count,
							  int maximumRecords) {
		super(p, propertyFile, outputDir, count, maximumRecords);
		this.queue = queue;
		this.category = category;
	}
	
	public void run() {
		this.p.registerThread();
		
		PagedIterable<GHRepository> search = queue.poll();

		File file = new File(outputDir.getPath() + "/"  + category);
		file.mkdir();
		
		while(search != null) {
			if (extractIterableToFile(search, file, category, count, maximumRecords, github)){
				break;
			}
			
			search = queue.poll();
		}
		
		p.unregisterThread();
	}

}
