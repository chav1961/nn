module nn.ordinal {
	requires transitive chav1961.purelib;
	requires java.base;
	requires nn.api;
	
	uses chav1961.nn.api.interfaces.PipeBuilderFactory;
	provides chav1961.nn.api.interfaces.PipeBuilderFactory with 
		chav1961.nn.ordinal.OrdinalPipelineFactory;
}
