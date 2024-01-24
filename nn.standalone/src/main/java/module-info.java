import chav1961.nn.api.interfaces.factories.LayerFactory;
import chav1961.nn.api.interfaces.factories.TenzorFactory;

module nn.standalone {
	requires transitive chav1961.purelib;
	requires java.base;
	requires transitive nn.api;
	requires tenzor;
	
	exports chav1961.nn.standalone.layer to nn.test;
	exports chav1961.nn.standalone.util to nn.test;
	
	uses TenzorFactory;
	provides TenzorFactory with chav1961.nn.standalone.util.StandaloneTenzorFactory; 

	uses LayerFactory;
	provides LayerFactory with chav1961.nn.standalone.layer.StandaloneLayerFactory; 
}
