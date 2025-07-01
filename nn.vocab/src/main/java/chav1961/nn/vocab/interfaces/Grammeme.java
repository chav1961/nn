package chav1961.nn.vocab.interfaces;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Grammeme {
	private final int			index;
	private final Supplier<Grammeme>	parent;
	private final Grammeme[]	children;
	private final String		alias;
	private final String		description;
	private final String		name;
	
	public Grammeme(final int index, final Supplier<Grammeme> parent, final String name, final String alias, final String description, final Grammeme... children) {
		this.index = index;
		this.parent = parent;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.children = children;
	}

	public int getIndex() {
		return index;
	}
	
	public Grammeme getParent() {
		return parent == null ? null : parent.get();
	}

	public String getAlias() {
		return alias;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public Grammeme[] getChildren() {
		return children;
	}

	public void walk(final Consumer<Grammeme> callback) {
		if (callback == null) {
			throw new NullPointerException("Callback can't be null");
		}
		else {
			callback.accept(this);
			for(Grammeme item : getChildren()) {
				item.walk(callback);
			}
		}
	}
	
	public <T> T seek(final Function<Grammeme, T> callback) {
		if (callback == null) {
			throw new NullPointerException("Callback can't be null");
		}
		else {
			return seekInternal(callback);
		}
	}
	
	@Override
	public String toString() {
		if (parent != null) {
			return "Grammeme [parent=" + parent.get().name + ", name=" + name + ", alias=" + alias + ", description=" + description + "]";
		}
		else {
			return "Grammeme [name=" + name + ", alias=" + alias + ", description=" + description + ", children=" + Arrays.toString(children) + "]";
		}
	}

	private <T> T seekInternal(final Function<Grammeme, T> callback) {
		T	val = callback.apply(this);
		
		if (val != null) {
			return val;
		}
		else {
			for(Grammeme item : children) {
				if ((val = item.seekInternal(callback)) != null) {
					return val;
				}
			}
			return null;
		}
	}

}