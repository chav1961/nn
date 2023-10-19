package chav1961.nn.opencl.util;

import java.net.URI;

import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.Tenzor.TenzorFactory;
import chav1961.purelib.basic.exceptions.EnvironmentException;

public class OpenCLTenzorFactory implements Tenzor.TenzorFactory {

	@Override
	public boolean canServe(URI resource) throws NullPointerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TenzorFactory newInstance(URI resource)
			throws EnvironmentException, NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getDefaultTensorType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor newInstance(int size, int... advanced) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor newInstance(float[] content, int size, int... advanced) {
		// TODO Auto-generated method stub
		return null;
	}

}
