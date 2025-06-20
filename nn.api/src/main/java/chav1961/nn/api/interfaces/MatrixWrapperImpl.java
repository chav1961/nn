package chav1961.nn.api.interfaces;

import java.io.DataOutput;
import java.io.IOException;

class MatrixWrapperImpl implements MatrixWrapper {
	private final MatrixClass 	clazz;
	private final int[] 		dimensions;
	private final Object		content;
	
	MatrixWrapperImpl(final MatrixClass clazz, final int[] dimensions, final Object content) {
		this.clazz = clazz;
		this.dimensions = dimensions.clone();
		this.content = content;
	}

	@Override
	public MatrixClass getMatrixClass() {
		return clazz;
	}

	@Override
	public int[] getDimensions() {
		return dimensions.clone();
	}

	@Override
	public <T> T getContent() {
		return (T)content;
	}

	@Override
	public void upload(final DataOutput target) throws IOException {
		if (target == null) {
			throw new NullPointerException("Target can't be null");
		}
		else {
			switch (getMatrixClass()) {
				case DOUBLE2_ARRAY	:
					final double[][]	d2Content = getContent();
					
					for(double[] item : d2Content) {
						for(double val : item) {
							target.writeDouble(val);
						}
					}
					break;
				case DOUBLE_ARRAY	:
					final double[]		dContent = getContent();
					
					for(double val : dContent) {
						target.writeDouble(val);
					}
					break;
				case FLOAT2_ARRAY	:
					final float[][]		f2Content = getContent();
					
					for(float[] item : f2Content) {
						for(float val : item) {
							target.writeFloat(val);
						}
					}
					break;
				case FLOAT_ARRAY	:
					final float[]		fContent = getContent();
					
					for(float val : fContent) {
						target.writeFloat(val);
					}
					break;
				default:
					throw new UnsupportedOperationException("Matrix class ["+getMatrixClass()+"] is not supported yet");
			}
		}
	}
}