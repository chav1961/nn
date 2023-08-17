package chav1961.nn.standalone.network;


import java.net.URI;

import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.Layer;
import chav1961.nn.core.interfaces.LayerFactory;
import chav1961.nn.core.interfaces.Tensor;
import chav1961.nn.core.interfaces.TensorFactory;
import chav1961.nn.core.interfaces.Trainer;
import chav1961.nn.core.network.AbstractNeuralNetwork;
import chav1961.nn.standalone.internal.ConvolutionalLayer;
import chav1961.nn.standalone.internal.FullyConnectedLayer;
import chav1961.nn.standalone.internal.InputLayer;
import chav1961.nn.standalone.internal.MaxPoolingLayer;
import chav1961.nn.standalone.internal.OutputLayer;
import chav1961.nn.standalone.internal.SoftmaxOutputLayer;
import chav1961.nn.standalone.internal.TensorImpl;
import chav1961.purelib.basic.URIUtils;

abstract class StandaloneNeuralNetwork<Tr extends Trainer> extends AbstractNeuralNetwork<Tr> {
	private static final long serialVersionUID = -1188484880395191026L;

	public static final String	STANDALONE_SCHEME = "standalone";
	private static final URI	STANDALONE_URL = URI.create(NEURAL_NETWORK_SCHEME+":"+STANDALONE_SCHEME+":/");
	
	private final TensorFactory	tf = new TensorFactory() {
										@Override
										public Tensor newInstanceTensorImpl(float[][][][] vals) {
											return new TensorImpl(vals);
										}
										
										@Override
										public Tensor newInstance(Tensor t) {
											return new TensorImpl(t);
										}
										
										@Override
										public Tensor newInstance(int rows, int cols, int depth, int fourthDim, float[] values) {
											return new TensorImpl(rows, cols, depth, fourthDim, values);
										}
										
										@Override
										public Tensor newInstance(int rows, int cols, int depth, int fourthDim) {
											return new TensorImpl(rows, cols, depth, fourthDim);
										}
										
										@Override
										public Tensor newInstance(float[][][] vals) {
											return new TensorImpl(vals);
										}
										
										@Override
										public Tensor newInstance(int rows, int cols, int depth, float[] values) {
											return new TensorImpl(rows, cols, depth, values);
										}
										
										@Override
										public Tensor newInstance(int rows, int cols, int depth) {
											return new TensorImpl(rows, cols, depth);
										}
										
										@Override
										public Tensor newInstance(float[][] vals) {
											return new TensorImpl(vals);
										}
										
										@Override
										public Tensor newInstance(int rows, int cols, float[] values) {
											return new TensorImpl(rows, cols, values);
										}
										
										@Override
										public Tensor newInstance(int rows, int cols) {
											return new TensorImpl(rows, cols);
										}
										
										@Override
										public Tensor newInstance(float[] values) {
											return new TensorImpl(values);
										}
										
										@Override
										public Tensor newInstance(int cols, float val) {
											return new TensorImpl(cols, val);
										}
										
										@Override
										public Tensor newInstance(int cols) {
											return new TensorImpl(cols);
										}
									};
	private final LayerFactory	lf = new LayerFactory() {
										@Override
										public Layer newSoftmaxOutputLayer(final String... labels) {
											return new SoftmaxOutputLayer(labels);
										}
										
										@Override
										public Layer newSoftmaxOutputLayer(final int width) {
											return new SoftmaxOutputLayer(width);
										}
										
										@Override
										public Layer newOutputLayer(final ActivationType actType, final String... outputLabels) {
											return new OutputLayer(outputLabels, actType);
										}
										
										@Override
										public Layer newOutputLayer(final String... outputLabels) {
											return new OutputLayer(outputLabels);
										}
										
										@Override
										public Layer newOutputLayer(final int width, final ActivationType actType) {
											return new OutputLayer(width, actType);
										}
										
										@Override
										public Layer newOutputLayer(int width) {
											return new OutputLayer(width);
										}
										
										@Override
										public Layer newMaxPoolingLayer(final int filterWidth, final int filterHeight, final int stride) {
											return new MaxPoolingLayer(filterWidth, filterHeight, stride);
										}
										
										@Override
										public Layer newFullyConnectedLayer(final int width, final ActivationType actType) {
											return new FullyConnectedLayer(width, actType);
										}
										
										@Override
										public Layer newFullyConnectedLayer(final int width) {
											return new FullyConnectedLayer(width);
										}
										
										@Override
										public Layer newConvolutionalLayer(final int filterWidth, final int filterHeight, final int channels, final ActivationType activationType) {
											return new ConvolutionalLayer(filterWidth, filterHeight, channels, activationType);
										}
										
										@Override
										public Layer newConvolutionalLayer(final int filterWidth, final int filterHeight, final int channels, final int stride, final ActivationType activationType) {
											return new ConvolutionalLayer(filterWidth, filterHeight, channels, stride, activationType);
										}
								
										@Override
										public Layer newInputLayer(int width, int height, int depth) {
											return new InputLayer(width, height, depth);
										}
								
										@Override
										public Layer newInputLayer(int width, int height) {
											return new InputLayer(width, height);
										}
								
										@Override
										public Layer newInputLayer(int width) {
											return new InputLayer(width);
										}
									};
 
	
	StandaloneNeuralNetwork() {
	}

	@Override
	public boolean canServe(final URI resource) throws NullPointerException { 
		if (resource == null) {
			throw new NullPointerException("Resource URI can't be null");
		}
		else {
			return URIUtils.canServeURI(resource, STANDALONE_URL) && resource.toString().contains(":"+getNetworkType().getSubscheme()+":");
		}
	}
	
	@Override
	public TensorFactory getTensorFactory() {
		return tf;
	}
	
	@Override
	public LayerFactory getLayerFactory() {
		return lf; 	
	}
}
