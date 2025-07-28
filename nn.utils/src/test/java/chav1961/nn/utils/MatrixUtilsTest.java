package chav1961.nn.utils;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.MatrixClass;
import chav1961.nn.api.interfaces.MatrixWrapper;
import chav1961.purelib.basic.exceptions.CalculationException;

public class MatrixUtilsTest {

	@Test
	public void staticFloatMultiplyTest() throws CalculationException {
		final float[]	source = new float[] {1,2,3};
		final float[][] matrix = new float[][] {
									new float[] {1,2,3},
									new float[] {4,5,6},
									new float[] {7,8,9}
								};
		
		Assert.assertArrayEquals(new float[] {30, 36, 42}, MatrixUtils.multiplyFloatVectorAndMatrix(source, matrix, 1), 0.001f);
		Assert.assertArrayEquals(new float[] {30, 36, 42}, MatrixUtils.multiplyFloatVectorAndMatrix(source, matrix, 2), 0.001f);
		Assert.assertArrayEquals(new float[] {30, 36, 42}, MatrixUtils.multiplyFloatVectorAndMatrix(source, matrix, 3), 0.001f);
		Assert.assertArrayEquals(new float[] {30, 36, 42}, MatrixUtils.multiplyFloatVectorAndMatrix(source, matrix, 4), 0.001f);

		final float[][] matrix2 = new float[][] {
									new float[] {1,2,3,4},
									new float[] {5,6,7,8},
									new float[] {9,10,11,12}
								};
		Assert.assertArrayEquals(new float[] {38, 44, 50, 56}, MatrixUtils.multiplyFloatVectorAndMatrix(source, matrix2, 1), 0.001f);
		Assert.assertArrayEquals(new float[] {38, 44, 50, 56}, MatrixUtils.multiplyFloatVectorAndMatrix(source, matrix2, 2), 0.001f);
		Assert.assertArrayEquals(new float[] {38, 44, 50, 56}, MatrixUtils.multiplyFloatVectorAndMatrix(source, matrix2, 3), 0.001f);
		Assert.assertArrayEquals(new float[] {38, 44, 50, 56}, MatrixUtils.multiplyFloatVectorAndMatrix(source, matrix2, 4), 0.001f);
	}

	@Test
	public void staticDoubleMultiplyTest() throws CalculationException {
		final double[]		source = new double[] {1,2,3};
		final double[][]	matrix = new double[][] {
									new double[] {1,2,3},
									new double[] {4,5,6},
									new double[] {7,8,9}
								};
		
		Assert.assertArrayEquals(new double[] {30, 36, 42}, MatrixUtils.multiplyDoubleVectorAndMatrix(source, matrix, 1), 0.001);
		Assert.assertArrayEquals(new double[] {30, 36, 42}, MatrixUtils.multiplyDoubleVectorAndMatrix(source, matrix, 2), 0.001);
		Assert.assertArrayEquals(new double[] {30, 36, 42}, MatrixUtils.multiplyDoubleVectorAndMatrix(source, matrix, 3), 0.001);
		Assert.assertArrayEquals(new double[] {30, 36, 42}, MatrixUtils.multiplyDoubleVectorAndMatrix(source, matrix, 4), 0.001);

		final double[][] 	matrix2 = new double[][] {
									new double[] {1,2,3,4},
									new double[] {5,6,7,8},
									new double[] {9,10,11,12}
								};
		Assert.assertArrayEquals(new double[] {38, 44, 50, 56}, MatrixUtils.multiplyDoubleVectorAndMatrix(source, matrix2, 1), 0.001);
		Assert.assertArrayEquals(new double[] {38, 44, 50, 56}, MatrixUtils.multiplyDoubleVectorAndMatrix(source, matrix2, 2), 0.001);
		Assert.assertArrayEquals(new double[] {38, 44, 50, 56}, MatrixUtils.multiplyDoubleVectorAndMatrix(source, matrix2, 3), 0.001);
		Assert.assertArrayEquals(new double[] {38, 44, 50, 56}, MatrixUtils.multiplyDoubleVectorAndMatrix(source, matrix2, 4), 0.001);
	}

