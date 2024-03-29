package chav1961.nn.api.interfaces;

import java.net.URI;
import java.util.ServiceLoader;

import chav1961.purelib.basic.interfaces.SpiService;

public interface XLayer extends AnyLayer {
	
	public static interface LayerFactory extends SpiService<XLayer.LayerFactory> {
		String LAYER_FACTORY_SCHEMA = "layerfactory"; 
		
		URI getDefaultLayerType();
		XLayer newInstance(LayerType type, final Object... parameters);		
	}
	
	XLayer setActivationType(ActivationType activationType, String... parameters);
	XLayer setLossType(LossType lossType);
	OptimizerType getOptimizerType();
	
	XLayer prepare(XNeuralNetwork nn, boolean forwardOnly);
	boolean canConnectBefore(XNeuralNetwork nn, XLayer before);
	XLayer connectBefore(XNeuralNetwork nn, XLayer before);
	boolean canConnectAfter(XNeuralNetwork nn, XLayer after);
	XLayer connectAfter(XNeuralNetwork nn, XLayer after);
	XTenzor forward(XNeuralNetwork nn, XTenzor input);
	XTenzor backward(XNeuralNetwork nn, XTenzor errors);
	XLayer unprepare(XNeuralNetwork nn);
	
	public static class Factory {
		private static XLayer.LayerFactory	factory;
		private static URI					layerURI;
		
		static {
			for(XLayer.LayerFactory item : ServiceLoader.load(XLayer.LayerFactory.class)) {
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
		
		public static XLayer.LayerFactory getFactory(final URI newLayerURI) {
			if (newLayerURI == null) {
				throw new NullPointerException("Layer type to set cen't be null");
			}
			else {
				for(XLayer.LayerFactory item : ServiceLoader.load(XLayer.LayerFactory.class)) {
					if (item.canServe(newLayerURI)) {
						return item;
					}
				}
				throw new IllegalArgumentException("No any service provider found to support layer URI ["+newLayerURI+"]");
			}
		}
		
		public static XLayer newInstance(final LayerType type, final int... sizes) {
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
		
		public static XLayer newInstance(final URI layerURI, final LayerType type, final int... sizes) {
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
				XLayer.LayerFactory f = null;
				
				if (layerURI.equals(getDefaultLayerURI())) {
					f = factory;
				}
				else {
					for(XLayer.LayerFactory item : ServiceLoader.load(XLayer.LayerFactory.class)) {
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
