package tu.kn.ghrepoclassifier.samplegeneration;

import au.com.bytecode.opencsv.CSVWriter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.okhttp.Cache;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedSearchIterable;
import org.kohsuke.github.extras.OkHttpConnector;
import tu.kn.ghrepoclassifier.featureextraction.FeatureExtraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by felix on 24.11.16.
 */
public class GenerateSamples {

	public static void extractIterable(PagedSearchIterable<GHRepository> searchResult, File out, String category){
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(out), ',', CSVWriter.NO_QUOTE_CHARACTER);
		
			// feed in your array (or convert your data to an array)
			String[] entries = {"","", category};

			System.out.println("size: " + searchResult.getTotalCount());
			
			int count = 0;
			for (GHRepository repo : searchResult) {
				entries[1] = FeatureExtraction.extractFeatures(repo);
				entries[0] = repo.getHtmlUrl().toString();
				count++;
				System.out.println(count + ": " + entries[0] + ", " + entries[1]);
				writer.writeNext(entries);
				writer.flush();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//HOMEWORK
	public static void generateHomeWork(GitHub github){

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File file = new File(classLoader.getResource("data/").getFile() + "homework.csv");

		PagedSearchIterable<GHRepository> homeworkRepos = 
			github.searchRepositories().q("homework").stars("0").forks("0").list().withPageSize(100);
		
		extractIterable(homeworkRepos, file, "HW");
	}

	public static void main(String[] args) throws IOException {

		Cache cache = new Cache(new File("/tmp/ghrepoclassifier"), 1000 * 1024 * 1024); // 1000MB cache

		GitHub github = GitHubBuilder.fromPropertyFile()
			.withConnector(new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache))))
			.build();
		
		System.out.println(github.getMyself());

		//HOMEWORK
		generateHomeWork(github);

		//DEV
		Map<String, GHRepository> devRepos = github.getOrganization("apache").getRepositories();

		System.out.println("dev repos size: " + devRepos.size()); //864

		Map<String, GHRepository> dev1Repos = github.getOrganization("google").getRepositories();

		System.out.println("dev repos size: " + dev1Repos.size()); //866

		Map<String, GHRepository> dev2Repos = github.getOrganization("facebook").getRepositories();

		System.out.println("dev repos size: " + dev2Repos.size()); //170

		Map<String, GHRepository> dev3Repos = github.getOrganization("baidu").getRepositories();

		System.out.println("dev repos size: " + dev3Repos.size()); //21
		
		
		//DOCS
		PagedSearchIterable<GHRepository> docsRepos = 
			github.searchRepositories().q("documentation").list().withPageSize(100);
		
		System.out.println("docs repos size: " + docsRepos.getTotalCount()); //30549

		PagedSearchIterable<GHRepository> docsRepos1 = 
			github.searchRepositories().q("documentation").stars(">5").list().withPageSize(100);

		System.out.println("docs repos size: " + docsRepos1.getTotalCount()); //2918
		

		PagedSearchIterable<GHRepository> docsReposDEU = 
			github.searchRepositories().q("Dokumentation").list().withPageSize(100);

		System.out.println("docs repos size: " + docsReposDEU.getTotalCount()); //259

		PagedSearchIterable<GHRepository> docsRepos2 = github.searchRepositories().q("manual").list().withPageSize(100);

		System.out.println("docs repos size: " + docsRepos2.getTotalCount()); //9020
		
		//WEB
		PagedSearchIterable<GHRepository> webRepos = 
			github.searchRepositories().q(".github.io").list().withPageSize(100);

		System.out.println("web repos size: " + webRepos.getTotalCount()); //560623


		//DATA
		PagedSearchIterable<GHRepository> dataRepos = 
			github.searchRepositories().q("data").size(">100000").list().withPageSize(100);

		System.out.println("data repos size: " + dataRepos.getTotalCount()); //7065 
		
		//EDU
		PagedSearchIterable<GHRepository> eduRepos = 
			github.searchRepositories().q("lecture").forks(">10").list().withPageSize(100);

		System.out.println("edu repos size: " + eduRepos.getTotalCount()); //290

		PagedSearchIterable<GHRepository> eduRepos1 = 
			github.searchRepositories().q("tutorial").forks(">10").list().withPageSize(100);

		System.out.println("edu repos size: " + eduRepos1.getTotalCount()); //2081 */
	}
}
