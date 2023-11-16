package chav1961.nn.api.interfaces;

public interface AnyTenzor {
	int getArity();
	int getSize(int dimension);
	TenzorFactory getFactory();
}
