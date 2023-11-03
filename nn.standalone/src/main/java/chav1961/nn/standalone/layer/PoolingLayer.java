package chav1961.nn.standalone.layer;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.Layer.InternalTenzorType;
import chav1961.purelib.basic.exceptions.SyntaxException;

class PoolingLayer extends AbstractLayer {
	private final int	stride;
	private boolean		connectedBefore = false;
	private boolean		connectedAfter = false;
	private Layer		nextLayer;
	private int[][][][]	maxLocations;
	private Tenzor		savedInput, savedOutput;
	
	public PoolingLayer(final int cellWidth, final int cellHeight, final int stride) {
		super(LayerType.POOLING, new int[] {cellWidth, cellHeight});
		this.stride = stride;
	}

	public int getStride() {
		return stride;
	}
	
	@Override
	public Tenzor getInternalTenzor(final InternalTenzorType type) {
		if (type == null) {
			throw new NullPointerException("Tenzor type can't be null");
		}
		else {
			switch (type) {
				case UNKNOWN : case WEIGHTS :
					throw new IllegalArgumentException("Tenzor type ["+type+"] is missing in the layer");
				default:
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
	protected void prepareInternal(final NeuralNetwork nn) {
		connectedBefore = false;
		connectedAfter = false;
	}

	@Override
	protected boolean canConnectBeforeInternal(final NeuralNetwork nn, final Layer before) {
		return before.getLayerType() == LayerType.CONVOLUTIONAL;
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
		final int		targetWidth = input.getSize(Layer.WIDTH) - getSize(Layer.WIDTH) + 1;
		final int		targetHeight = input.getSize(Layer.HEIGHT) - getSize(Layer.HEIGHT) + 1;
		final int		targetDepth = input.getSize(Layer.DEPTH); 
		final Tenzor	output = nn.getTenzorFactory().newInstance(targetWidth, targetHeight, targetDepth); 
		
		for(int channel = 0; channel < targetDepth; channel++) {
			processForward(input, output, targetWidth, targetHeight, targetDepth, channel);
		}
		if (!isForwardOnly()) {
			savedInput = input.duplicate();
			savedOutput = output.duplicate();
		}
		return output;
	}

	@Override
	protected Tenzor backwardInternal(final NeuralNetwork nn, final Tenzor errors) throws SyntaxException {
		final int		targetWidth = 0, targetHeight = 0, targetDepth = 0; 
		final Tenzor	result = nn.getTenzorFactory().newInstance(savedInput.getSize(Layer.WIDTH), savedInput.getSize(Layer.HEIGHT), savedInput.getSize(Layer.DEPTH));
		
		for(int channel = 0, maxChannel = errors.getSize(Layer.DEPTH); channel < maxChannel; channel++) {
			try {
				switch (nextLayer.getLayerType()) {
					case CONVOLUTIONAL	:
						processConvolutionalBackward(errors, savedOutput, result, targetWidth, targetHeight, targetDepth, channel);
						break;
					case FEED_FORWARD : case OUTPUT :
						processFeedForwardBackward(errors, savedOutput, result, targetWidth, targetHeight, targetDepth, channel);
						break;
					case INPUT: case POOLING:
					default:
						throw new RuntimeException("Call developers...");
				}
			} catch (SyntaxException exc) {
				throw new IllegalArgumentException(exc);
			}
		}
		return errors;
	}
	
	@Override
	protected void unprepareInternal(final NeuralNetwork nn) {
		connectedBefore = false;
		connectedAfter = false;
	}
	
	private void processForward(final Tenzor input, final Tenzor output, final int targetWidth, final int targetHeight, final int targetDepth, final int channel) {
		final int		step = getStride();
		final boolean	fo = isForwardOnly();
		final int		filterWidth = getSize(Layer.WIDTH), filterHeight = getSize(Layer.HEIGHT);  
	    final int 		maxPoint[][][][] = !fo ? new int[targetDepth][targetHeight][targetWidth][2] : null;

        for (int inRow = 0, outRow = 0, outCol = 0; inRow < targetHeight; inRow += step, outRow++, outCol = 0) {
            for (int inCol = 0; inCol < targetWidth; inCol += step, outCol++) {
                float	maxValue = input.get(inRow, inCol, channel);
                int		maxCol = inCol;
                int		maxRow = inRow;
                
                for (int filterRow = 0; filterRow < filterHeight; filterRow++) {
                    for (int filterCol = 0; filterCol < filterWidth; filterCol++) {
                        if (maxValue < input.get(inRow + filterRow, inCol + filterCol, channel)) {
                            maxRow = inRow + filterRow;
                            maxCol = inCol + filterCol;
                            maxValue = input.get(maxRow, maxCol, channel);
                        }
                    }
                }
                if (!fo) {
                    maxPoint[channel][outRow][outCol][0] = maxRow;
                    maxPoint[channel][outRow][outCol][1] = maxCol;
                }
                output.set(maxValue, outRow, outCol, channel);
            }
        }
        if (!fo) {
        	maxLocations = maxPoint;
        }
	}

	private void processConvolutionalBackward(final Tenzor errors, final Tenzor output, final Tenzor result, final int targetWidth, final int targetHeight, final int targetDepth, final int channel) {
		// TODO Auto-generated method stub
        final ConvolutionalLayer	nextConvLayer = (ConvolutionalLayer) nextLayer;
		final Tenzor				deltas = output.calculateN("%1 - %0", errors);
		final int					convStride = nextConvLayer.getStride();
        final int 					filterWidth = nextConvLayer.getSize(Layer.WIDTH);
        final int 					filterHeight = nextConvLayer.getSize(Layer.HEIGHT);
        final int 					filterCenterX = (filterWidth - 1) / 2;
        final int 					filterCenterY = (filterHeight - 1) / 2;

        for (int ndz = 0, maxNdz = deltas.getSize(Layer.DEPTH); ndz < maxNdz; ndz++) { 
            for (int ndr = 0, maxNdr = deltas.getSize(Layer.WIDTH); ndr < maxNdr; ndr++) { 
                for (int ndc = 0, maxNdc = deltas.getSize(Layer.HEIGHT); ndc < maxNdc; ndc++) { 
                    final float nextLayerDelta = deltas.get(ndr, ndc, ndz); 

                    for (int filterRow = 0; filterRow < filterHeight; filterRow++) {
                        for (int filterCol = 0; filterCol < filterWidth; filterCol++) {
                            final int outRow = ndr * convStride + filterRow - filterCenterY;
                            final int outCol = ndc * convStride + filterCol - filterCenterX;

                            if (outRow >= 0 && outRow <= maxNdr &&  outCol >= 0 && outCol < maxNdc) {
                                deltas.add(outRow, outCol, channel, nextLayerDelta * nextConvLayer.filters[ndz].get(filterRow, filterCol, channel));
                            }
                        }
                    }
                }
            }
        }
	}

	private void processFeedForwardBackward(final Tenzor errors, final Tenzor output, final Tenzor result, final int targetWidth, final int targetHeight, final int targetDepth, final int channel) throws SyntaxException {
		final Tenzor	weights = nextLayer.getInternalTenzor(InternalTenzorType.WEIGHTS);
		final float[]	deltas = output.calculateN("%1 - %0", errors).getContent();
		
        for (int row = 0, maxRow = result.getSize(Layer.WIDTH); row < maxRow; row++) {
            for (int col = 0, maxCol = result.getSize(Layer.HEIGHT); col < maxCol; col++) {
                for (int index = 0, maxIndex = deltas.length; index < maxIndex; index++) {
                    result.set(result.get(row, col, channel) + deltas[index] * weights.get(col, row, channel, index), row, col, channel);
                }
            }
        }
    }

}
