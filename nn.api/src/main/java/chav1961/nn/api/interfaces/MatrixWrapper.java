package chav1961.nn.api.interfaces;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Random;

import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.interfaces.DoubleSequence;

public interface MatrixWrapper {
	MatrixClass getMatrixClass();
	int[] getDimensions();
	<T> T getContent();	
	
	void upload(DataOutput target) throws IOException;

	public static MatrixWrapper random(final MatrixClass clazz, final int... dimensions) throws NullPointerException, IllegalArgumentException{
		return random(clazz, System.nanoTime(), dimensions);
	}

	public static MatrixWrapper random(final MatrixClass clazz, final long seed, final int... dimensions) throws NullPointerException, IllegalArgumentException{
		final DoubleSequence	ds = new DoubleSequence() {
									final Random	rand = new Random(seed);
									
									@Override
									public double next() {
										return rand.nextDouble();
									}
								};
		
		return random(clazz, ds, dimensions);
	}	
	
	public static MatrixWrapper random(final MatrixClass clazz, final DoubleSequence seq, final int... dimensions) throws NullPointerException, IllegalArgumentException{
		if (clazz == null) {
			throw new NullPointerException("Matrix class can't be null");
		}
		else if (seq == null) {
			throw new NullPointerException("Sequence generator can't be null");
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
							item[index] = seq.next();
						}
					}
					return new MatrixWrapperImpl(clazz, dimensions, d2Result);
				case DOUBLE_ARRAY	:
					final double[]		dResult = new double[dimensions[0]];

					for(int index = 0; index < dResult.length; index++) {
						dResult[index] = seq.next();
					}
					return new MatrixWrapperImpl(clazz, dimensions, dResult);
				case FLOAT2_ARRAY	:
					final float[][]		f2Result = new float[dimensions[0]][dimensions[1]];
					
					for(float[] item : f2Result) {
						for(int index = 0; index < item.length; index++) {
							item[index] = (float)seq.next();
						}
					}
					return new MatrixWrapperImpl(clazz, dimensions, f2Result);
				case FLOAT_ARRAY	:
					final float[]		fResult = new float[dimensions[0]];

					for(int index = 0; index < fResult.length; index++) {
						fResult[index] = (float)seq.next();
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
	
	public static MatrixWrapper of(final float... content) {
		if (content == null) {
			throw new NullPointerException("Content to wrap can't be null"); 
		}
		else {
			return new MatrixWrapperImpl(MatrixClass.FLOAT_ARRAY, new int[] {content.length}, content);
		}
	}

	public static MatrixWrapper of(final double... content) {
		if (content == null) {
			throw new NullPointerException("Content to wrap can't be null"); 
		}
		else {
			return new MatrixWrapperImpl(MatrixClass.DOUBLE_ARRAY, new int[] {content.length}, content);
		}
	}
	
	public static MatrixWrapper of(final float[]... content) {
		if (content == null || Utils.checkArrayContent4Nulls(content) >= 0) {
			throw new IllegalArgumentException("Content to wrap is null or contains nulls inside"); 
		}
		else if (!areLineSizesIdentical(content)) {
			throw new IllegalArgumentException("Matrix lines has different sizes inside the matrix, but need to be identical"); 
		}
		else {
			return new MatrixWrapperImpl(MatrixClass.FLOAT2_ARRAY, new int[] {content.length, content[0].length}, content);
		}
	}

	public static MatrixWrapper of(final double[]... content) {
		if (content == null || Utils.checkArrayContent4Nulls(content) >= 0) {
			throw new IllegalArgumentException("Content to wrap is null or contains nulls inside"); 
		}
		else if (!areLineSizesIdentical(content)) {
			throw new IllegalArgumentException("Matrix lines has different sizes inside the matrix, but need to be identical"); 
		}
		else {
			return new MatrixWrapperImpl(MatrixClass.DOUBLE2_ARRAY, new int[] {content.length, content[0].length}, content);
		}
	}
	
	private static boolean areLineSizesIdentical(final Object[] content) {
		if (content.length == 0) {
			return true;
		}
		else {
			final int	size = Array.getLength(content[0]);
			
			for(Object item : content) {
				if (Array.getLength(item) != size) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static void checkDimensions(final int[] dimensions) {
		for(int index = 0; index < dimensions.length; index++) {
			if (dimensions[index] <= 0) {
				throw new IllegalArgumentException("Dimension # ["+index+"] must be greater than 0");
			}
		}
	}
}
