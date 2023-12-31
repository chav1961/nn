package chav1961.nn.api.interfaces;

import java.net.URI;

import chav1961.nn.api.interfaces.Layer.LayerType;
import chav1961.purelib.basic.interfaces.SpiService;

public interface LayerFactory extends SpiService<LayerFactory> {
	String LAYER_FACTORY_SCHEMA = "layerfactory"; 
	
	URI getDefaultLayerType();
	Layer newInstance(LayerType type, final Object... parameters);
	boolean isXLayerSupported();
	XLayer newXInstance(LayerType type, final Object... parameters);
}