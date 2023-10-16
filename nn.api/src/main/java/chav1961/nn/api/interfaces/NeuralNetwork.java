package chav1961.nn.api.interfaces;

public interface NeuralNetwork {
	Tenzor.TenzorFactory getTenzorFactory();
	Layer.LayerFactory getLayerFactory();
	NeuralNetwork add(Layer... layers);
	
	NeuralNetwork prepare();
	Tenzor forward(Tenzor input);
	Tenzor backward(Tenzor errors);
	NeuralNetwork unprepare();
}
