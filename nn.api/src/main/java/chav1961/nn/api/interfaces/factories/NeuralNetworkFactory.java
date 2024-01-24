package chav1961.nn.api.interfaces.factories;

import java.net.URI;

import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.XNeuralNetwork;
import chav1961.purelib.basic.interfaces.SpiService;

public interface NeuralNetworkFactory extends SpiService<NeuralNetworkFactory> {
	String NETWORD_FACTORY_SCHEMA = "networkfactory"; 
	
	URI getDefaultLayerType();
	NeuralNetwork newInstance(final Object... parameters);
	boolean isXNetworkSupported();
	XNeuralNetwork newXInstance(final Object... parameters);
}
