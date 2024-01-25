package chav1961.nn.standalone.factories;

import java.net.URI;

import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.XNeuralNetwork;
import chav1961.nn.api.interfaces.factories.NeuralNetworkFactory;
import chav1961.nn.standalone.StandaloneNetworkImpl;
import chav1961.nn.standalone.util.Constants;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.EnvironmentException;

public class StandaloneNeuralNetworkFactory implements NeuralNetworkFactory {
	private static final URI	URI_TEMPLATE = URI.create(NETWORD_FACTORY_SCHEMA+':'+Constants.SCHEMA_SUFFIX+":/");
	
	@Override
	public boolean canServe(final URI resource) throws NullPointerException {
		if (resource == null) {
			throw new NullPointerException("Resource to test can't be null");
		}
		else {
			return URIUtils.canServeURI(resource, URI_TEMPLATE);
		}
	}

	@Override
	public NeuralNetworkFactory newInstance(final URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		if (resource == null) {
			throw new NullPointerException("Resource to test can't be null");
		}
		else if (!canServe(resource)) {
			throw new IllegalArgumentException("Resource URI ["+resource+"] can't be served by this factory");
		}
		else {
			return this;
		}
	}

	@Override
	public URI getDefaultLayerType() {
		return URI_TEMPLATE;
	}

	@Override
	public NeuralNetwork<?,?> newInstance(final Object... parameters) {
		return new StandaloneNetworkImpl();
	}

	@Override
	public boolean isXNetworkSupported() {
		return true;
	}

	@Override
	public XNeuralNetwork newXInstance(Object... parameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
