package chav1961.nn.standalone.layer;

import java.util.Arrays;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.utils.calc.TenzorUtils;
import chav1961.purelib.basic.exceptions.SyntaxException;

class OutputLayer extends AbstractLayer {
	private final int[]	dim;
	private boolean		prepared = false;
	private boolean		connected = false;
	private Tenzor	weights;
	private Tenzor	output;
	
	OutputLayer(final int... dimensions) {
		super(LayerType.OUTPUT, dimensions);
		this.dim = dimensions;
	}

	@Override
	public Tenzor getInternalTenzor(final InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Internal tenzor type can't be null");
		}
		else {
			switch (type) {
				case WEIGHTS :
					return weights;
				case UNKNOWN	:
					throw new IllegalArgumentException("Tenzor type ["+type+"] is missing in the layer");
				default :
					throw new UnsupportedOperationException("Tenzor type ["+type+"] is not supported yet");
			}
		}
	}

	@Override
	public Layer prepare(final NeuralNetwork nn) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (prepared) {
			throw new IllegalStateException("Layer is already prepared");
		}
		else {
			prepared = true;
			return this;
		}
	}

	@Override
	public boolean canConnectBefore(final NeuralNetwork nn, final Layer before) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (before == null) {
			throw new NullPointerException("Before layer can't be null");
		}
		else {
			return before.getLayerType() != LayerType.OUTPUT;
		}
	}

	@Override
	public Layer connectBefore(final NeuralNetwork nn, final Layer before) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (before == null) {
			throw new NullPointerException("Before layer can't be null");
		}
		else if (!prepared) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (connected) {
			throw new IllegalStateException("Attempt to connect twice");
		}
		else if (canConnectBefore(nn, before)) {
			connected = true;
			weights = nn.getTenzorFactory().newInstance(before.getSize(0), getSize(0));
			return this;
		}
		else {
			throw new IllegalStateException("Layer type ["+before.getLayerType()+"] can't be connected before");
		}
	}

	@Override
	public boolean canConnectAfter(final NeuralNetwork nn, final Layer after) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (after == null) {
			throw new NullPointerException("After layer can't be null");
		}
		else {
			return false;
		}
	}

	@Override
	public Layer connectAfter(NeuralNetwork nn, Layer after) {
		throw new IllegalStateException("Can not connect anything after output layer");
	}

	@Override
	public Tenzor forward(final NeuralNetwork nn, final Tenzor input) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (input == null) {
			throw new NullPointerException("Input tenzor can't be null");
		}
		else if (!prepared) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (!connected) {
			throw new IllegalStateException("Layer is not connected anywhere");
		}
		else if (!Arrays.equals(dim, TenzorUtils.extractDimension(input))) { 
			throw new IllegalArgumentException("Input tenzor size "+Arrays.toString(TenzorUtils.extractDimension(input))+" differ with declared layer size "+Arrays.toString(dim));
		}
		else {
			try {
				output = input.calculate("vector("+getActivationFunctionName()+"(trans(matrix(%0,1) x %1)))", weights);
				
				return output;
			} catch (SyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	@Override
	public Tenzor backward(final NeuralNetwork nn, final Tenzor errors) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (errors == null) {
			throw new NullPointerException("Errors tenzor can't be null");
		}
		else if (output == null) {
			throw new IllegalStateException("Calling backward(...) without calling forward(...). Call forward(...) before!");
		}
		else if (errors.getArity() != output.getArity()) { 
			throw new IllegalArgumentException("Errors tenzor arity ["+errors.getArity()+"] differ with layer declared arity ["+getArity()+"]");
		}
		else {
			try {
				final Tenzor	result = errors.calculate("vector(trans(matrix("+getActivationFunctionPrimeName()+"(%1 - %0),1) x %2))", output, weights);
				final Tenzor	temp = weights.calculate("trans(matrix("+getActivationFunctionPrimeName()+"(%2 - %1),1) x %0)", output, errors);
				
				weights.set(temp);
				return result;
			} catch (SyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	@Override
	public Layer unprepare(final NeuralNetwork nn) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (!prepared) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else {
			connected = false;
			prepared = false;
			return this;
		}
	}
}
