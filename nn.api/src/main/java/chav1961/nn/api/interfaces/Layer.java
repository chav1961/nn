package chav1961.nn.api.interfaces;

import java.net.URI;
import java.util.Arrays;
import java.util.ServiceLoader;

import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.interfaces.SpiService;

public interface Layer {
	int WIDTH = 0;
	int HEIGHT = 1;
	int DEPTH = 2;
	
	public static enum ActivationType {
	    LINEAR,
	    LEAKY_RELU,
	    RELU,
	    SIGMOID,
	    SOFTMAX,     
	    TANH;
	}	
	
	public static enum LayerType {
		INPUT,
		FEED_FORWARD,
		CONVOLUTIONAL,
		POOLING,
		OUTPUT
	}
	
	public static enum LossType {
	    CROSS_ENTROPY,
	    MEAN_SQUARED_ERROR;
	}

	public static enum OptimizerType {
	    MOMENTUM,
	    SGD;
	}
	
	public static enum InternalTenzorType {
		WEIGHTS,
		UNKNOWN
	}
	
	public static interface LayerFactory extends SpiService<Layer.LayerFactory> {
		String LAYER_FACTORY_SCHEMA = "layerfactory"; 
		
		URI getDefaultLayerType();
		Layer newInstance(LayerType type, final Object... parameters);		
	}
	
	LayerType getLayerType();
	int getArity();
	int getSize(int index);
	ActivationType getActivationType();
	String[] getActivationParameters();
	Layer setActivationType(ActivationType activationType, String... parameters);
	LossType getLossType();
	Layer setLossType(LossType lossType);
	OptimizerType getOptimizerType();
	Layer setOptimizerType(OptimizerType optimizerType);
	
	Tenzor getInternalTenzor(InternalTenzorType type);
	Layer setInternalTenzor(InternalTenzorType type, Tenzor tenzor);
	boolean isInternalTenzorSupported(InternalTenzorType type);
	
	Layer prepare(NeuralNetwork nn, boolean forwardOnly);
	boolean isForwardOnly();
	boolean canConnectBefore(NeuralNetwork nn, Layer before);
	Layer connectBefore(NeuralNetwork nn, Layer before);
	boolean canConnectAfter(NeuralNetwork nn, Layer after);
	Layer connectAfter(NeuralNetwork nn, Layer after);
	Tenzor forward(NeuralNetwork nn, Tenzor input);
	Tenzor backward(NeuralNetwork nn, Tenzor errors);
	Layer unprepare(NeuralNetwork nn);
	
	public static class Factory {
		private static Layer.LayerFactory	factory;
		private static URI					layerURI;
		
		static {
			for(Layer.LayerFactory item : ServiceLoader.load(Layer.LayerFactory.class)) {
				layerURI = item.getDefaultLayerType();
				factory = item;
				break;
			}
		}
		
		public static URI getDefaultLayerURI() {
			return layerURI;
		}

		public static void setDefaultLayerURI(final URI newLayerURI) {
			if (newLayerURI == null) {
				throw new NullPointerException("Layer type to set cen't be null");
			}
			else {
				factory = getFactory(newLayerURI);
				layerURI = factory.getDefaultLayerType();
			}
		}
		
		public static Layer.LayerFactory getFactory(final URI newLayerURI) {
			if (newLayerURI == null) {
				throw new NullPointerException("Layer type to set cen't be null");
			}
			else {
				for(Layer.LayerFactory item : ServiceLoader.load(Layer.LayerFactory.class)) {
					if (item.canServe(newLayerURI)) {
						return item;
					}
				}
				throw new IllegalArgumentException("No any service provider found to support layer URI ["+newLayerURI+"]");
			}
		}
		
		public static Layer newInstance(final LayerType type, final int... sizes) {
			if (type == null) {
				throw new NullPointerException("Layer type can't be null");
			}
			else if (sizes == null) {
				throw new NullPointerException("Sise list can't be null");
			}
			else if (getDefaultLayerURI() == null) {
				throw new IllegalStateException("No default layer URI was defined. Call setDefaultLayerURI(...) before");
			}
			else {
				return newInstance(getDefaultLayerURI(), type, sizes);
			}
		}
		
		public static Layer newInstance(final URI layerURI, final LayerType type, final int... sizes) {
			if (layerURI == null) {
				throw new NullPointerException("Layer URI can't be null");
			}
			else if (type == null) {
				throw new NullPointerException("Layer type can't be null");
			}
			else if (sizes == null) {
				throw new NullPointerException("Sise list can't be null");
			}
			else {
				Layer.LayerFactory f = null;
				
				if (layerURI.equals(getDefaultLayerURI())) {
					f = factory;
				}
				else {
					for(Layer.LayerFactory item : ServiceLoader.load(Layer.LayerFactory.class)) {
						if (item.canServe(layerURI)) {
							f = item;
							break;
						}
					}
					if (f == null) {
						throw new IllegalStateException("No layer factory found for ["+layerURI+"]");
					}
				}
				return f.newInstance(type, sizes);
			}
		}
	}
}
