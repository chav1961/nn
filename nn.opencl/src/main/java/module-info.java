import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.Tenzor;

module nn.opencl {
	requires transitive chav1961.purelib;
	requires java.base;
	requires nn.api;
	requires tenzor;
	
	uses Tenzor.TenzorFactory;
	provides Tenzor.TenzorFactory with chav1961.nn.opencl.util.OpenCLTenzorFactory; 

	uses Layer.LayerFactory;
	provides Layer.LayerFactory with chav1961.nn.opencl.layer.OpenCLLayerFactory; 
}
