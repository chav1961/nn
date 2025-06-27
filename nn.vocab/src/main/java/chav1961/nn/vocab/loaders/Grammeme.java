package chav1961.nn.vocab.loaders;

import java.util.Arrays;

public class Grammeme {
	public final Grammeme	parent;
	public final String		alias;
	public final String		description;
	public final String		name;
	public final Grammeme[]	children;
	
	public Grammeme(final Grammeme parent, final String name, final String alias, final String description, final Grammeme... children) {
		this.parent = parent;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.children = children;
	}

	@Override
	public String toString() {
		if (parent != null) {
			return "Grammeme [parent=" + parent.name + ", name=" + name + ", alias=" + alias + ", description=" + description + "]";
		}
		else {
			return "Grammeme [name=" + name + ", alias=" + alias + ", description=" + description + ", children=" + Arrays.toString(children) + "]";
		}
	}
}