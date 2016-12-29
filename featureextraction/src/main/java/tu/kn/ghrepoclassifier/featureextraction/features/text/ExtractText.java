package tu.kn.ghrepoclassifier.featureextraction.features.text;

import org.jsoup.Jsoup;
import org.markdown4j.Markdown4jProcessor;
import tu.kn.ghrepoclassifier.serialization.data.CommitData;
import tu.kn.ghrepoclassifier.serialization.data.ContentData;
import tu.kn.ghrepoclassifier.serialization.data.IssueData;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 06.12.16.
 */
public class ExtractText {
	
	public static String markupToText(String markup) {
		String html = null;
		try {
			html = new Markdown4jProcessor().process(markup);
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("Error converting markup to text");
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
	
	public static String extractFeatures(RepoData repo) throws IOException {
		String l = "";

		l += repo.getName();
		l += " " + repo.getDescription();

		ContentData readme = repo.getReadme();
		if (readme != null) {
			l += " " + markupToText(readme.getContent());
		}
		
		for (CommitData commit: repo.getCommits()) {
			l += " " + commit.getMessage();
		}
		for (IssueData issue: repo.listIssues()) {
			l += " " + markupToText(issue.getBody());
		}

		ContentData indexHtml = repo.getIndexHTML();
		if (indexHtml != null) {
			String indexHtmlContent = indexHtml.getContent();
			l += " " + Jsoup.parse(indexHtmlContent, "ISO-8859-1").text();
		}

		l = l.replace("\'", "");
		l = l.replace("\n", " ");
		l = l.replace("\r", " ");
		
		l = l.toLowerCase();
		
		return '\'' + l + '\'';
	}

	public static List<String> getFeatureLabels() {
		List<String> features = new ArrayList();
		features.add("text");
		return features;
	}
}
