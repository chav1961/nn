
import chav1961.nn.api.interfaces.factories.NeuralNetworkFactory;

module nn.standalone {
	requires transitive chav1961.purelib;
	requires java.base;
	requires transitive nn.api;
	requires tenzor;
	requires nn.core;
	
	exports chav1961.nn.standalone.layer to nn.test;
	exports chav1961.nn.standalone.util to nn.test;
	
	uses NeuralNetworkFactory;
	provides NeuralNetworkFactory with chav1961.nn.standalone.factories.StandaloneNeuralNetworkFactory;
}
