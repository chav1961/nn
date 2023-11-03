package chav1961.nn.standalone.layer;

import java.util.Arrays;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.Layer.InternalTenzorType;
import chav1961.nn.utils.calc.LayerUtils;
import chav1961.nn.utils.calc.TenzorUtils;
import chav1961.purelib.basic.exceptions.SyntaxException;

class InputLayer extends AbstractLayer {
	private final int[]	dim;
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
	public boolean isInternalTenzorSupported(final InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Tenzor type can't be null");
		}
		else {
			return false;
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
		else if (!isPrepared()) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (!connected) {
			throw new IllegalStateException("Layer is not connected anywhere");
		}
		else if (isForwardOnly()) {
			throw new IllegalStateException("Layer was prepared for 'forward only' mode. Don't call this method!");
		}
		else {
			return errors;
		}
	}

	@Override
	protected void prepareInternal(final NeuralNetwork nn) {
		connected = false;
	}

	@Override
	protected boolean canConnectBeforeInternal(final NeuralNetwork nn, final Layer before) {
		return false;
	}
	
	@Override
	protected boolean canConnectAfterInternal(final NeuralNetwork nn, final Layer after) {
		return after.getLayerType() != LayerType.INPUT && Arrays.equals(dim, LayerUtils.extractDimension(after));
	}

	@Override
	protected void connectBeforeInternal(final NeuralNetwork nn, final Layer before) {
		throw new IllegalStateException("Can't connect anything before input layer");
	}
	
	@Override
	protected void connectAfterInternal(final NeuralNetwork nn, final Layer after) {
		setConnected(true);
	}

	@Override
	protected Tenzor forwardInternal(final NeuralNetwork nn, final Tenzor input) throws SyntaxException {
		return input;
	}

	@Override
	protected Tenzor backwardInternal(final NeuralNetwork nn, final Tenzor errors) throws SyntaxException {
		if (!Arrays.equals(dim, TenzorUtils.extractDimension(errors))) { 
			throw new IllegalArgumentException("Errors tenzor size "+Arrays.toString(TenzorUtils.extractDimension(errors))+" differ with layer declared size "+Arrays.toString(dim));
		}
		else {
			return errors;
		}
	}
	
	@Override
	protected void unprepareInternal(final NeuralNetwork nn) {
		connected = false;
	}
}
