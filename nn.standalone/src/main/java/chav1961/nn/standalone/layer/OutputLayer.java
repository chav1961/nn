package chav1961.nn.standalone.layer;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.purelib.basic.exceptions.SyntaxException;

public class OutputLayer extends AbstractLayer {
	private Tenzor	weights;
	private Tenzor	output;
	
	OutputLayer(int[] dimensions) {
		super(LayerType.OUTPUT, dimensions);
	}

	@Override
	public Tenzor getInternalTenzor(InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Internal tenzor type can't be null");
		}
		else {
			switch (type) {
				case WEIGHTS :
					return weights;
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
		else {
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
		else {
			weights = nn.getTenzorFactory().newInstance(before.getSize(0), getSize(0));
			return this;
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
		else if (input.getArity() != getArity()) { 
			throw new IllegalArgumentException("Input tenzor arity ["+input.getArity()+"] differ with layer declared arity ["+getArity()+"]");
		}
		else {
			try {
				output = input.calculate(getActivationFunctionName()+"(trans(%0 x %1))", weights);
				
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
		else if (errors.getArity() != getArity()) { 
			throw new IllegalArgumentException("Errors tenzor arity ["+errors.getArity()+"] differ with layer declared arity ["+getArity()+"]");
		}
		else {
			try {
				final Tenzor	result = errors.calculate("trans("+getActivationFunctionPrimeName()+"(%1 - %0) x %2)", output, weights);
				final Tenzor	temp = weights.calculate("", output, errors);
				
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
		else {
			return this;
		}
	}
}
