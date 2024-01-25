package chav1961.nn.standalone;

import java.io.IOException;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.core.network.AbstractNeuralNetwork;
import chav1961.nn.standalone.factories.StandaloneLayerFactory;
import chav1961.nn.standalone.factories.StandaloneTenzorFactory;

public class StandaloneNetworkImpl extends AbstractNeuralNetwork<NeuralNetwork<?,?>,Layer<?,?>> implements NeuralNetwork<NeuralNetwork<?,?>,Layer<?,?>> {
	public StandaloneNetworkImpl() {
		super(new StandaloneTenzorFactory(), new StandaloneLayerFactory<NeuralNetwork<?,?>, Layer<?,?>>());
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tenzor forward(Tenzor input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor backward(Tenzor errors) {
		// TODO Auto-generated method stub
		return null;
	}

}
