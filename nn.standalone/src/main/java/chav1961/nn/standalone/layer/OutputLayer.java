package chav1961.nn.standalone.layer;

import java.util.Arrays;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.utils.calc.TenzorUtils;
import chav1961.purelib.basic.exceptions.SyntaxException;

class OutputLayer extends AbstractLayer {
	private final int[]	dim;
	private Tenzor		input;
	private Tenzor		weights;
	private Tenzor		output;
	
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
	public Layer setInternalTenzor(final InternalTenzorType type, final Tenzor tenzor) {
		if (type == null) {
			throw new NullPointerException("Tenzor type can't be null");
		}
		else if (tenzor == null) {
			throw new NullPointerException("Tenzor can't be null");
		}
		else {
			switch (type) {
				case WEIGHTS	:
					this.weights = tenzor;
					break;
				case UNKNOWN	: 
					throw new IllegalArgumentException("Tenzor type ["+type+"] is missing in the layer");
				default:
					throw new UnsupportedOperationException("Tenzor type ["+type+"] is not supported yet");
			}
			return this;
		}
	}
	
	@Override
	public boolean isInternalTenzorSupported(final InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Tenzor type can't be null");
		}
		else {
			return type == InternalTenzorType.WEIGHTS;
		}
	}	
	
	@Override
	protected void prepareInternal(final NeuralNetwork nn) {
	}
	
	@Override
	protected boolean canConnectBeforeInternal(final NeuralNetwork nn, final Layer before) {
		return before.getLayerType() != LayerType.OUTPUT;
	}
	
	@Override
	protected boolean canConnectAfterInternal(final NeuralNetwork nn, final Layer after) {
		return false;
	}

	@Override
	protected void connectBeforeInternal(NeuralNetwork nn, Layer before) {
		weights = nn.getTenzorFactory().newInstance(before.getSize(0), getSize(0));
		setConnected(true);
	}
	
	@Override
	protected void connectAfterInternal(NeuralNetwork nn, Layer after) {
		throw new IllegalStateException("Can not connect anything after output layer");
	}

	@Override
	protected Tenzor forwardInternal(final NeuralNetwork nn, final Tenzor input) throws SyntaxException {
		if (!Arrays.equals(dim, TenzorUtils.extractDimension(input))) { 
			throw new IllegalArgumentException("Input tenzor size "+Arrays.toString(TenzorUtils.extractDimension(input))+" differ with declared layer size "+Arrays.toString(dim));
		}
		else {
			this.input = input.duplicate();
			this.output = input.calculate(""+getActivationFunctionName()+"((%0.m1 x %1).T).v", weights);
			
			return output;
		}
	}

	@Override
	protected Tenzor backwardInternal(NeuralNetwork nn, Tenzor errors) throws SyntaxException {
		if (errors.getArity() != output.getArity()) { 
			throw new IllegalArgumentException("Errors tenzor arity ["+errors.getArity()+"] differ with layer declared arity ["+getArity()+"]");
		}
		else {
			final Tenzor	temp = weights.calculate("%3.m2 x "+getActivationFunctionPrimeName()+"(%2 - %1).m1", output, errors, input);
			final Tenzor	result = errors.calculate("("+getActivationFunctionPrimeName()+"(%1 - %0).m1 x %2.T).v", output, weights);
			
			weights.add(temp);
			return result;
		}
	}
	
	@Override
	protected void unprepareInternal(final NeuralNetwork nn) {
	}
}

