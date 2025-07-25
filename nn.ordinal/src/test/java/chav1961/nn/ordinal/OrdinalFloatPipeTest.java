package chav1961.nn.ordinal;

import org.junit.Assert;
import org.junit.Test;

import chav1961.purelib.basic.exceptions.CalculationException;

public class OrdinalFloatPipeTest {

	@Test
	public void muptiplyTest() throws CalculationException {
		final float[]	source = new float[] {1,2,3};
		final float[][] matrix = new float[][] {
									new float[] {1,2,3},
									new float[] {4,5,6},
									new float[] {7,8,9}
								};
		
		Assert.assertArrayEquals(new float[] {30, 36, 42}, OrdinalFloatPipe.forwardMutiply(source, matrix, 1), 0.001f);
		Assert.assertArrayEquals(new float[] {30, 36, 42}, OrdinalFloatPipe.forwardMutiply(source, matrix, 2), 0.001f);
		Assert.assertArrayEquals(new float[] {30, 36, 42}, OrdinalFloatPipe.forwardMutiply(source, matrix, 3), 0.001f);
		Assert.assertArrayEquals(new float[] {30, 36, 42}, OrdinalFloatPipe.forwardMutiply(source, matrix, 4), 0.001f);

		final float[][] matrix2 = new float[][] {
									new float[] {1,2,3,4},
									new float[] {5,6,7,8},
									new float[] {9,10,11,12}
								};
		Assert.assertArrayEquals(new float[] {38, 44, 50, 56}, OrdinalFloatPipe.forwardMutiply(source, matrix2, 1), 0.001f);
		Assert.assertArrayEquals(new float[] {38, 44, 50, 56}, OrdinalFloatPipe.forwardMutiply(source, matrix2, 2), 0.001f);
		Assert.assertArrayEquals(new float[] {38, 44, 50, 56}, OrdinalFloatPipe.forwardMutiply(source, matrix2, 3), 0.001f);
		Assert.assertArrayEquals(new float[] {38, 44, 50, 56}, OrdinalFloatPipe.forwardMutiply(source, matrix2, 4), 0.001f);
	}

	@Test
	public void softMaxTest() throws CalculationException {
		final float[]	source = new float[] {0.5f,0.6f,0.7f};

		Assert.assertArrayEquals(new float[] {0.30061f, 0.332225f, 0.367165f}, OrdinalFloatPipe.activateSoftMax(source, 1), 0.001f);
		Assert.assertArrayEquals(new float[] {0.30061f, 0.332225f, 0.367165f}, OrdinalFloatPipe.activateSoftMax(source, 2), 0.001f);
		Assert.assertArrayEquals(new float[] {0.30061f, 0.332225f, 0.367165f}, OrdinalFloatPipe.activateSoftMax(source, 3), 0.001f);
		Assert.assertArrayEquals(new float[] {0.30061f, 0.332225f, 0.367165f}, OrdinalFloatPipe.activateSoftMax(source, 4), 0.001f);
	}
}
