package tu.kn.ghrepoclassifier.generation;

import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by felix on 27.11.16.
 */
public class ReflectionUtils {
	private static Field getField(Class clazz, String fieldName)
		throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Class superClass = clazz.getSuperclass();
			if (superClass == null) {
				throw e;
			} else {
				return getField(superClass, fieldName);
			}
		}
	}

	public static GHRepositorySearchBuilder cloneSearchBuilder(GHRepositorySearchBuilder search) {
		Object myObj = search;
		Class myClass = myObj.getClass();
		Field listField = null;
		Field rootField = null;

		GHRepositorySearchBuilder searchCopy = null;
		try {
			listField = getField(myClass, "terms");
			listField.setAccessible(true);

			rootField = getField(myClass, "root");
			rootField.setAccessible(true);

			Constructor<GHRepositorySearchBuilder> constructor = myClass.getDeclaredConstructors()[0];
			constructor.setAccessible(true);


			ArrayList<String> terms = (ArrayList<String>) listField.get(myObj);
			ArrayList<String> copy = (ArrayList<String>) terms.clone();
			
			GitHub gh = (GitHub) rootField.get(myObj);
			searchCopy = (GHRepositorySearchBuilder)constructor.newInstance(gh);

			for (String term : copy) {
				searchCopy.q(term);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return searchCopy;
	}
}
