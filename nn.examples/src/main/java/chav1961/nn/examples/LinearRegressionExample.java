package chav1961.nn.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import chav1961.nn.api.interfaces.AnyLayer.LayerType;
import chav1961.nn.api.interfaces.AnyNeuralNetwork;
import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.factories.NeuralNetworkFactory;
import chav1961.nn.core.dataset.DataSetManager;

public class LinearRegressionExample {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try(final NeuralNetwork<NeuralNetwork<?,?>,Layer<?,?>>	ann = (NeuralNetwork<NeuralNetwork<?,?>,Layer<?,?>>)AnyNeuralNetwork.Factory.newInstance(URI.create(NeuralNetworkFactory.NETWORD_FACTORY_SCHEMA+":standalone:/")).newInstance(); 
			final InputStream		is = LinearRegressionExample.class.getResourceAsStream("/datasets/linear.csv");
			final Reader			rdr = new InputStreamReader(is)) {
			final DataSetManager	mgr = DataSetManager.fromCsv(1, false, false, false, ann.getTenzorFactory(), rdr); 
			
			System.err.println("Size="+mgr.size());
			ann.add(ann.getLayerFactory().newInstance(LayerType.INPUT),
					ann.getLayerFactory().newInstance(LayerType.FEED_FORWARD),
					ann.getLayerFactory().newInstance(LayerType.OUTPUT));
			ann.prepare(true);
			ann.unprepare();
		}
	}

}
