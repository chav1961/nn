package chav1961.nn.api.interfaces;

public interface NeuralNetwork {
	TenzorFactory getTenzorFactory();
	LayerFactory getLayerFactory();
	NeuralNetwork add(Layer... layers);
	Layer[] getLayers();
	
	NeuralNetwork prepare(boolean forwardOnly);
	Tenzor forward(Tenzor input);
	Tenzor backward(Tenzor errors);
	XTenzor forward(XTenzor input);
	XTenzor backward(XTenzor errors);
	NeuralNetwork unprepare();
}
