package chav1961.nn.standalone.layer;

import org.junit.Assert;
import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.TenzorFactory;
import chav1961.nn.api.interfaces.Layer.InternalTenzorType;
import chav1961.nn.api.interfaces.LayerFactory;
import chav1961.nn.core.network.NeuralNetworkImpl;
import chav1961.nn.standalone.util.StandaloneTenzorFactory;
import chav1961.nn.utils.calc.TenzorUtils;

public class FeedForwardLayerTest {
	@Test
	public void lifeCycleTest() {
		final TenzorFactory		tf = new StandaloneTenzorFactory();
		final LayerFactory		lf = new StandaloneLayerFactory();
		final NeuralNetwork		nn = new NeuralNetworkImpl(tf, lf);
		final Tenzor			t = tf.newInstance(10);
		final Tenzor			t2 = tf.newInstance(20);
		final InputLayer		il = new InputLayer(10);
		final FeedForwardLayer	ffl = new FeedForwardLayer(10);
		final OutputLayer		ol = new OutputLayer(10);
		
		Assert.assertEquals(1, ffl.getArity());
		Assert.assertEquals(10, ffl.getSize(0));
		
		Assert.assertNull(ffl.getInternalTenzor(InternalTenzorType.WEIGHTS));
		
		try {ffl.getInternalTenzor(null);
			Assert.fail("Mandatory exception was not dectected (null -1st argument)");
		} catch (NullPointerException exc) {			
		}
		try {ffl.getInternalTenzor(InternalTenzorType.UNKNOWN);
			Assert.fail("Mandatory exception was not dectected (null -1st argument)");
		} catch (IllegalArgumentException exc) {			
		}

		Assert.assertTrue(ffl.canConnectBefore(nn, il));
		Assert.assertFalse(ffl.canConnectBefore(nn, ol));
		
		try {ffl.canConnectBefore(null, ffl);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {ffl.canConnectBefore(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		Assert.assertFalse(ffl.canConnectAfter(nn, il));
		
		try {ffl.canConnectAfter(null, ffl);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {ffl.canConnectAfter(nn, null);
			Assert.fail("Mandatory exceptin was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		
		try {
			ffl.connectBefore(nn, ffl);
			Assert.fail("Mandatory exceptin was not detected (calling connect before preparation)");
		} catch (IllegalStateException exc) {
		}
		try {
			ffl.forward(nn, t);
			Assert.fail("Mandatory exceptin was not detected (calling forware before preparation)");
		} catch (IllegalStateException exc) {
		}
		
		ffl.prepare(nn, false);

		try {
			ffl.prepare(null, false);
			Assert.fail("Mandatory exceptin was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			ffl.prepare(nn, false);
			Assert.fail("Mandatory exceptin was not detected (attempt to prepare twice)");
		} catch (IllegalStateException exc) {
		}
		
		ffl.connectBefore(nn, il);
		Assert.assertNotNull(ffl.getInternalTenzor(InternalTenzorType.WEIGHTS));
		
		((Tenzor)ffl.getInternalTenzor(InternalTenzorType.WEIGHTS)).fill(1, TenzorUtils.allIndicesMask(ffl.getInternalTenzor(InternalTenzorType.WEIGHTS)));

		try {
			ffl.connectBefore(nn, il);
			Assert.fail("Mandatory exceptin was not detected (attempt to connect twice)");
		} catch (IllegalStateException exc) {
		}
		
		Assert.assertTrue(t.equals(ffl.forward(nn, t), 0.001f));
		Assert.assertTrue(t.equals(ffl.backward(nn, t), 0.001f));
	}
}
