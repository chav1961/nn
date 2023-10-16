package chav1961.nn.standalone.layer;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;

class FeedForwardLayer extends AbstractLayer {
	private Tenzor			weights;
	
	FeedForwardLayer(final Tenzor.TenzorFactory factory, final int numberOfNeurons) {
		super(LayerType.FEED_FORWARD, numberOfNeurons);
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.FEED_FORWARD;
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
			// TODO Auto-generated method stub
			return null;
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
			// TODO Auto-generated method stub
			return null;
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
			return after.getLayerType() != LayerType.INPUT;
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
			// TODO Auto-generated method stub
			return null;
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
		else {
			// TODO Auto-generated method stub
			return null;
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
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public Layer unprepare(final NeuralNetwork nn) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
