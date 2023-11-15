package nn.test;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.Layer.LayerType;
import chav1961.nn.api.interfaces.LayerFactory;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.TenzorFactory;
import chav1961.nn.core.network.NeuralNetworkImpl;
import chav1961.nn.standalone.layer.StandaloneLayerFactory;
import chav1961.nn.standalone.util.StandaloneTenzorFactory;

public class NeuralNetworkTest {
	@Test
	public void lifeCycleTest() {
		final TenzorFactory	tf = new StandaloneTenzorFactory();
		final LayerFactory	lf = new StandaloneLayerFactory();
		final NeuralNetwork	nn = new NeuralNetworkImpl(tf, lf);
		
		Assert.assertEquals(tf, nn.getTenzorFactory()); 
		Assert.assertEquals(lf, nn.getLayerFactory());
		
		try{new NeuralNetworkImpl(null, lf);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{new NeuralNetworkImpl(tf, null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		
		nn.add(lf.newInstance(LayerType.INPUT, 10), lf.newInstance(LayerType.OUTPUT, 10));
		
		try {nn.add((Layer[])null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try {nn.add((Layer)null);
			Assert.fail("Mandatory exception was not detected (nulls inside 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		
		try {nn.forward(tf.newInstance(10));
			Assert.fail("Mandatory exception was not detected (calling forward before preparation)");
		} catch (IllegalStateException exc) {
		}
		
		try {nn.backward(tf.newInstance(10));
			Assert.fail("Mandatory exception was not detected (calling forward before preparation)");
		} catch (IllegalStateException exc) {
		}
		
		nn.prepare(true);

		try {nn.prepare(true);
			Assert.fail("Mandatory exception was not detected (attempt to prepare NN twice)");
		} catch (IllegalStateException exc) {
		}
		
		try {nn.add(lf.newInstance(LayerType.INPUT, 10));
			Assert.fail("Mandatory exception was not detected (attempt to add layer into already prepared NN)");
		} catch (IllegalStateException exc) {
		}
		
		try {nn.backward(tf.newInstance(10));
			Assert.fail("Mandatory exception was not detected (attempt to backward without forward)");
		} catch (IllegalStateException exc) {
		}
		
		nn.forward(tf.newInstance(10));
		
		try {nn.forward(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}		
		
		nn.backward(tf.newInstance(10));

		try {nn.backward(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}		
		
		nn.unprepare();

		try {nn.unprepare();
			Assert.fail("Mandatory exception was not detected (attempt to unprepare NN twice)");
		} catch (IllegalStateException exc) {
		}
	}
}
