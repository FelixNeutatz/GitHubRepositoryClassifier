package tu.kn.ghrepoclassifier.serialization.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kohsuke.github.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * Created by felix on 03.12.16.
 */
public class RepoData implements Serializable{

	private static final long serialVersionUID = 1L;
	//repo
	private String description, homepage, name, full_name;
	private URL html_url;    // this is the UI
	private String git_url, ssh_url, clone_url, svn_url, mirror_url;
	private boolean has_issues, has_wiki, fork, has_downloads, has_pages;
	private boolean _private;
	private int forks_count, stargazers_count, watchers_count, size, open_issues_count, subscribers_count;
	private Date pushed_at;
	private String default_branch,language;
	private int id;
	
	
	private Set<String> branches;
	
	
	private List<String> releases;
	private Map<String,Long> languages;
	private String license;
	private List<CommitData> commits;
	private List<ContributorData> contributors;
	private ContentData readme;
	
	private ContentData indexHTML;
	
	private List<IssueData> issues;
	
	private String ourClassification;

	public RepoData(GHRepository repo) {
		this(repo,"");
	}
	
	public RepoData(GHRepository repo, String ourClassification) {
		this.id = repo.getId();
		this.description = repo.getDescription();
		this.homepage = repo.getHomepage();
		this.name = repo.getName();
		this.full_name = repo.getFullName();
		this.html_url = repo.getHtmlUrl();
		this.git_url = repo.getGitTransportUrl();
		this.ssh_url = repo.getSshUrl();
		this.clone_url = repo.gitHttpTransportUrl();
		this.svn_url = repo.getSvnUrl();
		this.mirror_url = repo.getMirrorUrl();
		this.has_issues = repo.hasIssues();
		this.has_wiki = repo.hasWiki();
		this.fork = repo.isFork();
		this.has_downloads = repo.hasDownloads();
		this.has_pages = repo.hasPages();
		this._private = repo.isPrivate();
		this.forks_count = repo.getForks();
		this.stargazers_count = repo.getStargazersCount();
		this.watchers_count = repo.getWatchers();
		this.size = repo.getSize();
		this.open_issues_count = repo.getOpenIssueCount();
		this.subscribers_count = repo.getSubscribersCount();
		this.pushed_at = repo.getPushedAt();
		this.default_branch = repo.getDefaultBranch();
		this.language = repo.getLanguage();

		try {
			this.branches = new HashSet<>();
			branches.addAll(repo.getBranches().keySet());
		} catch (IOException e) {
			this.branches = new HashSet<>();
			e.printStackTrace();
		}

		this.releases = new ArrayList<>();
		try {
			for (GHRelease release : repo.listReleases().withPageSize(100)) {
				this.releases.add(release.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.languages = repo.listLanguages();
		} catch (IOException e) {
			this.languages = new HashMap<>();
			e.printStackTrace();
		}

		try {
			GHLicense l = repo.getLicense();
			if (l != null) {
				this.license = l.getName();
			} else {
				this.license = "";
			}
		} catch (Exception e) {
			this.license = "";
			e.printStackTrace();
		}

		this.commits = new ArrayList<>();
		try {
			for (GHCommit commit : repo.listCommits().withPageSize(100)){
				this.commits.add (new CommitData(commit));
			}
		} catch (Error e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		this.contributors = new ArrayList<>();
		try {
			for (GHRepository.Contributor contributor : repo.listContributors().withPageSize(100)){
				this.contributors.add(new ContributorData(contributor));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			this.readme = new ContentData(repo.getReadme());
		} catch (FileNotFoundException e) {
			this.readme = new ContentData();
		} catch (Exception e1) {
			this.readme = new ContentData();
			e1.printStackTrace();
		}

		try {
			this.indexHTML = new ContentData(repo.getFileContent("index.html"));
		} catch (FileNotFoundException e) {
			this.indexHTML = new ContentData();
		} catch (Exception e1) {
			this.indexHTML = new ContentData();
			e1.printStackTrace();
		}

		this.issues = new ArrayList<>();
		try {
			for (GHIssue issue : repo.listIssues(GHIssueState.ALL).withPageSize(100)){
				this.issues.add (new IssueData(issue));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.ourClassification = ourClassification;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
			// if deriving: appendSuper(super.hashCode()).
			.append(this.id)
			.append(this.description)
			.append(this.homepage)
			.append(this.name)
			.append(this.full_name)
			.append(this.html_url)
			.append(this.git_url)
			.append(this.ssh_url)
			.append(this.clone_url)
			.append(this.svn_url)
			.append(this.mirror_url)
			.append(this.has_issues)
			.append(this.has_wiki)
			.append(this.fork)
			.append(this.has_downloads)
			.append(this.has_pages)
			.append(this._private)
			.append(this.forks_count)
			.append(this.stargazers_count)
			.append(this.watchers_count)
			.append(this.size)
			.append(this.open_issues_count)
			.append(this.subscribers_count)
			.append(this.pushed_at)
			.append(this.default_branch)
			.append(this.language)
			.append(this.branches)
			.append(this.releases)
			.append(this.languages)
			.append(this.license)
			.append(this.commits)
			.append(this.contributors)
			.append(this.readme)
			.append(this.indexHTML)
			.append(this.issues)
			.append(this.ourClassification)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RepoData))
			return false;
		if (obj == this)
			return true;

		RepoData rhs = (RepoData) obj;
		return new EqualsBuilder()
			// if deriving: appendSuper(super.equals(obj)).
			.append(this.id, rhs.id)
			.append(this.description, rhs.description)
			.append(this.homepage, rhs.homepage)
			.append(this.name, rhs.name)
			.append(this.full_name, rhs.full_name)
			.append(this.html_url, rhs.html_url)
			.append(this.git_url, rhs.git_url)
			.append(this.ssh_url, rhs.ssh_url)
			.append(this.clone_url, rhs.clone_url)
			.append(this.svn_url, rhs.svn_url)
			.append(this.mirror_url, rhs.mirror_url)
			.append(this.has_issues, rhs.has_issues)
			.append(this.has_wiki, rhs.has_wiki)
			.append(this.fork, rhs.fork)
			.append(this.has_downloads, rhs.has_downloads)
			.append(this.has_pages, rhs.has_pages)
			.append(this._private, rhs._private)
			.append(this.forks_count, rhs.forks_count)
			.append(this.stargazers_count, rhs.stargazers_count)
			.append(this.watchers_count, rhs.watchers_count)
			.append(this.size, rhs.size)
			.append(this.open_issues_count, rhs.open_issues_count)
			.append(this.subscribers_count, rhs.subscribers_count)
			.append(this.pushed_at, rhs.pushed_at)
			.append(this.default_branch, rhs.default_branch)
			.append(this.language, rhs.language)
			.append(this.branches, rhs.branches)
			.append(this.releases, rhs.releases)
			.append(this.languages, rhs.languages)
			.append(this.license, rhs.license)
			.append(this.commits, rhs.commits)
			.append(this.contributors, rhs.contributors)
			.append(this.readme, rhs.readme)
			.append(this.indexHTML, rhs.indexHTML)
			.append(this.issues, rhs.issues)
			.append(this.ourClassification, rhs.ourClassification)
			.isEquals();
	}

	public String getDescription() {
		return description;
	}

	public String getHomepage() {
		return homepage;
	}

	public String getName() {
		return name;
	}

	public String getFull_name() {
		return full_name;
	}

	public URL getHtmlUrl() {
		return html_url;
	}

	public String getGit_url() {
		return git_url;
	}

	public String getSsh_url() {
		return ssh_url;
	}

	public String getClone_url() {
		return clone_url;
	}

	public String getSvn_url() {
		return svn_url;
	}

	public String getMirror_url() {
		return mirror_url;
	}

	public boolean isHas_issues() {
		return has_issues;
	}

	public boolean isHas_wiki() {
		return has_wiki;
	}

	public boolean isFork() {
		return fork;
	}

	public boolean hasDownloads() {
		return has_downloads;
	}

	public boolean isHas_pages() {
		return has_pages;
	}

	public boolean is_private() {
		return _private;
	}

	public int getForks() {
		return forks_count;
	}

	public int getStargazersCount() {
		return stargazers_count;
	}

	public int getWatchers() {
		return watchers_count;
	}

	public int getSize() {
		return size;
	}

	public int getOpenIssueCount() {
		return open_issues_count;
	}

	public int getSubscribersCount() {
		return subscribers_count;
	}

	public Date getPushed_at() {
		return pushed_at;
	}

	public String getDefault_branch() {
		return default_branch;
	}

	public String getLanguage() {
		return language;
	}

	public int getId() {
		return id;
	}

	public Set<String> getBranches() {
		return branches;
	}

	public List<String> listReleases() {
		return releases;
	}

	public Map<String, Long> listLanguages() {
		return languages;
	}

	public String getLicense() {
		return license;
	}

	public List<CommitData> getCommits() {
		return commits;
	}

	public List<ContributorData> listContributors() {
		return contributors;
	}

	public ContentData getReadme() {
		return readme;
	}

	public ContentData getIndexHTML() {
		return indexHTML;
	}

	public String getOurClassification() {
		return ourClassification;
	}

	public List<IssueData> listIssues() {
		return issues;
	}
}
