package tu.kn.ghrepoclassifier.featureextraction.features.text;

import org.jsoup.Jsoup;
import tu.kn.ghrepoclassifier.serialization.data.CommitData;
import tu.kn.ghrepoclassifier.serialization.data.ContentData;
import tu.kn.ghrepoclassifier.serialization.data.IssueData;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

/**
 * Created by felix on 06.12.16.
 */
public class ExtractText {
	
	public static String markupToText(String markup) {
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

		ContentData readme = repo.getReadme();
		if (readme != null) {
			l += "\n" + markupToText(readme.getContent());
		}
		
		for (CommitData commit: repo.getCommits()) {
			l += "\n" + commit.getMessage();
			//System.out.println("commit: " + commit.getMessage());
		}
		for (IssueData issue: repo.listIssues()) {
			l += "\n" + markupToText(issue.getBody());
		}

		ContentData indexHtml = repo.getIndexHTML();
		if (indexHtml != null) {
			String indexHtmlContent = indexHtml.getContent();
			l += "\n" + Jsoup.parse(indexHtmlContent, "ISO-8859-1").text();
		}

		l = l.replace("\"", "");
		
		System.out.println(l);
		
		return "\"" + l + "\"";
	}
}
