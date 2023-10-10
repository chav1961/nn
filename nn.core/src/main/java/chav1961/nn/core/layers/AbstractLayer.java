package chav1961.nn.core.layers;

import chav1961.nn.api.interfaces.Layer;

public abstract class AbstractLayer implements Layer {
	private final LayerType	type;
	private final int[]		sizes;
	private ActivationType	activationType = ActivationType.LINEAR;
	private LossType		lossType = LossType.CROSS_ENTROPY;
	private OptimizerType	optimizerType = OptimizerType.MOMENTUM;
	
	protected AbstractLayer(final LayerType type, final int... sizes) {
		if (type == null) {
			throw new NullPointerException("Layer type can't be null");
		}
		else if (sizes == null || sizes.length == 0) {
			throw new IllegalArgumentException("Layer sizes can't be null or empty array");
		}
		else {
			this.type = type;
			this.sizes = sizes.clone();
		}
	}
	
	@Override
	public LayerType getLayerType() {
		return type;
	}

	@Override
	public int getArity() {
		return sizes.length;
	}

	@Override
	public int getSize(final int index) {
		if (index < 0 || index >= getArity()) {
			throw new IllegalArgumentException("Index number ["+index+"] out of range 0.." + (getArity() - 1));
		}
		else {
			return sizes[index];
		}
	}

	@Override
	public ActivationType getActivationType() {
		return activationType;
	}

	@Override
	public Layer setActivationType(final ActivationType activationType) {
		if (activationType == null) {
			throw new NullPointerException("Activation type to set can't be null");
		}
		else {
			this.activationType = activationType;
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
			throw new NullPointerException("Loss type to set can't be null");
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
			throw new NullPointerException("Optimizer type to set can't be null");
		}
		else {
			this.optimizerType = optimizerType;
			return this;
		}
	}
}
