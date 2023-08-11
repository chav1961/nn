module nn.core {
	requires transitive chav1961.purelib;
	requires java.base;
	requires visrec.api;
	requires commons.lang3;
	
	exports chav1961.nn.core.network;
	exports chav1961.nn.core.data;
	exports chav1961.nn.core.interfaces;
	exports chav1961.nn.core.layers;
	exports chav1961.nn.core.train;
	exports chav1961.nn.core.utils;
}
