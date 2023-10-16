package chav1961.nn.standalone.layer;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;

class InputLayer extends AbstractLayer {
	InputLayer(final int... dimensions) {
		super(LayerType.INPUT, dimensions);
	}

	@Override
	public Tenzor getInternalTenzor(final InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Internal tenzor type can't be null");
		}
		else {
			switch (type) {
				case WEIGHTS :
					throw new UnsupportedOperationException("Tenzor type ["+type+"] doesn't contaion in the layer");
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
			return false;
		}
	}

	@Override
	public Layer connectBefore(final NeuralNetwork nn, final Layer after) {
		throw new IllegalStateException("Can't connect anything before input layer");
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
			return true;
		}
	}

	@Override
	public Layer connectAfter(final NeuralNetwork nn, final Layer after) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (after == null) {
			throw new NullPointerException("After layer can't be null");
		}
		else {
			return this;
		}
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
			return input;
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
		else {
			return errors;
		}
	}

	@Override
	public Layer unprepare(NeuralNetwork nn) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else {
			return this;
		}
	}
}
