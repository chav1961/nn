package nn.test;

import org.junit.Assert;
import org.junit.Test;

import chav1961.nn.api.interfaces.Layer.LayerFactory;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor.TenzorFactory;
import chav1961.nn.core.network.NeuralNetworkImpl;
import chav1961.nn.standalone.layer.StandaloneLayerFactory;
import chav1961.nn.standalone.util.StandaloneTenzorFactory;

public class NeuralNetworkTest {
	@Test
	public void test() {
		final TenzorFactory	tf = new StandaloneTenzorFactory();
		final LayerFactory	lf = new StandaloneLayerFactory();
		final NeuralNetwork	nn = new NeuralNetworkImpl(tf, lf);
		
		
	}
}
