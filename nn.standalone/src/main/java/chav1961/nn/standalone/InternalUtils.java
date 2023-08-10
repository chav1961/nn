package chav1961.nn.standalone;

import chav1961.nn.core.interfaces.ActivationFunction;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.standalone.internal.Linear;
import chav1961.nn.standalone.internal.Relu;
import chav1961.nn.standalone.internal.Sigmoid;
import chav1961.nn.standalone.internal.Tanh;

class InternalUtils {
    /**
     * Creates and returns specified type of activation function.
     * A factory method for creating activation functions;
     *
     * @param type type of the activation function
     *
     * @return returns instance of specified activation function type
     * @throws NullPointerException null activation type
     * @throws UnsupportedOperationException unsupported activation type
     */
    public static ActivationFunction create(final ActivationType type) throws NullPointerException, UnsupportedOperationException {
    	if (type == null) {
    		throw new NullPointerException("Activation type can't be null");
    	}
    	else {
            switch (type) {
	            case LINEAR:
	                return new Linear();
	            case RELU:
	                return new Relu();
	            case SIGMOID:
	                return new Sigmoid();
	            case TANH:
	                return new Tanh();
	            default:
	                throw new UnsupportedOperationException("Activation type ["+type+"] is not supported yet");
	        }
    	}
    }
}
