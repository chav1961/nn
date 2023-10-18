package chav1961.nn.standalone.layer;

import chav1961.nn.api.interfaces.Layer;

abstract class AbstractLayer implements Layer {
	private final LayerType	type;
	private final int[]		dimensions;
	private ActivationType	activationType = ActivationType.LINEAR;
	private Object[]		activationParameters = new Object[0];
	private LossType		lossType = LossType.CROSS_ENTROPY;
	private OptimizerType	optimizerType = OptimizerType.MOMENTUM;
	
	AbstractLayer(final LayerType type, final int... dimensions) {
		if (type == null) {
			throw new NullPointerException("Layer type can't be null");
		}
		else if (dimensions == null || dimensions.length == 0) {
			throw new IllegalArgumentException("Dimensions can't be null or empty array");
		}
		else {
			this.type = type;
			this.dimensions = dimensions;
		}
	}
	
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
	public Object[] getActivationParameters() {
		return activationParameters;
	}
	
	@Override
	public Layer setActivationType(final ActivationType activationType, final Object... activationParameters) {
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
}
