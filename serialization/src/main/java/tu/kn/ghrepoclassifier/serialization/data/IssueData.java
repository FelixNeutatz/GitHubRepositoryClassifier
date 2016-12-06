package tu.kn.ghrepoclassifier.serialization.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kohsuke.github.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by felix on 04.12.16.
 */
public class IssueData implements Serializable{
	private static final long serialVersionUID = 5L;

	private int id;
	private String state;
	private int number;
	private Date closed_at;
	private int comments;
	private String body;
	
	public IssueData(GHIssue issue) throws Exception {
		this.id = issue.getId();

		switch (issue.getState()) {
			case ALL:		this.state = "all";
							break;
			case CLOSED:	this.state = "closed";
							break;
			case OPEN:		this.state = "open";
							break;
			default:		throw new Exception("unknown state"); 
		}
		
		this.number = issue.getNumber();
		this.closed_at = issue.getClosedAt();
		this.comments = issue.getCommentsCount();
		this.body = issue.getBody();
	}

	public int getId() {
		return id;
	}

	public String getState() {
		return state;
	}

	public int getNumber() {
		return number;
	}

	public Date getClosed_at() {
		return closed_at;
	}

	public int getComments() {
		return comments;
	}

	public String getBody() {
		return body;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
			// if deriving: appendSuper(super.hashCode()).
			.append(this.id)
			.append(this.state)
			.append(this.number)
			.append(this.closed_at)
			.append(this.comments)
			.append(this.body)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IssueData))
			return false;
		if (obj == this)
			return true;

		IssueData rhs = (IssueData) obj;
		return new EqualsBuilder()
			// if deriving: appendSuper(super.equals(obj)).
			.append(this.id, rhs.id)
			.append(this.state, rhs.state)
			.append(this.number, rhs.number)
			.append(this.closed_at, rhs.closed_at)
			.append(this.comments, rhs.comments)
			.append(this.body, rhs.body)
			.isEquals();
	}
}
