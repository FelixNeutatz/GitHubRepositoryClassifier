package tu.kn.ghrepoclassifier.featureextraction.features;

import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.IOException;

/**
 * Created by kevin on 28.12.16.
 */
public interface FeatureExtractor {

	String extractFeatures(RepoData repo) throws Exception;

}
