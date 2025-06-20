package chav1961.nn.ordinal;

import java.net.URI;

import chav1961.nn.api.interfaces.Pipe;
import chav1961.nn.api.interfaces.PipeBuilder;
import chav1961.nn.api.interfaces.PipeBuilderFactory;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.EnvironmentException;

public class OrdinalPipelineFactory implements PipeBuilderFactory {
	private static final URI	RESOURCE = URI.create(PipeBuilderFactory.SCHEMA+":ordinal");

	public OrdinalPipelineFactory() {
	}
	
	@Override
	public boolean canServe(final URI resource) throws NullPointerException {
		if (resource == null) {
			throw new NullPointerException("Resource to test can't be null");
		}
		else {
			return URIUtils.canServeURI(resource, RESOURCE);
		}
	}

	@Override
	public Pipe newInstance(final URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		throw new UnsupportedOperationException("Don't use this method, use newInstance(URI,Object...) instead");
	}
	
	@Override
	public Pipe newInstance(final URI resource, final Object... parameters) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		if (resource == null) {
			throw new NullPointerException("Resource to test can't be null");
		}
		else if (!canServe(resource)) {
			throw new IllegalArgumentException("Resource ["+resource+"] can't be servied with this provider");
		}
		else if (parameters == null || parameters.length != 1 || Utils.checkArrayContent4Nulls(null) >= 0) {
			throw new IllegalArgumentException("Parameters nust contain exactly one non-null value");
		}
		else if (!(parameters[0] instanceof PipeBuilder)) {
			throw new IllegalArgumentException("Parameter must be PipeBuilder instance");
		}
		else {
			final PipeBuilder	pb = (PipeBuilder)parameters[0];
			// TODO Auto-generated method stub
			
			return null;
		}
	}

}
