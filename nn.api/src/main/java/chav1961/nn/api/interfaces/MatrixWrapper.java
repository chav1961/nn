package chav1961.nn.api.interfaces;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;

public interface MatrixWrapper {
	MatrixClass getMatrixClass();
	int[] getDimensions();
	<T> T getContent();	
	
	void upload(DataOutput target) throws IOException;

	public static MatrixWrapper random(final MatrixClass clazz, final int... dimensions) throws NullPointerException, IllegalArgumentException{
		return random(clazz, System.nanoTime(), dimensions);
	}

	public static MatrixWrapper random(final MatrixClass clazz, final long seed, final int... dimensions) throws NullPointerException, IllegalArgumentException{
		if (clazz == null) {
			throw new NullPointerException("Matrix class can't be null");
		}
		else if (dimensions == null || dimensions.length != clazz.numberOfDimensions()) {
			throw new IllegalArgumentException("Matrix dimensions can't be null and must contain exactly ["+clazz.numberOfDimensions()+"] numbers");
		}
		else {
			checkDimensions(dimensions);
			final Random	rand = new Random(seed);
			
			switch (clazz) {
				case DOUBLE2_ARRAY	:
					final double[][]	d2Result = new double[dimensions[0]][dimensions[1]];
					
					for(double[] item : d2Result) {
						for(int index = 0; index < item.length; index++) {
							item[index] = rand.nextDouble();
						}
					}
					return new MatrixWrapperImpl(clazz, dimensions, d2Result);
				case DOUBLE_ARRAY	:
					final double[]		dResult = new double[dimensions[0]];

					for(int index = 0; index < dResult.length; index++) {
						dResult[index] = rand.nextDouble();
					}
					return new MatrixWrapperImpl(clazz, dimensions, dResult);
				case FLOAT2_ARRAY	:
					final float[][]		f2Result = new float[dimensions[0]][dimensions[1]];
					
					for(float[] item : f2Result) {
						for(int index = 0; index < item.length; index++) {
							item[index] = rand.nextFloat();
						}
					}
					return new MatrixWrapperImpl(clazz, dimensions, f2Result);
				case FLOAT_ARRAY	:
					final float[]		fResult = new float[dimensions[0]];

					for(int index = 0; index < fResult.length; index++) {
						fResult[index] = rand.nextFloat();
					}
					return new MatrixWrapperImpl(clazz, dimensions, fResult);
				default:
					throw new UnsupportedOperationException("Matrix class ["+clazz+"] is not supported yet");
			}
		}
	}
	
	public static MatrixWrapper download(DataInput source, MatrixClass clazz, int... dimensions) throws IOException, NullPointerException, IllegalArgumentException{
		if (source == null) {
			throw new NullPointerException("Input source can't be null");
		}
		else if (clazz == null) {
			throw new NullPointerException("Matrix class can't be null");
		}
		else if (dimensions == null || dimensions.length != clazz.numberOfDimensions()) {
			throw new IllegalArgumentException("Matrix dimensions can't be null and must contain exactly ["+clazz.numberOfDimensions()+"] numbers");
		}
		else {
			checkDimensions(dimensions);
			
			switch (clazz) {
				case DOUBLE2_ARRAY	:
					final double[][]	d2Result = new double[dimensions[0]][dimensions[1]];
					
					for(double[] item : d2Result) {
						for(int index = 0; index < item.length; index++) {
							item[index] = source.readDouble();
						}
					}
					break;
				case DOUBLE_ARRAY	:
					final double[]		dResult = new double[dimensions[0]];

					for(int index = 0; index < dResult.length; index++) {
						dResult[index] = source.readDouble();
					}
					break;
				case FLOAT2_ARRAY	:
					final float[][]		f2Result = new float[dimensions[0]][dimensions[1]];
					
					for(float[] item : f2Result) {
						for(int index = 0; index < item.length; index++) {
							item[index] = source.readFloat();
						}
					}
					break;
				case FLOAT_ARRAY	:
					final float[]		fResult = new float[dimensions[0]];

					for(int index = 0; index < fResult.length; index++) {
						fResult[index] = source.readFloat();
					}
					break;
				default:
					throw new UnsupportedOperationException("Matrix class ["+clazz+"] is not supported yet");
			}
		}
		return null;
	}
	
	private static void checkDimensions(final int[] dimensions) {
		for(int index = 0; index < dimensions.length; index++) {
			if (dimensions[index] <= 0) {
				throw new IllegalArgumentException("Dimension # ["+index+"] must be greater than 0");
			}
		}
	}
}
