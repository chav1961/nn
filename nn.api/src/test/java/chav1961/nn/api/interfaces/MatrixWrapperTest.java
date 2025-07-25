package chav1961.nn.api.interfaces;

import org.junit.Assert;
import org.junit.Test;

public class MatrixWrapperTest {

	@Test
	public void basicTest() {
		final MatrixWrapper	mw = new MatrixWrapperImpl(MatrixClass.FLOAT_ARRAY, new int[] {2}, new float[] {1,2});
		
		Assert.assertEquals(MatrixClass.FLOAT_ARRAY, mw.getMatrixClass());
		Assert.assertArrayEquals(new int[] {2}, mw.getDimensions());
		Assert.assertArrayEquals(new float[] {1, 2}, mw.getContent(), 0.001f);
	}
	
	@Test
	public void interfaceOfTest() {
		final MatrixWrapper	mw = MatrixWrapper.of(new float[] {1,2,3});
		
		Assert.assertEquals(MatrixClass.FLOAT_ARRAY, mw.getMatrixClass());
		Assert.assertArrayEquals(new int[] {3}, mw.getDimensions());
		Assert.assertArrayEquals(new float[] {1, 2, 3}, mw.getContent(), 0.001f);
		
		try{MatrixWrapper.of((float[])null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}

		final MatrixWrapper	mwd = MatrixWrapper.of(new double[] {1,2,3});
		
		Assert.assertEquals(MatrixClass.DOUBLE_ARRAY, mwd.getMatrixClass());
		Assert.assertArrayEquals(new int[] {3}, mwd.getDimensions());
		Assert.assertArrayEquals(new double[] {1, 2, 3}, mwd.getContent(), 0.001f);
		
		try{MatrixWrapper.of((double[])null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}

		final MatrixWrapper	mw2 = MatrixWrapper.of(new float[][] {new float[] {1,2,3}, new float[] {4,5,6}});
		
		Assert.assertEquals(MatrixClass.FLOAT2_ARRAY, mw2.getMatrixClass());
		Assert.assertArrayEquals(new int[] {2, 3}, mw2.getDimensions());
		Assert.assertArrayEquals(new float[] {1, 2, 3}, ((float[][])mw2.getContent())[0], 0.001f);
		
		try{MatrixWrapper.of((float[][])null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{MatrixWrapper.of(new float[][] {new float[] {1,2,3}, null});
			Assert.fail("Mandatory exception was not detected (nulls inside the 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{MatrixWrapper.of(new float[][] {new float[] {1,2,3}, new float[] {4,5}});
			Assert.fail("Mandatory exception was not detected (nulls inside the 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	
		final MatrixWrapper	mwd2 = MatrixWrapper.of(new double[][] {new double[] {1,2,3}, new double[] {4,5,6}});
		
		Assert.assertEquals(MatrixClass.DOUBLE2_ARRAY, mwd2.getMatrixClass());
		Assert.assertArrayEquals(new int[] {2, 3}, mwd2.getDimensions());
		Assert.assertArrayEquals(new double[] {1, 2, 3}, ((double[][])mwd2.getContent())[0], 0.001f);
		
		try{MatrixWrapper.of((double[][])null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{MatrixWrapper.of(new double[][] {new double[] {1,2,3}, null});
			Assert.fail("Mandatory exception was not detected (nulls inside the 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{MatrixWrapper.of(new double[][] {new double[] {1,2,3}, new double[] {4,5}});
			Assert.fail("Mandatory exception was not detected (nulls inside the 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	}	
}
