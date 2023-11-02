module nn.core {
	requires transitive chav1961.purelib;
	requires java.base;
	requires transitive nn.api;
	requires java.desktop;
	
	exports chav1961.nn.core.network;
}
