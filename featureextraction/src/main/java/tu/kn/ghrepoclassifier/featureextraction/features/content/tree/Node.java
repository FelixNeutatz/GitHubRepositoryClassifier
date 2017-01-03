package tu.kn.ghrepoclassifier.featureextraction.features.content.tree;

/**
 * Created by felix on 02.01.17.
 */
public abstract class Node {
	protected String name;
	protected int level;
	
	public Node(String name, int level) {
		this.name = name;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public abstract long getSize();
}
