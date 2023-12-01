package chav1961.nn.standalone.layer;

import java.util.EnumMap;
import java.util.Set;

import chav1961.nn.api.interfaces.AnyTenzor;
import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XTenzor;
import chav1961.purelib.basic.exceptions.SyntaxException;

abstract class AbstractLayer implements Layer {
	private final LayerType	type;
	private final int[]		dimensions;
	private final EnumMap<InternalTenzorType, AnyTenzor>	internalTenzors = new EnumMap<>(InternalTenzorType.class);
	private boolean			prepared = false;
	private boolean			forwardOnly = false;
	private boolean			connected = false;
	private ActivationType	activationType = ActivationType.LINEAR;
	private String[]		activationParameters = new String[0];
	private LossType		lossType = LossType.CROSS_ENTROPY;
	private OptimizerType	optimizerType = OptimizerType.MOMENTUM;
	
	AbstractLayer(final LayerType type, final Set<InternalTenzorType> supports, final int... dimensions) {
		if (type == null) {
			throw new NullPointerException("Layer type can't be null");
		}
		else if (dimensions == null || dimensions.length == 0) {
			throw new IllegalArgumentException("Dimensions can't be null or empty array");
		}
		else {
			this.type = type;
			this.dimensions = dimensions;
			for(InternalTenzorType item : supports) {
				internalTenzors.put(item, null);
			}
		}
	}

	protected abstract void prepareInternal(NeuralNetwork nn);
	protected abstract boolean canConnectBeforeInternal(NeuralNetwork nn, Layer before);
	protected abstract void connectBeforeInternal(NeuralNetwork nn, Layer before);
	protected abstract boolean canConnectAfterInternal(NeuralNetwork nn, Layer after);
	protected abstract void connectAfterInternal(NeuralNetwork nn, Layer after);
	protected abstract Tenzor forwardInternal(NeuralNetwork nn, Tenzor input) throws SyntaxException;
	protected abstract Tenzor backwardInternal(NeuralNetwork nn, Tenzor errors) throws SyntaxException;
	protected abstract XTenzor forwardInternal(NeuralNetwork nn, XTenzor input) throws SyntaxException;
	protected abstract XTenzor backwardInternal(NeuralNetwork nn, XTenzor errors) throws SyntaxException;
	protected abstract void unprepareInternal(NeuralNetwork nn);
	
	@Override
	public LayerType getLayerType() {
		return type;
	}

	@Override
	public int getArity() {
		return dimensions.length;
	}

	@Override
	public int getSize(final int index) {
		if (index < 0 || index >= getArity()) {
			throw new IllegalArgumentException("Size index ["+index+"] out of range 0.."+(getArity()-1));
		}
		else {
			return dimensions[index];
		}
	}

	@Override
	public ActivationType getActivationType() {
		return activationType;
	}

	@Override
	public String[] getActivationParameters() {
		return activationParameters;
	}
	
	@Override
	public Layer setActivationType(final ActivationType activationType, final String... activationParameters) {
		if (activationType == null) {
			throw new NullPointerException("Activation type can't be null");
		}
		else {
			this.activationType = activationType;
			this.activationParameters = activationParameters;
			return this;
		}
	}

	@Override
	public LossType getLossType() {
		return lossType;
	}

	@Override
	public Layer setLossType(final LossType lossType) {
		if (lossType == null) {
			throw new NullPointerException("Loss type can't be null");
		}
		else {
			this.lossType = lossType;
			return this;
		}
	}

	@Override
	public OptimizerType getOptimizerType() {
		return optimizerType;
	}

	@Override
	public Layer setOptimizerType(final OptimizerType optimizerType) {
		if (optimizerType == null) {
			throw new NullPointerException("Loss type can't be null");
		}
		else {
			this.optimizerType = optimizerType;
			return this;
		}
	}

	String getActivationFunctionName() {
		switch (getActivationType()) {
			case LEAKY_RELU	: return "leakyReLu";
			case LINEAR		: return "linear";
			case RELU		: return "relu";
			case SIGMOID	: return "sigmoid";
			case SOFTMAX	: return "softmax";
			case TANH		: return "tanh";
			default :
				throw new UnsupportedOperationException("Activation type ["+getActivationType()+"] is not supported yet");
		}
	}

	String getActivationFunctionPrimeName() {
		switch (getActivationType()) {
			case LEAKY_RELU	: return "DleakyReLu";
			case LINEAR		: return "Dlinear";
			case RELU		: return "Drelu";
			case SIGMOID	: return "Dsigmoid";
			case SOFTMAX	: return "Dsoftmax";
			case TANH		: return "Dtanh";
			default :
				throw new UnsupportedOperationException("Activation type ["+getActivationType()+"] is not supported yet");
		}
	}
	
