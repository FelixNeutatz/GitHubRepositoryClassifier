package tu.kn.ghrepoclassifier.generation;

import org.apache.commons.lang.mutable.MutableInt;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.PagedSearchIterable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static tu.kn.ghrepoclassifier.generation.ReflectionUtils.cloneSearchBuilder;

/**
 * Created by felix on 27.11.16.
 */
public class SplitSearch {
	public static void splitRecursive(final GHRepositorySearchBuilder search,
									  Date startDate,
									  Date endDate,
									  Queue<PagedSearchIterable<GHRepository>> resultSearches,
									  int maxRequestPerSearch,
									  MutableInt currentNumberRequests,
									  int totalRequests) {
		
		if (currentNumberRequests.intValue() < totalRequests) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String range = "\"" + sdf.format(startDate) + " .. " + sdf.format(endDate) + "\"";

			GHRepositorySearchBuilder copy = cloneSearchBuilder(search);

			//first only query with pagesize one becaus we only need the total count
			PagedSearchIterable<GHRepository> result = copy.created(range).list().withPageSize(1);

			System.out.println(range + " : " + result.getTotalCount());

			if (result.getTotalCount() < maxRequestPerSearch) {
				//fire the search query again, but this time with maximum page size
				GHRepositorySearchBuilder copy2 = cloneSearchBuilder(search);
				PagedSearchIterable<GHRepository> result2 = copy2.created(range).list().withPageSize(100);
				resultSearches.add(result2);
				currentNumberRequests.add(result.getTotalCount());
			} else {
				Date middleDate = new Date((long) (startDate.getTime() +
					((endDate.getTime() - startDate.getTime()) * 0.5)));

				splitRecursive(search, startDate, middleDate, resultSearches,
					maxRequestPerSearch, currentNumberRequests, totalRequests);

				long timeadj = 24 * 60 * 60 * 1000;
				Date middleDatePlusDay = new Date(middleDate.getTime() + timeadj);

				splitRecursive(search, middleDatePlusDay, endDate, resultSearches,
					maxRequestPerSearch, currentNumberRequests, totalRequests);
			}
		}
	}

	public static Queue<PagedSearchIterable<GHRepository>> splitSearch(GHRepositorySearchBuilder search,
																	   ConcurrentLinkedQueue rQueue,
																	   int maxRequestPerSearch,
																	   int totalRequests) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = new GregorianCalendar(2008, 2 - 1, 8); //founding date of Github 2008-02-08

		Date startDate = c.getTime();
		Date endDate = Calendar.getInstance().getTime();

		splitRecursive(search, startDate, endDate, rQueue, maxRequestPerSearch, new MutableInt(0), totalRequests);

		return rQueue;
	}

	/*
	The GitHub API returns maximum 1000 results for each search.
	This means we have to split up the search in many subsearches.
	This is achieved by splitting it up into multiple ranges.
	Eg.: search1: created: January - February, search2: created: March - April, ...
	The splitting is done in a binary search fashion.
	
	We return a concurrent queue of subsearches.
	 */
	public static Queue<PagedSearchIterable<GHRepository>> splitSearch(GHRepositorySearchBuilder search,
																	   ConcurrentLinkedQueue rQueue,
																	   int totalRequests) {
		return splitSearch(search,rQueue,1000, totalRequests);
	}
}
