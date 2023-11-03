package chav1961.nn.standalone.layer;

import java.net.URI;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.Layer.LayerFactory;
import chav1961.nn.api.interfaces.Layer.LayerType;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.EnvironmentException;

public class StandaloneLayerFactory implements Layer.LayerFactory {
	public static final URI	FACTORY_URI = URI.create(Layer.LayerFactory.LAYER_FACTORY_SCHEMA+":standard:/");

	@Override
	public boolean canServe(final URI resource) throws NullPointerException {
		if (resource == null) {
			throw new NullPointerException("Resource to test can't be null");
		}
		else {
			return URIUtils.canServeURI(resource, getDefaultLayerType());
		}
	}

	@Override
	public LayerFactory newInstance(final URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		if (resource == null) {
			throw new NullPointerException("Resource to test can't be null");
		}
		else if (canServe(resource)) {
			return this;
		}
		else {
			throw new IllegalArgumentException("URI ["+resource+"] can't be served with the given facltory");
		}
	}

	@Override
	public URI getDefaultLayerType() {
		return FACTORY_URI;
	}

	@Override
	public Layer newInstance(final LayerType type, final Object... parameters) {
		// TODO Auto-generated method stub
		if (type == null) {
			throw new NullPointerException("Layer type can't be null");
		}
		else {
			switch (type) {
				case CONVOLUTIONAL	:
					break;
				case FEED_FORWARD	:
					return new FeedForwardLayer(extractIntArray(parameters, 1)[0]);
				case INPUT			:
					return new InputLayer(extractIntArray(parameters, 0));
				case OUTPUT			:
					return new OutputLayer(extractIntArray(parameters, 0));
				case POOLING		:
					final int[]	poolingParm = extractIntArray(parameters, 3);
					
					return new PoolingLayer(poolingParm[0], poolingParm[1], poolingParm[2]);
				default:
					throw new UnsupportedOperationException("Layer type ["+type+"] is not supported yet");
			}
			return null;
		}
	}
	
	private static int[] extractIntArray(final Object[] parameters, final int awaitedNumbers) {
		if (parameters == null || Utils.checkArrayContent4Nulls(parameters) >= 0) {
			throw new IllegalArgumentException("Parameters is null or contains nulls inside");
		}
		else {
			final int[]	content = new int[parameters.length];
			
			for(int index = 0; index < parameters.length; index++) {
				if (parameters[index] instanceof Number) {
					content[index] = ((Number)parameters[index]).intValue();
				}
				else {
					throw new IllegalArgumentException("Parameter at index ["+index+"] is not a number");
				}
			}
			if (awaitedNumbers > 0 && content.length != awaitedNumbers) {
				throw new IllegalArgumentException("Awaited number of parameters ["+awaitedNumbers+"] is differ than passed ["+content.length+"]");
			}
			return content;
		}
	}
}
