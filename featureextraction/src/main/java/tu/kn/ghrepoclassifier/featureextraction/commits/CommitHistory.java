package tu.kn.ghrepoclassifier.featureextraction.commits;

import tu.kn.ghrepoclassifier.serialization.data.CommitData;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by felix on 27.11.16.
 */
public class CommitHistory {

	private Stats timeBetweenCommits;
	private Stats changedLines;
	private Stats addedLines;
	private Stats deletedLines;
	private long [] weekDayCount;
	private long projectTotalTime;
	private long n;
	private long startTime;
	
	private RepoData repo;
	
	public CommitHistory(RepoData repo) {
		this.repo = repo;
		timeBetweenCommits = new Stats("TimeBetweenCommits");
		changedLines = new Stats("ChangedLines");
		addedLines = new Stats("AddedLines");
		deletedLines = new Stats("DeletedLines");
		weekDayCount = new long [7];
		projectTotalTime = 0;
		n = 0;
		startTime = 0L;
		
		calculate();
	}

	public void calculate() {
		Date firstCommitDate = null;
		long startTime = 0L;

		Date lastCommitDate = null;

		try {
			for (CommitData commit: repo.getCommits()) {
				//gather statistics about the lines added by each commit
				long added = commit.getLinesAdded();
				addedLines.add(added);

				//gather statistics about the lines deleted by each commit
				long deleted = commit.getLinesDeleted();
				deletedLines.add(deleted);

				//gather statistics about the lines changed by each commit
				long changed = commit.getLinesChanged();
				changedLines.add(changed);

				//gather statistics about the time between succeeding commits
				Date commitDate = commit.getCommitDate();
				if (n == 0) {
					firstCommitDate = commitDate;
				} else {
					long diff = lastCommitDate.getTime() - commitDate.getTime();
					long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

					timeBetweenCommits.add(minutes);
				}
				lastCommitDate = commitDate;

				//log on which week day the commit happened
				Calendar c = Calendar.getInstance();
				c.setTime(commitDate);
				int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

				weekDayCount[dayOfWeek - 1]++;

				//total number of commits
				n++;
			}

			//get total time of the project in days
			long diff = 0;
			if (firstCommitDate != null && lastCommitDate != null) {
				startTime = firstCommitDate.getTime();
				diff = startTime - lastCommitDate.getTime();
			}
			projectTotalTime = TimeUnit.MILLISECONDS.toDays(diff);

		} catch (java.lang.Error e) {

		}
	}
	
	public String summarizeCommitHistory() {
		String l = "";

		l += projectTotalTime;
		l += "," + startTime;
		l += "," + addedLines.toString();
		l += "," + changedLines.toString();
		l += "," + deletedLines.toString();
		l += "," + timeBetweenCommits.toString();

		for (int d = 0; d < 7; d++) {
			if (n == 0) {
				l += "," + 0;
			} else {
				l += "," + ((double) weekDayCount[d] / n);
			}
		}

		return l;
	}

	public static String getFeatureLabels() {
		String l = "";
		l += "projectTotalTime\n";
		l += "startTime\n";
		l += new Stats("AddedLines").getFeatureLabels();
		l += new Stats("ChangedLines").getFeatureLabels();
		l += new Stats("DeletedLines").getFeatureLabels();
		l += new Stats("TimeBetweenCommits").getFeatureLabels();

		for (int d = 0; d < 7; d++) {
			l += "weekDayCount" + d + "\n";
		}
		
		return l;
	}
}
