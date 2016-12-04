package tu.kn.ghrepoclassifier.serialization.data;

import org.kohsuke.github.GHRepository;

import java.io.Serializable;

/**
 * Created by felix on 03.12.16.
 */
public class ContributorData implements Serializable{
	private static final long serialVersionUID = 2L;
	
	private int contributions;
	private String login;
	
	public ContributorData(GHRepository.Contributor contributor) {
		this.contributions = contributor.getContributions();
		this.login = contributor.getLogin();
	}

	public int getContributions() {
		return contributions;
	}
}
