package tu.kn.ghrepoclassifier.serialization.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by felix on 03.12.16.
 */
public class CommitData implements Serializable{
	private static final long serialVersionUID = 4L;
	
	private Date authoredDate;
	private Date commitDate;
	private String message;
	private int comment_count;
	private int total;
	private int additions;
	private int deletions;
	
	public CommitData(GHCommit commit) {
		try {
			this.total = commit.getLinesChanged();
		} catch (IOException e) {
			this.total = 0;
		}

		try {
			this.additions = commit.getLinesAdded();
		} catch (IOException e) {
			this.additions = 0;
		}

		try {
			this.deletions = commit.getLinesDeleted();
		} catch (IOException e) {
			this.deletions = 0;
		}

		try {
			GHCommit.ShortInfo info = commit.getCommitShortInfo();
			this.comment_count = info.getCommentCount();
			this.message = info.getMessage();
			this.authoredDate = info.getAuthoredDate();
			this.commitDate = info.getCommitDate();
		} catch (IOException e) {
			this.comment_count = 0;
			this.message = "";
			this.authoredDate = new Date();
			this.commitDate = new Date();
		}
	}

	public Date getAuthoredDate() {
		return authoredDate;
	}

	public Date getCommitDate() {
		return commitDate;
	}

	public String getMessage() {
		return message;
	}

	public int getComment_count() {
		return comment_count;
	}

	public int getLinesChanged() {
		return total;
	}

	public int getLinesAdded() {
		return additions;
	}

	public int getLinesDeleted() {
		return deletions;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
			// if deriving: appendSuper(super.hashCode()).
			.append(authoredDate)
			.append(commitDate)
			.append(message)
			.append(comment_count)
			.append(total)
			.append(additions)
			.append(deletions)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CommitData))
			return false;
		if (obj == this)
			return true;

		CommitData rhs = (CommitData) obj;
		return new EqualsBuilder()
			// if deriving: appendSuper(super.equals(obj)).
			.append(authoredDate, rhs.authoredDate)
			.append(commitDate, rhs.commitDate)
			.append(message, rhs.message)
			.append(comment_count, rhs.comment_count)
			.append(total, rhs.total)
			.append(additions, rhs.additions)
			.append(deletions, rhs.deletions)
			.isEquals();
	}
}
