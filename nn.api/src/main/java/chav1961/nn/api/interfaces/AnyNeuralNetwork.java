package chav1961.nn.api.interfaces;

import java.io.Closeable;
import java.net.URI;
import java.util.ServiceLoader;

import chav1961.nn.api.interfaces.factories.LayerFactory;
import chav1961.nn.api.interfaces.factories.NeuralNetworkFactory;
import chav1961.nn.api.interfaces.factories.TenzorFactory;

public interface AnyNeuralNetwork<T extends AnyNeuralNetwork<?,?>, L extends AnyLayer<?,?>> extends Closeable {
	TenzorFactory getTenzorFactory();
	LayerFactory getLayerFactory();
	T add(AnyLayer<T,L>... layers);
	AnyLayer<T,L>[] getLayers();
	
	T prepare(boolean forwardOnly);
	T unprepare();
	
	public final static class Factory {
		private Factory() {}

		public static boolean canServe(final URI neuralNetworkUri) throws NullPointerException, IllegalArgumentException {
			if (neuralNetworkUri == null || !NeuralNetworkFactory.NETWORD_FACTORY_SCHEMA.equals(neuralNetworkUri.getScheme())) {
				throw new IllegalArgumentException("Neural network factory URI can't be null and must have scheme ["+NeuralNetworkFactory.NETWORD_FACTORY_SCHEMA+"]"); 
			}
			else {
				for (NeuralNetworkFactory item : ServiceLoader.load(NeuralNetworkFactory.class)) {
					if (item.canServe(neuralNetworkUri)) {
						return true;
					}
				}
				return false; 
			}
		}
		
		public static NeuralNetworkFactory newInstance(final URI neuralNetworkUri) throws NullPointerException, IllegalArgumentException{
			if (canServe(neuralNetworkUri)) {
				for (NeuralNetworkFactory item : ServiceLoader.load(NeuralNetworkFactory.class)) {
					if (item.canServe(neuralNetworkUri)) {
						return item.newInstance(neuralNetworkUri);
					}
				}
			}
			throw new IllegalArgumentException("Neural network URI ["+neuralNetworkUri+"] can't be served by any SPI"); 
		}
	}
}
