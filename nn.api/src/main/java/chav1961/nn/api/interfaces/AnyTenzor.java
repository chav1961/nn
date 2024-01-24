package chav1961.nn.api.interfaces;

import chav1961.nn.api.interfaces.factories.TenzorFactory;

public interface AnyTenzor {
	int getArity();
	int getSize(int dimension);
	TenzorFactory getFactory();
}
