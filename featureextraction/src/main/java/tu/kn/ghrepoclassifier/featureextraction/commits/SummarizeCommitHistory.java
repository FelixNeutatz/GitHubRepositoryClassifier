package tu.kn.ghrepoclassifier.featureextraction.commits;

import tu.kn.ghrepoclassifier.serialization.data.CommitData;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by felix on 27.11.16.
 */
public class SummarizeCommitHistory {

	public static String summarizeCommitHistory(RepoData repo) {
		return summarizeCommitHistory(repo, Integer.MAX_VALUE);
	}
	
	public static String summarizeCommitHistory(RepoData repo, int commitNumber) {
		String l = "";

		Stats timeBetweenCommits = new Stats("TimeBetweenCommits");
		Stats changedLines = new Stats("ChangedLines");
		Stats addedLines = new Stats("AddedLines");
		Stats deletedLines = new Stats("DeletedLines");

		long [] weekDayCount = new long [7];

		Date firstCommitDate = null;

		Date lastCommitDate = null;

		long projectTotalTime = 0;

		long n = 0;

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

				if (n >= commitNumber) {
					break;
				}
			}

			//get total time of the project in days
			long diff = firstCommitDate.getTime() - lastCommitDate.getTime();
			projectTotalTime = TimeUnit.MILLISECONDS.toDays(diff);

		} catch (java.lang.Error e) {

		}
		System.err.println();

		l += projectTotalTime;
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
}
