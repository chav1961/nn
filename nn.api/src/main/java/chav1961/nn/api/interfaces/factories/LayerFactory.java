package chav1961.nn.api.interfaces.factories;

import java.net.URI;

import chav1961.nn.api.interfaces.AnyLayer.LayerType;
import chav1961.nn.api.interfaces.AnyLayer;
import chav1961.nn.api.interfaces.AnyNeuralNetwork;
import chav1961.purelib.basic.interfaces.SpiService;

public interface LayerFactory<T extends AnyNeuralNetwork<?,?>, L extends AnyLayer<?,?>> extends SpiService<LayerFactory<?,?>> {
	String LAYER_FACTORY_SCHEMA = "layerfactory"; 
	
	URI getDefaultLayerType();
	AnyLayer<T,L> newInstance(LayerType type, final Object... parameters);
	boolean isXLayerSupported();
	AnyLayer<T,L> newXInstance(LayerType type, final Object... parameters);
}