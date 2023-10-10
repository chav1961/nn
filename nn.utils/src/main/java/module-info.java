module nn.utils {
	requires transitive chav1961.purelib;
	requires java.base;
	requires nn.api;
	
	exports chav1961.nn.utils.calc to nn.standalone;
}
