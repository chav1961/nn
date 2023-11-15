package chav1961.nn.api.interfaces;

public interface XNeuralNetwork {
	TenzorFactory getTenzorFactory();
	LayerFactory getLayerFactory();
	XNeuralNetwork add(Layer... layers);
	Layer[] getLayers();
	
	XNeuralNetwork prepare(boolean forwardOnly);
	XTenzor forward(XTenzor input);
	XTenzor backward(XTenzor errors);
	XNeuralNetwork unprepare();
}