	@Override
	public <T extends AnyTenzor> T getInternalTenzor(final InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Tenzor type can't be null");
		}
		else if (isInternalTenzorSupported(type)) {
			return (T) internalTenzors.get(type);
		}
		else {
			throw new IllegalArgumentException("Tenzor type ["+type+"] is missing in the layer");
		}
	}

	@Override
	public <T extends AnyTenzor> Layer setInternalTenzor(final InternalTenzorType type, final T tenzor) {
		if (type == null) {
			throw new NullPointerException("Tenzor type can't be null");
		}
		else if (tenzor == null) {
			throw new NullPointerException("Tenzor can't be null");
		}
		else if (isInternalTenzorSupported(type)) {
			internalTenzors.put(type, tenzor);
			return this;
		}
		else {
			throw new IllegalArgumentException("Tenzor type ["+type+"] is missing in the layer");
		}
	}
	
	@Override
	public boolean isInternalTenzorSupported(final InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Tenzor type can't be null");
		}
		else {
			return internalTenzors.containsKey(type);
		}
	}
	
	
	@Override
	public boolean isForwardOnly() {
		return forwardOnly;
	}
	
	@Override
	public Layer prepare(final NeuralNetwork nn, final boolean forwardOnly) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (isPrepared()) {
			throw new IllegalStateException("Layer is already prepared");
		}
		else {
			setPrepared(true);
			setForwardOnly(forwardOnly);
			setConnected(false);
			prepareInternal(nn);
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
			return canConnectBeforeInternal(nn, before);
		}
	}
	
	@Override
	public boolean canConnectAfter(final NeuralNetwork nn, final Layer after) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (after == null) {
			throw new NullPointerException("Layer can't be null");
		}
		else {
			return canConnectAfterInternal(nn, after);
		}
	}

	@Override
	public Layer connectBefore(final NeuralNetwork nn, final Layer before) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (before == null) {
			throw new NullPointerException("Layer can't be null");
		}
		else if (!isPrepared()) {
			throw new IllegalStateException("Payer is not prepared");
		}
		else if (!canConnectBefore(nn, before)) {
			throw new IllegalArgumentException("Illegal layer type ["+before.getLayerType()+"] to connect before this layer or it's parameters are uncompatiple with current layer parameters");
		}
		else if (isConnected()) {
			throw new IllegalStateException("Attempt to connect twice");
		}
		else {
			connectBeforeInternal(nn, before);
			return this;
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
		else if (!isPrepared()) {
			throw new IllegalStateException("Layer is not prepared");
		}
		else if (!canConnectAfter(nn, after)) {
			throw new IllegalArgumentException("Illegal layer type ["+after.getLayerType()+"] to connect after this layer or it's parameters are uncompatiple with current layer parameters");
		}
		else if (isConnected()) {
			throw new IllegalStateException("Attempt to connect twice");
		}
		else {
			connectAfterInternal(nn, after);
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
		else if (!isPrepared()) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (!isConnected()) {
			throw new IllegalStateException("Layer is not connected anywhere");
		}
		else {
			try {
				return forwardInternal(nn, input);
			} catch (SyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	@Override
	public XTenzor forward(final NeuralNetwork nn, final XTenzor input) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (input == null) {
			throw new NullPointerException("Input tenzor can't be null");
		}
		else if (!isPrepared()) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (!isConnected()) {
			throw new IllegalStateException("Layer is not connected anywhere");
		}
		else {
			try {
				return forwardInternal(nn, input);
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
		else if (!isPrepared()) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (!isConnected()) {
			throw new IllegalStateException("Layer is not connected anywhere");
		}
		else if (isForwardOnly()) {
			throw new IllegalStateException("Layer was prepared for 'forward only' mode. Don't call this method!");
		}
		else {
			try {
				return backwardInternal(nn, errors);
			} catch (SyntaxException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}
	
	@Override
	public XTenzor backward(final NeuralNetwork nn, final XTenzor errors) {
		if (nn == null) {
			throw new NullPointerException("Neural network can't be null");
		}
		else if (errors == null) {
			throw new NullPointerException("Errors tenzor can't be null");
		}
		else if (!isPrepared()) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else if (!isConnected()) {
			throw new IllegalStateException("Layer is not connected anywhere");
		}
		else if (isForwardOnly()) {
			throw new IllegalStateException("Layer was prepared for 'forward only' mode. Don't call this method!");
		}
		else {
			try {
				return backwardInternal(nn, errors);
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
		else if (!isPrepared()) {
			throw new IllegalStateException("Layer is not prepared or was unprepared earlier");
		}
		else {
			setPrepared(false);
			setConnected(false);
			unprepareInternal(nn);
			return this;
		}
	}
	
	protected void setForwardOnly(final boolean forwardOnly) {
		this.forwardOnly = forwardOnly;
	}

	protected boolean isPrepared() {
		return prepared;
	}
	
	protected void setPrepared(final boolean prepared) {
		this.prepared = prepared;
	}

	protected boolean isConnected() {
		return connected;
	}
	
	protected void setConnected(final boolean connected) {
		this.connected = connected;
	}
}
