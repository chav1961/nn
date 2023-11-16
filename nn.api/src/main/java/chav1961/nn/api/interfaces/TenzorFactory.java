package chav1961.nn.api.interfaces;

import java.net.URI;

import chav1961.purelib.basic.interfaces.SpiService;

public interface TenzorFactory extends SpiService<TenzorFactory> {
	String TENZOR_FACTORY_SCHEMA = "tenzorfactory"; 
	
	URI getDefaultTenzorType();
	Tenzor newInstance(int size, int... advanced);		
	Tenzor newInstance(float[] content, int... advanced);
	boolean isXTenzorSupported();
	XTenzor newInstanceX(int size, int... advanced);		
	XTenzor newInstanceX(double[] content, int... advanced);
}