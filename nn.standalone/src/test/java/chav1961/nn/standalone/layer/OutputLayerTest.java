package chav1961.nn.standalone.layer;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.Layer.InternalTenzorType;
import chav1961.nn.api.interfaces.factories.LayerFactory;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.nn.core.network.NeuralNetworkImpl;
import chav1961.nn.standalone.util.StandaloneTenzorFactory;
import chav1961.nn.utils.calc.TenzorUtils;

public class OutputLayerTest {
	@Test
	public void lifeCycleTest() {
		final TenzorFactory	tf = new StandaloneTenzorFactory();
		final LayerFactory	lf = new StandaloneLayerFactory();
		final NeuralNetwork	nn = new NeuralNetworkImpl(tf, lf);
		final Tenzor		t = tf.newInstance(10);
		final Tenzor		t2 = tf.newInstance(20);
		final InputLayer	il = new InputLayer(10);
		final OutputLayer	ol = new OutputLayer(10);
		
		Assert.assertEquals(1, ol.getArity());
		Assert.assertEquals(10, ol.getSize(0));
		
		Assert.assertNull(ol.getInternalTenzor(InternalTenzorType.WEIGHTS));
		
		try {ol.getInternalTenzor(null);
			Assert.fail("Mandatory exception was not dectected (null -1st argument)");
		} catch (NullPointerException exc) {			
		}
		try {ol.getInternalTenzor(InternalTenzorType.UNKNOWN);
			Assert.fail("Mandatory exception was not dectected (null -1st argument)");
		} catch (IllegalArgumentException exc) {			
		}

		Assert.assertTrue(ol.canConnectBefore(nn, il));
		Assert.assertFalse(ol.canConnectBefore(nn, ol));
		
		try {ol.canConnectBefore(null, ol);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {ol.canConnectBefore(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		Assert.assertFalse(ol.canConnectAfter(nn, ol));
		
		try {ol.canConnectAfter(null, ol);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {ol.canConnectAfter(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		
		try {
			ol.connectBefore(nn, ol);
			Assert.fail("Mandatory exceptin was not detected (calling connect before preparation)");
		} catch (IllegalStateException exc) {
		}
		try {
			ol.forward(nn, t);
			Assert.fail("Mandatory exceptin was not detected (calling forware before preparation)");
		} catch (IllegalStateException exc) {
		}
		
		ol.prepare(nn, false);

		try {
			ol.prepare(null, false);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			ol.prepare(nn, false);
			Assert.fail("Mandatory exceptin was not detected (attempt to prepare twice)");
		} catch (IllegalStateException exc) {
		}
		
		ol.connectBefore(nn, il);
		Assert.assertNotNull(ol.getInternalTenzor(InternalTenzorType.WEIGHTS));
		
		((Tenzor)ol.getInternalTenzor(InternalTenzorType.WEIGHTS)).fill(1, TenzorUtils.allIndicesMask(ol.getInternalTenzor(InternalTenzorType.WEIGHTS)));

		try {
			ol.connectBefore(nn, il);
			Assert.fail("Mandatory exceptin was not detected (attempt to connect twice)");
		} catch (IllegalStateException exc) {
		}
		
		Assert.assertTrue(t.equals(ol.forward(nn, t), 0.001f));
		Assert.assertTrue(t.equals(ol.backward(nn, t), 0.001f));
	}
}
