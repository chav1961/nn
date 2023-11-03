package chav1961.nn.standalone.layer;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.Layer.InternalTenzorType;
import chav1961.nn.api.interfaces.Layer.LayerFactory;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.Tenzor.TenzorFactory;
import chav1961.nn.core.network.NeuralNetworkImpl;
import chav1961.nn.standalone.util.StandaloneTenzorFactory;

public class InputLayerTest {
	@Test
	public void lifeCycleTest() {
		final TenzorFactory	tf = new StandaloneTenzorFactory();
		final LayerFactory	lf = new StandaloneLayerFactory();
		final NeuralNetwork	nn = new NeuralNetworkImpl(tf, lf);
		final Tenzor		t = tf.newInstance(10);
		final Tenzor		t2 = tf.newInstance(20);
		final InputLayer	il = new InputLayer(10);
		final OutputLayer	ol = new OutputLayer(10);
		
		Assert.assertEquals(1, il.getArity());
		Assert.assertEquals(10, il.getSize(0));
		
		try{il.getInternalTenzor(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{il.getInternalTenzor(InternalTenzorType.WEIGHTS);
			Assert.fail("Mandatory exception was not detected (unsupported 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		
		Assert.assertFalse(il.canConnectBefore(nn, il));
		
		try {il.canConnectBefore(null, il);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {il.canConnectBefore(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		Assert.assertTrue(il.canConnectAfter(nn, ol));
		Assert.assertFalse(il.canConnectAfter(nn, il));
		
		try {il.canConnectAfter(null, il);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {il.canConnectAfter(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		try {
			il.connectAfter(nn, ol);
			Assert.fail("Mandatory exceptin was not detected (calling connect before preparation)");
		} catch (IllegalStateException exc) {
		}
		try {
			il.forward(nn, t);
			Assert.fail("Mandatory exceptin was not detected (calling forware before preparation)");
		} catch (IllegalStateException exc) {
		}
		
		il.prepare(nn, false);
		
		try {
			il.prepare(null, false);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			il.prepare(nn, false);
			Assert.fail("Mandatory exceptin was not detected (attempt to prepare twice)");
		} catch (IllegalStateException exc) {
		}
		
		
		il.connectAfter(nn, ol);
		
		try {
			il.connectAfter(null, ol);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			il.connectAfter(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		
		Assert.assertTrue(t.equals(il.forward(nn, t), 0.01f));
		
		try {
			il.forward(null, t);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			il.forward(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try {
			il.forward(nn, t2);
			Assert.fail("Mandatory exceptin was not detected (2-nd argument size differs)");
		} catch (IllegalArgumentException exc) {
		}
		
		Assert.assertTrue(t.equals(il.backward(nn, t), 0.01f));
		
		try {
			il.backward(null, t);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			il.backward(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try {
			il.backward(nn, t2);
			Assert.fail("Mandatory exceptin was not detected (2-nd argument size differs)");
		} catch (IllegalArgumentException exc) {
		}
		
		il.unprepare(nn);
		
		try {il.unprepare(null);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {il.unprepare(nn);
			Assert.fail("Mandatory exceptin was not detected (attempr to unprepare unprepared layer)");
		} catch (IllegalStateException exc) {
		}
	}
}
