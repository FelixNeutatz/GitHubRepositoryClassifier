package tu.kn.ghrepoclassifier.generation.main;

import com.google.common.collect.Iterators;
import org.kohsuke.github.*;
import tu.kn.ghrepoclassifier.Config;
import tu.kn.ghrepoclassifier.generation.Parallelizer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import static java.nio.file.Files.readAllLines;


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
	
	public static void generateHW1(GitHub github, Parallelizer p, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("homework").stars("0").forks("0");
		p.runInParallel(search, "HW", maximumRecords);
	}

	/*
		repositories which contain "tutorial" and have many forks (> 10), 
		are very likely to fit the education description:
			Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
	
		search result size: 2081
	 */
	public static void generateEDU1(GitHub github, Parallelizer p, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("tutorial").forks(">10");
		p.runInParallel(search, "EDU", maximumRecords);
	}

	/*
		repositories which contain "tutorial" and have many forks (> 10), 
		are very likely to fit the education description:
			Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
	
		search result size: 290
	 */
	public static void generateEDU2(GitHub github, Parallelizer p, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("lecture").forks(">10");
		p.runInParallel(search, "EDU", maximumRecords);
	}

	/*
		WEB
		Search for repository names ending with ".github.io"
			see: https://pages.github.com/
		This fits:
			Repositories für das Hosting persönlicher Web-Seiten oder Blogs
		search result size: 560623
	 */
	public static void generateWEB(GitHub github, Parallelizer p, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q(".github.io");
		p.runInParallel(search, "WEB", maximumRecords);
	}

	/*
		DEV
		All repositories of Apache are development projects
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, 
			einer API, oder ähnliche Softwareentwicklungsprojekte 
		search result size: 864
	*/
	public static void generateDEV1(GitHub github, Parallelizer p, int maximumRecords) throws IOException {
		PagedIterable<GHRepository> devRepos = github.getOrganization("apache").listRepositories(100);
		p.runInParallel(devRepos, "DEV", maximumRecords);
	}

	/*
		DEV
		Almost all repositories of Google are development projects
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, 
			einer API, oder ähnliche Softwareentwicklungsprojekte 
		search result size: 866
	*/
	public static void generateDEV2(GitHub github, Parallelizer p, int maximumRecords) throws IOException {
		PagedIterable<GHRepository> devRepos = github.getOrganization("google").listRepositories(100);
		p.runInParallel(devRepos, "DEV",maximumRecords);
	}

	/*
		DEV
		Almost all repositories of Facebook are development projects
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, 
			einer API, oder ähnliche Softwareentwicklungsprojekte 
		search result size: 170
	*/
	public static void generateDEV3(GitHub github, Parallelizer p, int maximumRecords) throws IOException {
		PagedIterable<GHRepository> devRepos = github.getOrganization("facebook").listRepositories(100);
		p.runInParallel(devRepos, "DEV", maximumRecords);
	}

	/*
		DEV
		Almost all repositories of Baidu are development projects
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, 
			einer API, oder ähnliche Softwareentwicklungsprojekte 
		search result size: 21
	*/
	public static void generateDEV4(GitHub github, Parallelizer p, int maximumRecords) throws IOException {
		PagedIterable<GHRepository> devRepos = github.getOrganization("baidu").listRepositories(100);
		p.runInParallel(devRepos, "DEV", maximumRecords);
	}

	/*
		DEV
		Popular framework repositories that do not belong to the other categories.
		This fits:
			Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek,
			einer API, oder ähnliche Softwareentwicklungsprojekte
		search result size: 3088
	*/
	public static void generateDEV5(GitHub github, Parallelizer p, int maximumRecords) throws IOException {
		GHRepositorySearchBuilder search = github.searchRepositories()
				.q("framework NOT tutorial NOT documentation NOT lecture NOT manual NOT homework").stars(">100");
		p.runInParallel(search, "DEV", maximumRecords);
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
	public static void generateDOCS1(GitHub github, Parallelizer p, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("documentation NOT lecture");
		//GHRepositorySearchBuilder search = github.searchRepositories().q("documentation NOT lecture").stars(">5");
		p.runInParallel(search, "DOCS", maximumRecords);
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
	public static void generateDOCS2(GitHub github, Parallelizer p, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("manual NOT lecture");
		//GHRepositorySearchBuilder search = github.searchRepositories().q("manual NOT lecture").stars(">5");
		p.runInParallel(search, "DOCS", maximumRecords);
	}

	/*
		DATA
		Search for repositories which contain the word "dataset"
		This fits:
			Repositories für die Speicherung von Datensätzen
		To be more sure that it is really in the category DATA, we can contraint the repo size to be big.
		search result size: 14105
	 */
	public static void generateDATA(GitHub github, Parallelizer p, int maximumRecords){
		GHRepositorySearchBuilder search = github.searchRepositories().q("dataset");
		//GHRepositorySearchBuilder search = github.searchRepositories().q("dataset").size(">100000");
		p.runInParallel(search, "DATA", maximumRecords);
	}
	
	public static void main(String[] args) throws IOException {

		Iterator<String> propertyFiles = null;
		int numberAccounts = 0;
		GitHub github = null;
		try {
			List<String> accountFileList = readAllLines(new File(
							Config.get("sample.generation.git-accounts.file")).toPath(),
					StandardCharsets.UTF_8);

			numberAccounts = accountFileList.size();
			propertyFiles = Iterators.cycle(accountFileList);

			github = GitHubBuilder.fromPropertyFile(propertyFiles.next()).build();
		} catch (Exception e) {
			numberAccounts = 1;
			github = GitHub.connectAnonymously();
		}

		File outputDir = new File(Config.get("sample.generation.output.path"));
		
		Parallelizer p = new Parallelizer(numberAccounts, propertyFiles, numberAccounts, outputDir);
		
		//EDU
		//Repositories mit didaktischen Inhalten und Quelltexten für Vorlesungen und Tutorien
		generateEDU1(github, p, 1750);
		generateEDU2(github, p, 250);

		
		//HOMEWORK
		//Repositories mit Lösungen und Quelltexten für Hausaufgaben und Übungsblätter
		generateHW1(github, p, 2000);
		
		//DEV
		//Repositories für die Entwicklung eines Tools, einer Softwareanwendung, einer App, einer Bibliothek, einer API,
		//oder ähnliche Softwareentwicklungsprojekte 
		generateDEV1(github, p, 850);
		generateDEV2(github, p, 850);
		generateDEV3(github, p, 170);
		generateDEV4(github, p, 20);
		generateDEV5(github, p, 1000);

	
		//DOCS
		//Repositories für die Verwaltung und 
		//Speicherung von nicht-didaktischen (d.h. nicht EDU) Inhalten und Quelltexten
		generateDOCS1(github, p, 1000);
		generateDOCS2(github, p, 1000);
		
		//DATA
		//Repositories für die Speicherung von Datensätzen
		generateDATA(github, p, 2000);
		
		//WEB
		//Repositories für das Hosting persönlicher Web-Seiten oder Blogs
		generateWEB(github, p, 2000);
		
	}
}
