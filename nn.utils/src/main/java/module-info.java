module tenzor {
	requires transitive chav1961.purelib;
	requires java.base;
	requires transitive nn.api;
	
	exports chav1961.nn.utils.calc to nn.standalone, nn.opencl, nn.test;
}
