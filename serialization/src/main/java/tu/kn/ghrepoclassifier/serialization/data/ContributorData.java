package tu.kn.ghrepoclassifier.serialization.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
			// if deriving: appendSuper(super.hashCode()).
			.append(this.contributions)
			.append(this.login)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContributorData))
			return false;
		if (obj == this)
			return true;

		ContributorData rhs = (ContributorData) obj;
		return new EqualsBuilder()
			// if deriving: appendSuper(super.equals(obj)).
			.append(this.contributions, rhs.contributions)
			.append(this.login, rhs.login)
			.isEquals();
	}
}
