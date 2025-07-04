module nn.vocab {
	requires transitive chav1961.purelib;
	requires java.base;
	requires nn.api;
	requires java.xml;
	
	exports chav1961.nn.vocab.filters;
	exports chav1961.nn.vocab.interfaces;
	exports chav1961.nn.vocab.loaders;
}
