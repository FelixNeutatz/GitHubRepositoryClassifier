package tu.kn.ghrepoclassifier.samplegeneration;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by felix on 24.11.16.
 */
public class GenerateSamples {

	public static void main(String[] args) throws IOException {

		GitHub github = GitHub.connectUsingOAuth("780afd229d60f12f1ce6aad7adc8712690d20268");
		
		System.out.println(github.getMyself());

		/*
		//HOMEWORK
		PagedSearchIterable<GHRepository> homeworkRepos = github.searchRepositories().q("homework").stars("0").forks("0").list();
		
		System.out.println("homework repos size: " + homeworkRepos.getTotalCount()); //62401
		
		Iterator i = homeworkRepos.iterator();
		while(i.hasNext()) {
			System.out.println(i.next());
		}
		
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
		PagedSearchIterable<GHRepository> docsRepos = github.searchRepositories().q("documentation").list();
		
		System.out.println("docs repos size: " + docsRepos.getTotalCount()); //30549

		PagedSearchIterable<GHRepository> docsRepos1 = github.searchRepositories().q("documentation").stars(">5").list();

		System.out.println("docs repos size: " + docsRepos1.getTotalCount()); //2918
		

		PagedSearchIterable<GHRepository> docsReposDEU = github.searchRepositories().q("Dokumentation").list();

		System.out.println("docs repos size: " + docsReposDEU.getTotalCount()); //259

		PagedSearchIterable<GHRepository> docsRepos2 = github.searchRepositories().q("manual").list();

		System.out.println("docs repos size: " + docsRepos2.getTotalCount()); //9020
		
		//WEB
		PagedSearchIterable<GHRepository> webRepos = github.searchRepositories().q(".github.io").list();

		System.out.println("web repos size: " + webRepos.getTotalCount()); //560623


		//DATA
		PagedSearchIterable<GHRepository> dataRepos = github.searchRepositories().q("data").size(">100000").list();

		System.out.println("data repos size: " + dataRepos.getTotalCount()); //7065 
		
		//EDU
		PagedSearchIterable<GHRepository> eduRepos = github.searchRepositories().q("lecture").forks(">10").list();

		System.out.println("edu repos size: " + eduRepos.getTotalCount()); //290

		PagedSearchIterable<GHRepository> eduRepos1 = github.searchRepositories().q("tutorial").forks(">10").list();

		System.out.println("edu repos size: " + eduRepos1.getTotalCount()); //2081 */
		
		
		
		

	}
}
