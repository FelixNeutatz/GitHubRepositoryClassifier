package tu.kn.ghrepoclassifier.generation.thread;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import tu.kn.ghrepoclassifier.generation.Parallelizer;
import tu.kn.ghrepoclassifier.generation.Repo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static tu.kn.ghrepoclassifier.serialization.SaveSearch.extractRepoToFile;

/**
 * Created by felix on 27.11.16.
 */
public class GenerateFromRepo extends AbstractThread {
	private Repo repo;
	
	public GenerateFromRepo(Parallelizer p,
							Repo repo,
							String propertyFile,
							File outputDir,
							AtomicInteger count) {
		super(p, propertyFile, outputDir, count, -1);
		this.repo = repo;
		
	}
	
	public void run() {
		p.registerThread();
		try {
			GitHub github = GitHubBuilder.fromPropertyFile(propertyFile).build();
			
			File file = new File(outputDir.getPath() + "/"  + repo.getLabel());
			file.mkdir();
			
			extractRepoToFile(repo.getFullName(), file, repo.getLabel(), count, github);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.unregisterThread();
	}

}
