package tu.kn.ghrepoclassifier.serialization.data;

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

	private int total,additions,deletions;
	
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
}
