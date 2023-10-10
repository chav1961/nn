package chav1961.nn.api.interfaces;

import java.net.URI;

import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.interfaces.SpiService;

public interface Layer {
	int WIDTH = 0;
	int HEIGHT = 1;
	int DEPTH = 2;
	
	public enum ActivationType {
	    LINEAR,
	    LEAKY_RELU,
	    RELU,
	    SIGMOID,
	    SOFTMAX,     
	    TANH;
	}	
	
	public static enum LayerType {
		FEED_FORWARD,
		CONVOLUTIONAL,
		POOLING
	}
	
	public enum LossType {
	    CROSS_ENTROPY,
	    MEAN_SQUARED_ERROR;
	}

	public enum OptimizerType {
	    MOMENTUM,
	    SGD;
	}
	
	public static interface LayerFactory extends SpiService<Layer.LayerFactory> {
		String LAYER_FACTORY_SCHEMA = "layerfactory"; 
		
		URI getDefaultLayerType();
		Layer newInstance(LayerType type);		
	}
	
	LayerType getLayerType();
	int getArity();
	int getSize(int index);
	ActivationType getActivationType();
	Layer setActivationType(ActivationType activationType);
	LossType getLossType();
	Layer setLossType(LossType lossType);
	OptimizerType getOptimizerType();
	Layer setOptimizerType(OptimizerType optimizerType);
	
	Layer prepare(NeuralNetwork nn);
	boolean canConnectBefore(NeuralNetwork nn, Layer before);
	boolean canConnectAfter(NeuralNetwork nn, Layer after);
	Tenzor forward(NeuralNetwork nn, Tenzor input);
	Tenzor backward(NeuralNetwork nn, Tenzor errors);
	Layer unprepare(NeuralNetwork nn);
	
	public static class Factory implements LayerFactory {

		@Override
		public URI getDefaultLayerType() {
			// TODO Auto-generated method stub
			return null;
		}

		
		@Override
		public boolean canServe(URI resource) throws NullPointerException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public LayerFactory newInstance(URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Layer newInstance(LayerType type) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
