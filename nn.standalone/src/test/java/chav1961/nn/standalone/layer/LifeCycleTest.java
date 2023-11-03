package chav1961.nn.standalone.layer;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Layer.LayerFactory;
import chav1961.nn.api.interfaces.Tenzor.TenzorFactory;
import chav1961.nn.core.network.NeuralNetworkImpl;
import chav1961.nn.standalone.util.StandaloneTenzorFactory;

public class LifeCycleTest {
	@Test
	public void lifeCycleTest() {
		final TenzorFactory	tf = new StandaloneTenzorFactory();
		final LayerFactory	lf = new StandaloneLayerFactory();
		final NeuralNetwork	nn = new NeuralNetworkImpl(tf, lf);
		
		test(nn, new InputLayer(10), null, new OutputLayer(10), new OutputLayer(10), new InputLayer(10));
		test(nn, new OutputLayer(10), new InputLayer(10), new OutputLayer(10), null, new InputLayer(10));
	}

	private void test(final NeuralNetwork nn, final Layer layer, final Layer validPredecessor, final Layer invalidPredecessor, final Layer validFollower, final Layer invalidFollower) {
		if (validPredecessor != null) {
			Assert.assertTrue(layer.canConnectBefore(nn, validPredecessor));
		}
		Assert.assertFalse(layer.canConnectBefore(nn, invalidPredecessor));
		
		try {
			layer.canConnectBefore(null, validPredecessor);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			layer.canConnectBefore(nn, null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}

		if (validFollower != null) {
			Assert.assertTrue(layer.canConnectAfter(nn, validFollower));
		}
		Assert.assertFalse(layer.canConnectAfter(nn, invalidFollower));
		
		try {
			layer.canConnectAfter(null, validFollower);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			layer.canConnectAfter(nn, null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		
		try {
			layer.connectBefore(null, invalidPredecessor);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			layer.connectBefore(nn, null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		if (validPredecessor != null) {
			try {
				layer.connectBefore(nn, validPredecessor);
				Assert.fail("Mandatory exception was not detected (layer not prepared)");
			} catch (IllegalStateException exc) {
			}
		}

		try {
			layer.connectAfter(null, invalidFollower);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {
			layer.connectAfter(nn, null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		if (validFollower != null) {
			try {
				layer.connectAfter(nn, validFollower);
				Assert.fail("Mandatory exception was not detected (layer not prepared)");
			} catch (IllegalStateException exc) {
			}
		}
		
		layer.prepare(nn, false);

		try{
			layer.prepare(null, false);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{
			layer.prepare(nn, false);
			Assert.fail("Mandatory exception was not detected (attempt to prepare twice)");
		} catch (IllegalStateException exc) {
		}

		
		try {
			layer.connectBefore(nn, invalidPredecessor);
			Assert.fail("Mandatory exception was not detected (invalid predecessor)");
		} catch (IllegalArgumentException exc) {
		}
		
		if (validPredecessor != null) {
			layer.connectBefore(nn, validPredecessor);
	
			try {
				layer.connectBefore(nn, validPredecessor);
				Assert.fail("Mandatory exception was not detected (attempt to connect twice)");
			} catch (IllegalStateException exc) {
			}
		}
		
		try {
			layer.connectAfter(nn, invalidFollower);
			Assert.fail("Mandatory exception was not detected (invalid follower)");
		} catch (IllegalArgumentException exc) {
		}

		if (validFollower != null) {
			layer.connectAfter(nn, validFollower);
	
			try {
				layer.connectAfter(nn, validFollower);
				Assert.fail("Mandatory exception was not detected (attempt to connect twice)");
			} catch (IllegalStateException exc) {
			}
		}
		
		// -----
		
		layer.unprepare(nn);
		
		try{layer.unprepare(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{layer.unprepare(nn);
			Assert.fail("Mandatory exception was not detected (attempt to unprepare twice)");
		} catch (IllegalStateException exc) {
		}
	}
}
