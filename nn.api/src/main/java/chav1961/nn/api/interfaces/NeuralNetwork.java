package chav1961.nn.api.interfaces;

public interface NeuralNetwork {
	TenzorFactory getTenzorFactory();
	LayerFactory getLayerFactory();
	NeuralNetwork add(Layer... layers);
	Layer[] getLayers();
	
	NeuralNetwork prepare(boolean forwardOnly);
	Tenzor forward(Tenzor input);
	Tenzor backward(Tenzor errors);
	NeuralNetwork unprepare();
}
