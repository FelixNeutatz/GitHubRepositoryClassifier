package tu.kn.ghrepoclassifier.generation.thread;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import tu.kn.ghrepoclassifier.generation.Parallelizer;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by felix on 27.11.16.
 */
public abstract class AbstractThread extends Thread {
	
	protected Parallelizer p;
	protected AtomicInteger count;
	protected int  maximumRecords;
	protected File outputDir;
	protected GitHub github;
	
	public AbstractThread(Parallelizer p,
						  String propertyFile,
						  File outputDir,
						  AtomicInteger count,
						  int maximumRecords) {
		this.p = p;
		this.count = count;
		this.maximumRecords = maximumRecords;
		this.outputDir = outputDir;

		try {
			if (propertyFile != null) {
				github = GitHubBuilder.fromPropertyFile(propertyFile).build();
			} else {
				github = GitHub.connectAnonymously();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	abstract public void run();

}
