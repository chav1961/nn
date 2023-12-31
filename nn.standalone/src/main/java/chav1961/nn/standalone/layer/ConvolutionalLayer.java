package chav1961.nn.standalone.layer;

import java.util.Set;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XTenzor;
import chav1961.purelib.basic.exceptions.SyntaxException;

class ConvolutionalLayer extends AbstractLayer {
	private final int	stride;
	private boolean		connectedBefore = false;
	private boolean		connectedAfter = false;

	ConvolutionalLayer(final int cellWidth, final int cellHeight, final int stride) {
		super(LayerType.CONVOLUTIONAL, Set.of(), new int[] {cellWidth, cellHeight});
		this.stride = stride;
	}

	public int getStride() {
		return stride;
	}
	
	@Override
	public boolean isInternalTenzorSupported(InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Tenzor type can't be null");
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void prepareInternal(final NeuralNetwork nn) {
		connectedBefore = false;
		connectedAfter = false;
	}

	@Override
	protected boolean canConnectBeforeInternal(final NeuralNetwork nn, final Layer before) {
		return before.getLayerType() != LayerType.OUTPUT;
	}

	@Override
	protected boolean canConnectAfterInternal(final NeuralNetwork nn, final Layer after) {
		return after.getLayerType() != LayerType.INPUT;
	}

	@Override
	protected void connectBeforeInternal(final NeuralNetwork nn, final Layer before) {
		connectedBefore = true;
		if (connectedBefore && connectedAfter) {
			setConnected(true);
		}
	}
	
	@Override
	protected void connectAfterInternal(final NeuralNetwork nn, final Layer after) {
		connectedAfter = true;
		if (connectedBefore && connectedAfter) {
			setConnected(true);
		}
	}

	@Override
	protected Tenzor forwardInternal(final NeuralNetwork nn, final Tenzor input) throws SyntaxException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected XTenzor forwardInternal(final NeuralNetwork nn, final XTenzor input) throws SyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Tenzor backwardInternal(final NeuralNetwork nn, final Tenzor errors) throws SyntaxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected XTenzor backwardInternal(final NeuralNetwork nn, final XTenzor errors) throws SyntaxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void unprepareInternal(final NeuralNetwork nn) {
		connectedBefore = false;
		connectedAfter = false;
	}
}
