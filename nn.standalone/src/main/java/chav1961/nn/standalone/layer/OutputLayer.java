package chav1961.nn.standalone.layer;

import java.util.Arrays;
import java.util.Set;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XTenzor;
import chav1961.nn.utils.calc.TenzorUtils;
import chav1961.purelib.basic.exceptions.SyntaxException;

class OutputLayer extends AbstractLayer {
	private final int[]	dim;
	private Tenzor		input;
	private Tenzor		output;
	private XTenzor		inputX;
	private XTenzor		outputX;
	
	OutputLayer(final int... dimensions) {
		super(LayerType.OUTPUT, Set.of(InternalTenzorType.WEIGHTS), dimensions);
		this.dim = dimensions;
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
		setInternalTenzor(InternalTenzorType.WEIGHTS, nn.getTenzorFactory().newInstance(before.getSize(0), getSize(0)));
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
			this.output = input.calculate(""+getActivationFunctionName()+"((%0.m1 x %1).T).v", getInternalTenzor(InternalTenzorType.WEIGHTS));
			
			return output;
		}
	}

	@Override
	protected XTenzor forwardInternal(final NeuralNetwork nn, final XTenzor input) throws SyntaxException {
		if (!Arrays.equals(dim, TenzorUtils.extractDimension(input))) { 
			throw new IllegalArgumentException("Input tenzor size "+Arrays.toString(TenzorUtils.extractDimension(input))+" differ with declared layer size "+Arrays.toString(dim));
		}
		else {
			this.inputX = input.duplicate();
			this.outputX = input.calculate(""+getActivationFunctionName()+"((%0.m1 x %1).T).v", getInternalTenzor(InternalTenzorType.WEIGHTS));
			
			return outputX;
		}
	}
	
	@Override
	protected Tenzor backwardInternal(NeuralNetwork nn, Tenzor errors) throws SyntaxException {
		if (errors.getArity() != output.getArity()) { 
			throw new IllegalArgumentException("Errors tenzor arity ["+errors.getArity()+"] differ with layer declared arity ["+getArity()+"]");
		}
		else {
			final Tenzor	temp = ((Tenzor)getInternalTenzor(InternalTenzorType.WEIGHTS)).calculate("%3.m2 x "+getActivationFunctionPrimeName()+"(%2 - %1).m1", output, errors, input);
			final Tenzor	result = errors.calculate("("+getActivationFunctionPrimeName()+"(%1 - %0).m1 x %2.T).v", output, getInternalTenzor(InternalTenzorType.WEIGHTS));
			
			((Tenzor)getInternalTenzor(InternalTenzorType.WEIGHTS)).add(temp);
			return result;
		}
	}

	@Override
	protected XTenzor backwardInternal(NeuralNetwork nn, XTenzor errors) throws SyntaxException {
		if (errors.getArity() != output.getArity()) { 
			throw new IllegalArgumentException("Errors tenzor arity ["+errors.getArity()+"] differ with layer declared arity ["+getArity()+"]");
		}
		else {
			final XTenzor	temp = ((XTenzor)getInternalTenzor(InternalTenzorType.WEIGHTS)).calculate("%3.m2 x "+getActivationFunctionPrimeName()+"(%2 - %1).m1", outputX, errors, inputX);
			final XTenzor	result = errors.calculate("("+getActivationFunctionPrimeName()+"(%1 - %0).m1 x %2.T).v", outputX, getInternalTenzor(InternalTenzorType.WEIGHTS));
			
			((XTenzor)getInternalTenzor(InternalTenzorType.WEIGHTS)).add(temp);
			return result;
		}
	}
	
	@Override
	protected void unprepareInternal(final NeuralNetwork nn) {
	}
}

