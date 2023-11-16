package chav1961.nn.opencl.layer;

import java.net.URI;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.Layer.LayerType;
import chav1961.nn.api.interfaces.LayerFactory;
import chav1961.nn.api.interfaces.XLayer;
import chav1961.purelib.basic.exceptions.EnvironmentException;

public class OpenCLLayerFactory implements LayerFactory {

	@Override
	public boolean canServe(URI resource) throws NullPointerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LayerFactory newInstance(URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getDefaultLayerType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Layer newInstance(LayerType type, Object... parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isXLayerSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public XLayer newXInstance(LayerType type, Object... parameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
