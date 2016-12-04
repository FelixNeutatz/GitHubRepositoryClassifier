package tu.kn.ghrepoclassifier.serialization;

import org.junit.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.*;

/**
 * Created by felix on 04.12.16.
 */
public class SerializerTest {

	@Test
	public void storeAndLoad() throws Exception {
		GitHub github = GitHubBuilder.fromCredentials().build();
		GHRepository repo = github.getRepository("peelframework/peel");

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File outputDir = new File(classLoader.getResource("data/").getFile());
		
		RepoData data = new RepoData(repo, "DEV");
		Serializer.writeToDir(outputDir, data);

		RepoData readData = Serializer.readFromFile(outputDir.getAbsolutePath() + "/data" + data.getId() + ".bin" );
		
		if (!data.equals(readData)) {
			throw new Exception("Written and read data are not the same.");
		}
	}
}
