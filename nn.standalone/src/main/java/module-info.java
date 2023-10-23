import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.Tenzor;

module nn.standalone {
	requires transitive chav1961.purelib;
	requires java.base;
	requires transitive nn.api;
	requires tenzor;
	
	exports chav1961.nn.standalone.layer to nn.test;
	exports chav1961.nn.standalone.util to nn.test;
	
	uses Tenzor.TenzorFactory;
	provides Tenzor.TenzorFactory with chav1961.nn.standalone.util.StandaloneTenzorFactory; 

	uses Layer.LayerFactory;
	provides Layer.LayerFactory with chav1961.nn.standalone.layer.StandaloneLayerFactory; 
}
