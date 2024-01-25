package chav1961.nn.api.interfaces;

public interface AnyLayer<T extends AnyNeuralNetwork<?,?>, L extends AnyLayer<?,?>> {
	int WIDTH = 0;
	int HEIGHT = 1;
	int DEPTH = 2;
	
	public static enum ActivationType {
		NONE,
	    LINEAR,
	    LEAKY_RELU,
	    RELU,
	    SIGMOID,
	    SOFTMAX,     
	    TANH
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
	    MEAN_SQUARED_ERROR
	}

	public static enum OptimizerType {
	    MOMENTUM,
	    SGD
	}
	
	public static enum InternalTenzorType {
		WEIGHTS,
		UNKNOWN
	}

	LayerType getLayerType();
	int getArity();
	int getSize(int index);
	ActivationType getActivationType();
	String[] getActivationParameters();
	LossType getLossType();
	OptimizerType getOptimizerType();

	<TT extends AnyTenzor> TT getInternalTenzor(InternalTenzorType type);
	<TT extends AnyTenzor> AnyLayer<T,L> setInternalTenzor(InternalTenzorType type, TT tenzor);
	
	boolean canConnectBefore(AnyNeuralNetwork<T,L> nn, AnyLayer<T,L> before);
	Layer connectBefore(AnyNeuralNetwork<T,L> nn, AnyLayer<T,L> before);
	boolean canConnectAfter(AnyNeuralNetwork<T,L> nn, AnyLayer<T,L> after);
	Layer connectAfter(AnyNeuralNetwork<T,L> nn, AnyLayer<T,L> after);
	
	
	boolean isInternalTenzorSupported(InternalTenzorType type);
	boolean isForwardOnly();
}
