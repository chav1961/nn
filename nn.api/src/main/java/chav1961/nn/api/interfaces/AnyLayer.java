package chav1961.nn.api.interfaces;

public interface AnyLayer {
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

	<T extends AnyTenzor> T getInternalTenzor(InternalTenzorType type);
	<T extends AnyTenzor> Layer setInternalTenzor(InternalTenzorType type, T tenzor);
	
	boolean isInternalTenzorSupported(InternalTenzorType type);
	boolean isForwardOnly();
}
