package chav1961.nn.standalone.util;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;

import chav1961.nn.api.interfaces.Tenzor;
import chav1961.purelib.basic.exceptions.SyntaxException;

public class StandaloneTenzorFactoryTest {
	@Test
	public void spiTest() {
		final StandaloneTenzorFactory	stf = new StandaloneTenzorFactory();
		final URI						serveURI = URI.create(Tenzor.TenzorFactory.TENZOR_FACTORY_SCHEMA+":standalone:/"); 
		
		Assert.assertTrue(stf.canServe(serveURI));
		Assert.assertEquals(stf, stf.newInstance(serveURI));
		
		try {
			stf.canServe(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			stf.newInstance((URI)null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
	}

	@Test
	public void createTenzorTest() {
		final StandaloneTenzorFactory	stf = new StandaloneTenzorFactory(); 
		final Tenzor	t1 = stf.newInstance(2, 3, 4);

		Assert.assertEquals(3, t1.getArity());
		Assert.assertEquals(2, t1.getSize(0));
		Assert.assertEquals(3, t1.getSize(1));
		Assert.assertEquals(4, t1.getSize(2));
		Assert.assertEquals(2*3*4, t1.getContent().length);

		final Tenzor	dup = t1.duplicate();

		Assert.assertTrue(dup.equals(t1, 0.001f));
		
		try {
			stf.newInstance(0);
			Assert.fail("Mandatory exception was not detected (non-positive size)");
		} catch (IllegalArgumentException exc) {
		}
		try {
			stf.newInstance(1, 0);
			Assert.fail("Mandatory exception was not detected (non-positive size)");
		} catch (IllegalArgumentException exc) {
		}

		
		final Tenzor	t2 = stf.newInstance(new float[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24},  2, 3, 4);
		
		Assert.assertEquals(3, t2.getArity());
		Assert.assertEquals(2, t2.getSize(0));
		Assert.assertEquals(3, t2.getSize(1));
		Assert.assertEquals(4, t2.getSize(2));
		Assert.assertEquals(2*3*4, t2.getContent().length);
		Assert.assertArrayEquals(new float[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24}, t2.getContent(), 0.001f);
		
		try {
			stf.newInstance(null, 1, 1);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try {
			stf.newInstance(new float[0], 1, 1);
			Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try {
			stf.newInstance(new float[1], 2, 2);
			Assert.fail("Mandatory exception was not detected (no enough data in the 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try {
			stf.newInstance(new float[1], 0, 2);
			Assert.fail("Mandatory exception was not detected (non=-positive size)");
		} catch (IllegalArgumentException exc) {
		}
		try {
			stf.newInstance(new float[1], 2, 0);
			Assert.fail("Mandatory exception was not detected (non=-positive size)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void basicTenzorTest() {
		final StandaloneTenzorFactory	stf = new StandaloneTenzorFactory(); 
		final Tenzor	t1 = stf.newInstance(2, 3);
		final float[]	temp = new float[6];
		
		// fill() test
		t1.fill(1, -1, -1);
		Assert.assertArrayEquals(new float[] {1,1,1, 1,1,1}, t1.getContent(), 0.001f);
		t1.fill(2, 0, -1);
		Assert.assertArrayEquals(new float[] {2,2,2, 1,1,1}, t1.getContent(), 0.001f);
		t1.fill(3, -1, 1);
		Assert.assertArrayEquals(new float[] {2,3,2, 1,3,1}, t1.getContent(), 0.001f);
		
		try{t1.fill(0, 1);
			Assert.fail("Mandatory exception was not detected (wrong number of dimensions)");
		} catch (IllegalArgumentException exc) {
		}
		
		// get()/set() test
		Assert.assertEquals(3, t1.get(0, 1), 0.001f);
		t1.set(4, 0, 1);
		Assert.assertEquals(4, t1.get(0, 1), 0.001f);
		
		try{t1.get(1);
			Assert.fail("Mandatory exception was not detected (wrong number of dimensions)");
		} catch (IllegalArgumentException exc) {
		}
		try{t1.set(0,1);
			Assert.fail("Mandatory exception was not detected (wrong number of dimensions)");
		} catch (IllegalArgumentException exc) {
		}
		
		// slice get() test
		Arrays.fill(temp, 0);
		t1.get(temp, -1, -1);
		Assert.assertArrayEquals(new float[] {2,4,2, 1,3,1}, temp, 0.001f);
		
		Arrays.fill(temp, 0);
		t1.get(temp, 0, -1);
		Assert.assertArrayEquals(new float[] {2,4,2, 0,0,0}, temp, 0.001f);

		Arrays.fill(temp, 0);
		t1.get(temp, -1, 1);
		Assert.assertArrayEquals(new float[] {4,3, 0,0, 0,0}, temp, 0.001f);

		try{t1.get(null, 1, 1);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{t1.get(new float[0], 1, 1);
			Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{t1.get(new float[1], -1, -1);
			Assert.fail("Mandatory exception was not detected (not enough space in the 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}

		// slice set() test
		t1.set(new float[] {5}, 1, 1);
		Assert.assertArrayEquals(new float[] {2,4,2, 1,5,1}, t1.getContent(),  0.001f);

		t1.set(new float[] {6, 7, 8}, 1, -1);
		Assert.assertArrayEquals(new float[] {2,4,2, 6,7,8}, t1.getContent(),  0.001f);

		t1.set(new float[] {9, 10}, -1, 0);
		Assert.assertArrayEquals(new float[] {9,4,2, 10,7,8}, t1.getContent(),  0.001f);

		try{t1.set(null, 1, 1);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{t1.set(new float[0], 1, 1);
			Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{t1.set(new float[1], -1, -1);
			Assert.fail("Mandatory exception was not detected (do enough data in the 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{t1.set(new float[1], -1);
			Assert.fail("Mandatory exception was not detected (wrong dimensions)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void arithmeticTenzorTest() {
		final StandaloneTenzorFactory	stf = new StandaloneTenzorFactory(); 
		final Tenzor	t1 = stf.newInstance(new float[] {1,2,3, 4,5,6}, 2, 3);
		final Tenzor	t2 = stf.newInstance(new float[] {1,2,3, 4,5,6}, 2, 3);
		final Tenzor	t2x = stf.newInstance(new float[] {1,2, 3,4, 5,6}, 3, 2);

		// add
		Assert.assertArrayEquals(new float[] {2,4,6, 8,10,12}, t1.addN(t2).getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {11,12,13, 14,15,16}, t1.addN(10).getContent(), 0.001f);
		
		try{t1.addN(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{t1.addN(stf.newInstance(2, 2));
			Assert.fail("Mandatory exception was not detected (incompatible dimensions)");
		} catch (IllegalArgumentException exc) {
		}
		
		// sub
		Assert.assertArrayEquals(new float[] {0,0,0, 0,0,0}, t1.subN(t2).getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {-9,-8,-7, -6,-5,-4}, t1.subN(10).getContent(), 0.001f);
		
		try{t1.subN(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{t1.subN(stf.newInstance(2, 2));
			Assert.fail("Mandatory exception was not detected (incompatible dimensions)");
		} catch (IllegalArgumentException exc) {
		}
		
		// mul
		Assert.assertArrayEquals(new float[] {1,4,9, 16,25,36}, t1.mulN(t2).getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {2,4,6, 8,10,12}, t1.mulN(2).getContent(), 0.001f);

		try{t1.mulN(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{t1.mulN(stf.newInstance(2, 2));
			Assert.fail("Mandatory exception was not detected (incompatible dimensions)");
		} catch (IllegalArgumentException exc) {
		}

		// matrix mul
		Assert.assertArrayEquals(new float[] {22,28, 49,64}, t1.duplicate().matrixMul(t2x).getContent(), 0.001f);

		try{t1.matrixMul(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{t1.matrixMul(t2);
			Assert.fail("Mandatory exception was not detected (incompatible dimensions)");
		} catch (IllegalArgumentException exc) {
		}
		
		// div
		Assert.assertArrayEquals(new float[] {1,1,1, 1,1,1}, t1.divN(t2).getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {2,4,6, 8,10,12}, t1.divN(0.5f).getContent(), 0.001f);
		
		try{t1.divN(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{t1.divN(stf.newInstance(2, 2));
			Assert.fail("Mandatory exception was not detected (incompatible dimensions)");
		} catch (IllegalArgumentException exc) {
		}

		// trans
		Assert.assertArrayEquals(new float[] {1,4, 2,5, 3,6}, t1.duplicate().trans().getContent(), 0.001f);
		
		// convert and forEach
		Assert.assertArrayEquals(new float[] {-1,-2,-3, -4,-5,-6}, t1.convertN((v,i)->-v).getContent(), 0.001f);
		
		try{t1.convertN(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		
		final float[]	sum = new float[] {0};
		
		t1.forEach((v,i)->{
			sum[0] += v;
		});
		Assert.assertEquals(21, sum[0], 0.001f);
		
		try{t1.forEach(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
	}

	@Test
	public void calculateTenzorTest() throws SyntaxException {
		final StandaloneTenzorFactory	stf = new StandaloneTenzorFactory(); 
		final Tenzor	t1 = stf.newInstance(new float[] {1,2,3, 4,5,6}, 2, 3);

		// Terms
		Assert.assertArrayEquals(new float[] {1}, t1.calculate("1").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {1,2,3, 4,5,6}, t1.calculate("%0").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {6}, t1.calculate("max(%0)").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {1}, t1.calculate("min(%0)").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {21}, t1.calculate("sumabs(%0)").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {91}, t1.calculate("sumsqr(%0)").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {1, (float) Math.sqrt(2), (float) Math.sqrt(3), 2, (float) Math.sqrt(5), (float) Math.sqrt(6)}, t1.calculate("sqrt(%0)").getContent(), 0.001f);
		
		// Unaries
		Assert.assertArrayEquals(new float[] {-1}, t1.calculate("-1").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {-1,-2,-3, -4,-5,-6}, t1.calculate("-%0").getContent(), 0.001f);

		// Infix
		Assert.assertArrayEquals(new float[] {2,3,4, 5,6,7}, t1.calculate("%0 + 1").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {2,4,6, 8,10,12}, t1.calculate("%0 + %1", t1).getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {0,1,2, 3,4,5}, t1.calculate("%0 - 1").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {0,0,0, 0,0,0}, t1.calculate("%0 - %1", t1).getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {2,4,6, 8,10,12}, t1.calculate("%0 * 2").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {1,4,9, 16,25,36}, t1.calculate("%0 * %1", t1).getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {0.1f,0.2f,0.3f, 0.4f,0.5f,0.6f}, t1.calculate("%0 / 10").getContent(), 0.001f);
		Assert.assertArrayEquals(new float[] {1,1,1, 1,1,1}, t1.calculate("%0 / %1", t1).getContent(), 0.001f);
	}
}
