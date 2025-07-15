module nn.vocab {
	requires transitive chav1961.purelib;
	requires transitive nn.api;
	requires java.base;
	requires java.xml;
	
	exports chav1961.nn.vocab.filters;
	exports chav1961.nn.vocab.interfaces;
	exports chav1961.nn.vocab.loaders;
}