	@Test
	public void multiplyTest() throws CalculationException {
		final MatrixUtils	mu = MatrixUtils.getDefaultInstance();

		final float[]		source1 = new float[] {1,2,3};
		final float[][] 	matrix1 = new float[][] {
									new float[] {1,2,3},
									new float[] {4,5,6},
									new float[] {7,8,9}
								};

		Assert.assertEquals(MatrixWrapper.of(new float[] {30, 36, 42}), 
									mu.multiplyVectorAndMatrix(
										MatrixWrapper.of(source1), 
										MatrixWrapper.of(matrix1), 
										1
									)
								);
								
		
		final double[]		source2 = new double[] {1,2,3};
		final double[][]	matrix2 = new double[][] {
									new double[] {1,2,3},
									new double[] {4,5,6},
									new double[] {7,8,9}
								};
		
		Assert.assertEquals(MatrixWrapper.of(new double[] {30, 36, 42}), 
								mu.multiplyVectorAndMatrix(
									MatrixWrapper.of(source2), 
									MatrixWrapper.of(matrix2), 
									1
								)
							);
		
		try{mu.multiplyVectorAndMatrix(null, MatrixWrapper.of(matrix1), 1);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{mu.multiplyVectorAndMatrix(MatrixWrapper.of(matrix1), MatrixWrapper.of(matrix1), 1);
			Assert.fail("Mandatory exception was not detected (1-st argument is not a vector)");
		} catch (IllegalArgumentException exc) {
		}
		try{mu.multiplyVectorAndMatrix(MatrixWrapper.of(source2), MatrixWrapper.of(matrix1), 1);
			Assert.fail("Mandatory exception was not detected (1-st argument type incompatible with 2-nd argument type)");
		} catch (IllegalArgumentException exc) {
		}
		final float[]		source3 = new float[] {1,2,3,4};

		try{mu.multiplyVectorAndMatrix(MatrixWrapper.of(source1), null, 1);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try{mu.multiplyVectorAndMatrix(MatrixWrapper.of(source1), MatrixWrapper.of(source1), 1);
			Assert.fail("Mandatory exception was not detected (2-nd argument is not a matrix)");
		} catch (IllegalArgumentException exc) {
		}
		try{mu.multiplyVectorAndMatrix(MatrixWrapper.of(source3), MatrixWrapper.of(source1), 1);
			Assert.fail("Mandatory exception was not detected (1-st argument not corrensponding dimensions with 2-nd argument)");
		} catch (IllegalArgumentException exc) {
		}

		try{mu.multiplyVectorAndMatrix(MatrixWrapper.of(source1), MatrixWrapper.of(matrix1), 0);
			Assert.fail("Mandatory exception was not detected (3-rd argument out of range)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void staticFloatTransposeTest() throws CalculationException {
		final float[][] matrix1 = new float[][] {
								new float[] {1,2,3},
								new float[] {4,5,6},
								new float[] {7,8,9}
							};
		final float[][] result1 = new float[][] {
								new float[] {1,4,7},
								new float[] {2,5,8},
								new float[] {3,6,9}
							};
							
		compare(result1, MatrixUtils.transposeFloatMatrix(matrix1, 1)); 
		compare(result1, MatrixUtils.transposeFloatMatrix(matrix1, 2)); 
		compare(result1, MatrixUtils.transposeFloatMatrix(matrix1, 3)); 
		compare(result1, MatrixUtils.transposeFloatMatrix(matrix1, 4)); 

		final double[][] matrix2 = new double[][] {
								new double[] {1,2,3},
								new double[] {4,5,6},
								new double[] {7,8,9}
							};
		final double[][] result2 = new double[][] {
								new double[] {1,4,7},
								new double[] {2,5,8},
								new double[] {3,6,9}
							};
				
		compare(result2, MatrixUtils.transposeDoubleMatrix(matrix2, 1)); 
		compare(result2, MatrixUtils.transposeDoubleMatrix(matrix2, 2)); 
		compare(result2, MatrixUtils.transposeDoubleMatrix(matrix2, 3)); 
		compare(result2, MatrixUtils.transposeDoubleMatrix(matrix2, 4)); 
	}

	private void compare(final float[][] left, final float[][] right) {
		Assert.assertEquals(left.length, right.length);
		for(int index = 0; index < left.length; index++) {
			Assert.assertArrayEquals(left[index], right[index], 0.001f);
		}
	}	

	private void compare(final double[][] left, final double[][] right) {
		Assert.assertEquals(left.length, right.length);
		for(int index = 0; index < left.length; index++) {
			Assert.assertArrayEquals(left[index], right[index], 0.001);
		}
	}	
}
