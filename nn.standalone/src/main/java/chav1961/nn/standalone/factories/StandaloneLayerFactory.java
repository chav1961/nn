package chav1961.nn.standalone.factories;

import java.net.URI;

import chav1961.nn.api.interfaces.AnyLayer.LayerType;
import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.factories.LayerFactory;
import chav1961.nn.standalone.util.Constants;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.EnvironmentException;

public class StandaloneLayerFactory<T extends NeuralNetwork<?,?>, L extends Layer<?,?>> implements LayerFactory<T, L> {
	private static final URI	URI_TEMPLATE = URI.create(LAYER_FACTORY_SCHEMA+':'+Constants.SCHEMA_SUFFIX+":/");
	
	@Override
	public boolean canServe(URI resource) throws NullPointerException {
		if (resource == null) {
			throw new NullPointerException("Resource to check can't be null");
		}
		else {
			return URIUtils.canServeURI(resource, URI_TEMPLATE);
		}
	}

	@Override
	public LayerFactory<T, L> newInstance(URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		if (resource == null) {
			throw new NullPointerException("Resource to check can't be null");
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
	public boolean isXLayerSupported() {
		return true;
	}

	
	@Override
	public Layer<T,L> newInstance(LayerType type, Object... parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Layer<T,L> newXInstance(LayerType type, Object... parameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
