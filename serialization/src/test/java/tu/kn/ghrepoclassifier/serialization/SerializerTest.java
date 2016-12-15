package tu.kn.ghrepoclassifier.serialization;

import org.junit.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.nio.file.Files.readAllLines;

/**
 * Created by felix on 04.12.16.
 */
public class SerializerTest {

	@Test
	public void storeAndLoad() throws Exception {
		List<String> accountFileList = readAllLines(new File(
			Config.get("sample.generation.git-accounts.file")).toPath(),
			StandardCharsets.UTF_8);
		String gitAuthenticationFile = accountFileList.get(0);
		
		GitHub github = GitHubBuilder.fromPropertyFile(gitAuthenticationFile).build();
		GHRepository repo = github.getRepository("peelframework/peel");

		File outputDir = new File(Config.get("sample.generation.output.path"));
		
		RepoData data = new RepoData(repo, "DEV");
		Serializer.writeToDir(outputDir, data);

		String downloadedRepo = outputDir.getAbsolutePath() + "/data" + data.getId() + ".bin";
		RepoData readData = Serializer.readFromFile(downloadedRepo);
		
		new File(downloadedRepo).delete();
		
		if (!data.equals(readData)) {
			throw new Exception("Written and read data are not the same.");
		}
	}
}
