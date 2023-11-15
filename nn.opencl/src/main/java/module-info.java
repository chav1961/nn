import chav1961.nn.api.interfaces.LayerFactory;
import chav1961.nn.api.interfaces.TenzorFactory;

module nn.opencl {
	requires transitive chav1961.purelib;
	requires java.base;
	requires nn.api;
	requires tenzor;
	
	uses TenzorFactory;
	provides TenzorFactory with chav1961.nn.opencl.util.OpenCLTenzorFactory; 

	uses LayerFactory;
	provides LayerFactory with chav1961.nn.opencl.layer.OpenCLLayerFactory; 
}
