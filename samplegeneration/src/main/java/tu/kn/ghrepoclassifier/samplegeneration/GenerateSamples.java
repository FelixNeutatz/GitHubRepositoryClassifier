package tu.kn.ghrepoclassifier.samplegeneration;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.*;

import java.io.File;
import java.io.IOException;

import static tu.kn.ghrepoclassifier.samplegeneration.Parallelize.runInParallel;
import static tu.kn.ghrepoclassifier.samplegeneration.Parallelize.runThread;


/**
 * Created by felix on 24.11.16.
 */
public class GenerateSamples {
	/*
		repositories which contain "homework" and have no forks and no stars, 
		are very likely to fit the homework description:
			Repositories mit Lösungen und Quelltexten für Hausaufgaben und Übungsblätter
		because students work on their own
	
		search result size: 62401
	 */
	public static void generateHW1(GitHub github, String [] propertyFiles, File outputDir, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("homework").stars("0").forks("0");
		runInParallel(propertyFiles, search, "HW", outputDir, maximumRecords);
	}

	/*
		repositories which contain "tutorial" and have many forks (> 10), 
		are very likely to fit the education description:
			Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
	
		search result size: 2081
	 */
	public static void generateEDU1(GitHub github, String [] propertyFiles, File outputDir, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("tutorial").forks(">10");
		runInParallel(propertyFiles, search, "EDU", outputDir, maximumRecords);
	}

	/*
		repositories which contain "tutorial" and have many forks (> 10), 
		are very likely to fit the education description:
			Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
	
		search result size: 290
	 */
	public static void generateEDU2(GitHub github, String [] propertyFiles, File outputDir, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("lecture").forks(">10");
		runInParallel(propertyFiles, search, "EDU", outputDir, maximumRecords);
	}

	/*
		WEB
		Search for repository names ending with ".github.io"
			see: https://pages.github.com/
		This fits:
			Repositories für das Hosting persönlicher Web-Seiten oder Blogs
		search result size: 560623
	 */
	public static void generateWEB(GitHub github, String [] propertyFiles, File outputDir, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q(".github.io");
		runInParallel(propertyFiles, search, "WEB", outputDir, maximumRecords);
	}

	/*
		DEV
		All repositories of Apache are development projects
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, 
			einer API, oder ähnliche Softwareentwicklungsprojekte 
		search result size: 864
	*/
	public static void generateDEV1(GitHub github, String propertyFile, File outputDir, int maximumRecords) throws IOException {
		PagedIterable<GHRepository> devRepos = github.getOrganization("apache").listRepositories(100);
		runThread(propertyFile, devRepos, "DEV", outputDir, maximumRecords);
	}

	/*
		DEV
		Almost all repositories of Google are development projects
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, 
			einer API, oder ähnliche Softwareentwicklungsprojekte 
		search result size: 866
	*/
	public static void generateDEV2(GitHub github, String propertyFile, File outputDir, int maximumRecords) throws IOException {
		PagedIterable<GHRepository> devRepos = github.getOrganization("google").listRepositories(100);
		runThread(propertyFile, devRepos, "DEV", outputDir, maximumRecords);
	}

	/*
		DEV
		Almost all repositories of Facebook are development projects
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, 
			einer API, oder ähnliche Softwareentwicklungsprojekte 
		search result size: 170
	*/
	public static void generateDEV3(GitHub github, String propertyFile, File outputDir, int maximumRecords) throws IOException {
		PagedIterable<GHRepository> devRepos = github.getOrganization("facebook").listRepositories(100);
		runThread(propertyFile, devRepos, "DEV", outputDir, maximumRecords);
	}

	/*
		DEV
		Almost all repositories of Baidu are development projects
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, 
			einer API, oder ähnliche Softwareentwicklungsprojekte 
		search result size: 21
	*/
	public static void generateDEV4(GitHub github, String propertyFile, File outputDir, int maximumRecords) throws IOException {
		PagedIterable<GHRepository> devRepos = github.getOrganization("baidu").listRepositories(100);
		runThread(propertyFile, devRepos, "DEV", outputDir, maximumRecords);
	}

