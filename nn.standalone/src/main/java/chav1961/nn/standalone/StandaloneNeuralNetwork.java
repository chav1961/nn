package chav1961.nn.standalone;


import java.net.URI;

import chav1961.nn.core.interfaces.Tensor;
import chav1961.nn.core.interfaces.TensorFactory;
import chav1961.nn.core.interfaces.Trainer;
import chav1961.nn.core.network.AbstractNeuralNetwork;
import chav1961.nn.standalone.internal.TensorImpl;
import chav1961.purelib.basic.URIUtils;

abstract class StandaloneNeuralNetwork<Tr extends Trainer> extends AbstractNeuralNetwork<Tr> {
	private static final long serialVersionUID = -1188484880395191026L;

	public static final String	STANDALONE_SCHEME = "standalone";
	private static final URI	STANDALONE_URL = URI.create(NEURAL_NETWORK_SCHEME+":"+STANDALONE_SCHEME+":/");
	
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
		// TODO Auto-generated method stub
		return new TensorFactory() {
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
	}
}
