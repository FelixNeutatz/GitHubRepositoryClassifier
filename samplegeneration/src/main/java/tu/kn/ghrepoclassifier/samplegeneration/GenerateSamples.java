package tu.kn.ghrepoclassifier.samplegeneration;

import org.kohsuke.github.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static tu.kn.ghrepoclassifier.samplegeneration.SplitSearch.splitSearch;

/**
 * Created by felix on 24.11.16.
 */
public class GenerateSamples {

	public static void runInParallel(String [] propertyFiles, GHRepositorySearchBuilder search, String category) {
		ConcurrentLinkedQueue<PagedSearchIterable<GHRepository>> queue = new ConcurrentLinkedQueue();
		splitSearch(search, queue);
		
		AtomicInteger count = new AtomicInteger(0);

		for (int i = 0; i < propertyFiles.length; i++) {
			Thread t = new GenerateThread(i, queue, propertyFiles[i], category, count);
			t.start();
		}
	}

	/*
		repositories which contain "homework" and have no forks and no stars, 
		are very likely to fit the homework description:
			Repositories mit Lösungen und Quelltexten für Hausaufgaben und Übungsblätter
		because students work on their own
	
		search result size: 62401
	 */
	public static void generateHW1(GitHub github, String [] propertyFiles){
		GHRepositorySearchBuilder search = github.searchRepositories().q("homework").stars("0").forks("0");
		runInParallel(propertyFiles, search, "HW");
	}

	/*
		repositories which contain "tutorial" and have many forks (> 10), 
		are very likely to fit the education description:
			Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
	
		search result size: 2081
	 */
	public static void generateEDU1(GitHub github, String [] propertyFiles){
		GHRepositorySearchBuilder search = github.searchRepositories().q("tutorial").forks(">10");
		runInParallel(propertyFiles, search, "EDU");
	}

	/*
		repositories which contain "tutorial" and have many forks (> 10), 
		are very likely to fit the education description:
			Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
	
		search result size: 290
	 */
	public static void generateEDU2(GitHub github, String [] propertyFiles){
		GHRepositorySearchBuilder search = github.searchRepositories().q("lecture").forks(">10");
		runInParallel(propertyFiles, search, "EDU");
	}

	/*
		WEB
		see: https://pages.github.com/
		search result size: 560623
	 */
	public static void generateWEB(GitHub github, String [] propertyFiles){
		GHRepositorySearchBuilder search = github.searchRepositories().q(".github.io");
		runInParallel(propertyFiles, search, "WEB");
	}


	public static void main(String[] args) throws IOException {

		String [] propertyFiles = {"/home/felix/.github"
			, "/home/felix/gitpropmax"
			, "/home/felix/maxpropgit2"
		};
		
		GitHub github = GitHubBuilder.fromPropertyFile( propertyFiles[0]).build();
		System.out.println(github.getMyself());

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));

		//EDU
		//Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
		generateEDU1(github, propertyFiles);
		//generateEDU2(github, propertyFiles);

		/*
		//HOMEWORK
		//Repositories mit Lösungen und Quelltexten für Hausaufgaben und Übungsblätter
		generateHW1(github,propertyFiles);

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
			github.searchRepositories().q("documentation NOT lecture").list().withPageSize(100);
		
		System.out.println("docs repos size: " + docsRepos.getTotalCount()); //30549

		PagedSearchIterable<GHRepository> docsRepos1 = 
			github.searchRepositories().q("documentation").stars(">5").list().withPageSize(100);

		System.out.println("docs repos size: " + docsRepos1.getTotalCount()); //2918
		

		PagedSearchIterable<GHRepository> docsReposDEU = 
			github.searchRepositories().q("Dokumentation NOT Vorlesung").list().withPageSize(100);

		System.out.println("docs repos size: " + docsReposDEU.getTotalCount()); //259

		PagedSearchIterable<GHRepository> docsRepos2 = github.searchRepositories().q("manual").list().withPageSize(100);

		System.out.println("docs repos size: " + docsRepos2.getTotalCount()); //9020
		
		//DATA
		PagedSearchIterable<GHRepository> dataRepos = 
			github.searchRepositories().q("data").size(">100000").list().withPageSize(100);

		System.out.println("data repos size: " + dataRepos.getTotalCount()); //7065 
		*/
		
	}
}
