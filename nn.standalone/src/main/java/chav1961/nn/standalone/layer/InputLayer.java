package chav1961.nn.standalone.layer;

import java.util.Arrays;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.utils.calc.LayerUtils;
import chav1961.nn.utils.calc.TenzorUtils;

class InputLayer extends AbstractLayer {
	private final int[]	dim;
	private boolean		prepared = false;
	private boolean		connected = false;
	
	InputLayer(final int... dimensions) {
		super(LayerType.INPUT, dimensions);
		this.dim = dimensions;
	}

	@Override
	public Tenzor getInternalTenzor(final InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Internal tenzor type can't be null");
		}
		else {
			switch (type) {
				case WEIGHTS 	:
					throw new IllegalArgumentException("Tenzor type ["+type+"] is missing in the layer");
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
			return after.getLayerType() != LayerType.INPUT && Arrays.equals(dim, LayerUtils.extractDimension(after));
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
		else if (!prepared) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (canConnectAfter(nn, after)) {
			connected = true;
			return this;
		}
		else {
			throw new IllegalStateException("Layer type ["+after.getLayerType()+"] can't be connected after");
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
		else if (!Arrays.equals(dim, TenzorUtils.extractDimension(input))) { 
			throw new IllegalArgumentException("Input tenzor size "+Arrays.toString(TenzorUtils.extractDimension(input))+" differ with layer declared size "+Arrays.toString(dim));
		}
		else if (!prepared) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (!connected) {
			throw new IllegalStateException("Layer is not connected anywhere");
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
		else if (!Arrays.equals(dim, TenzorUtils.extractDimension(errors))) { 
			throw new IllegalArgumentException("Errors tenzor size "+Arrays.toString(TenzorUtils.extractDimension(errors))+" differ with layer declared size "+Arrays.toString(dim));
		}
		else if (!prepared) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (!connected) {
			throw new IllegalStateException("Layer is not connected anywhere");
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