	/*
		DOCS
		Search for repositories which contain the word "documentation" and not the word "lecture".
		This fits:
			Repositories für die Verwaltung und 
			Speicherung von nicht-didaktischen (d.h. nicht EDU) Inhalten und Quelltexten
		To be more sure that it is really in the category DOCS, we can constraint it by its fame.
		search result size: 30631
	 */
	public static void generateDOCS1(GitHub github, String [] propertyFiles, File outputDir, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("documentation NOT lecture");
		//GHRepositorySearchBuilder search = github.searchRepositories().q("documentation NOT lecture").stars(">5");
		runInParallel(propertyFiles, search, "DOCS", outputDir, maximumRecords);
	}

	/*
		DOCS
		Search for repositories which contain the word "manual" and not the word "lecture".
		This fits:
			Repositories für die Verwaltung und 
			Speicherung von nicht-didaktischen (d.h. nicht EDU) Inhalten und Quelltexten
		To be more sure that it is really in the category DOCS, we can constraint it by its fame.
		search result size: 9020
	 */
	public static void generateDOCS2(GitHub github, String [] propertyFiles, File outputDir, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("manual NOT lecture");
		//GHRepositorySearchBuilder search = github.searchRepositories().q("manual NOT lecture").stars(">5");
		runInParallel(propertyFiles, search, "DOCS", outputDir, maximumRecords);
	}

	/*
		DATA
		Search for repositories which contain the word "dataset"
		This fits:
			Repositories für die Speicherung von Datensätzen
		To be more sure that it is really in the category DATA, we can contraint the repo size to be big.
		search result size: 14105
	 */
	public static void generateDATA(GitHub github, String [] propertyFiles, File outputDir, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("dataset");
		//GHRepositorySearchBuilder search = github.searchRepositories().q("dataset").size(">100000");
		runInParallel(propertyFiles, search, "DATA", outputDir, maximumRecords);
	}

	public static void main(String[] args) throws IOException {

		String [] propertyFiles = {"/home/felix/.github"
			, "/home/felix/gitpropmax"
			, "/home/felix/maxpropgit2"
		};
		
		GitHub github = GitHubBuilder.fromPropertyFile( propertyFiles[0]).build();
		System.out.println(github.getMyself());

		GHRateLimit limit = github.getRateLimit();
		System.out.println("remaining requests: " + limit.remaining);
		System.out.println("limit: " + limit.limit);
		System.out.println("reset date: " + limit.reset);
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File outputDir = new File(classLoader.getResource("data/").getFile());
		FileUtils.cleanDirectory(outputDir);
		
		//EDU
		//Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
		generateEDU1(github, propertyFiles, outputDir, 750);
		generateEDU2(github, propertyFiles, outputDir, 250);

		
		//HOMEWORK
		//Repositories mit Lösungen und Quelltexten für Hausaufgaben und Übungsblätter
		generateHW1(github,propertyFiles, outputDir, 1000);
		
		//DEV
		//Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, einer API,
		//oder ähnliche Softwareentwicklungsprojekte 
		generateDEV1(github, propertyFiles[0], outputDir, 500);
		generateDEV2(github, propertyFiles[1], outputDir, 250);
		generateDEV3(github, propertyFiles[2], outputDir, 230);
		generateDEV4(github, propertyFiles[0], outputDir, 20);

		//DOCS
		//Repositories für die Verwaltung und 
		//Speicherung von nicht-didaktischen (d.h. nicht EDU) Inhalten und Quelltexten
		generateDOCS1(github, propertyFiles, outputDir, 500);
		generateDOCS2(github, propertyFiles, outputDir, 500);
		
		//DATA
		//Repositories für die Speicherung von Datensätzen
		generateDATA(github, propertyFiles, outputDir, 1000);
		
		
	}
}
