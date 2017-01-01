package tu.kn.ghrepoclassifier.serialization.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kohsuke.github.GHContent;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by felix on 03.12.16.
 */
public class ContentData implements Serializable{
	private static final long serialVersionUID = 3L;
	
	private String type;
	private String encoding;
	private long size;
	private String sha;
	private String name;
	private String path;
	private String content;
	private String url; // this is the API url
	private String git_url;    // this is the Blob url
	private String html_url;    // this is the UI
	private String download_url;

	public ContentData() {
		this.type = "";
		this.encoding = "";
		this.size = 0;
		this.sha = "";
		this.name = "";
		this.path = "";
		this.content = "";
		this.url = "";
		this.git_url = "";
		this.html_url = "";
		this.download_url = "";
	}
	
	public ContentData(GHContent content) {
		this.type = content.getType();
		this.encoding = content.getEncoding();
		this.size = content.getSize();
		this.sha = content.getSha();
		this.name = content.getName();
		this.path = content.getPath();
		
		try {
			this.content = content.getContent();
		} catch (Exception e) {
			this.content = "";
		}
		
		this.url = content.getUrl();
		this.git_url = content.getGitUrl();
		this.html_url = content.getHtmlUrl();
		
		try {
			this.download_url = content.getDownloadUrl();
		} catch (Exception e) {
			this.download_url = "";
		}
	}

	public String getType() {
		return type;
	}

	public String getEncoding() {
		return encoding;
	}

	public long getSize() {
		return size;
	}

	public String getSha() {
		return sha;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getContent() {
		return content;
	}

	public String getUrl() {
		return url;
	}

	public String getGit_url() {
		return git_url;
	}

	public String getHtml_url() {
		return html_url;
	}

	public String getDownload_url() {
		return download_url;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
			// if deriving: appendSuper(super.hashCode()).
			.append(type)
			.append(encoding)
			.append(size)
			.append(sha)
			.append(name)
			.append(path)
			.append(content)
			.append(url)
			.append(git_url)
			.append(html_url)
			.append(download_url)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContentData))
			return false;
		if (obj == this)
			return true;

		ContentData rhs = (ContentData) obj;
		return new EqualsBuilder()
			// if deriving: appendSuper(super.equals(obj)).
			.append(type, rhs.type)
			.append(encoding, rhs.encoding)
			.append(size, rhs.size)
			.append(sha, rhs.sha)
			.append(name, rhs.name)
			.append(path, rhs.path)
			.append(content, rhs.content)
			.append(url, rhs.url)
			.append(git_url, rhs.git_url)
			.append(html_url, rhs.html_url)
			.append(download_url, rhs.download_url)
			.isEquals();
	}
}
