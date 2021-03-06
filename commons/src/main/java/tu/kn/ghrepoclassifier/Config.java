package tu.kn.ghrepoclassifier;

import java.util.Properties;

public class Config {

	private static Properties props;

	private Config() {
	}

	public static String get(String key) {
		try {
			if (props == null) {
				props = new Properties();
				props.load(Config.class.getResourceAsStream("/conf/conf.properties"));
				//validateConfig();
			}
			return props.getProperty(key);
		} catch (Exception e) {
			props = null;
			if (key.equals("sample.generation.content.output.path")) {
				return "/tmp/GitHubClassifier/content";
			}
			e.printStackTrace();
			return null;
		}
	}

	private static void validateConfig() {

		if (nullOrEmpty("sample.generation.output.path")) {
			throw new IllegalStateException("[sample.generation.output.path] in conf.properties must point to the " +
				"folder " +
				"that contains the downloaded repo information (.bin files) within the corresponding folders for each " +
				"class");
		}
		if (nullOrEmpty("sample.generation.git-accounts.file")) {
			throw new IllegalStateException("[sample.generation.git-accounts.file] in conf.properties must point to the " +
				"file " +
				"that contains the paths to all files which contain the authentication handle of various Github users");
		}
		if (nullOrEmpty("feature.extraction.output.path")) {
			throw new IllegalStateException("[feature.extraction.output.path] in conf.properties must point to the " +
				"folder " +
				"that contains one .csv file of samples for every class");
		}
		if (nullOrEmpty("attachmentA.download.output.path")) {
			throw new IllegalStateException("[attachmentA.download.output.path] in conf.properties must point to the " +
				"folder " +
				"that contains the downloaded repo information (.bin files) for all repos in attachment A");
		}
		if (nullOrEmpty("attachmentA.repos.file")) {
			throw new IllegalStateException("[attachmentA.repos.file] in conf.properties must point to the " +
				"file " +
				"that contains all labeled repos of attachment A");
		}
		if (nullOrEmpty("attachmentA.feature.extraction.output.path")) {
			throw new IllegalStateException("[attachmentA.feature.extraction.output.path] in conf.properties must point to the " +
				"folder " +
				"that contains one .csv file of samples of attachment A for every class");
		}
		if (nullOrEmpty("attachmentB.download.output.path")) {
			throw new IllegalStateException("[attachmentB.download.output.path] in conf.properties must point to the " +
				"folder " +
				"that contains the downloaded repo information (.bin files) for all repos in attachment B");
		}
		if (nullOrEmpty("attachmentB.repos.file")) {
			throw new IllegalStateException("[attachmentB.repos.file] in conf.properties must point to the " +
				"file " +
				"that contains all labeled repos of attachment B");
		}
	}

	private static boolean nullOrEmpty(String key) {
		return props.getProperty(key) == null || "".equals(props.getProperty(key));
	}

	private static boolean endsWith(String key, String pattern) {
		return props.getProperty(key).endsWith(pattern);
	}
}
