package tu.kn.ghrepoclassifier.featureextraction;

import org.jsoup.Jsoup;
import tu.kn.ghrepoclassifier.serialization.data.CommitData;
import tu.kn.ghrepoclassifier.serialization.data.IssueData;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

/**
 * Created by felix on 06.12.16.
 */
public class ExtractText {
	
	public static String markdownToText(String markup) {
		String html = null;
		try {
			html = new Markdown4jProcessor().process(markup);
		} catch (IOException e) {
			e.printStackTrace();
			return markup;
		}

		String text = null;
		try {
			text = Jsoup.parse(html, "ISO-8859-1").text();
		} catch (Exception e1) {
			return markup;
		}
		return text;
	}
	
	public static String extractText(RepoData repo) throws IOException {
		String l = "";

		l += repo.getName();
		l += "\n" + repo.getDescription();
		l += "\n" + markdownToText(repo.getReadme().getContent());
		
		for (CommitData commit: repo.getCommits()) {
			l += "\n" + commit.getMessage();
			//System.out.println("commit: " + commit.getMessage());
		}
		for (IssueData issue: repo.listIssues()) {
			l += "\n" + markdownToText(issue.getBody());
		}

		l = l.replace("\"", "");
		
		return "\"" + l + "\"";
	}
}
