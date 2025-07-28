package chav1961.nn.api.interfaces;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + Arrays.hashCode(dimensions);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MatrixWrapperImpl other = (MatrixWrapperImpl) obj;
		if (clazz != other.clazz) return false;
		if (content == null) {
			if (other.content != null) return false;
		} else if (!equals(content, other.content)) return false;
		if (!Arrays.equals(dimensions, other.dimensions)) return false;
		return true;
	}
	
	private static boolean equals(final Object left, final Object right) {
		if (left.getClass().isArray() && right.getClass().isArray()
			&& Array.getLength(left) == Array.getLength(right)
			&& left.getClass().getComponentType() == right.getClass().getComponentType())  {
			for(int index = 0, maxIndex = Array.getLength(left); index < maxIndex; index++) {
				if (!Objects.equals(Array.get(left, index), Array.get(right, index))) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
}