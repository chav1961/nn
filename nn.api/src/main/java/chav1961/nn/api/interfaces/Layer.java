package chav1961.nn.api.interfaces;

import java.net.URI;
import java.util.ServiceLoader;

import chav1961.nn.api.interfaces.factories.LayerFactory;

public interface Layer extends AnyLayer {

	Layer setActivationType(ActivationType activationType, String... parameters);
	Layer setLossType(LossType lossType);
	Layer setOptimizerType(OptimizerType optimizerType);
	
	Layer prepare(NeuralNetwork nn, boolean forwardOnly);
	boolean canConnectBefore(NeuralNetwork nn, Layer before);
	Layer connectBefore(NeuralNetwork nn, Layer before);
	boolean canConnectAfter(NeuralNetwork nn, Layer after);
	Layer connectAfter(NeuralNetwork nn, Layer after);
	Tenzor forward(NeuralNetwork nn, Tenzor input);
	Tenzor backward(NeuralNetwork nn, Tenzor errors);
	XTenzor forward(NeuralNetwork nn, XTenzor input);
	XTenzor backward(NeuralNetwork nn, XTenzor errors);
	Layer unprepare(NeuralNetwork nn);
	
	public static class Factory {
		private static LayerFactory	factory;
		private static URI					layerURI;
		
		static {
			for(LayerFactory item : ServiceLoader.load(LayerFactory.class)) {
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
		
		public static LayerFactory getFactory(final URI newLayerURI) {
			if (newLayerURI == null) {
				throw new NullPointerException("Layer type to set cen't be null");
			}
			else {
				for(LayerFactory item : ServiceLoader.load(LayerFactory.class)) {
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
				LayerFactory f = null;
				
				if (layerURI.equals(getDefaultLayerURI())) {
					f = factory;
				}
				else {
					for(LayerFactory item : ServiceLoader.load(LayerFactory.class)) {
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
