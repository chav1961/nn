package chav1961.nn.opencl.util;

import java.net.URI;

import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XTenzor;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.purelib.basic.exceptions.EnvironmentException;

public class OpenCLTenzorFactory implements TenzorFactory {

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
	public URI getDefaultTenzorType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor newInstance(int size, int... advanced) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor newInstance(float[] content, int... advanced) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isXTenzorSupported() {
		return false;
	}

	@Override
	public XTenzor newInstanceX(int size, int... advanced) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor newInstanceX(double[] content, int... advanced) {
		// TODO Auto-generated method stub
		return null;
	}
}
