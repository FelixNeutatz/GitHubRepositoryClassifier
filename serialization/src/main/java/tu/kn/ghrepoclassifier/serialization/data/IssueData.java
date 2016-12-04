package tu.kn.ghrepoclassifier.serialization.data;

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
}
