package tu.kn.ghrepoclassifier.generation;

/**
 * Created by felix on 15.12.16.
 */
public class Repo {
	private String url;
	private String label;

	public Repo(String url, String label) {
		this.url = url;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}
	
	public String getFullName() {
		String [] splits = url.split("/");
		return splits[splits.length - 2] + "/" + splits[splits.length - 1];
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
